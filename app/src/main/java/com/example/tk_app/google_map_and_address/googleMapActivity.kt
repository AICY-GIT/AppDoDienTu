package com.example.tk_app.google_map_and_address

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.tk_app.R
import com.example.tk_app.classify_product.CartItemModel
import com.example.tk_app.pay.PurchaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class googleMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var googleMap: GoogleMap
    private val hardcodedPoint: LatLng = LatLng(21.0245, 105.84117) // Example: HA NOI
    private var userSelectedPoint: LatLng? = null
    private lateinit var btnChoose: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
        val productList = intent.getParcelableArrayListExtra<CartItemModel>("productList")
        val totalCartPriceString = intent.getStringExtra("totalCartPrice")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btnChoose = findViewById(R.id.btnChoose)
        btnChoose.setOnClickListener{
            if (userSelectedPoint != null) {
                // Calculate distance
                val distance = calculateDistance(userSelectedPoint!!, hardcodedPoint)
                // Check if distance is greater than 0 (i.e., points are different)
                if (distance > 0) {
                    // Move to another activity (replace YourNewActivity::class.java with the actual activity class)
                    val intent = Intent(this, PurchaseActivity::class.java)
                    intent.putParcelableArrayListExtra("productList", productList)
                    intent.putExtra("userSelectedPoint", userSelectedPoint)
                    intent.putExtra("distance", distance)
                    intent.putExtra("totalCartPrice", totalCartPriceString)
                    startActivity(intent)
                } else {
                    // Show a message indicating that the selected point is the same as the hardcoded point
                    Toast.makeText(this, "Please select a point different from the hardcoded point.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show a message indicating that no point is selected
                Toast.makeText(this, "Please select a point on the map.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.setOnMapClickListener(this) // Set the click listener here

        // Set the initial camera position to focus on Vietnam
        val vietnamBounds = LatLngBounds(LatLng(8.18, 102.14), LatLng(23.39, 109.46))
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(vietnamBounds, 0)
        googleMap.moveCamera(cameraUpdate)

        // Initialize the map with the hardcoded point
        updateMapWithPoints()
    }

    override fun onMapClick(latLng: LatLng) {
        if (isPointInVietnamBounds(latLng)) {
            // Handle map click to set userSelectedPoint
            userSelectedPoint = latLng

            // Update the map with the new points
            updateMapWithPoints()
        } else {
            // Show a message indicating that the point is outside Vietnam
            Toast.makeText(this, "Please select a point within Vietnam.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun isPointInVietnamBounds(latLng: LatLng): Boolean {
        // Define the bounds of Vietnam
        val minLat = 8.18
        val maxLat = 23.39
        val minLng = 102.14
        val maxLng = 109.46

        // Check if the point is within the bounds
        return latLng.latitude in minLat..maxLat && latLng.longitude in minLng..maxLng
    }

    private fun updateMapWithPoints() {
        // Clear existing markers
        googleMap.clear()
        // Check if userSelectedPoint is not null before adding the marker
        userSelectedPoint?.let {
            googleMap.addMarker(MarkerOptions().position(it).title("User Selected Point"))
        }
        // Always add the hardcoded point marker
        googleMap.addMarker(MarkerOptions().position(hardcodedPoint).title("Hardcoded Point"))
        // Move the camera to a zoom level that fits both markers
        userSelectedPoint?.let { userPoint ->
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds.builder().include(userPoint).include(hardcodedPoint).build(), 50
                )
            )
        }
        // Calculate distance
        userSelectedPoint?.let { userPoint ->
            val distance  = calculateDistance(userPoint, hardcodedPoint)
            Toast.makeText(this, "Distance: $distance km", Toast.LENGTH_SHORT).show()
        }
    }
    private fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val radius = 6371.0 // Earth's radius in kilometers

        val lat1 = Math.toRadians(point1.latitude)
        val lon1 = Math.toRadians(point1.longitude)
        val lat2 = Math.toRadians(point2.latitude)
        val lon2 = Math.toRadians(point2.longitude)

        val dLon = lon2 - lon1
        val dLat = lat2 - lat1

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return radius * c
    }
}