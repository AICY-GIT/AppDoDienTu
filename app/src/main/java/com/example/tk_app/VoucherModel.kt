package com.example.tk_app

class VoucherModel {
    var voucherId: String? = null
    var name: String? = null
    var code: String? = null
    var discountPercentage: String? = null
    var maxDiscount: String? = null
    var amount: String? = null
    var dateStart: Any? = null
    var dateEnd: Any? = null

    // Add a no-argument constructor
    constructor()

    // Optionally, you can keep your existing constructor if needed
    constructor(
        voucherId: String?,
        name: String?,
        code: String?,
        discountPercentage: String?,
        maxDiscount: String?,
        amount: String?,
        dateStart: Any?,
        dateEnd: Any?
    ) {
        this.voucherId = voucherId
        this.name = name
        this.code = code
        this.discountPercentage = discountPercentage
        this.maxDiscount = maxDiscount
        this.amount = amount
        this.dateStart = dateStart
        this.dateEnd = dateEnd
    }
}
