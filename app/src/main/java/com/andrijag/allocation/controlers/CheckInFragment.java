package com.andrijag.allocation.controlers;

import android.annotation.SuppressLint;
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

import com.andrijag.allocation.CheckMutation;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CheckInFragment extends Fragment implements View.OnClickListener {

    private Button goToLogin;

    public CheckInFragment() {}

//    public static CheckInFragment newInstance(String param1, String param2) {
//        CheckInFragment fragment = new CheckInFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_check_in, container, false);

        CheckMutation checkMutation = CheckMutation.builder().build();

        Storage.provideApolloClient(Objects.requireNonNull(getActivity()))
                .mutate(checkMutation)
                .enqueue(new ApolloCall.Callback<CheckMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CheckMutation.Data> response) {

//                        assert response.data() != null;
                        Log.i("CHECK", String.valueOf(response));

                        SharedPreferences pref = Objects.requireNonNull(getActivity()).getSharedPreferences("Allocation",0);


                        if (response.data()!=null){
                            Log.i("CHECK", response.data().check().token());
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = pref.edit();
                            editor.putString("token", "Bearer " + response.data().check().token());
                            editor.apply();
                            Intent intent = new Intent(getActivity(), EventsActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Fragment login = new LoginFragment();
                            if(pref.getBoolean("isRememberMe", false)){
                                login = LoginFragment.newInstance(
                                        pref.getString("username", ""),
                                        pref.getString("password", ""),
                                        pref.getBoolean("isRememberMe", false)
                                );
                            } else {
                                 login = new LoginFragment();
                            }

                            FragmentManager fragmentManager = getParentFragmentManager();
                            assert fragmentManager != null;
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            fragmentTransaction.replace(R.id.fragment_container, login);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("CHECK", e.getMessage(), e);
                    }
                });

        return view;


    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.buttonLogin:
//
//                Fragment login = new LoginFragment();
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                fragmentTransaction.replace(R.id.fragment_container, login);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//                break;

//        }
    }
}
