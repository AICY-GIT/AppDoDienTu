package com.example.tk_app.account

data class User(
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var cashOnDelivery: Boolean = false // default value for cash on delivery
) {
    constructor() : this("", "", "", "", false)
}
