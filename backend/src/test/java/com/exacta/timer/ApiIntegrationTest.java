package com.exacta.timer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.exacta.timer.entity.Role;
import com.exacta.timer.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerLoginAndCreateClient() throws Exception {
        String email = "ada+" + UUID.randomUUID() + "@exacta.test";
        String registerJson = """
                {
                  "name": "Ada Admin",
                  "email": "%s",
                  "password": "supersecret",
                  "hourlyRate": 350.00
                }
                """.formatted(email);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.password").doesNotExist());

        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            user.setRole(Role.ADMIN);
            userRepository.save(user);
        });

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "supersecret"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(login.getResponse().getContentAsString());
        String token = body.get("accessToken").asText();

        MvcResult clientResult = mockMvc.perform(post("/api/v1/clients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Northwind Legal",
                                  "contactEmail": "billing@northwind.test",
                                  "company": "Northwind LLP"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Northwind Legal"))
                .andReturn();

        long clientId = objectMapper.readTree(clientResult.getResponse().getContentAsString())
                .get("id")
                .asLong();

        MvcResult projectResult = mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Merger diligence",
                                  "clientId": %s,
                                  "status": "ACTIVE"
                                }
                                """.formatted(clientId)))
                .andExpect(status().isCreated())
                .andReturn();

        long projectId = objectMapper.readTree(projectResult.getResponse().getContentAsString())
                .get("id")
                .asLong();

        mockMvc.perform(post("/api/v1/time-entries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectId": %s,
                                  "startTime": "2026-08-17T14:00:00Z",
                                  "endTime": "2026-08-17T16:30:00Z",
                                  "description": "SPA markup",
                                  "isBillable": true,
                                  "status": "STOPPED"
                                }
                                """.formatted(projectId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unbilledRevenue.entryCount").value(1))
                .andExpect(jsonPath("$.unbilledRevenue.amount").value(875.0))
                .andExpect(jsonPath("$.recentEntries.length()").value(1));

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isUnauthorized());
    }
}
