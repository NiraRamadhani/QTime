package com.hmm.q_time;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DoctorListActivity extends AppCompatActivity {
    private static final String TAG = "ListDoctorActivity";
    private RequestQueue mQueue;
    //vars
    private ArrayList<String> mDoctorNames = new ArrayList<>();
    private ArrayList<Integer> mDoctorImages = new ArrayList<>();
    private ArrayList<Doctor> mDoctors = new ArrayList<>();
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "onCreate: started.");
        initSeedDoctor();


    }

    private void initSeedDoctor(){
        mQueue = Volley.newRequestQueue(this);
        jsonParse();

        Log.d(TAG, "initSeedDoctorCalled");
    }

    private void jsonParse(){
        String url = "https://qtime-android.herokuapp.com/api/doctors";
        Log.d(TAG, "jsonParse: called");

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("doctors");
                            Log.d(TAG, "Length: " + jsonArray.length());
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject doctor = jsonArray.getJSONObject(i);
                                String id = doctor.getString("_id");
                                String name = doctor.getString("name");
                                String type = doctor.getString("type");
                                JSONObject location = doctor.getJSONObject("location");
                                String latitude = location.getString("latitude");
                                String longitude = location.getString("longitude");
                                String imageUrl = doctor.getString("image");
                                Doctor newDoctor = new Doctor(id, name, type, latitude, longitude, imageUrl);
                                mDoctors.add(newDoctor);
                                Log.d(TAG, name + ", " + type + ", " + location + ", " + imageUrl);
                            }
                            spinner.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        initRecyclerView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.doctor_recycler_view);
        DoctorAdapter adapter = new DoctorAdapter(this, mDoctors);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
