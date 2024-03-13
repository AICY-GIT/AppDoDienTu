package com.example.tk_app

class OrderModel(
    var orderId: String? = null,
    var totalPrice: String? = null,
    var shippingCost: String? = null,
    var status: String? = null,
    var userId: String? = null,
    var paymentMethod: String? = null,
    //thêm ngày và địa chỉ
    var orderDate: String? = null,
    var shippingAddress: String? = null
) {
    // Constructor không tham số
    constructor() : this(null, null, null, null, null, null, null,null)
}
