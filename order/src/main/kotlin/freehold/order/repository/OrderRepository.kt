package freehold.order.repository

import freehold.order.model.Order
import freehold.order.model.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderRepository : JpaRepository<Order, UUID> {
    fun findByCustomerId(customerId: UUID): List<Order>
    fun findByStatus(status: OrderStatus): List<Order>
}
