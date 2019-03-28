package com.hmm.q_time;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        QueueFragment.OnFragmentInteractionListener, NearbyFragment.OnFragmentInteractionListener,
        MessageFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener{

    private ImageButton signOut;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private View root;
    private float maxValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        signOut = findViewById(R.id.sign_out);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        TabLayout mTablayout = findViewById(R.id.tab_layout);
        mTablayout.addTab(mTablayout.newTab().setIcon(R.drawable.ic_homepage));
        mTablayout.addTab(mTablayout.newTab().setIcon(R.drawable.ic_queue_tab));
        mTablayout.addTab(mTablayout.newTab().setIcon(R.drawable.ic_nearby_tab));
        mTablayout.addTab(mTablayout.newTab().setIcon(R.drawable.ic_message_tab));
        mTablayout.addTab(mTablayout.newTab().setIcon(R.drawable.ic_profile_tab));
        mTablayout.setTabGravity(TabLayout.GRAVITY_FILL );

        final ViewPager mViewPager = findViewById(R.id.view_pager);
        final PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTablayout.getTabCount());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTablayout));

        mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        root = findViewById(R.id.view_pager);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            Toast.makeText(this, "The device has no light sensor !", Toast.LENGTH_SHORT).show();
            finish();
        }

        // max value for light sensor
        maxValue = lightSensor.getMaximumRange();

        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float value = sensorEvent.values[0];

                // between 0 and 255
                if (value > 40 && value < 100) {
                    int newValue = (int) (255f * value / maxValue);
                    root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));
                } else if (value > 100) {
                    int newValue = (int) (255f * value / maxValue);
                    root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));
                } else {
                    int newValue = (int) (255f * value / maxValue);
                    root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightEventListener);
    }
}
