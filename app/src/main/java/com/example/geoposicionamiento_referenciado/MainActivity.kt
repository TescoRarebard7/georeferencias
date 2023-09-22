package com.example.geoposicionamiento_referenciado

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.geoposicionamiento_referenciado.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager

    private var targetlocations = mutableListOf<LocationData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }else{
            Toast.makeText(this, "se otorgaron los permisos", Toast.LENGTH_SHORT).show()
            startLocation()
        }

        setup()
    }

    private fun setup() {
        targetlocations.add(LocationData("Casita",19.624665,-99.114824,R.drawable.casita))

    }

    @SuppressLint("MissingPermission")
    private fun startLocation() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {

                    Log.e("onLocationChanged", location.latitude.toString() + location.longitude.toString())
                    val closesLocation = targetlocations.minByOrNull { target ->
                        location.distanceTo(target.toLocation())
                    }

                    val nombre = if (closesLocation != null && closesLocation.isWithinRange(location,10.0)){
                        closesLocation.name
                    } else {
                        "Acercate a una ubicacion"
                    }
                    binding.name.text = nombre

                    val drawableResId = closesLocation?.image?:R.drawable.ubicacion
                    val drawable: Drawable? = ContextCompat.getDrawable(this@MainActivity,drawableResId)
                    binding.image.setImageDrawable(drawable)
                }
            }
        )
    }



    data class LocationData(val name:String,val latitude:Double,val longitude:Double,val image:Int){
        fun toLocation(): Location{
            val location = Location("")
            location.latitude = latitude
            location.longitude = longitude
            return location
        }

        fun isWithinRange(location: Location,radius:Double):Boolean{
            return location.distanceTo(toLocation()) <= radius
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "permisos concedidos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}