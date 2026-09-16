package com.fruitivia.quality;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fruitivia.procurement.*;
import com.fruitivia.quality.dto.QualityInspectionCreateRequest;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class QualityInspectionControllerTests {

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
    private QualityInspectionRepository qualityInspectionRepository;

    @Autowired
    private QualityInspectionHistoryRepository qualityInspectionHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String qcToken;
    private String buyerToken;

    private ProcurementItem procurementItem;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        qualityInspectionHistoryRepository.deleteAll();
        qualityInspectionRepository.deleteAll();
        procurementItemRepository.deleteAll();
        procurementOrderRepository.deleteAll();
        userRepository.deleteAll();
        supplierRepository.deleteAll();
        warehouseRepository.deleteAll();
        varietyRepository.deleteAll();
        fruitRepository.deleteAll();

        qcToken = createUserAndGetToken("qc_insp@fruitivia.com", Role.QC_INSPECTOR);
        buyerToken = createUserAndGetToken("buyer_insp@fruitivia.com", Role.BUYER);

        Supplier supplier = Supplier.builder().supplierCode("SUP-QC-1").name("Supplier QC").build();
        supplier = supplierRepository.saveAndFlush(supplier);

        Warehouse warehouse = Warehouse.builder().name("WH-QC-1").location("Loc").type(com.fruitivia.warehouse.WarehouseType.COLD_STORAGE).capacity(100.0).build();
        warehouse = warehouseRepository.saveAndFlush(warehouse);

        Fruit fruit = Fruit.builder().name("Apple QC").description("Apples").build();
        fruit = fruitRepository.saveAndFlush(fruit);

        FruitVariety variety = FruitVariety.builder().name("Fuji").fruit(fruit).shelfLifeDays(10).build();
        variety = varietyRepository.saveAndFlush(variety);

        ProcurementOrder order = ProcurementOrder.builder()
                .orderNumber("PO-QC-1")
                .supplier(supplier)
                .warehouse(warehouse)
                .purchaseDate(Instant.now())
                .status(ProcurementStatus.APPROVED)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(new BigDecimal("1000"))
                .currency("INR")
                .build();
        order = procurementOrderRepository.saveAndFlush(order);

        procurementItem = ProcurementItem.builder()
                .procurementOrder(order)
                .fruit(fruit)
                .variety(variety)
                .quantity(new BigDecimal("100"))
                .unitPrice(new BigDecimal("10"))
                .totalPrice(new BigDecimal("1000"))
                .build();
        procurementItem = procurementItemRepository.saveAndFlush(procurementItem);
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
    void testQcCanCreateInspection() throws Exception {
        QualityInspectionCreateRequest request = QualityInspectionCreateRequest.builder()
                .procurementItemId(procurementItem.getId())
                .inspectedQuantity(new BigDecimal("100"))
                .acceptedQuantity(new BigDecimal("80"))
                .rejectedQuantity(new BigDecimal("20"))
                .grade(QualityGrade.GRADE_A)
                .build();

        mockMvc.perform(post("/api/v1/quality")
                        .header("Authorization", "Bearer " + qcToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.inspectionNumber").exists())
                .andExpect(jsonPath("$.acceptedQuantity").value(80));
    }

    @Test
    void testQcCannotCreateInspectionWithInvalidQuantities() throws Exception {
        QualityInspectionCreateRequest request = QualityInspectionCreateRequest.builder()
                .procurementItemId(procurementItem.getId())
                .inspectedQuantity(new BigDecimal("100"))
                .acceptedQuantity(new BigDecimal("80"))
                .rejectedQuantity(new BigDecimal("30")) // 80 + 30 != 100
                .grade(QualityGrade.GRADE_A)
                .build();

        mockMvc.perform(post("/api/v1/quality")
                        .header("Authorization", "Bearer " + qcToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Or 400 Bad Request
    }

    @Test
    void testBuyerCannotAccessQualityControl() throws Exception {
        mockMvc.perform(get("/api/v1/quality")
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isForbidden());
    }
}
