package com.example.tk_app.pay

import CartAdapter
import OrderDetailsModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tk_app.HMac.Api.CreateOrder
import com.example.tk_app.MainActivity
import com.example.tk_app.OrderModel
import com.example.tk_app.R
import com.example.tk_app.classify_product.CartItemModel
import com.example.tk_app.google_map_and_address.googleMapActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vn.momo.momo_partner.AppMoMoLib
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PurchaseActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalPriceTextView: TextView
    private lateinit var productListTextView: TextView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userPhoneTextView: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var shippingAddressEditText: EditText
    private lateinit var rbCashOnDelivery: RadioButton
    private lateinit var rbPayWithZalo:RadioButton
    private lateinit var rbPayWithMomo:RadioButton
    private lateinit var rgPaymentMethod:RadioGroup
    private var amount = "10000"
    private val fee = "0"
    var environment = 0 //developer default
    private val merchantName = "APP DO DIEN TU"
    private val merchantCode = "CGV19072017"
    private val merchantNameLabel = "APP DO DIEN TU"
    private val description = "Thanh toan MoMo"
    private lateinit var btnCHooseLocation: Button
    private lateinit var btnSave:Button
    private lateinit var shippingFeeTextView:TextView
    private var shippingCost:Int = 0
    private var totalcost:Double= 0.0;
    private lateinit var productList: ArrayList<CartItemModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        Mapping()
        //get data from map
        val userSelectedPoint: LatLng? = intent.getParcelableExtra("userSelectedPoint")
        val distance: Double = intent.getDoubleExtra("distance", 0.0)

        userSelectedPoint?.let {
            shippingAddressEditText.setText("$it")
        }
        distance?.let {
            shippingCost = (it * 0.15).toInt()
            shippingFeeTextView.setText(shippingCost.toString())
        }

        //khoi tao zalo
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        //khoi tao momo
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        productListTextView = findViewById(R.id.productListTextView)
        //get data from cart
        val totalCartPriceString = intent.getStringExtra("totalCartPrice")
        val totalCartPrice = totalCartPriceString?.toDoubleOrNull() ?: 0.0
        productList = intent.getParcelableArrayListExtra<CartItemModel>("productList") as? ArrayList<CartItemModel> ?: ArrayList()

        if (productList != null) {

            totalcost=totalCartPrice + shippingCost
            totalPriceTextView.text = totalcost.toString()
            totalCartPriceString?.let {
                amount=it
            }
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            cartAdapter = CartAdapter(this, productList)
            recyclerView.adapter = cartAdapter

            var productsText = "Product List:\n"
            for (product in productList) {
                productsText += "${product?.name} - ${product?.price} - ${product?.quantity}\n"
            }
            productListTextView.text = productsText
        } else {
            // Handle the case when productList is null
        }
       //button fun
        btnCHooseLocation.setOnClickListener{
            val i = Intent(this, googleMapActivity::class.java)
            i.putParcelableArrayListExtra("productList", productList)
            i.putExtra("totalCartPrice", totalCartPriceString)
            startActivity(i)
        }
        btnSave.setOnClickListener {
            savePaymentInfoToFirebase()
        }
        // Receive data from Intent
        // Tham chiếu đến Firebase Database và lấy thông tin người dùng
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("Account/User").child(userID!!)

        // Lắng nghe sự thay đổi dữ liệu từ Firebase Database
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("Name").value.toString()
                val userEmail = snapshot.child("Email").value.toString()
                val userPhone = snapshot.child("Phone").value.toString()

                // Hiển thị thông tin người dùng trên TextView tương ứng
                userNameTextView.text = "Họ tên: $userName"
                userEmailTextView.text = "Email: $userEmail"
                userPhoneTextView.text = "Phone: $userPhone"
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra khi truy xuất dữ liệu từ Firebase
            }
        })
    }
    private fun savePaymentInfoToFirebase() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val shippingAddress = shippingAddressEditText.text.toString()
        val isCashOnDeliveryChecked = rbCashOnDelivery.isChecked
        val isPayWithZalo=rbPayWithZalo.isChecked
        val isPayWithMomo=rbPayWithMomo.isChecked
        //to here!

        val checkedRadioButtonId=rgPaymentMethod.checkedRadioButtonId

        if (shippingAddress.isBlank() || checkedRadioButtonId==-1) {
            val errorMessage = when {
                shippingAddress.isBlank() && checkedRadioButtonId==-1 -> "Hãy nhập địa chỉ và chọn phương thức thanh toán!"
                shippingAddress.isBlank() -> "Hãy nhập địa chỉ thanh toán!"
                else -> "Bạn phải chọn phương thức thanh toán!"
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            return // Dừng quá trình lưu nếu không đủ điều kiện
        }
        if(isCashOnDeliveryChecked){
            //updatePaymentToFirebase(shippingAddress,userID)
            createOrderFirebase(userID,"CoD");
        }else if(isPayWithMomo){
            if (userID != null) {
                requestPaymentMomo(userID)
            }
        }else if(isPayWithZalo){
            RequestZalo()
        }
    }
//    private fun updatePaymentToFirebase(shippingAddress: String, userID: String?) {
//        // Lưu thông tin thanh toán
//
//        val isCashOnDeliveryChecked = rbCashOnDelivery.isChecked
//        val isPayWithZalo = rbPayWithZalo.isChecked
//        val isPayWithMomo = rbPayWithMomo.isChecked
//
//        val paymentRef = FirebaseDatabase.getInstance().reference.child("Payments").child(userID!!)
//        val newPaymentKey = paymentRef.push().key
//
//        // Sử dụng child() để xác định đường dẫn cụ thể trong Firebase
//        val path = "Payments/$userID/$newPaymentKey"
//        val paymentInfoRef = FirebaseDatabase.getInstance().reference.child(path)
//
//        val paymentInfo = Payment(newPaymentKey, totalcost.toString(), shippingAddress, isCashOnDeliveryChecked, isPayWithMomo, isPayWithZalo)
//
//        // Sử dụng setValue() để lưu thông tin thanh toán theo đường dẫn cụ thể
//        paymentInfoRef.setValue(paymentInfo)
//            .addOnSuccessListener {
//                val cartReference = FirebaseDatabase.getInstance().reference.child("Cart").child("Cart_Fashion").child(userID!!)
//                // Update status to 'complete' in the cart
//                cartReference.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        for (productSnapshot in dataSnapshot.children) {
//                            val productStatus = productSnapshot.child("status").value.toString()
//                            if (productStatus == "wait") {
//                                productSnapshot.ref.updateChildren(mapOf("status" to "complete"))
//                            }
//                        }
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        // Handle error if needed
//                    }
//                })
//                Toast.makeText(this, "Mua hàng thành công!", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this@PurchaseActivity, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Lỗi khi lưu thông tin thanh toán!", Toast.LENGTH_SHORT).show()
//            }
//    }
private fun createOrderDetailsFirebase(orderKey: String, userId: String) {
    val orderDetailList: MutableList<OrderDetailsModel> = mutableListOf()

    for (item in productList.orEmpty()) {
        val orderDetail = OrderDetailsModel(orderKey, item.price, "", item.productmenId, item.quantity, getCurrentDateTimeAsString())
        orderDetailList.add(orderDetail)
    }

    // Save each OrderDetailsModel to Firebase with userId
    for (orderDetail in orderDetailList) {
        saveOrderDetailToFirebase(orderDetail, userId)
    }

    // Update status to 'complete' in the cart
    val cartReference = FirebaseDatabase.getInstance().reference.child("Cart").child("Cart_Fashion").child(userId)
    cartReference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (productSnapshot in dataSnapshot.children) {
                val productStatus = productSnapshot.child("status").value.toString()
                if (productStatus == "wait") {
                    productSnapshot.ref.child("status").setValue("complete")
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle error if needed
        }
    })
}

    private fun saveOrderDetailToFirebase(orderDetail: OrderDetailsModel, userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val newOrderDetailKey = databaseReference.child("OrderDetails").child(userId).push().key

        if (newOrderDetailKey != null) {
            orderDetail.orderDetailsId=newOrderDetailKey
            val path = "OrderDetails/$userId/$newOrderDetailKey"
            val orderDetailRef = databaseReference.child(path)

            orderDetailRef.setValue(orderDetail)
                .addOnSuccessListener {
                    // Handle success if needed
                }
                .addOnFailureListener {
                    // Handle failure if needed
                }
        }
    }

    private fun createOrderFirebase(userID: String?,paymentMetod:String) {
        if (userID != null) {
            val myOrderModel = OrderModel("", totalcost.toString(), shippingCost.toString(), "Dang giao", userID,paymentMetod)

            val databaseReference = FirebaseDatabase.getInstance().reference
            val newOrderKey = databaseReference.child("Orders").child(userID).push().key

            if (newOrderKey != null) {
                myOrderModel.orderId = newOrderKey
                val path = "Orders/$userID/$newOrderKey"
                val orderRef = databaseReference.child(path)

                orderRef.setValue(myOrderModel)
                    .addOnSuccessListener {
                        createOrderDetailsFirebase(newOrderKey, userID)
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@PurchaseActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error saving order information!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }


    //Get token through MoMo app
    private fun requestPaymentMomo(IdDonHang : String) {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT)
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN)

        val eventValue: MutableMap<String, Any> = HashMap()
        //client Required
        eventValue["merchantname"] =
            merchantName //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue["merchantcode"] =
            merchantCode //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue["amount"] = amount //Kiểu integer
        eventValue["orderId"] =
            IdDonHang //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue["orderLabel"] = "Mã đơn hàng" //gán nhãn

        //client Optional - bill info
        eventValue["merchantnamelabel"] = "Dịch vụ" //gán nhãn
        eventValue["fee"] = fee //Kiểu integer
        eventValue["description"] = description //mô tả đơn hàng - short description

        //client extra data
        eventValue["requestId"] = merchantCode + "merchant_billId_" + System.currentTimeMillis()
        eventValue["partnerCode"] = merchantCode
        val productList = intent.getParcelableArrayListExtra<CartItemModel>("productList")
        //Example extra data
        val gson = Gson()
        val cartItemJsonArray = JSONArray()

        if (productList != null) {
            for (cartItem in productList) {
                val cartItemJsonString = gson.toJson(cartItem)
                val cartItemJsonObject = JSONObject(cartItemJsonString)
                cartItemJsonArray.put(cartItemJsonObject)
            }
        }
        val objExtraData = JSONObject()
        try {
            objExtraData.put("cart_items", cartItemJsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        eventValue["extraData"] = objExtraData.toString()
        eventValue["extra"] = ""
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue)
    }
    //Get token callback from MoMo app an submit to server side
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null) {
                if (data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Toast.makeText(this,"message: Thanh cong" +"Get token " + data.getStringExtra("message"),Toast.LENGTH_SHORT)
                    val token = data.getStringExtra("data") //Token response
                    Log.d("Token ",token.toString())
                    val phoneNumber = data.getStringExtra("phonenumber")
                    var env = data.getStringExtra("env")
                    if (env == null) {
                        env = "app"
                    }
                    if (token != null && token != "") {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                        // Call updatePaymentToFirebase function here
                        val userID = FirebaseAuth.getInstance().currentUser?.uid
                        val shippingAddress = shippingAddressEditText.text.toString()
                        createOrderFirebase(userID,"MoMo");
                    } else {
                        Toast.makeText(this,"message: that bai",Toast.LENGTH_SHORT)
                    }
                } else if (data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    val message =
                        if (data.getStringExtra("message") != null) data.getStringExtra("message") else "Thất bại"
                    Toast.makeText(this,"message: $message",Toast.LENGTH_SHORT)
                } else if (data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Toast.makeText(this,"message: that bai",Toast.LENGTH_SHORT)
                } else {
                    //TOKEN FAIL
                    Toast.makeText(this,"message: that bai",Toast.LENGTH_SHORT)
                }
            } else {
                Toast.makeText(this,"message: that bai",Toast.LENGTH_SHORT)
            }
        } else {
            Toast.makeText(this,"message: that bai",Toast.LENGTH_SHORT)
        }
    }
    private fun RequestZalo(){
        val orderApi = CreateOrder()
        try {
            val data = orderApi.createOrder(amount)
            Log.d("Amount", amount)
            val code = data.getString("return_code")
            Toast.makeText(applicationContext, "return_code: $code", Toast.LENGTH_LONG).show()
            Log.e( "RequestZalo: ",data.toString() )
            if (code == "1") {
                val token: String = data.getString("zp_trans_token")
                ZaloPaySDK.getInstance().payOrder(this@PurchaseActivity,token,"demozpdk://app",object :
                    PayOrderListener {
                    override fun onPaymentSucceeded(p0: String?, p1: String?, p2: String?) {
                        TODO("Not yet implemented")
                        val userID = FirebaseAuth.getInstance().currentUser?.uid
                        val shippingAddress = shippingAddressEditText.text.toString()
                        createOrderFirebase(userID,"ZaloPay");
                    }
                    override fun onPaymentCanceled(p0: String?, p1: String?) {
                        TODO("Not yet implemented")
                    }
                    override fun onPaymentError(p0: ZaloPayError?, p1: String?, p2: String?) {
                        TODO("Not yet implemented")
                    }

                });
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }
    private fun Mapping(){
        userNameTextView = findViewById(R.id.userNameTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)
        userPhoneTextView = findViewById(R.id.userPhoneTextView)
        shippingAddressEditText = findViewById(R.id.shippingAddressEditText)
        rbCashOnDelivery = findViewById(R.id.rb_whenship_purchase)
        rbPayWithMomo=findViewById(R.id.rb_withMomo_purchase)
        rbPayWithZalo=findViewById(R.id.rb_withZalo_purchase)
        rgPaymentMethod=findViewById(R.id.rg_paymentMenthod_purchase)
        btnCHooseLocation = findViewById(R.id.btn_ChooseLocation_purchase)
        btnSave = findViewById(R.id.btn_buy_purchase)
        shippingFeeTextView = findViewById(R.id.tv_shippingFee_purchase)
    }
    fun getCurrentDateTimeAsString(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDateTime = Date()
        return dateFormat.format(currentDateTime)
    }
}