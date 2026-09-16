package com.fruitivia.warehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fruitivia.security.CustomUserDetails;
import com.fruitivia.security.JwtService;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import com.fruitivia.warehouse.dto.WarehouseCreateRequest;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class WarehouseControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE storage_locations");
        jdbcTemplate.execute("TRUNCATE TABLE warehouses");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        adminToken = createUserAndGetToken("admin_wh@fruitivia.com", Role.ADMIN);
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
    void testCreateWarehouse() throws Exception {
        WarehouseCreateRequest request = WarehouseCreateRequest.builder()
                .name("Cold Storage A")
                .location("Delhi")
                .type(WarehouseType.COLD_STORAGE)
                .capacity(500.0)
                .minTemperature(2.0)
                .maxTemperature(8.0)
                .minHumidity(80.0)
                .maxHumidity(90.0)
                .build();

        mockMvc.perform(post("/api/v1/warehouses")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cold Storage A"))
                .andExpect(jsonPath("$.type").value("COLD_STORAGE"));
    }

    @Test
    void testGetWarehousesWithFilters() throws Exception {
        Warehouse wh1 = Warehouse.builder().name("W1").type(WarehouseType.GENERAL).capacity(100.0).build();
        wh1.setActive(true);
        warehouseRepository.saveAndFlush(wh1);

        Warehouse wh2 = Warehouse.builder().name("W2").type(WarehouseType.COLD_STORAGE).capacity(50.0).build();
        wh2.setActive(true);
        warehouseRepository.saveAndFlush(wh2);

        mockMvc.perform(get("/api/v1/warehouses?type=COLD_STORAGE")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("W2"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
