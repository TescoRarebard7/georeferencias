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
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.geoposicionamiento_referenciado.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager

    private var targetlocations = mutableListOf<LocationData>()
    var ultimoDrawable: Int? = null


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
            startLocation()
        }else{
            Toast.makeText(this, "se otorgaron los permisos", Toast.LENGTH_SHORT).show()
            startLocation()
        }

        setup()
    }

    private fun setup() {
        targetlocations.add(LocationData("CCAI",19.631050,-99.113775,R.drawable.ccai,"" +
                "Un centro de cooperación académica industrial es una entidad educativa o de investigación que colabora estrechamente con la industria para promover la innovación, el desarrollo tecnológico y la formación de profesionales altamente capacitados. Este centro facilita la interacción entre el mundo académico y el sector industrial, fomentando proyectos conjuntos, investigación aplicada y programas de capacitación que benefician a ambas partes al combinar el conocimiento académico con las necesidades y la experiencia práctica de la industria"))
        targetlocations.add(LocationData("Biblioteca digital",19.631633,-99.113574,R.drawable.biblioteca_digital,"Una biblioteca digital es una colección de recursos y materiales de lectura en formato digital que proporciona acceso en línea a libros, revistas, documentos y otros contenidos electrónicos, permitiendo a los usuarios leer, investigar y aprender desde dispositivos electrónicos como computadoras y dispositivos móviles."))
        targetlocations.add(LocationData("Servicio Social/ Residencias",19.628361,-99.114387,R.drawable.tesco_img_arco,"Un edificio de servicio social y residencias es una infraestructura diseñada para servir como un centro multifuncional que combina instalaciones destinadas al servicio social y alojamiento residencial. Este tipo de edificio se encuentra comúnmente en campus universitarios, comunidades educativas o instalaciones gubernamentales."))
        targetlocations.add(LocationData("Edificio C",19.629659,-99.114339,R.drawable.edificio_c,"Edificio C"))
        targetlocations.add(LocationData("Edificio A", 19.630982,-99.114540,R.drawable.edificio_a,"Edificio A"))
        targetlocations.add(LocationData("Estacionamiento",19.630099,-99.113936,R.drawable.estacionamiento,"Un estacionamiento es un área designada y preparada específicamente para que los vehículos se detengan y permanezcan temporalmente sin estar en movimiento. Por lo general, los estacionamientos pueden encontrarse en diversos lugares, como centros comerciales, edificios de oficinas, aeropuertos, calles urbanas y otros espacios públicos o privados. "))
        targetlocations.add(LocationData("Puerta principal",19.6320,-99.1140,R.drawable.puerta_principal,"Pos es la entrada que mas"))

    }

    @SuppressLint("MissingPermission")
    private fun startLocation() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            500,
            0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {

                    Log.e("onLocationChanged", location.latitude.toString() + location.longitude.toString())
                    val closesLocation = targetlocations.minByOrNull { target ->
                        location.distanceTo(target.toLocation())
                    }
                    var visibilityTextView = View.GONE
                    val imageManager = binding.image


                    val nombre = if (closesLocation != null && closesLocation.isWithinRange(location,25.0)){
                        closesLocation.name
                    } else {
                        "Acercate a una ubicacion"
                    }
                    binding.name.text = nombre

                    val image = if (closesLocation != null && closesLocation.isWithinRange(location,25.0)){
                        closesLocation.image
                    }else{
                        R.drawable.ubicacion
                    }
                    val desc = if (closesLocation != null && closesLocation.isWithinRange(location,25.0)){
                        closesLocation.desc
                    } else {
                        ""
                    }

                    visibilityTextView = if (desc == ""){
                        View.GONE
                    }else{
                        View.VISIBLE
                    }


                    binding.description.text = desc
                    binding.description.visibility = visibilityTextView

                    if (image != ultimoDrawable){
                        val drawable: Drawable? = ContextCompat.getDrawable(this@MainActivity,image)

                        val requestOptions =
                            RequestOptions().transform(RoundedCorners(40))
                        Glide.with(this@MainActivity)
                            .load(drawable)
                            .apply(requestOptions)
                            .into(imageManager)
                        ultimoDrawable = image
                    }


                }
            }
        )
    }



    data class LocationData(val name:String,val latitude:Double,val longitude:Double,val image:Int,val desc:String){
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