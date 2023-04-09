package no.brox.clockshop;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public record Discount(
    Integer productId,
    Integer numberOfUnits,
    Integer price
) {

  public static class DiscountMapper implements RowMapper<Discount> {

    @Override
    public Discount map(
        ResultSet rs,
        StatementContext ctx
    ) throws SQLException {

      return new Discount(
          rs.getInt("product_id"),
          rs.getInt("num_units"),
          rs.getInt("price")
      );
    }
  }
}
