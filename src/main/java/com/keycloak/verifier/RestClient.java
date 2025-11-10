package com.keycloak.verifier;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import org.jboss.logging.Logger;


public class RestClient {
    private static final Logger log = Logger.getLogger(RestClient.class);
    private static final String CATH_BACKEND_BASE_URL_KEY = "CATH_BACKEND_BASE_URL";
    private static final String DEFAULT_BASE_URL = "http://localhost:8085";
    private static final String VERIFY_PATH = "PATH_VERIFY_API";

    public static int assignDoctorToHospital(UUID doctorId) throws IOException {
        try {
            String baseUrl = System.getenv(CATH_BACKEND_BASE_URL_KEY);
            String verifyPath = System.getenv(VERIFY_PATH);
            log.infof("SAB_BACKEND_BASE_URL from env: %s", baseUrl);

            if (baseUrl == null || baseUrl.isEmpty()) {
                log.warnf("Environment variable %s is not set. Using default: %s",
                        CATH_BACKEND_BASE_URL_KEY, DEFAULT_BASE_URL);
                baseUrl = DEFAULT_BASE_URL;
            }
            String fullApiUrl = baseUrl + verifyPath + "/assign-to-hospital" + "?doctorId=" + doctorId.toString();

            log.infof("Calling URL: %s", fullApiUrl);

            URI uri = URI.create(fullApiUrl);
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(uri)
                            .timeout(Duration.ofSeconds(10))
                            .header("Content-Type", "application/json; utf-8")
                            .PUT(HttpRequest.BodyPublishers.noBody())
                            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.infof("Received status code: %d", response.statusCode());
            return response.statusCode();

        } catch (IOException | InterruptedException e) {
            log.errorf("Failed to assign doctor: %s", e.getMessage(), e);
            throw new IOException("Failed to assign doctor: " + e.getMessage(), e);
        }
    }
}
