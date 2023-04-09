package no.brox.clockshop.products;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductDao productDao;

  public ProductService(ProductDao productDao) {
    this.productDao = productDao;
  }

  Integer checkout(List<Integer> requestedProducts) {
    return requestedProducts
        .stream()
        .distinct()
        .map(productDao::getDiscountedProduct)
        .map(p -> price(p, Collections.frequency(requestedProducts, p.id())))
        .reduce(0, Integer::sum);
  }

  Integer price(Product product, Integer count) {
    ArrayList<Discount> discounts = new ArrayList<>(product.discounts());
    discounts.sort(Discount::compareTo);

    var sum = 0;
    var num = count;
    int times;

    for (int i = 0; i < product.discounts().size(); i++) {
      var discount = discounts.get(i);
      times = num / discount.numberOfUnits();

      if (times > 0) {
        sum += times * discount.price();
        num -= times * discount.numberOfUnits();
      }
    }
    sum += num * product.unitPrice();
    return sum;
  }
}
