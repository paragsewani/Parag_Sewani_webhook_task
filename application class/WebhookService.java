package com.example.webhooksql;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String SUBMIT_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    public void executeWebhookFlow() {
        try {
            logger.info("Starting webhook flow...");
            
            // Step 1: Generate webhook
            WebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse == null || webhookResponse.getAccessToken() == null) {
                logger.error("Failed to generate webhook or retrieve access token");
                return;
            }
            
            logger.info("Webhook generated successfully");
            logger.info("Access Token received: {}", webhookResponse.getAccessToken().substring(0, 20) + "...");
            
            // Step 2: Generate SQL query solution
            String sqlQuery = generateSqlQuery();
            logger.info("SQL query generated");
            
            // Step 3: Submit solution
            submitSolution(webhookResponse.getAccessToken(), sqlQuery);
            
        } catch (Exception e) {
            logger.error("Error in webhook flow: ", e);
        }
    }

    private WebhookResponse generateWebhook() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                GENERATE_WEBHOOK_URL, 
                request, 
                WebhookResponse.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            logger.error("Error generating webhook: ", e);
            return null;
        }
    }

    private String generateSqlQuery() {
        // The problem asks for the count of younger employees in the same department
        // We need to compare DOB (Date of Birth) - older DOB means younger person
        
        return """
            SELECT 
                e1.EMP_ID,
                e1.FIRST_NAME,
                e1.LAST_NAME,
                d.DEPARTMENT_NAME,
                COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
            FROM 
                EMPLOYEE e1
            INNER JOIN 
                DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID
            LEFT JOIN 
                EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT 
                AND e2.DOB > e1.DOB
            GROUP BY 
                e1.EMP_ID, 
                e1.FIRST_NAME, 
                e1.LAST_NAME, 
                d.DEPARTMENT_NAME
            ORDER BY 
                e1.EMP_ID DESC
            """.trim();
    }

    private void submitSolution(String accessToken, String sqlQuery) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("finalQuery", sqlQuery);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                SUBMIT_WEBHOOK_URL, 
                request, 
                String.class
            );
            
            logger.info("Solution submitted successfully");
            logger.info("Response: {}", response.getBody());
            
        } catch (Exception e) {
            logger.error("Error submitting solution: ", e);
        }
    }
}
