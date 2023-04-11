package no.brox.clockshop.products;

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

  Integer price(Product product, Integer productCount) {
    /*
    * For some given product with a list of discounts, apply the best discount on as many of the units as
    * possible, then continue with the second-best discount on the remaining units, etc. until all units
    * have had their (possibly discounted) price added to the subtotal, then return the subtotal.
    * */
    var discounts = product.discounts();
    discounts.sort(Discount::compareTo);    // sort valid discounts to apply the best one first

    var totalPriceAccumulator = 0;          // accumulator variable for subtotal for this product
    var numberOfUnitsLeft = productCount;   // how many items of this product are left to checkout
    int discountApplicationCount;           // how many times the discount can be applied

    for (int i = 0; i < product.discounts().size(); i++) {
      var discount = discounts.get(i);
      discountApplicationCount = numberOfUnitsLeft / discount.numberOfUnits();

      if (discountApplicationCount > 0) {
        totalPriceAccumulator += discountApplicationCount * discount.price();
        numberOfUnitsLeft -= discountApplicationCount * discount.numberOfUnits();
      }
    }
    totalPriceAccumulator += numberOfUnitsLeft * product.unitPrice();
    return totalPriceAccumulator;
  }
}
