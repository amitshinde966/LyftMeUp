package com.lyft.lyftmeup;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    static int count1=1;
    static Map<String, Float> colorList = new HashMap<String , Float>();
    private static LatLng sourceLatLng, destinationLatLng;
    private static final int LOCATION_REQUEST_CODE = 990;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    Geocoder geocoder;
    MapFragment map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));

        map.onCreate(savedInstanceState);
        context = getApplicationContext();
        ((EditText) findViewById(R.id.whereTo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "click on destination location to place marker", Toast.LENGTH_LONG).show();
            }
        });

        geocoder = new Geocoder(this, Locale.getDefault());
        map.getMapAsync(this);

        ((Button) findViewById(R.id.doneSelection)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RelativeLayout) findViewById(R.id.rl1)).setVisibility(View.GONE);
                //if(sourceLatLng!=null && destinationLatLng!=null) {
                Toast.makeText(getApplicationContext(), " Fetching Drivers details", Toast.LENGTH_LONG).show();
                findDriversNearBy(sourceLatLng, destinationLatLng);
                //}
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                sourceLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                Toast.makeText(getApplicationContext(), " got user location", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Latitude", "disable");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Latitude", "enable");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Latitude", "status");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext() != null) {
            sourceLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sourceLatLng));
        }
    }




    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(latLng!=null) {
                    mMap.clear();
                    destinationLatLng = latLng;
                    mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination Location"));
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses != null) {
                        String areaName = addresses.get(0).getLocality();
                        String cityName = addresses.get(0).getAddressLine(0);
                        ((EditText) findViewById(R.id.whereTo)).setText(areaName + " " + cityName);
                    }

                    ((Button) findViewById(R.id.doneSelection)).setVisibility(View.VISIBLE);
                }

            }
        });

        if(sourceLatLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(sourceLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(6));
        }
        else{
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(18.49291795, 74.02415989409091)));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
            map.getMapAsync(this);
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    void findDriversNearBy(LatLng sourceLatLng, LatLng destinationLatLng){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("id", userId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverRequest/"+userId);


        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation("source", new GeoLocation(sourceLatLng.latitude, sourceLatLng.longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }
        });

        geoFire.setLocation("destination", new GeoLocation(destinationLatLng.latitude, destinationLatLng.longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }
        });

        final DatabaseReference workerLocation = FirebaseDatabase.getInstance().getReference().child("RiderRequest");
        geoFire = new GeoFire(workerLocation);
        GeoQuery geoQuery1;
        geoQuery1 = geoFire.queryAtLocation(new GeoLocation(sourceLatLng.latitude, sourceLatLng.longitude), 5);
        geoQuery1.removeAllListeners();

        geoQuery1.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(key!= null){
                    displayUser(key);
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    void displayUser(final String key){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UserRequest").child(key).child("source").child("l");
        ref.addChildEventListener(new ChildEventListener() {
            Marker m1;
            List<String> location = new ArrayList<String>();
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    location.add(dataSnapshot.getValue().toString());
                    if(location.size() == 2) {
                        //List<Object> map = (List<Object>) dataSnapshot.getValue();
                        double locationLat = 0;
                        double locationLong = 0;
                        if (location.get(0) != null) {
                            locationLat = Double.parseDouble(location.get(0).toString());
                        }
                        if (location.get(1) != null) {
                            locationLong = Double.parseDouble(location.get(1).toString());
                        }
                        LatLng workerLatLng = new LatLng(locationLat, locationLong);
                        m1 = mMap.addMarker(new MarkerOptions().alpha(12*count1).position(workerLatLng).title("User Source Location"));
                        colorList.put(key, 12.0f * count1);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("UserRequest").child(key).child("destination").child("l");
        ref1.addChildEventListener(new ChildEventListener() {
            Marker m1;
            List<String> location = new ArrayList<String>();
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    location.add(dataSnapshot.getValue().toString());
                    if(location.size() == 2) {
                        //List<Object> map = (List<Object>) dataSnapshot.getValue();
                        double locationLat = 0;
                        double locationLong = 0;
                        if (location.get(0) != null) {
                            locationLat = Double.parseDouble(location.get(0).toString());
                        }
                        if (location.get(1) != null) {
                            locationLong = Double.parseDouble(location.get(1).toString());
                        }
                        LatLng workerLatLng = new LatLng(locationLat, locationLong);

                        if(colorList.containsKey(key))
                            m1 = mMap.addMarker(new MarkerOptions().alpha(colorList.get(key)).position(workerLatLng).title("User Destination Location"));

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
