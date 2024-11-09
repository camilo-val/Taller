package com.example.tallernativas;

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.taller.MainActivity1
import com.example.taller.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val CODIGO_PERMISO_UBICACION = 100

    private var isPermisos = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private  lateinit var locationCallback: LocationCallback

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Solicitud de permisos
        solicitarPermisosUbicacion()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun solicitarPermisosUbicacion(){
        when{
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                isPermisos = true
                iniciarLocalizacion()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )->{
                mostrarDialogoExplicativo()
            }
            else->{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    CODIGO_PERMISO_UBICACION
                )
            }
        }
    }

    private fun mostrarDialogoExplicativo(){
        //Alerta
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Permisos necesarios")
            .setMessage("Esta aplicacion requiere acceder a tu ubicación para mostrar tus coordenadas ¿Deseas permitir el acceso?")
            //Boton positivo que solita permisos
            .setPositiveButton("Sí"){_,_->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    CODIGO_PERMISO_UBICACION
                )
            }
            .setNegativeButton("No"){dialog,_->
                dialog.dismiss()
                mostrarMensajePermisosDenegados()
            }
            .create()
            .show()
    }

    private fun mostrarMensajePermisosDenegados(){
        //Mostrar mensaje Toast
        Toast.makeText(
            this,
            "La aplicación necesita permisos de ubicación para funcionar",
            Toast.LENGTH_LONG
        ).show()
    }

    //Iniciar la localización

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun iniciarLocalizacion(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            //Configuracion General
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                30000
            ).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

            locationCallback = object : LocationCallback(){
                @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    //Actualizacion UI
                    locationResult.lastLocation?.let { location -> actualizarUbicacion(location) }
                }
            }

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
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let { actualizarUbicacion(it) }
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "Error obteniendo ubicación", e)
                    Toast.makeText(
                        this,
                        "Error al obtener la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        }catch (e:SecurityException){
            Log.e("Location","Error de permisos: ${e.message}")
            Toast.makeText(
                this,
                "Error: Permisos no disponibles",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun actualizarUbicacion(location: android.location.Location){
        binding.apply {
            tvLatitud.text = String.format("%.6f",location.latitude)
            tvLongitud.text = String.format("%.6f",location.longitude)
        }

        Log.d("Location","Lat: ${location.longitude}, Lon: ${location.longitude}")

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CODIGO_PERMISO_UBICACION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                ) {
                    // Todos los permisos fueron concedidos
                    isPermisos = true
                    iniciarLocalizacion()
                } else {
                    // Algunos permisos fueron denegados
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        // El usuario denegó los permisos pero podemos volver a preguntar
                        mostrarDialogoExplicativo()
                    } else {
                        // El usuario denegó los permisos y marcó "No volver a preguntar"
                        mostrarDialogoConfiguracion()
                    }
                }
            }
        }
    }

    private fun mostrarDialogoConfiguracion() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Permisos requeridos")
            .setMessage("Es necesario habilitar los permisos desde la configuración de la aplicación para poder funcionar correctamente.")
            .setPositiveButton("Ir a Configuración") { _, _ ->
                abrirConfiguracionAplicacion()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                mostrarMensajePermisosDenegados()
            }
            .create()
            .show()
    }

    private fun abrirConfiguracionAplicacion() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }

    override fun
            onDestroy() {
        super.onDestroy()
        if (::locationCallback.isInitialized && ::fusedLocationClient.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    fun onActivityMaps(view: View?) {
        val intent = Intent(this, MainActivity1::class.java)
        startActivity(intent)
    }






}