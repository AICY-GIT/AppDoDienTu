import com.google.firebase.database.ServerValue
import java.util.*

class OrderDetailsModel(
    var orderId: String?,
    var price: String?,
    var orderDetailsId: String?,
    var productId: String?,
    var amount: String?,
    var orderDate: Any? // Use Any to allow ServerValue.TIMESTAMP
) {
    // Default constructor

    // Primary constructor with date paramete
}
