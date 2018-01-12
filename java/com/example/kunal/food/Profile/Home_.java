package com.example.kunal.food.Profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kunal.food.LoginAndResgistration.Login;
import com.example.kunal.food.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home_ extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private String Url = "";
    private String placeType = "";

    private Button logout;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<PlacesPOJO> arrayList;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private MyAdapter myAdapter;
    private int size = 0;
    private double longitude = 0.0, latitude = 0.0;


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.placeslist_, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            PlacesPOJO placePOJO = arrayList.get(position);

            final String placeName =  placePOJO.getPlace_name();
            String placeAddress =  placePOJO.getPlace_address();
            String placeRating =  placePOJO.getPlace_rating();
            final String lat = placePOJO.getLatitide();
            final String lng = placePOJO.getLogitude();
            String type = "";

            for(String placeType : placePOJO.getType()){
                type += placeType + ", ";
            }
            type = type.replaceAll(", $", "");

            holder.name.setText("Name: " + placeName);
            holder.address.setText("Address: " + placeAddress);
            holder.type.setText("Type: " + type);
            holder.rating.setRating(Float.parseFloat(placeRating));

            holder.viewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), SeeOnMap.class);
                    intent.putExtra("placeName", placeName);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView name, address, type;
            private RatingBar rating;
            private LinearLayout viewMore;

            public ViewHolder(View itemView) {
                super(itemView);
                viewMore = itemView.findViewById(R.id.viewMore);
                name = itemView.findViewById(R.id.place_name);
                address = itemView.findViewById(R.id.place_address);
                type = itemView.findViewById(R.id.place_type);
                rating = itemView.findViewById(R.id.place_rating);

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        placeType = bundle.getString("placeName");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        logout = findViewById(R.id.logout);
        recyclerView = findViewById(R.id.myList);
        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        arrayList = new ArrayList<>();
        myAdapter = new MyAdapter();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        firebaseAuth = FirebaseAuth.getInstance();

       //InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(this.getWindowToken(), 0);

        if(firebaseAuth.getCurrentUser() == null){
            Fragment login = new Login();
            Required_Fragment(login);
        }

        logout.setOnClickListener(this);

    }

    public void Required_Fragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        if(view == logout){
            progressDialog.setMessage("Login out...");
            progressDialog.show();
            firebaseAuth.signOut();
            Fragment login = new Login();
            Required_Fragment(login);
        }
    }

     /* Getting Places Data From Google places Api*/

    private void placesData() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{

                    JSONArray jsonArray = response.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++){

                        size = jsonArray.length();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONObject jsonObject1 = jsonObject.getJSONObject("geometry");

                        JSONObject jsonObject2 = jsonObject1.getJSONObject("location");

                        String latitude = jsonObject2.getString("lat");
                        String longitude = jsonObject2.getString("lng");


                        String place_name = jsonObject.getString("name");
                        String place_address = jsonObject.getString("vicinity");
                        String place_rating = "";

                        if(!jsonObject.has("rating")){
                            place_rating = "0";
                        }else {
                            place_rating = jsonObject.getString("rating");
                        }

                        if(!jsonObject.has("types")){
                            ArrayList<String> type = new ArrayList<>();
                            type.add("Null");
                            Toast.makeText(getApplicationContext(), "Null...", Toast.LENGTH_SHORT).show();
                        }else{

                            JSONArray jsonArray2 = jsonObject.getJSONArray("types");
                            ArrayList<String> type = new ArrayList<>();

                            for(int j = 0; j < jsonArray2.length(); j++){
                                type.add((String) jsonArray2.get(j));
                            }

                            PlacesPOJO placePOJO = new PlacesPOJO(place_name, place_address, place_rating,latitude, longitude, type);
                            arrayList.add(placePOJO);
                            myAdapter.notifyDataSetChanged();

                        }

                    }


                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Exception : " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error : " + error.getStackTrace(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
    }

    protected synchronized void buildGoogleApiClient(){

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {

        //Toast.makeText(Home_.this, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + latitude +  "," + longitude + "&radius=1000&type=" + placeType + "&sensor=true&key=AIzaSyBPdpFS5jLYg4FbgPHycPjk2cpuuu2pjRc";

       /* Url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + latitude +  "," + longitude + "&radius=1000&type=school&sensor=true&key=AIzaSyBPdpFS5jLYg4FbgPHycPjk2cpuuu2pjRc";*/

        arrayList.clear();
        myAdapter.notifyDataSetChanged();
        placesData();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
