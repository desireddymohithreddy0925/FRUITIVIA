package com.fruitivia.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fruitivia.batch.dto.FruitBatchCreateRequest;
import com.fruitivia.procurement.*;
import com.fruitivia.quality.*;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class FruitBatchControllerTests {

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
    private ProcurementStateHistoryRepository procurementStateHistoryRepository;

    @Autowired
    private QualityInspectionRepository qualityInspectionRepository;

    @Autowired
    private QualityInspectionHistoryRepository qualityInspectionHistoryRepository;

    @Autowired
    private FruitBatchRepository fruitBatchRepository;
    
    @Autowired
    private FruitBatchHistoryRepository fruitBatchHistoryRepository;

    @Autowired
    private BatchSequenceRepository batchSequenceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String qcToken;

    private QualityInspection passedInspection;
    private QualityInspection failedInspection;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        fruitBatchHistoryRepository.deleteAll();
        fruitBatchRepository.deleteAll();
        batchSequenceRepository.deleteAll();
        qualityInspectionHistoryRepository.deleteAll();
        qualityInspectionRepository.deleteAll();
        procurementItemRepository.deleteAll();
        procurementStateHistoryRepository.deleteAll();
        procurementOrderRepository.deleteAll();
        userRepository.deleteAll();
        supplierRepository.deleteAll();
        warehouseRepository.deleteAll();
        varietyRepository.deleteAll();
        fruitRepository.deleteAll();

        adminToken = createUserAndGetToken("admin_batch@fruitivia.com", Role.ADMIN);
        qcToken = createUserAndGetToken("qc_batch@fruitivia.com", Role.QC_INSPECTOR);

        Supplier supplier = Supplier.builder().supplierCode("SUP-B-1").name("Supplier Batch").build();
        supplier = supplierRepository.saveAndFlush(supplier);

        warehouse = Warehouse.builder().name("WH-B-1").location("Loc").type(com.fruitivia.warehouse.WarehouseType.COLD_STORAGE).capacity(100.0).build();
        warehouse = warehouseRepository.saveAndFlush(warehouse);

        Fruit fruit = Fruit.builder().name("Apple").description("Apples").build();
        fruit = fruitRepository.saveAndFlush(fruit);

        FruitVariety variety = FruitVariety.builder().name("Fuji").fruit(fruit).shelfLifeDays(10).build();
        variety = varietyRepository.saveAndFlush(variety);

        ProcurementOrder order = ProcurementOrder.builder()
                .orderNumber("PO-B-1")
                .supplier(supplier)
                .warehouse(warehouse)
                .purchaseDate(Instant.now())
                .harvestDate(Instant.now())
                .status(ProcurementStatus.APPROVED)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(new BigDecimal("1000"))
                .currency("INR")
                .build();
        order = procurementOrderRepository.saveAndFlush(order);

        ProcurementItem procurementItem = ProcurementItem.builder()
                .procurementOrder(order)
                .fruit(fruit)
                .variety(variety)
                .quantity(new BigDecimal("100"))
                .unitPrice(new BigDecimal("10"))
                .totalPrice(new BigDecimal("1000"))
                .build();
        procurementItem = procurementItemRepository.saveAndFlush(procurementItem);

        User qcUser = userRepository.findByEmail("qc_batch@fruitivia.com").orElseThrow();

        passedInspection = QualityInspection.builder()
                .inspectionNumber("QC-1")
                .procurementItem(procurementItem)
                .inspector(qcUser)
                .inspectedQuantity(new BigDecimal("100"))
                .acceptedQuantity(new BigDecimal("100"))
                .rejectedQuantity(new BigDecimal("0"))
                .grade(QualityGrade.GRADE_A)
                .status(InspectionStatus.PASSED)
                .inspectionDate(Instant.now())
                .build();
        passedInspection = qualityInspectionRepository.saveAndFlush(passedInspection);

        failedInspection = QualityInspection.builder()
                .inspectionNumber("QC-2")
                .procurementItem(procurementItem)
                .inspector(qcUser)
                .inspectedQuantity(new BigDecimal("100"))
                .acceptedQuantity(new BigDecimal("0"))
                .rejectedQuantity(new BigDecimal("100"))
                .grade(QualityGrade.REJECTED)
                .status(InspectionStatus.FAILED)
                .inspectionDate(Instant.now())
                .build();
        failedInspection = qualityInspectionRepository.saveAndFlush(failedInspection);
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
    void testAdminCanCreateBatch() throws Exception {
        FruitBatchCreateRequest request = FruitBatchCreateRequest.builder()
                .qualityInspectionId(passedInspection.getId())
                .approvedQuantity(new BigDecimal("100"))
                .warehouseId(warehouse.getId())
                .packingDate(Instant.now())
                .build();

        mockMvc.perform(post("/api/v1/batches")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.batchNumber").exists());
    }

    @Test
    void testCannotCreateBatchFromFailedInspection() throws Exception {
        FruitBatchCreateRequest request = FruitBatchCreateRequest.builder()
                .qualityInspectionId(failedInspection.getId())
                .approvedQuantity(new BigDecimal("0"))
                .warehouseId(warehouse.getId())
                .packingDate(Instant.now())
                .build();

        mockMvc.perform(post("/api/v1/batches")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCannotCreateBatchWithMoreThanAcceptedQuantity() throws Exception {
        FruitBatchCreateRequest request = FruitBatchCreateRequest.builder()
                .qualityInspectionId(passedInspection.getId())
                .approvedQuantity(new BigDecimal("200")) // Accepted was 100
                .warehouseId(warehouse.getId())
                .packingDate(Instant.now())
                .build();

        mockMvc.perform(post("/api/v1/batches")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
