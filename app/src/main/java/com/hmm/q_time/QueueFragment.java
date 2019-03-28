package com.hmm.q_time;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QueueFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QueueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QueueFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "QueueFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private ImageButton mButtonDone;
    private ImageButton mButtonCancel;
    private TextView mNameTextView;
    private TextView mCurrentQueue;

    private String doctorId;
    private int currentQueue;
    private Boolean isInQueue;
    private String mIdQueue;

    public QueueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QueueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QueueFragment newInstance(String param1, String param2) {
        QueueFragment fragment = new QueueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        isOnQueueRequest();
    }

    @Override
    public void onResume() {
        super.onResume();
        isOnQueueRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_queue, container, false);
        mButtonDone = v.findViewById(R.id.done);
        mButtonDone.setEnabled(false);
        mButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Done Request
                doneQueueRequest();
                Toast.makeText(getContext(),"Thanks for your patient",Toast.LENGTH_SHORT).show();
            }
        });
        mButtonCancel = v.findViewById(R.id.cancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel Request
                Toast.makeText(getContext(),"CANCEL",Toast.LENGTH_SHORT).show();
                doneQueueRequest();
            }
        });
        mNameTextView = v.findViewById(R.id.doctor_detail_name);
        mCurrentQueue = v.findViewById(R.id.doctor_detail_current_queue);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void isOnQueueRequest(){
        String URL = "https://qtime-android.herokuapp.com/api/queue/user/" + auth.getCurrentUser().getEmail();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject queue = response.getJSONObject("queue");
                    mIdQueue = queue.getString("_id");
                    doctorId = queue.getString("doctor");
                    mNameTextView.setText(doctorId);
                    if(queue != null){
                        isInQueue = true;
                        getQueueNumber();
                    }else{
                        isInQueue = false;
                        mButtonCancel.setEnabled(false);
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
        Volley.newRequestQueue(getContext()).add(request);
    }

    private void getQueueNumber(){
        String URL = "https://qtime-android.herokuapp.com/api/queue/doctor/"+ doctorId;
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
                            mCurrentQueue.setText("Current queue: "+currentQueue);
                            mCurrentQueue.setTextColor(getResources().getColor(R.color.black));
                            if(currentQueue == 1){
                                mButtonDone.setEnabled(true);
                            }
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
        Volley.newRequestQueue(getContext()).add(request);
    }

    private void doneQueueRequest(){
        try {
            String URL = "https://qtime-android.herokuapp.com/api/queue/done";
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("_id", mIdQueue);
            jsonBody.put("done", "true");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(QueueFragment.this).attach(QueueFragment.this).commit();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            Volley.newRequestQueue(getContext()).add(request);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

}
