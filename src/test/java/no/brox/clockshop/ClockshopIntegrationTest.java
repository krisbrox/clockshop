package no.brox.clockshop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
class ClockshopIntegrationTest {
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2");
  List<String> productIds;

  @Autowired
  TestUtils utils;

  @DynamicPropertySource
  static void registerDBProperties(DynamicPropertyRegistry registry) {
    postgres.start();


    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Test
  public void testAppInit() {}

  @Test
  public void testCheckout() throws IOException {
    productIds = new ArrayList<>();
    productIds.add("001");


    utils.checkout(productIds);
  }
}
