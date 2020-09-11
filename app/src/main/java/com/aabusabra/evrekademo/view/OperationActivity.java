package com.aabusabra.evrekademo.view;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aabusabra.evrekademo.R;
import com.aabusabra.evrekademo.helper.FirebaseCRUDHelper;
import com.aabusabra.evrekademo.model.Container;
import com.aabusabra.evrekademo.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OperationActivity extends FragmentActivity implements OnMapReadyCallback {

    private String TAG = OperationActivity.class.getSimpleName();
    private Context mContext;
    private ActivityCompat currentActivity;
    private GoogleMap map;
    List<Marker> markersList = new ArrayList<>();

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 53203;
    private int DEFAULT_ZOOM = 25;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLocation;


    //database reference
    DatabaseReference db;

    private List<Container> allContainers = new ArrayList<>();

    private LatLngBounds.Builder builder;
    private CameraUpdate cu;
    private Marker selectedMarker;


    private BottomSheetBehavior sheetBehavior;
    private View bottom_sheet;
    private View markerContainer, saveDialogContainer, saveSuccessContainer;
    private TextView containerIDtxt, containerNextCollectionDatetxt, containerfullnessRatetxt;
    private Button savebtn, navigatebtn, relocatebtn;


    private int selectedPosition;
    private Marker movedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        mContext = getApplicationContext();

        //getting the reference of container
        db = FirebaseDatabase.getInstance().getReference("Container");

        Utils.overrideFonts(getApplicationContext(), getWindow().getDecorView().getRootView());


        if (!Utils.haveNetworkConnection(mContext))
        {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.activity_customized_toast, findViewById(R.id.custom_toast_container));

            TextView text = layout.findViewById(R.id.text);
            text.setText(R.string.no_internet_connection_msg);

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 40);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
           return;
        }


        if (!mLocationPermissionGranted)
        {
            getLocationPermission();
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        getLastLocation();

        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);


        markerContainer = findViewById(R.id.markerContainer);
        saveDialogContainer = findViewById(R.id.saveDialogContainer);
        saveSuccessContainer = findViewById(R.id.saveSuccessContainer);

        navigatebtn = findViewById(R.id.navigatebtn);
        relocatebtn = findViewById(R.id.relocatebtn);
        savebtn = findViewById(R.id.savebtn);

        containerIDtxt = findViewById(R.id.containerIDtxt);
        containerNextCollectionDatetxt = findViewById(R.id.containerNextCollectiontxt);
        containerfullnessRatetxt = findViewById(R.id.containerFullNessRatetxt);

        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);




        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        relocatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDialogContainer.setVisibility(View.VISIBLE);
                markerContainer.setVisibility(View.GONE);
                saveSuccessContainer.setVisibility(View.GONE);

            }
        });


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateMarkerPosition(allContainers.get(selectedPosition).getKey());


            }
        });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        map = googleMap;
        map.clear();
        markersList.clear();


        if (mLocation != null)
        {

            map.setMyLocationEnabled(true);
            LatLng currentLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            MarkerOptions currentMarker = new MarkerOptions().position(currentLatLng);


            BitmapDescriptor markerIcon = Utils.bitmapDescriptorFromVector(mContext,R.drawable.ic_circle,50,50);
            currentMarker.icon(markerIcon);
            map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));

            map.addMarker(currentMarker);

        }



    }



    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(OperationActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    onMapReady(map);
                }
            }
        }
    }



    private void getLastLocation()
    {


        Task<Location> task = mFusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {

                    mLocation = location;
                    onMapReady(map);

                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clearing the previous container list
                allContainers.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting container
                    Container c = postSnapshot.getValue(Container.class);
                    //adding container to the list
                    allContainers.add(c);
                }

//                Log.e(TAG,allContainers.size()+" containers!");

                putMarkersOnMap();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }




    public void putMarkersOnMap() {
        //clear the map before draw markers
        markersList.clear();
        map.clear();

        if (mLocation != null)
        {
            Marker myLocationIS = map.addMarker(new MarkerOptions().position(new LatLng(mLocation.getLatitude(),mLocation.getLongitude())).title(mContext.getResources().getString(R.string.me)));

            BitmapDescriptor markerIcon = Utils.bitmapDescriptorFromVector(mContext,R.drawable.ic_circle,50,50);

            myLocationIS.setIcon(markerIcon);

            markersList.add(myLocationIS);
        }



        if (allContainers !=null && allContainers.size() >0)
        {
            BitmapDescriptor markerIcon = Utils.bitmapDescriptorFromVector(mContext,R.drawable.ic_household_bin,110,140);

            for (int i=0;i<allContainers.size();i++)
            {
                Marker markerIS = map.addMarker(new MarkerOptions().position(new LatLng(allContainers.get(i).getContainer_latitude(),allContainers.get(i).getContainer_longitude()))
                .title(getResources().getString(R.string.container)+": "+allContainers.get(i).getContainerID())
                .icon(markerIcon));


                markersList.add(markerIS);
            }

        }


//        Log.e("Marker size is",markersList.size()+"");


        //animate camera again to the user's current location
        builder = new LatLngBounds.Builder();
        builder.include(markersList.get(0).getPosition());
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
        map.animateCamera(cu);



        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                markerContainer.setVisibility(View.GONE);
                saveDialogContainer.setVisibility(View.VISIBLE);
                saveSuccessContainer.setVisibility(View.GONE);
            }

            @Override
            public void onMarkerDrag(Marker marker) {

                marker.setAlpha(0.4f);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                marker.setAlpha(1.0f);

                movedMarker = marker;
                selectedMarker = null;


            }
        });



        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {



            @Override
            public boolean onMarkerClick(Marker marker) {


                if ((marker.getPosition().latitude == mLocation.getLatitude()) && (marker.getPosition().longitude == mLocation.getLongitude()))
                {

                    if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                            sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                    }


                }else{
                    BitmapDescriptor normalIcon = Utils.bitmapDescriptorFromVector(mContext,R.drawable.ic_household_bin,110,140);
                    BitmapDescriptor clickedIcon = Utils.bitmapDescriptorFromVector(mContext,R.drawable.ic_battery_bin,110,140);

                    if (null != selectedMarker) {
                        selectedMarker.setIcon(normalIcon);
                        selectedMarker.setDraggable(false);
                    }
                    selectedMarker = marker;
                    selectedMarker.setIcon(clickedIcon);
                    selectedMarker.setDraggable(true);

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                    int currentPos =0;
                    for (int i=0;i<markersList.size();i++)
                    {
                        if ((marker.getPosition().latitude == markersList.get(i).getPosition().latitude) &&
                                (marker.getPosition().longitude == markersList.get(i).getPosition().longitude) )
                        {
                            currentPos = i;
                            break;
                        }
                    }

                    int positionIs = currentPos-1;

//                    int positionIs = Integer.parseInt(marker.getId().substring(1,marker.getId().length()))-2;


                    selectedPosition = positionIs;

                    containerIDtxt.setText(getResources().getString(R.string.container)+" "+allContainers.get(positionIs).getContainerID());
                    containerNextCollectionDatetxt.setText(Utils.convertLongToStringDateOnlyDate(Long.valueOf(allContainers.get(positionIs).getLastCollectionDate())));
                    containerfullnessRatetxt.setText("%"+allContainers.get(positionIs).getFullnessRate());

                    saveDialogContainer.setVisibility(View.GONE);
                    saveSuccessContainer.setVisibility(View.GONE);
                    markerContainer.setVisibility(View.VISIBLE);

                }


                return false;
            }


        });



    }



    private void updateMarkerPosition(String key) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Container").child(key);

        //updating data
        Container c = new Container(key,allContainers.get(selectedPosition).getContainerID(),allContainers.get(selectedPosition).getLastCollectionDate(),allContainers.get(selectedPosition).getFullnessRate(),movedMarker.getPosition().latitude,movedMarker.getPosition().longitude);
        db.setValue(c);

        allContainers.get(selectedPosition).setContainer_latitude(movedMarker.getPosition().latitude);
        allContainers.get(selectedPosition).setContainer_longitude(movedMarker.getPosition().longitude);

        markerContainer.setVisibility(View.GONE);
        saveDialogContainer.setVisibility(View.GONE);
        saveSuccessContainer.setVisibility(View.VISIBLE);


    }


}
