package ru.inponomarev.celestademo.dto

class OrderDto(
    val id: Int? = null,
    val customerId: Int? = null,
    val itemId: String? = null,
    val quantity: Int? = null,
    val price: Int? = null,
    val amount: Int? = null
)