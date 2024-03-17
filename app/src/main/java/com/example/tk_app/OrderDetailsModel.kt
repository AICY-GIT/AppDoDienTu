import com.google.firebase.database.ServerValue
import java.util.*

class OrderDetailsModel(
    var orderId: String?,
    var price: String?,
    var orderDetailsId: String?,
    var productId: String?,
    var amount: String?,
    var orderDate: String?, // Use Any to allow ServerValue.TIMESTAMP
) {
    // Default constructor
    constructor() : this(null, null, null, null, null, null)
    // Primary constructor with date paramete
}
