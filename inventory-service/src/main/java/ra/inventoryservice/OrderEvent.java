package ra.inventoryservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderEvent {
    private Long productId;
    private Integer quantity;
}
