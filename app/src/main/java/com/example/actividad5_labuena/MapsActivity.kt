package com.example.actividad5_labuena

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.actividad5_labuena.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.location.LocationRequest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val PERMISO_UBICACION = 0
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val PREFS_ARCHIVO = "prefs.xml"
    private lateinit var sharedPrefs : SharedPreferences
    private lateinit var pos : LatLng
    private var pinCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences(PREFS_ARCHIVO, Context.MODE_PRIVATE)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val counter = sharedPrefs.getString("counter", "-1")

        if (counter != null) {
            pinCount = counter.toInt()
        }

        Log.wtf("CONT", pinCount.toString())
        if (pinCount != 0){
            var lat = ""
            var lng = ""

            for (i in 0 until pinCount){ //pins guardados
                lat = sharedPrefs.getString("Lat: $i", "0").toString()
                lng = sharedPrefs.getString("Lng: $i", "0").toString()

                pos = LatLng(lat.toDouble(), lng.toDouble())
                makePin(pos)
            }
        }


        // Add a marker in classroom and move the camera
        val salonPos = LatLng(20.734797, -103.457287)
        mMap.addMarker(MarkerOptions().position(salonPos).title("SALON"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(salonPos, 18f))

        habilitarMyLocation()

        mMap.setOnMapClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("PIN")
                    .alpha(0.5f)
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_BLUE
                        )
                    )
            )

            //Toast de ubicacion del pin
            Toast.makeText(
                this,
                "$latLng",
                Toast.LENGTH_SHORT
            ).show()

            pinCount += 1

            val editor : SharedPreferences.Editor = sharedPrefs.edit()

            editor.putString("Lat: " + (pinCount - 1).toString(), latLng.latitude.toString())
            editor.putString("Lng: " + (pinCount - 1).toString(), latLng.longitude.toString())
            editor.putInt("pinCount: ", pinCount)
            editor.putString("counter", pinCount.toString())

            editor.commit()
        }

        mMap.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()

                //Toast de ubicacion del pin
                Toast.makeText(
                    this,
                    "${marker.position}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }


    }

    fun makePin(pinPos : LatLng){
        mMap.addMarker(
            MarkerOptions()
                .position(pinPos)
                .title("PIN")
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN
                    )
                )
        )
    }
    fun habilitarMyLocation() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION // COARSE
            )
            ==
            PackageManager.PERMISSION_GRANTED
        ) {

            mMap.isMyLocationEnabled = true

        } else {
            Toast.makeText(this, "PIDIENDO PERMISOS", Toast.LENGTH_SHORT).show()
            val permisos = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permisos, PERMISO_UBICACION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISO_UBICACION &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mMap.isMyLocationEnabled = true
        }
    }
}




