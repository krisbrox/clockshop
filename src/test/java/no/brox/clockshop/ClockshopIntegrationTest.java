package no.brox.clockshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class ClockshopIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @LocalServerPort
  int randomServerPort;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
      .withUsername("root")
      .withPassword("test");

  @DynamicPropertySource
  static void registerTestProperties(DynamicPropertyRegistry registry) {
    postgres.start();

    registry.add(
        "spring.datasource.url",
        postgres::getJdbcUrl
    );
    registry.add(
        "spring.datasource.username",
        postgres::getUsername
    );
    registry.add(
        "spring.datasource.password",
        postgres::getPassword
    );
  }

  List<String> productIds;

  @Test
  public void testAppInit() {
  }

  @Test
  public void testCheckout() {
    productIds = new ArrayList<>();
    productIds.add("001");

    HttpEntity<List<String>> request = new HttpEntity<>(
        productIds,
        new HttpHeaders()
    );

    Integer response = restTemplate.postForObject(
        "http://localhost:" + randomServerPort + "/checkout",
        request,
        Integer.class
    );
    assertThat(response).isEqualTo(1);
  }
}
