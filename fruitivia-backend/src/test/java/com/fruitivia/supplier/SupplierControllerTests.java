package com.fruitivia.supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruitivia.supplier.dto.SupplierCreateRequest;
import com.fruitivia.supplier.dto.SupplierUpdateRequest;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import com.fruitivia.security.JwtService;
import com.fruitivia.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fruitivia.procurement.ProcurementOrderRepository;
import com.fruitivia.procurement.ProcurementItemRepository;
import com.fruitivia.quality.QualityInspectionRepository;
import com.fruitivia.quality.QualityInspectionHistoryRepository;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@ActiveProfiles("test")
class SupplierControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProcurementOrderRepository procurementOrderRepository;

    @Autowired
    private ProcurementItemRepository procurementItemRepository;

    @Autowired
    private com.fruitivia.procurement.ProcurementStateHistoryRepository procurementStateHistoryRepository;

    @Autowired
    private QualityInspectionRepository qualityInspectionRepository;

    @Autowired
    private QualityInspectionHistoryRepository qualityInspectionHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String qcToken;
    private String buyerToken;

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
        procurementStateHistoryRepository.deleteAll();
        procurementOrderRepository.deleteAll();
        userRepository.deleteAll();
        supplierRepository.deleteAll();

        adminToken = createUserAndGetToken("admin@fruitivia.com", Role.ADMIN);
        qcToken = createUserAndGetToken("qc@fruitivia.com", Role.QC_INSPECTOR);
        buyerToken = createUserAndGetToken("buyer@fruitivia.com", Role.BUYER);
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
    void testAdminCanCreateSupplier() throws Exception {
        SupplierCreateRequest request = SupplierCreateRequest.builder()
                .name("Test Farmer")
                .contactPerson("John Doe")
                .phone("1234567890")
                .build();

        mockMvc.perform(post("/api/v1/suppliers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplierCode").exists())
                .andExpect(jsonPath("$.name").value("Test Farmer"));
    }

    @Test
    void testQcInspectorCannotCreateSupplier() throws Exception {
        SupplierCreateRequest request = SupplierCreateRequest.builder()
                .name("Test Farmer")
                .build();

        mockMvc.perform(post("/api/v1/suppliers")
                        .header("Authorization", "Bearer " + qcToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testBuyerCannotAccessSuppliersAtAll() throws Exception {
        mockMvc.perform(get("/api/v1/suppliers")
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testQcCanReadSuppliers() throws Exception {
        // Create supplier as admin
        SupplierCreateRequest request = SupplierCreateRequest.builder()
                .name("Test Farmer")
                .build();
        mockMvc.perform(post("/api/v1/suppliers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Read as QC
        mockMvc.perform(get("/api/v1/suppliers")
                        .header("Authorization", "Bearer " + qcToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }
}
