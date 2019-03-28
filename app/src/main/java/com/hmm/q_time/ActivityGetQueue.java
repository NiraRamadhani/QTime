package com.hmm.q_time;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityGetQueue extends AppCompatActivity {
    private static final String TAG = "ActivityGetQueue";
    private static final String EXTRA_DOCTOR = "Doctor";
    private static final int MAX_DISTANCE = 10000;

    private Button mQueueNowButton;
    private Button mQueueLaterButton;
    private TextView mDoctorName;
    private Doctor mDoctor;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LinearLayout mLocationBox;
    private TextView mCurrentQueueTextView;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private int currentQueue;
    private Boolean isInQueue;

    private double mCurrentLatitude;
    private double mCurrentLongitude;
    private double distance;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_queue);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        mDoctor = intent.getParcelableExtra(EXTRA_DOCTOR);
        Log.d(TAG, mDoctor.getId() + ", " + mDoctor.getName() + ", " + mDoctor.getType());


        mDoctorName = findViewById(R.id.doctor_detail_name);
        mDoctorName.setText(mDoctor.getName());

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mCurrentLatitude = location.getLatitude();
                            mCurrentLongitude = location.getLongitude();
                            Log.d(TAG, "doctor Latitude " + mDoctor.getLatitude());
                            Log.d(TAG, "doctor Longitude " + mDoctor.getLongitude());
                            Log.d(TAG, location.toString());
                            Log.d(TAG, location.getProvider());
                            distance = distance(mCurrentLatitude, Double.parseDouble(mDoctor.getLatitude()), mCurrentLongitude, Double.parseDouble(mDoctor.getLongitude()), 0.0, 0.0);
                            Toast.makeText(ActivityGetQueue.this, Double.toString(distance), Toast.LENGTH_SHORT);
                            Log.d(TAG, Double.toString(distance));
                        }
                    }
                });


        mLocationBox = findViewById(R.id.location);
        mLocationBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:"+mDoctor.getLatitude()+","+mDoctor.getLongitude()
                        +"?z=15&q="+mDoctor.getLatitude()+","+mDoctor.getLongitude()+"(Your doctor is here!)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        mQueueNowButton = findViewById(R.id.queue_now);
        mQueueNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(distance < MAX_DISTANCE){
                    Log.d(TAG, "OnClick: called");
                    Toast.makeText(ActivityGetQueue.this, "QUEUE NOW", Toast.LENGTH_SHORT).show();
                    //POST REQUEST
                    sendPostRequest();
                } else{
                    Toast.makeText(ActivityGetQueue.this, "YOU'RE TOO FAR FROM THE LOCATION", Toast.LENGTH_SHORT).show();
                }
            }
        });
        isOnQueueRequest();
        getQueueNumber();

    }

    private void sendPostRequest() {
        try {
            String URL = "https://qtime-android.herokuapp.com/api/queue";
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("user", auth.getCurrentUser().getEmail());
            jsonBody.put("doctor", mDoctor.getId());
            jsonBody.put("done", "false");

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "request sent");
                    Toast.makeText(getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void isOnQueueRequest(){
        String URL = "https://qtime-android.herokuapp.com/api/queue/user/" + auth.getCurrentUser().getEmail();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject queue = response.getJSONObject("queue");
                    if(queue != null){
                        isInQueue = true;
                        mQueueNowButton.setEnabled(false);
                        mQueueNowButton.setText("Already in queue");
                    }else{
                        isInQueue = false;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    private void getQueueNumber(){
        String URL = "https://qtime-android.herokuapp.com/api/queue/doctor/"+mDoctor.getId();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray queues = response.getJSONArray("queues");
                    for (int i = 0; i < queues.length(); i++){
                        JSONObject queue = queues.getJSONObject(i);
                        String user = queue.getString("user");
                        if(user.equals(auth.getCurrentUser().getEmail())){
                            currentQueue = i + 1;
                            mCurrentQueueTextView = findViewById(R.id.current_queue);
                            mCurrentQueueTextView.setText("Current queue: "+currentQueue);
                            mCurrentQueueTextView.setTextColor(getResources().getColor(R.color.black));
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}
