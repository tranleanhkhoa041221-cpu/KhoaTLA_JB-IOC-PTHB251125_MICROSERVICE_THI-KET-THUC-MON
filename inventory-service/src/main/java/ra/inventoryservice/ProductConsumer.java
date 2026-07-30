package ra.inventoryservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductConsumer {

    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;

    @KafkaListener(topics = "order-topic", groupId = "inventory-service-group")
    @Transactional
    public void consumeOrderEvent(String value) {
            OrderEvent orderEvent = objectMapper.readValue(value, OrderEvent.class);
            log.info("Received order event from kafka: productId={}, quantity={}", orderEvent.getProductId(), orderEvent.getQuantity());
            Product product = productRepository.findByIdWithLock(orderEvent.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (product.getQuantity() < orderEvent.getQuantity()) {
                log.warn("Not enough stock for productId={}. Requested={}, Available={}", orderEvent.getProductId(), orderEvent.getQuantity(), product.getQuantity());
                return;
            }
            product.setQuantity(product.getQuantity() - orderEvent.getQuantity());
            productRepository.save(product);
            log.info("Stock Updated for productId={}. Remaining={}", orderEvent.getProductId(), product.getQuantity());

    }
}
