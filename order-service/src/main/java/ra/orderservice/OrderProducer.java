package ra.orderservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderEvent(Long productId, Integer quantity) {
        OrderEvent orderEvent = new OrderEvent(productId, quantity);
        String json = objectMapper.writeValueAsString(orderEvent);
        log.info("Sending order event to kafka: productId={}, quantity={}", productId, quantity);
        kafkaTemplate.send("order-topic", productId.toString(), json);
        log.info("Send event successfully !!!! productId={}, quantity={}", productId, quantity);
    }

}
