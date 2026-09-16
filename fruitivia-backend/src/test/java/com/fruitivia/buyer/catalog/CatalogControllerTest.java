package com.fruitivia.buyer.catalog;

import com.fruitivia.fruit.Fruit;
import com.fruitivia.fruit.FruitRepository;
import com.fruitivia.security.CustomUserDetails;
import com.fruitivia.security.JwtService;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest

@ActiveProfiles("test")
public class CatalogControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private JwtService jwtService;

    private String buyerToken;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE fruit_batches");
        jdbcTemplate.execute("TRUNCATE TABLE fruit_varieties");
        jdbcTemplate.execute("TRUNCATE TABLE fruits");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        User buyerUser = User.builder()
                .name("Test Buyer")
                .email("buyer@test.com")
                .passwordHash("password")
                .role(Role.BUYER)
                .build();
        buyerUser.setActive(true);
        userRepository.save(buyerUser);
        buyerToken = jwtService.generateToken(new CustomUserDetails(buyerUser));

        Fruit fruit = Fruit.builder()
                .name("Apple")
                .description("A sweet fruit")
                .build();
        fruitRepository.save(fruit);
    }
    
    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE fruit_batches");
        jdbcTemplate.execute("TRUNCATE TABLE fruit_varieties");
        jdbcTemplate.execute("TRUNCATE TABLE fruits");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Test
    void buyerCanBrowseCatalog() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/fruits")
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Apple"));
    }
}
