    package com.example.tk_app.classify_product
    import android.os.Parcel
    import android.os.Parcelable

    data class CartItem(
        val name: String? = null,
        val price: String? = null,
        val quantity: String? = null,
        var productId: String? = null,
        val status: String? = null,
        var imageUrl: String? = null
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(price)
            parcel.writeString(quantity)
            parcel.writeString(productId)
            parcel.writeString(status)
            parcel.writeString(imageUrl)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<CartItem> {
            override fun createFromParcel(parcel: Parcel): CartItem {
                return CartItem(parcel)
            }

            override fun newArray(size: Int): Array<CartItem?> {
                return arrayOfNulls(size)
            }
        }
    }
