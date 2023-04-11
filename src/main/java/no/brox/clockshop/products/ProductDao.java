package no.brox.clockshop.products;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;

public interface ProductDao {

  @SqlQuery("SELECT p.id, p.name, p.unit_price, d.product_id, d.num_units, d.price"
            + " FROM products p LEFT OUTER JOIN multi_unit_discount d on p.id = d.product_id")
  @RegisterRowMapper(value = ProductMapper.class)
  @RegisterRowMapper(value = DiscountMapper.class)
  @UseRowReducer(ProductRowReducer.class)
  List<Product> getProducts();

  class ProductRowReducer implements LinkedHashMapRowReducer<Integer, Product> {
    @Override
    public void accumulate(Map<Integer, Product> map, RowView rowView) {
      Product product = map.computeIfAbsent(
          rowView.getColumn("id", Integer.class),
          id -> rowView.getRow(Product.class)
      );

      if (rowView.getColumn("product_id", Integer.class) != null) {
        product.discounts().add(rowView.getRow(Discount.class));
      }
    }
  }

  class ProductMapper implements RowMapper<Product> {

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

  class DiscountMapper implements RowMapper<Discount> {

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
