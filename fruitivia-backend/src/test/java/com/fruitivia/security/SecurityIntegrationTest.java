package com.fruitivia.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruitivia.auth.dto.RegisterRequest;
import com.fruitivia.buyer.Buyer;
import com.fruitivia.buyer.BuyerRepository;
import com.fruitivia.order.Order;
import com.fruitivia.order.OrderRepository;
import com.fruitivia.order.OrderStatus;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import com.fruitivia.quotation.Quotation;
import com.fruitivia.quotation.QuotationRepository;
import com.fruitivia.quotation.QuotationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class SecurityIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private QuotationRepository quotationRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        orderRepository.deleteAll();
        quotationRepository.deleteAll();
        buyerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testMassAssignmentPreventsAdminRole() throws Exception {
        // This request no longer has role in JSON directly since we removed it from DTO, 
        // but even if someone injects "role":"ADMIN" in the raw JSON, we must test it.
        String rawJson = "{\"name\":\"Malicious User\",\"email\":\"malicious@fruitivia.com\",\"password\":\"password123\",\"role\":\"ADMIN\"}";

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rawJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("BUYER"));

        User user = userRepository.findByEmail("malicious@fruitivia.com").orElseThrow();
        assert user.getRole() == Role.BUYER;
    }

    @Test
    @WithMockUser(username = "buyerA@fruitivia.com", roles = "BUYER")
    void testOrderIdor_BuyerACannotAccessBuyerBOrder() throws Exception {
        // Setup User A
        User userA = User.builder()
                .name("Buyer A")
                .email("buyerA@fruitivia.com")
                .passwordHash("hash")
                .role(Role.BUYER)
                .build();
        userA.setActive(true);
        userA = userRepository.save(userA);

        Buyer buyerA = Buyer.builder()
                .user(userA)
                .companyName("Company A")
                .contactEmail("buyerA@fruitivia.com")
                .contactPerson("Alice")
                .country("USA")
                .build();
        buyerA.setActive(true);
        buyerA = buyerRepository.save(buyerA);

        // Setup User B
        User userB = User.builder()
                .name("Buyer B")
                .email("buyerB@fruitivia.com")
                .passwordHash("hash")
                .role(Role.BUYER)
                .build();
        userB.setActive(true);
        userB = userRepository.save(userB);

        Buyer buyerB = Buyer.builder()
                .user(userB)
                .companyName("Company B")
                .contactEmail("buyerB@fruitivia.com")
                .contactPerson("Bob")
                .country("Canada")
                .build();
        buyerB.setActive(true);
        buyerB = buyerRepository.save(buyerB);

        // Quotation for B
        Quotation quotationB = Quotation.builder()
                .buyer(buyerB)
                .status(QuotationStatus.ACCEPTED)
                .build();
        quotationB = quotationRepository.save(quotationB);

        // Order for B
        Order orderB = Order.builder()
                .orderNumber("ORD-B")
                .buyer(buyerB)
                .quotation(quotationB)
                .status(OrderStatus.PAYMENT_PENDING)
                .totalAmount(BigDecimal.TEN)
                .currencyCode("USD")
                .build();
        orderB.setCreatedAt(Instant.now());
        orderB.setUpdatedAt(Instant.now());
        orderB = orderRepository.save(orderB);

        // Buyer A attempts to access Buyer B's order
        mockMvc.perform(get("/api/v1/orders/" + orderB.getId()))
                .andExpect(status().isForbidden()); // access denied is translated to 403
    }
}
