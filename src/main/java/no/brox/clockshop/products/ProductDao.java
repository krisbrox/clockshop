package no.brox.clockshop.products;

import java.util.Map;
import no.brox.clockshop.Discount;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;

public interface ProductDao {

  @SqlQuery("SELECT p.id, p.name, p.unit_price, d.product_id, d.num_units, d.price"
            + " FROM products p LEFT OUTER JOIN multi_item_discount d on p.id = d.product_id"
            + " where p.id = :product_id")
  @RegisterRowMapper(value = Product.ProductMapper.class)
  @RegisterRowMapper(value = Discount.DiscountMapper.class)
  @UseRowReducer(ProductRowReducer.class)
  Product getDiscountedProduct(@Bind("product_id") Integer productId);

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
}
