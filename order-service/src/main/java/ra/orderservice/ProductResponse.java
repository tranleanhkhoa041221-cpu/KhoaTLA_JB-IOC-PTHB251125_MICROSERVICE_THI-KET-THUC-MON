package ra.orderservice;

import lombok.Data;

@Data
public class ProductResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
}
