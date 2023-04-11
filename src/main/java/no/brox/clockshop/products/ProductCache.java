package no.brox.clockshop.products;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductCache {

  private final LoadingCache<String, Map<Integer, Product>> products;
  private final String PRODUCT_CACHE_KEY = "PRODUCT_CACHE_KEY";

  public ProductCache(ProductDao productDao) {
    products = Caffeine
        .newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(Duration.ofMinutes(5))
        .refreshAfterWrite(Duration.ofMinutes(1))
        .build(key -> productDao.getProducts()
            .stream().collect(Collectors.toMap(Product::id, p -> p)));
  }

  public void forceRefreshProducts() throws ExecutionException, InterruptedException {
    products.refresh(PRODUCT_CACHE_KEY).get();
  }

  public Product getProduct(Integer id) {
    return getProducts().get(id);
  }

  public Map<Integer, Product> getProducts() {
    return products.get(PRODUCT_CACHE_KEY);
  }

  public Set<Integer> availableProducts() {
    return getProducts()
        .keySet();
  }
}
