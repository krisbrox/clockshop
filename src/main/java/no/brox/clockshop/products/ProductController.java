package no.brox.clockshop.products;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
  private final ProductService productService;
  private final ProductCache productCache;

  public ProductController(ProductService productService, ProductCache productCache) {
    this.productService = productService;
    this.productCache = productCache;
  }

  @PostMapping(
			value = "/checkout",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
  public ResponseEntity<CheckoutDto> checkout(
		@RequestBody List<Integer> ids
  ) {
    if (!productCache.availableProducts().containsAll(ids)) {
      return ResponseEntity.badRequest().build();
    }

    Integer price = productService.checkout(ids);
    return ResponseEntity.ok(new CheckoutDto(price));
  }

  public record CheckoutDto(
      Integer price
  ) {}
}
