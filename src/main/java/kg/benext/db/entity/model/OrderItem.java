package kg.benext.db.entity.model;

import java.util.UUID;

public class OrderItem {
    private UUID id;
    private UUID orderId;
    private UUID productId;
    private Integer quantity;
    private Double price;
    private String productNameSnapshot;

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getProductNameSnapshot() { return productNameSnapshot; }
    public void setProductNameSnapshot(String productNameSnapshot) { this.productNameSnapshot = productNameSnapshot; }
}