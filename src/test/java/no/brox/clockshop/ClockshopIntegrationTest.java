package no.brox.clockshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import no.brox.clockshop.products.ProductCache;
import no.brox.clockshop.products.ProductController.CheckoutDto;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = "spring.flyway.clean-disabled=false"
)
@Testcontainers
@ActiveProfiles("test")
class ClockshopIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private Jdbi jdbi;

  @Autowired
  private ProductCache productCache;

  @LocalServerPort
  int randomServerPort;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
      .withDatabaseName("clockshop")
      .withUsername("testuser")
      .withPassword("testpassword");

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
  public void testEmptyCheckout() {
    HttpEntity<List<String>> request = new HttpEntity<>(
        new ArrayList<>(),
        new HttpHeaders()
    );

    CheckoutDto response = restTemplate.postForObject(
        "http://localhost:" + randomServerPort + "/checkout",
        request,
        CheckoutDto.class
    );
    assertThat(response.price()).isEqualTo(0);
  }

  @Test
  public void testSingleItemCheckout() {
    productIds = new ArrayList<>();
    productIds.add("001");

    HttpEntity<List<String>> request = new HttpEntity<>(
        productIds,
        new HttpHeaders()
    );

    CheckoutDto response = restTemplate.postForObject(
        "http://localhost:" + randomServerPort + "/checkout",
        request,
        CheckoutDto.class
    );
    assertThat(response.price()).isEqualTo(100);
  }

  @Test
  public void testMultiItemCheckout() {
    productIds = new ArrayList<>();
    productIds.addAll(List.of("001", "002", "001", "004", "003"));

    HttpEntity<List<String>> request = new HttpEntity<>(
        productIds,
        new HttpHeaders()
    );

    CheckoutDto response = restTemplate.postForObject(
        "http://localhost:" + randomServerPort + "/checkout",
        request,
        CheckoutDto.class
    );
    assertThat(response.price()).isEqualTo(360);
  }

  @Test
  public void testDiscountOptimization() throws ExecutionException, InterruptedException {
    jdbi.withHandle(handle -> {
      String sql = "insert into multi_unit_discount (product_id, num_units, price) values (2, 7, 350)";
      return handle.createUpdate(sql).execute();
    });
    productCache.forceRefreshProducts();

    HttpEntity<List<Integer>> request = new HttpEntity<>(
        new ArrayList<>(List.of(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2)),
        new HttpHeaders()
    );

    CheckoutDto response = restTemplate.postForObject(
        "http://localhost:" + randomServerPort + "/checkout",
        request,
        CheckoutDto.class
    );
    assertThat(response.price()).isEqualTo(350 + 120 * 2 + 80);
  }

  @BeforeEach
  void resetDb(@Autowired Flyway flyway) {
    flyway.clean();
    flyway.migrate();
  }
}
