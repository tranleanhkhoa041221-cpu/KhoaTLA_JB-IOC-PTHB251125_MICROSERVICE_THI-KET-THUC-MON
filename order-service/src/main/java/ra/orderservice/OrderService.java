package ra.orderservice;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        ProductResponse productResponse = productClient.getProduct(orderRequest.getProductId());

        if (productResponse.getQuantity() < orderRequest.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Out of stock");
        }

        Order order = new Order();
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setStatus("PENDING");
        Order saveOrder = orderRepository.save(order);
        orderProducer.sendOrderEvent(orderRequest.getProductId(), orderRequest.getQuantity());
        return saveOrder;


    }


}
