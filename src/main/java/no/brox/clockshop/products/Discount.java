package no.brox.clockshop.products;

public record Discount(
    Integer productId,
    Integer numberOfUnits,
    Integer price
) implements Comparable<Discount> {

  @Override
  public int compareTo(Discount o) {
    Integer thisDiscount = this.price / this.numberOfUnits;
    Integer thatDiscount = o.price / o.numberOfUnits;

    return thisDiscount.compareTo(thatDiscount);
  }
}
