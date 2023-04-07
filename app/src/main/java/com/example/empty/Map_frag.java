package com.example.empty;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.empty.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PrivateKey;

public class Map_frag extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private MapView mMapView;
    private GoogleMap mMap;

    private SharedPreferences sharedPreferences;

    private Context context;
    private FusedLocationProviderClient mLocationProviderClient;
    private MainActivity main;
    private FloatingActionButton start;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final LatLngBounds JHU_BOUNDS = new LatLngBounds(
            new LatLng(39.3256, -76.6228), new LatLng(39.3297, -76.6189));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        context = main.getApplicationContext();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        // ADDED THIS LINE TO AVOID USING THE ChatViewModel class
        binding.mapView.onCreate(savedInstanceState);

        // Get the MapView from the layout
        mMapView = binding.mapView;
        // Important: call onCreate() on the MapView
        mMapView.onCreate(savedInstanceState);
        // Register the callback for when the map is ready
        mMapView.getMapAsync(this);

        // Initialize the location provider client
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Important: call onResume() on the MapView
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Important: call onPause() on the MapView
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Important: call onSaveInstanceState() on the MapView
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // Important: call onLowMemory() on the MapView
        mMapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setLatLngBoundsForCameraTarget(JHU_BOUNDS);

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Enable the "My Location" layer on the map
            mMap.setMyLocationEnabled(true);

            // Get the user's current location
            FusedLocationProviderClient fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity());

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {

                            // Add a marker at the user's current location
                            LatLng currentLocation = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currentLocation)
                                    .title("You are here"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));

                        }

                    });
            // Apply the custom map style
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));
        } else {
            // Show an empty map if location permission is not granted
            Toast.makeText(getContext(), "Location permission not granted, showing empty map",
                    Toast.LENGTH_SHORT).show();
        }
    }
}