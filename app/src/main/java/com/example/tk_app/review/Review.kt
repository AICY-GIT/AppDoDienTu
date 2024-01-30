package com.example.tk_app.review

class Review(
    var productId: String?,
    var userId: String?,
    var rate: Double?
) {
    constructor() : this( "", "", null)
}
