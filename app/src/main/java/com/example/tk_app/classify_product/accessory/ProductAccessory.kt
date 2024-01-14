package com.example.tk_app.classify_product.accessory

class ProductAccessory (
    var productWomenId: String?,
    var imageUrl: String?,
    var material: String?,
    var price: String?,
    var name: String?,
    var type: String?,
    var details: String?,
    var origin: String?,
    var quantity: String?
) {
    constructor() : this("", "", "", "", "", "", "", "","")

}
