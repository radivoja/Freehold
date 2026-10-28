package freehold.order.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreateOrderRequest(
    val customerId: UUID,
    val goodsId: UUID,
    val goodsName: String,
    val quantity: Int,
    val totalPrice: BigDecimal,
    val deliveryAddress: String
)

data class OrderResponse(
    val id: UUID,
    val customerId: UUID,
    val goodsId: UUID,
    val goodsName: String,
    val quantity: Int,
    val totalPrice: BigDecimal,
    val deliveryAddress: String,
    val status: OrderStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

fun Order.toResponse() = OrderResponse(
    id = id!!,
    customerId = customerId,
    goodsId = goodsId,
    goodsName = goodsName,
    quantity = quantity,
    totalPrice = totalPrice,
    deliveryAddress = deliveryAddress,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt
)
