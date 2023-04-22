package com.example.empty;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.empty.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Map_frag extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback{

    private FragmentMapBinding binding;
    private MapView mMapView;
    private GoogleMap mMap;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    private ArrayList<LocationStruct> locStructListByDay;
    private ArrayList<LocationStruct> locStructListByWeek;
    private ArrayList<LocationStruct> locStructListByMonth;

    private static final LatLngBounds JHU_BOUNDS = new LatLngBounds(
            new LatLng(39.3256, -76.6228), new LatLng(39.3303, -76.6189));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        Context context = main.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();


        locStructListByDay = new ArrayList<>();
        locStructListByWeek = new ArrayList<>();
        locStructListByMonth = new ArrayList<>();

        String uid = sharedPreferences.getString("uid", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().
                child("users").child(uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locStructListByDay = new ArrayList<>();
                locStructListByWeek = new ArrayList<>();
                locStructListByMonth = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                        String dataDateStr = childSnapshot.getKey();
                        DateStr now = new DateStr();
                        if (dataDateStr == null) {
                            // client is null, error out
                            Log.e("DBREF:", "Data is unexpectedly null");
                        } else {
                            if (now.isDaily(dataDateStr)) {
                                for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                    LocationStruct locStruct = grandChildSnapshot.getValue(LocationStruct.class);
                                    locStructListByDay.add(locStruct);
                                }
                            }
                            if (now.isWeekly(dataDateStr)) {
                                for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                    LocationStruct locStruct = grandChildSnapshot.getValue(LocationStruct.class);
                                    locStructListByWeek.add(locStruct);
                                }
                            }
                            if (now.isMonthly(dataDateStr)) {
                                for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                    LocationStruct locStruct = grandChildSnapshot.getValue(LocationStruct.class);
                                    locStructListByMonth.add(locStruct);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d("datacheck", e.getClass().getSimpleName());
                        Log.d("datacheck", e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        binding.mapView.onCreate(savedInstanceState);

        // Get the MapView from the layout
        mMapView = binding.mapView;

        // Register the callback for when the map is ready
        mMapView.getMapAsync(this);

        // Initialize the location provider client

        if(sharedPreferences.getBoolean("startPlanTask", false)){
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                    editor.putFloat("latitude", (float) location.getLatitude());
                    editor.putFloat("longitude", (float) location.getLongitude());
                    editor.apply();
                });
            }
            editor.putBoolean("startPlanTask", false);
            editor.apply();
            main.replaceFragment(R.id.stuff_on_map, new CountDownFragment());
        }else {
            main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        }
        binding.stuffOnMap.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            View content = binding.stuffOnMap.getChildAt(0);
            int contentId = content.getId();
//                0 - no action; 1 - success; 2 - failed
            int task_completed = sharedPreferences.getInt("complete_success", 0);
            String viewName = getResources().getResourceName(contentId);
            String dwm_view_name = getResources().getResourceName(R.id.dwm_view);
            int selected = sharedPreferences.getInt("last_selected", 0);
            switch(selected) {
                case 1:
                    for (LocationStruct locStr : locStructListByWeek) {
                        int drawable = getResID(locStr.getType());
                        float latitude = locStr.getLatitude();
                        float longitude = locStr.getLongitude();
                        boolean complete = locStr.getComplete();
                        if (complete) {
                            markMapPast(drawable,latitude,longitude);
                        } else {
                            markMapPast(R.drawable.skull,latitude,longitude);
                        }
                    }
                    break;
                case 2:
                    for (LocationStruct locStr : locStructListByMonth) {
                        int drawable = getResID(locStr.getType());
                        float latitude = locStr.getLatitude();
                        float longitude = locStr.getLongitude();
                        boolean complete = locStr.getComplete();
                        if (complete) {
                            markMapPast(drawable,latitude,longitude);
                        } else {
                            markMapPast(R.drawable.skull,latitude,longitude);
                        }

                    }
                    break;
                default:
                    for (LocationStruct locStr : locStructListByDay) {
                        int drawable = getResID(locStr.getType());
                        float latitude = locStr.getLatitude();
                        float longitude = locStr.getLongitude();
                        boolean complete = locStr.getComplete();
                        if (complete) {
                            markMapPast(drawable,latitude,longitude);
                        } else {
                            markMapPast(R.drawable.skull,latitude,longitude);
                        }
                    }
                    break;
            }

            if (task_completed == 1 && viewName.equals(dwm_view_name)){
                markMap(sharedPreferences.getInt("workType", R.drawable.triangle_48));
                editor.putInt("complete_success", 0);
                editor.apply();
            }else if(task_completed == 2 && viewName.equals(dwm_view_name)){
                markMap(R.drawable.skull);
                editor.putInt("complete_success", 0);
                editor.apply();
            }

        });

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setLatLngBoundsForCameraTarget(JHU_BOUNDS);


        if (ContextCompat.checkSelfPermission(requireContext(),
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));

                        }

                    });
            // Apply the custom map style
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json));
        } else {
            // Show an empty map if location permission is not granted
            Toast.makeText(getContext(), "Location permission not granted, showing empty map",
                    Toast.LENGTH_SHORT).show();
        }

        Bundle args = getArguments();
        if (args != null) {
            LatLng search_location = args.getParcelable("location");
            MarkerOptions markerOptions = new MarkerOptions().position(search_location);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(search_location, 17));
        }

        int selected = sharedPreferences.getInt("last_selected", 0);
        switch(selected) {
            case 1:
                for (LocationStruct locStr : locStructListByWeek) {
                    int drawable = getResID(locStr.getType());
                    float latitude = locStr.getLatitude();
                    float longitude = locStr.getLongitude();
                    boolean complete = locStr.getComplete();
                    if (complete) {
                        markMapPast(drawable,latitude,longitude);
                    } else {
                        markMapPast(R.drawable.skull,latitude,longitude);
                    }

                }
                break;
            case 2:
                for (LocationStruct locStr : locStructListByMonth) {
                    int drawable = getResID(locStr.getType());
                    float latitude = locStr.getLatitude();
                    float longitude = locStr.getLongitude();
                    boolean complete = locStr.getComplete();
                    if (complete) {
                        markMapPast(drawable,latitude,longitude);
                    } else {
                        markMapPast(R.drawable.skull,latitude,longitude);
                    }

                }
                break;
            default:
                for (LocationStruct locStr : locStructListByDay) {
                    int drawable = getResID(locStr.getType());
                    float latitude = locStr.getLatitude();
                    float longitude = locStr.getLongitude();
                    boolean complete = locStr.getComplete();
                    if (complete) {
                        markMapPast(drawable,latitude,longitude);
                    } else {
                        markMapPast(R.drawable.skull,latitude,longitude);
                    }

                }
                break;
        }
    }
    public void draw(int workType, LatLng latLng){
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(workType))
                .position(latLng, 20f, 20f);
        mMap.addGroundOverlay(groundOverlayOptions);
    }
    public void markMap(int workType){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                LatLng currentLocation = new LatLng(location.getLatitude(),
                        location.getLongitude());
                draw(workType, currentLocation);
            });
        }
    }

    public void markMapPast(int workType, double latitude_custom, double longitude_custom){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                LatLng currentLocation = new LatLng(latitude_custom,
                        longitude_custom);
                draw(workType, currentLocation);
            });
        }
    }

    private int getResID(String type) {
        String[] types = type.split(" ");
        type = types[types.length - 1];

        switch (type) {
            case "Work":
                return R.drawable.circle_dashed_6_xxl;
            case "Class":
                return R.drawable.yellows;
            case "Team":
                return R.drawable.triangle_48;
            default:
                return R.drawable.star_2_xxl;
        }
    }


}