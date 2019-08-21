package com.vaidebike;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.vaidebike.service.LocationPointsService;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private Circle circle;
    private final int Request_User_Location_Code = 99;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    LocationManager locationManage;
    private boolean firstTime = true;
    private LatLngBounds bounds;
    private static final double EARTH_RADIUS = 6378100.0;
    private int offset;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false;
    private MapsActivity activity = this;
    private FusedLocationProviderClient fusedLocationClient;
    private int numberLocations = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // RECUPERA BOTAO DA INTERFACE
        ImageButton center = (ImageButton)findViewById(R.id.center);
        ImageButton help = (ImageButton)findViewById(R.id.help);

        // CONFIGURA O ONCLICK NO BOTAO CENTRALIZAR
        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastLocation != null)
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom (new LatLng(lastLocation.getLatitude() , lastLocation.getLongitude( ) ),17 ) );
                }
            }
        });

        // CONFIGURA O ONCLICK NO BOTAO HELP
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(activity, HelpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
            }
        });

        //#####################################################################################################################################################################
        //PEGA A PRIMEIRA LOCALIZACAO NO A PRIMEIRA VEZ
        //#####################################################################################################################################################################
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null)
                        {   if(firstTime)
                                locationChanged(location);
                        }
                    }
                });

        //#####################################################################################################################################################################
        //ATUALIZA A LOCALIZACAO EM TEMPO REAL
        //#####################################################################################################################################################################
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    locationChanged(location);
                }
            }
        };
    }

    //configura os controles do mapa
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        googleMap.setMapStyle(mapStyleOptions);
        mMap.clear();
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestingLocationUpdates = true;
            buildGoogleApiClient();
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    //verifica permissao
    public boolean checkUserLocationPermission() {
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            else
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            return false;
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Sem permissao para acessar a localizacao!", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    //inicia conexao com o googleApiClient
    protected synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {}

    public void locationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (googleApiClient != null && !firstTime)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        if (firstTime)
        {
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            inicializeMarkers(location);
            CameraUpdate cu;

            if(numberLocations > 0)
                cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);

            else
                cu = CameraUpdateFactory.newLatLngZoom(latLng, 15);

            mMap.moveCamera(cu);
            firstTime = false;
        }
        MarkerAnimation.animateMarkerToGB(currentUserLocationMarker, circle, latLng, new LatLngInterpolator.Spherical());
        lastLocation = location;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText(this, "Sem conexao com a internet", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Sem conexao com a internet", Toast.LENGTH_SHORT).show();
    }

    public void inicializeMarkers(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        int height = 40;
        int width = 40;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.current);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(smallMarker);

        currentUserLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .flat(true)
                .anchor(0.5f, 0.5f)
                .icon(bmD));

        circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .fillColor(Color.argb(30, 0, 94, 184))
                .strokeColor(Color.argb(10, 0, 94, 184))
                .strokeWidth(1.5f)
                .radius(location.getAccuracy()));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(currentUserLocationMarker.getPosition());
        points.add(currentUserLocationMarker.getPosition());
        /*Ajustando tamanho marcadores*/
        int height2 = 256;
        int width2 = 96;
        BitmapDrawable marker_vai_de_bike = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_vai_de_bike);
        Bitmap b2 = marker_vai_de_bike.getBitmap();
        Bitmap smallmarker_vai_de_bike = Bitmap.createScaledBitmap(b2, width2, height2, false);
        BitmapDescriptor bitmap_vai_de_bike = BitmapDescriptorFactory.fromBitmap(smallmarker_vai_de_bike);

        if(LocationPointsService.getPlaces() != null)
        for (LatLng point : LocationPointsService.getPlaces()) {
            numberLocations++;
            points.add(point);
            Marker place = mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .icon(bitmap_vai_de_bike));

            builder.include(place.getPosition());
            bounds = builder.build();
        }
    }

    private void startLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}