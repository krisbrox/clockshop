package no.brox.clockshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.jdbi.v3.core.Jdbi;
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

  @Autowired
  private Jdbi jdbi;

  @LocalServerPort
  int randomServerPort;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
      .withDatabaseName("clockshop")
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
  public void emptyShoppingCartIsFree() {
    HttpEntity<List<String>> request = new HttpEntity<>(
        new ArrayList<>(),
        new HttpHeaders()
    );

    Integer response = restTemplate.postForObject(
        "http://localhost:" + randomServerPort + "/checkout",
        request,
        Integer.class
    );
    assertThat(response).isEqualTo(0);
  }

  @Test
  public void testSingleItemCheckout() {
    resetDb();
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
    assertThat(response).isEqualTo(100);
  }

  @Test
  public void testMultiItemCheckout() {
    resetDb();
    productIds = new ArrayList<>();
    productIds.add("001");
    productIds.add("002");
    productIds.add("001");
    productIds.add("004");
    productIds.add("003");

    HttpEntity<List<String>> request = new HttpEntity<>(
        productIds,
        new HttpHeaders()
    );

    Integer response = restTemplate.postForObject(
        "http://localhost:" + randomServerPort + "/checkout",
        request,
        Integer.class
    );
    assertThat(response).isEqualTo(100);
  }

  private void resetDb() {
    jdbi.withHandle(handle -> handle.createUpdate("truncate table multi_item_discount").execute());
    jdbi.withHandle(handle -> handle.createUpdate("truncate table products cascade").execute());
    jdbi.withHandle(handle -> {
      String sql = """
           insert into products (id, name, unit_price)
           values
            (1, 'Rolex', 100),
            (2, 'Michael Kors', 80),
            (3, 'Swatch', 50),
            (4, 'Casio', 30)
          """.trim();
      return handle.createUpdate(sql).execute();
    });
    jdbi.withHandle(handle -> {
      String sql = """
           insert into multi_item_discount (product_id, num_units, price)
           values
            (1, 3, 200),
            (2, 2, 120)
          """.trim();
      return handle.createUpdate(sql).execute();
    });
  }
}
