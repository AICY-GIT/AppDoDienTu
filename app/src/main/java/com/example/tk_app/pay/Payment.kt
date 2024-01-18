package com.example.tk_app.pay

data class Payment(
    val totalPrice: String? = null,
    val shippingAddress: String? = null,
    val cashOnDelivery: Boolean = false,
    val payWithMono:Boolean =false,
    val  payWithZalo:Boolean=false
)
