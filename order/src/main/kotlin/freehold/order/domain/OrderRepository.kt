package freehold.order.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderRepository : JpaRepository<Order, UUID> {
    fun findByCustomerId(customerId: UUID): List<Order>
    fun findByStatus(status: OrderStatus): List<Order>
}
