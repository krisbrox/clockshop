package no.brox.clockshop.products;

import java.util.List;

public record Product(
    Integer id,
    Integer unitPrice,
    String name,
    List<Discount> discounts
) {

}
