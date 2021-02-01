package com.andrijag.allocation.controlers;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String USER_NAME = "usr_name";
    private static final String PASSWORD = "password";
    private static final String IS_CHECKED = "is_checked";

    private String mUsername, mPassword;
    private Boolean mIsRememberMe = false;
    private EditText mEditUsername, mEditPassword;
    private CheckBox mCheckRememberMe;

    public LoginFragment() {}

    public static LoginFragment newInstance(String param1, String param2, Boolean param3) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(USER_NAME, param1);
        args.putString(PASSWORD, param2);
        args.putBoolean(IS_CHECKED, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString(USER_NAME);
            mPassword = getArguments().getString(PASSWORD);
            mIsRememberMe = getArguments().getBoolean(IS_CHECKED);
        }
//        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Allocation",0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        this.mEditUsername = view.findViewById(R.id.username);
        this.mEditPassword = view.findViewById(R.id.password);
        if(this.mUsername!=null){
            this.mEditUsername.setText(mUsername);
        }
        if(this.mPassword!=null){
            this.mEditPassword.setText(mPassword);
        }
        this.mCheckRememberMe = view.findViewById(R.id.rememberMeCheck);
        if(this.mIsRememberMe){
            this.mCheckRememberMe.setChecked(this.mIsRememberMe);
        }

        Button mLoginBtn = view.findViewById(R.id.loginBtn);
        mLoginBtn.setOnClickListener(this);

//        pref = Objects.requireNonNull(getActivity()).getSharedPreferences("Allocation", 0); // 0 - for private mode


        return view;
    }

    private void login(){

        this.mUsername = this.mEditUsername.getText().toString();
        this.mPassword = this.mEditPassword.getText().toString();
        this.mIsRememberMe = this.mCheckRememberMe.isChecked();
        if(this.mUsername.equals("") || this.mPassword.equals("")){
            Toast.makeText(getActivity(), "Please, Enter Username and Password", Toast.LENGTH_SHORT).show();
        }

        LoginMutation loginMutation = LoginMutation.builder()
                .email(this.mEditUsername.getText().toString())
                .password(this.mEditPassword.getText().toString())
                .build();

        Storage.provideApolloClient(Objects.requireNonNull(getActivity()))
                .mutate(loginMutation)
                .enqueue(new ApolloCall.Callback<LoginMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<LoginMutation.Data> response) {
//                        assert response.data() != null;

                        if(response.hasErrors()){
                            Log.i("LOCATON  ERROR", response.errors().get(0).message());
                            goToErrorFragment(response.errors().get(0).message());
                        } else {
                            Log.i("MAMAMAMAMAM", response.data().login().token());

                            SharedPreferences pref = Objects.requireNonNull(getActivity()).getSharedPreferences("Allocation",0);
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = pref.edit();
                            editor.putString("token", "Bearer " + response.data().login().token());
                            editor.putString("username", mUsername);
                            editor.putString("password", mPassword);
                            editor.putBoolean("isRememberMe", mIsRememberMe);
                            editor.apply();

                            Intent intent = new Intent(getActivity(), EventsActivity.class);
                            startActivity(intent);
                        }


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

    public void goToErrorFragment(String message) {
        Fragment errorFragment = ErrorFragment.newInstance(message);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, errorFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
