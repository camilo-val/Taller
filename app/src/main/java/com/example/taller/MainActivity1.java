package com.example.taller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.tallernativas.MainActivity;import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity1 extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    EditText txtLatitud, txtLongitud;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        LatLng mexico = new LatLng(19.8077463, -99.4077038);
        mMap.addMarker(new MarkerOptions().position(mexico).title("México"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText(String.valueOf(latLng.latitude));
        txtLongitud.setText(String.valueOf(latLng.longitude));

        mMap.clear();
        LatLng mexico = new LatLng(latLng.latitude, latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(mexico).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        // Obtener las coordenadas de los EditText
        String latitudStr = txtLatitud.getText().toString();
        String longitudStr = txtLongitud.getText().toString();

        // Verificar que no estén vacíos
        if (!latitudStr.isEmpty() && !longitudStr.isEmpty()) {
            try {
                // Convertir las coordenadas a double
                double latitud = Double.parseDouble(latitudStr);
                double longitud = Double.parseDouble(longitudStr);

                // Crear una nueva ubicación LatLng
                LatLng nuevaUbicacion = new LatLng(latitud, longitud);

                // Limpiar el mapa y añadir la nueva marca
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(nuevaUbicacion).title("Nueva ubicación"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(nuevaUbicacion));
            } catch (NumberFormatException e) {
                // Manejo en caso de que los datos ingresados no sean números válidos
                txtLatitud.setError("Ingrese un valor numérico válido para latitud");
                txtLongitud.setError("Ingrese un valor numérico válido para longitud");
            }
        } else {
            // Mostrar error si los campos están vacíos
            txtLatitud.setError("Ingrese la latitud");
            txtLongitud.setError("Ingrese la longitud");
        }
    }




    public void onActivityGps(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}