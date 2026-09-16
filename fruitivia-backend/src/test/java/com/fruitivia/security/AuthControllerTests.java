package com.fruitivia.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruitivia.auth.dto.LoginRequest;
import com.fruitivia.auth.dto.RegisterRequest;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.fruitivia.buyer.BuyerRepository buyerRepository;

    @Autowired
    private com.fruitivia.order.OrderRepository orderRepository;

    @Autowired
    private com.fruitivia.quotation.QuotationRepository quotationRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        orderRepository.deleteAll();
        quotationRepository.deleteAll();
        buyerRepository.deleteAll(); // Clean DB before each test
        userRepository.deleteAll(); 
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Test Buyer")
                .email("buyer@fruitivia.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("buyer@fruitivia.com"))
                .andExpect(jsonPath("$.role").value("BUYER"));
    }

    @Test
    void testRegisterDuplicateEmail() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Admin")
                .email("admin@fruitivia.com")
                .password("password123")
                .build();

        // First registration
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate registration
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already in use"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("QC User")
                .email("qc@fruitivia.com")
                .password("securepass")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));
                
        User user = userRepository.findByEmail("qc@fruitivia.com").orElseThrow();
        user.setRole(Role.QC_INSPECTOR);
        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("qc@fruitivia.com")
                .password("securepass")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLoginInvalidPassword() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Engineer")
                .email("eng@fruitivia.com")
                .password("correctpass")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));
                
        User user = userRepository.findByEmail("eng@fruitivia.com").orElseThrow();
        user.setRole(Role.ENGINEER);
        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("eng@fruitivia.com")
                .password("wrongpass")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // Because Spring Security's AuthenticationProvider throws BadCredentialsException
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/test-auth/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testForbiddenRoleAccess() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Normal User")
                .email("normal@fruitivia.com")
                .password("securepass")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = LoginRequest.builder()
                .email("normal@fruitivia.com")
                .password("securepass")
                .build();

        String responseStr = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(responseStr).get("token").asText();

        // Normal user accessing admin-only endpoint
        mockMvc.perform(get("/api/v1/test-auth/admin-only")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }
}
