package freehold.customer.model.dto

import freehold.customer.model.GoodsType
import java.sql.Timestamp

class MarketGoodsDto (

    val goodsType: GoodsType,

    val timestamp: Timestamp?,

    val price: Double,

    val quantity : Int
)
