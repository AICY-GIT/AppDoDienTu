package com.example.tk_app.classify_product.electronic_device

class ProductElectronicDevice (
    var productelectronicId: String?,
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
