package no.brox.clockshop;

import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

@Service
public class ProductDao {

  final
  Jdbi jdbi;


  public ProductDao(Jdbi jdbi) {
    this.jdbi = jdbi;
  }
}
