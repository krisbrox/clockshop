package no.brox.clockshop.products;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductDao productDao;

  public ProductService(ProductDao productDao) {
    this.productDao = productDao;
  }

  Integer checkout(List<Integer> requestedProducts) {

    Set<Product> products = requestedProducts
        .stream()
        .distinct()
        .map(productDao::getDiscountedProduct)
        .collect(Collectors.toSet());

    Integer sum = products
        .stream()
        .map(p -> p.unitPrice())
        .reduce(0, (acc, p) -> acc + p);

    return sum;
  }

  Integer addProductPrice(Product product, Integer acc) {
    return acc + product.unitPrice();
  }
}
