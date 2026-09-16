package com.fruitivia.buyer.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruitivia.buyer.Buyer;
import com.fruitivia.buyer.BuyerRepository;
import com.fruitivia.fruit.Fruit;
import com.fruitivia.fruit.FruitRepository;
import com.fruitivia.fruit.FruitVariety;
import com.fruitivia.fruit.FruitVarietyRepository;
import com.fruitivia.quotation.dto.QuotationItemRequest;
import com.fruitivia.quotation.dto.QuotationRequest;
import com.fruitivia.security.CustomUserDetails;
import com.fruitivia.security.JwtService;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class BuyerQuotationControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuyerRepository buyerRepository;
    
    @Autowired
    private FruitRepository fruitRepository;
    
    @Autowired
    private FruitVarietyRepository fruitVarietyRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private User buyerUser;
    private String buyerToken;
    private Fruit fruit;
    private FruitVariety variety;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        buyerUser = User.builder()
                .name("Buyer One")
                .email("buyer1@fruitivia.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Role.BUYER)
                .build();
        userRepository.save(buyerUser);

        Buyer buyer = Buyer.builder()
                .user(buyerUser)
                .companyName("Buyer One Co")
                .contactEmail("buyer1@fruitivia.com")
                .build();
        buyerRepository.save(buyer);

        buyerToken = jwtService.generateToken(new CustomUserDetails(buyerUser));
        
        fruit = Fruit.builder().name("Apple").description("Apple").build();
        fruitRepository.save(fruit);
        
        variety = FruitVariety.builder().fruit(fruit).name("Gala").build();
        fruitVarietyRepository.save(variety);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE quotation_status_history");
        jdbcTemplate.execute("TRUNCATE TABLE quotation_items");
        jdbcTemplate.execute("TRUNCATE TABLE quotations");
        jdbcTemplate.execute("TRUNCATE TABLE fruit_varieties");
        jdbcTemplate.execute("TRUNCATE TABLE fruits");
        jdbcTemplate.execute("TRUNCATE TABLE buyers");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Test
    void shouldRequestQuotation() throws Exception {
        QuotationItemRequest item = new QuotationItemRequest();
        item.setFruitId(fruit.getId());
        item.setVarietyId(variety.getId());
        item.setQuantity(new BigDecimal("1000"));

        QuotationRequest request = new QuotationRequest();
        request.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/quotations/request")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REQUESTED"))
                .andExpect(jsonPath("$.items[0].quantity").value(1000));
    }
}
