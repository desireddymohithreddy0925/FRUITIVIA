package com.fruitivia.procurement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fruitivia.procurement.dto.ProcurementItemCreateRequest;
import com.fruitivia.procurement.dto.ProcurementOrderCreateRequest;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import com.fruitivia.security.JwtService;
import com.fruitivia.security.CustomUserDetails;
import com.fruitivia.supplier.Supplier;
import com.fruitivia.supplier.SupplierRepository;
import com.fruitivia.warehouse.Warehouse;
import com.fruitivia.warehouse.WarehouseRepository;
import com.fruitivia.fruit.Fruit;
import com.fruitivia.fruit.FruitRepository;
import com.fruitivia.fruit.FruitVariety;
import com.fruitivia.fruit.FruitVarietyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fruitivia.batch.FruitBatchRepository;
import com.fruitivia.batch.FruitBatchHistoryRepository;
import com.fruitivia.quality.QualityInspectionRepository;
import com.fruitivia.quality.QualityInspectionHistoryRepository;

@SpringBootTest
@ActiveProfiles("test")
class ProcurementControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private FruitRepository fruitRepository;
    
    @Autowired
    private FruitVarietyRepository varietyRepository;
    
    @Autowired
    private ProcurementOrderRepository procurementOrderRepository;
    
    @Autowired
    private ProcurementItemRepository procurementItemRepository;

    @Autowired
    private ProcurementStateHistoryRepository stateHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private QualityInspectionHistoryRepository qualityInspectionHistoryRepository;

    @Autowired
    private QualityInspectionRepository qualityInspectionRepository;

    @Autowired
    private FruitBatchHistoryRepository fruitBatchHistoryRepository;

    @Autowired
    private FruitBatchRepository fruitBatchRepository;

    private String adminToken;
    private String qcToken;
    private String buyerToken;

    private Supplier supplier;
    private Warehouse warehouse;
    private Fruit fruit;
    private FruitVariety variety;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        fruitBatchHistoryRepository.deleteAll();
        fruitBatchRepository.deleteAll();
        qualityInspectionHistoryRepository.deleteAll();
        qualityInspectionRepository.deleteAll();
        procurementItemRepository.deleteAll();
        stateHistoryRepository.deleteAll();
        procurementOrderRepository.deleteAll();
        userRepository.deleteAll();
        supplierRepository.deleteAll();
        warehouseRepository.deleteAll();
        varietyRepository.deleteAll();
        fruitRepository.deleteAll();

        adminToken = createUserAndGetToken("admin_proc@fruitivia.com", Role.ADMIN);
        qcToken = createUserAndGetToken("qc_proc@fruitivia.com", Role.QC_INSPECTOR);
        buyerToken = createUserAndGetToken("buyer_proc@fruitivia.com", Role.BUYER);

        supplier = Supplier.builder().supplierCode("SUP-PROC-1").name("Supplier 1").build();
        supplier = supplierRepository.saveAndFlush(supplier);

        warehouse = Warehouse.builder().name("WH-1").location("Loc").type(com.fruitivia.warehouse.WarehouseType.COLD_STORAGE).capacity(100.0).build();
        warehouse = warehouseRepository.saveAndFlush(warehouse);

        fruit = Fruit.builder().name("Mango").description("Mangoes").build();
        fruit = fruitRepository.saveAndFlush(fruit);

        variety = FruitVariety.builder().name("Alphonso").fruit(fruit).shelfLifeDays(10).build();
        variety = varietyRepository.saveAndFlush(variety);
    }

    private String createUserAndGetToken(String email, Role role) {
        User user = User.builder()
                .name(role.name() + " User")
                .email(email)
                .passwordHash(passwordEncoder.encode("password"))
                .role(role)
                .build();
        user.setActive(true);
        userRepository.saveAndFlush(user);
        return jwtService.generateToken(new CustomUserDetails(user));
    }

    @Test
    void testAdminCanCreateProcurementOrder() throws Exception {
        ProcurementItemCreateRequest item = ProcurementItemCreateRequest.builder()
                .fruitId(fruit.getId())
                .varietyId(variety.getId())
                .quantity(new BigDecimal("100"))
                .unitPrice(new BigDecimal("50"))
                .build();

        ProcurementOrderCreateRequest request = ProcurementOrderCreateRequest.builder()
                .supplierId(supplier.getId())
                .warehouseId(warehouse.getId())
                .purchaseDate(Instant.now())
                .items(List.of(item))
                .build();

        mockMvc.perform(post("/api/v1/procurement")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").exists())
                .andExpect(jsonPath("$.totalAmount").value(5000));
    }

    @Test
    void testBuyerCannotAccessProcurement() throws Exception {
        mockMvc.perform(get("/api/v1/procurement")
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isForbidden());
    }
}
