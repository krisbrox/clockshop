package no.brox.clockshop;

import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

@Service
public class ClockShopDao {

  final Jdbi jdbi;



  public ClockShopDao(Jdbi jdbi) {
    this.jdbi = jdbi;
  }
}
