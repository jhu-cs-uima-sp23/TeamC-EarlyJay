package com.example.empty;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import com.example.empty.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Map_frag extends Fragment implements OnMapReadyCallback{

    private FragmentMapBinding binding;
    private MapView mMapView;
    private GoogleMap mMap;

    private double latitude;
    private double longitude;

    private LatLng search_location;

    private SharedPreferences sharedPreferences;

    private Context context;
    private String uid;
    private FusedLocationProviderClient mLocationProviderClient;
    private MainActivity main;
    private SharedPreferences.Editor editor;

    private DatabaseReference reference;

    private ArrayList<LocationStruct> locStructListByDay;
    private ArrayList<LocationStruct> locStructListByWeek;
    private ArrayList<LocationStruct> locStructListByMonth;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GroundOverlayOptions groundOverlayOptions;
    private Map_child_viewModel shared_data;
    private static final LatLngBounds JHU_BOUNDS = new LatLngBounds(
            new LatLng(39.3256, -76.6228), new LatLng(39.3303, -76.6189));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        context = main.getApplicationContext();

        locStructListByDay = new ArrayList<>();
        locStructListByWeek = new ArrayList<>();
        locStructListByMonth = new ArrayList<>();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        uid = sharedPreferences.getString("uid", "");
        reference = FirebaseDatabase.getInstance().getReference().
                child("users").child(uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locStructListByDay = new ArrayList<>();
                locStructListByWeek = new ArrayList<>();
                locStructListByMonth = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                        LocationStruct locStruct = childSnapshot.getValue(LocationStruct.class);
                        if (locStruct == null) {
                            // client is null, error out
                            Log.e("DBREF:", "Data is unexpectedly null");
                        } else {
                            DateStr now = new DateStr();
                            String dataDateStr = locStruct.getDateStr();
                            if (now.isDaily(dataDateStr)) {
                                locStructListByDay.add(locStruct);
                            }
                            if (now.isWeekly(dataDateStr)) {
                                locStructListByWeek.add(locStruct);
                            }
                            if (now.isMonthly(dataDateStr)) {
                                locStructListByMonth.add(locStruct);
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });



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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();

        float latitude_tmp = sharedPreferences.getFloat("latitude", 0);
        float longitude_tmp = sharedPreferences.getFloat("longitude", 0);

        if (latitude_tmp!=0 && longitude_tmp!=0) {
            latitude = latitude_tmp;
            longitude = longitude_tmp;
           Log.d("Location", "Longitude: " + longitude + " Latitude: " + latitude);
        }

        binding.stuffOnMap.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
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
                    Log.d("TAG", "onLayoutChange: BINGO");
                    markMap(sharedPreferences.getInt("workType", R.drawable.triangle_48));
                    editor.putInt("complete_success", 0);
                    editor.apply();
                }else if(task_completed == 2 && viewName.equals(dwm_view_name)){
                    markMap(R.drawable.skull);
                    editor.putInt("complete_success", 0);
                    editor.apply();
                    Log.d("TAG", "task not completed");
                }else{
                    Log.d("TAG", "onLayoutChange: bruh nothing changed" + getResources().getResourceName(contentId));
                }
                Log.d("TAG", "onLayoutChange: bruh nothing changed" + viewName.equals(dwm_view_name));
                Log.d("TAG", "onLayoutChange: bruh nothing changed" + (task_completed));

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

        Bundle args = getArguments();
        if (args != null) {
            search_location = args.getParcelable("location");
            MarkerOptions markerOptions = new MarkerOptions().position(search_location);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(search_location, 17));
        }

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
//                            mMap.addMarker(new MarkerOptions().position(currentLocation)
//                                    .title("You are here"));
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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                LatLng currentLocation = new LatLng(location.getLatitude(),
                        location.getLongitude());
                draw(workType, currentLocation);
            });
        }
    }

    public void markMapPast(int workType, double latitude_custom, double longitude_custom){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

        if (type.equals("Work")) {
            return R.drawable.circle_dashed_6_xxl;
        } else if (type.equals("Class")) {
            return R.drawable.yellows;
        } else if (type.equals("Team")) {
            return R.drawable.triangle_48;
        } else {
            return R.drawable.star_2_xxl;
        }
    }

}