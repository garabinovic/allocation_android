package com.andrijag.allocation.controlers;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrijag.allocation.LoginMutation;
//import com.andrijag.allocation.MediaQuery;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText mUsername, mPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        this.mUsername = view.findViewById(R.id.username);
        this.mPassword = view.findViewById(R.id.password);
        Button mLoginBtn = view.findViewById(R.id.loginBtn);
        mLoginBtn.setOnClickListener(this);

        return view;
    }

    private void login(){

        LoginMutation loginMutation = LoginMutation.builder()
                .email("andrija@test.com")
                .password("123")
                .build();

//        UpvotePostMutation upvotePostMutation = UpvotePostMutation.builder()
//                .votes(3)
//                .build();

        Storage.provideApolloClient()
                .mutate(loginMutation)
                .enqueue(new ApolloCall.Callback<LoginMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<LoginMutation.Data> response) {
                        assert response.data() != null;
                        Log.i("MAMAMAMAMAM", response.data().login().token());
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("MAMAMAMAMAM", e.getMessage(), e);
                    }
                });
//                .enqueue(
//                        new ApolloCall.Callback<LoginMutation.Data>(new ApolloCall.Callback<UpvotePost.Data>() {
//                            @Override
//                            public void onResponse(@NotNull Response<UpvotePost.Data> response) {
//                                Log.i(TAG, response.toString());
//                            }
//
//                            @Override
//                            public void onFailure(@NotNull ApolloException e) {
//                                Log.e(TAG, e.getMessage(), e);
//                            }
//                        }) {
//                        };
//    );

//        Storage.provideApolloClient().query(
//                MediaQuery.builder()
//                        .id(15125)
//                        .build()
//        ).enqueue(new ApolloCall.Callback<MediaQuery.Data>() {
//            @Override
//            public void onResponse(@NotNull Response<MediaQuery.Data> response) {
//                Log.e("RADOINJA", response.data().Media().title().english());
//
//                Intent intent = new Intent(getActivity(), EventsActivity.class);
//                startActivity(intent);
//
//
//            }
//
//            @Override
//            public void onFailure(@NotNull ApolloException e) {
//                Log.e("ERR RADOINJA", e.toString());
//
//            }
//
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.loginBtn:
                login();

//                Toast.makeText(getActivity(), "LOGIN, " + this.mUsername.getText(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
