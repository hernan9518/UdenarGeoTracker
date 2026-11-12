package com.empresa.udenargeotracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Variable para guardar tu ubicación y poder calcular distancias
    private var currentUserLocation: android.location.Location? = null

    // Gestor moderno para pedir permisos en pantalla
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            enableUserLocation()
        } else {
            Toast.makeText(this, "Permiso de GPS denegado. No podemos ubicarte.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Vincular el mapa del XML e iniciar su carga en segundo plano
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Este método se dispara automáticamente cuando Google Maps termina de cargar
// Este método se dispara automáticamente cuando Google Maps termina de cargar
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        checkLocationPermissions()

        // 1. NUEVO: Evento para borrar el marcador al tocar su cuadro de información
        map.setOnInfoWindowClickListener { markerABorrar ->
            markerABorrar.remove()
            Toast.makeText(this, "Punto eliminado", Toast.LENGTH_SHORT).show()
        }

        // Evento de pulsación larga en el mapa para agregar pines interactivos
        map.setOnMapLongClickListener { latLngPulsado ->

            // Verificar que ya sepamos dónde está el usuario
            currentUserLocation?.let { ubicacionUsuario ->

                // Calcular la distancia usando la fórmula matemática del elipsoide WGS84
                val resultados = FloatArray(1)
                android.location.Location.distanceBetween(
                    ubicacionUsuario.latitude, ubicacionUsuario.longitude,
                    latLngPulsado.latitude, latLngPulsado.longitude,
                    resultados
                )

                // Formatear la distancia para que se vea bonita (ej. 1450.50)
                val distanciaEnMetros = resultados[0]
                val distanciaFormateada = String.format("%.2f", distanciaEnMetros)

                // Crear el marcador en donde el usuario tocó
                val marcador = map.addMarker(
                    MarkerOptions()
                        .position(latLngPulsado)
                        .title("A $distanciaFormateada metros de ti")
                        // 2. MODIFICADO: Agregamos una instrucción clara en el subtítulo
                        .snippet("Toca este cuadro para eliminar el punto")
                )

                // Mostrar el cuadrito de texto del marcador automáticamente
                marcador?.showInfoWindow()
            } ?: run {
                // Si aún no tenemos la ubicación del usuario, mostramos un aviso
                Toast.makeText(this, "Esperando tu ubicación GPS...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation()
        } else {
            // Pedir permisos al usuario
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun enableUserLocation() {
        // Doble validación de seguridad requerida por Android Studio
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // Activa el punto azul nativo de Google Maps
        map.isMyLocationEnabled = true

        // Obtener la última ubicación conocida y mover la cámara
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Guardar la ubicación en la variable global para los cálculos
                currentUserLocation = location

                val currentLatLng = LatLng(location.latitude, location.longitude)

                // Mover la cámara con una animación fluida y zoom nivel 15
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}