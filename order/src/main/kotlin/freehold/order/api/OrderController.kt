package freehold.order.api

import freehold.order.domain.OrderStatus
import freehold.order.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody request: CreateOrderRequest): OrderResponse =
        orderService.createOrder(request)

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: UUID): OrderResponse =
        orderService.getOrder(id)

    @GetMapping
    fun getAllOrders(): List<OrderResponse> =
        orderService.getAllOrders()

    @GetMapping("/customer/{customerId}")
    fun getOrdersByCustomer(@PathVariable customerId: UUID): List<OrderResponse> =
        orderService.getOrdersByCustomer(customerId)

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: UUID,
        @RequestParam status: OrderStatus
    ): OrderResponse = orderService.updateStatus(id, status)
}
