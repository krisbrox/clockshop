package no.brox.clockshop.products;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import no.brox.clockshop.Discount;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public record Product(
    Integer id,
    Integer unitPrice,
    String name,
    List<Discount> discounts
) {

  public static class ProductMapper implements RowMapper<Product> {

    @Override
    public Product map(
        ResultSet rs,
        StatementContext ctx
    ) throws SQLException {
      return new Product(
          rs.getInt("id"),
          rs.getInt("unit_price"),
          rs.getString("name"),
          new ArrayList<>()
      );
    }
  }
}
