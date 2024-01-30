package com.example.tk_app.classify_product.phones
class ProductPhone(
    var productmenId: String?,
    var imageUrl: String?,
    var material: String?,
    var price: String?,
    var name: String?,
    var type: String?,
    var details: String?,
    var origin: String?,
    var quantity: String?,
    var rate: Double?
) {
    constructor() : this("", "", "", "", "", "", "", "","",null)

}
