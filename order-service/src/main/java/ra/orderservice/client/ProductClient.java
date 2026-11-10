package ra.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ra.orderservice.dto.response.ProductResponse;

@FeignClient(name = "inventory-service")
public interface ProductClient {
    @GetMapping("/api/inventory/{productId}")
    ProductResponse getProduct(@PathVariable("productId") Long productId);
}
