package freehold.order.service

import freehold.order.api.CreateOrderRequest
import freehold.order.api.OrderResponse
import freehold.order.api.toResponse
import freehold.order.domain.Order
import freehold.order.domain.OrderRepository
import freehold.order.domain.OrderStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository
) {

    fun createOrder(request: CreateOrderRequest): OrderResponse {
        val order = Order(
            customerId = request.customerId,
            goodsId = request.goodsId,
            goodsName = request.goodsName,
            quantity = request.quantity,
            totalPrice = request.totalPrice,
            deliveryAddress = request.deliveryAddress,
            status = OrderStatus.PLACED,
            createdAt = LocalDateTime.now()
        )
        return orderRepository.save(order).toResponse()
    }

    fun getOrder(id: UUID): OrderResponse =
        orderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Order $id not found") }
            .toResponse()

    fun getOrdersByCustomer(customerId: UUID): List<OrderResponse> =
        orderRepository.findByCustomerId(customerId).map { it.toResponse() }

    fun getAllOrders(): List<OrderResponse> =
        orderRepository.findAll().map { it.toResponse() }

    fun updateStatus(id: UUID, status: OrderStatus): OrderResponse {
        val order = orderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Order $id not found") }
        order.status = status
        order.updatedAt = LocalDateTime.now()
        return orderRepository.save(order).toResponse()
    }
}
