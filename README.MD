- Kiến trúc hệ thống

[Client] → [API Gateway] → [Order Service] → (Feign/Kafka) → [Inventory Service]
↓
[Eureka Server]
Eureka Server: Service Registry, quản lý danh bạ các service.

API Gateway (WebMVC): Định tuyến request /api/orders/** và /api/inventory/** tới service tương ứng.

Order Service: CRUD đơn hàng, xử lý nghiệp vụ đặt hàng.

Inventory Service: CRUD sản phẩm, quản lý tồn kho.

⚙️ Công nghệ sử dụng
Java 17+

Spring Boot 3.x

Spring Cloud Netflix Eureka

Spring Cloud Gateway (WebMVC)

Spring Data JPA + PostgreSQL

Spring Kafka

OpenFeign

🚀 Cách chạy hệ thống
Khởi động Eureka Server

bash
mvn spring-boot:run -pl eureka-server
Khởi động API Gateway

bash
mvn spring-boot:run -pl api-gateway
Khởi động Inventory Service

bash
mvn spring-boot:run -pl inventory-service
Khởi động Order Service

bash
mvn spring-boot:run -pl order-service
📌 Yêu cầu: PostgreSQL, Kafka, Zookeeper đang chạy (có thể dùng docker-compose).

🧪 Test API với Postman (qua Gateway)
1. Tạo product
   POST /api/inventory

Body:

json
{ "productName": "Laptop Dell", "quantity": 10, "price": 1500 }
2. Lấy product
   GET /api/inventory/1

3. Đặt hàng
   POST /api/orders?productId=1&quantity=2

👉 Response thành công:

json
{
"id": 1,
"productId": 1,
"quantity": 2,
"status": "PENDING",
"totalPrice": 3000
}
👉 Response khi hết hàng:

json
{
"status": 409,
"error": "Conflict",
"path": "/api/orders"
}


- Task 3: Phân tích kiến trúc
1. Service Discovery
   Gateway không nên gọi trực tiếp IP/Port của service vì:

Service có thể scale nhiều instance, mỗi instance chạy port khác nhau.

IP/Port có thể thay đổi khi deploy lại.

Eureka cung cấp cơ chế Service Discovery: Gateway chỉ cần gọi theo serviceId (vd: lb://order-service), Eureka sẽ tự động phân phối request đến instance phù hợp.

2. Scale khi số lượng request tăng
   Khi request tăng đột biến, ta có thể scale Order Service bằng cách chạy thêm nhiều instance.

Gateway không cần thay đổi cấu hình, vì Eureka sẽ tự động cập nhật danh sách instance và Gateway sẽ load balance request.

3. So sánh OpenFeign vs Kafka
   Tiêu chí	OpenFeign (Đồng bộ)	Kafka (Bất đồng bộ)
   Cách giao tiếp	Gọi trực tiếp REST API qua ServiceID	Gửi event vào topic, consumer xử lý
   Độ trễ	Thấp, phản hồi ngay cho client	Cao hơn, vì phải qua queue
   Đảm bảo phản hồi	Client nhận kết quả ngay (vd: hết hàng → trả lỗi 409)	Client chỉ nhận ACK gửi event, kết quả xử lý sau
   Độ tin cậy	Phụ thuộc vào service đích, nếu down thì lỗi ngay	Kafka lưu event, service down vẫn có thể xử lý khi lên lại
   Use case phù hợp	Khi cần phản hồi tức thì cho client	Khi cần xử lý bất đồng bộ, mở rộng hệ thống (audit, notification, analytics)


 * Trong bài toán này:

OpenFeign phù hợp để client biết ngay kết quả đặt hàng (có đủ hàng hay không).

Kafka phù hợp để cập nhật tồn kho, gửi thông báo, hoặc tích hợp với hệ thống khác mà không ảnh hưởng đến tốc độ phản hồi cho client.

* Ưu điểm kiến trúc
Kết hợp cả Feign và Kafka:

Feign đảm bảo phản hồi chính xác cho client.

Kafka đảm bảo hệ thống mở rộng, xử lý bất đồng bộ, không mất dữ liệu khi service down.