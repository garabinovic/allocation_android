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
import android.widget.Toast;

import com.andrijag.allocation.EventStartMutation;
import com.andrijag.allocation.EventStopMutation;
import com.andrijag.allocation.LoginMutation;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StartStopEventFragment extends Fragment {

    private static final String EVENT_ID = "eventId";
    private static final String CAN_START = "canStart";
    private static final String CAN_STOP = "canStop";

    private String eventId;
    private Boolean canStart;
    private Boolean canStop;

    View view;
    Button button;

    public StartStopEventFragment() {
        // Required empty public constructor
    }

    public static StartStopEventFragment newInstance(String eventId, Boolean canStart, Boolean canStop) {
        StartStopEventFragment fragment = new StartStopEventFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_ID, eventId);
        args.putBoolean(CAN_START, canStart);
        args.putBoolean(CAN_STOP, canStop);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(EVENT_ID);
            canStart = getArguments().getBoolean(CAN_START);
            canStop = getArguments().getBoolean(CAN_STOP);
        }

        ((EventsActivity) Objects.requireNonNull(getActivity())).scanFab.hide();
        ((EventsActivity) Objects.requireNonNull(getActivity())).bottomAppBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(container!=null){
            container.removeAllViews(); // Inflate the layout for this fragment
        }

        view = inflater.inflate(R.layout.fragment_start_stop_event, container, false);

        button = view.findViewById(R.id.startBtn);
        if(canStart){
            button.setText(R.string.start);
        }
        if(canStop){
            button.setText(R.string.stop);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(canStart) {
                    start();
                } else if(canStop){
                    stop();
                } else {
                    Toast.makeText(getActivity(), "ALL FALSE", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((EventsActivity) Objects.requireNonNull(getActivity())).scanFab.show();
        ((EventsActivity) Objects.requireNonNull(getActivity())).bottomAppBar.setVisibility(View.VISIBLE);
    }

    public void start(){
        EventStartMutation eventStartMutation = EventStartMutation.builder()
                .id(eventId)
                .build();

        Storage.provideApolloClient(Objects.requireNonNull(getActivity()))
                .mutate(eventStartMutation)
                .enqueue(new ApolloCall.Callback<EventStartMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<EventStartMutation.Data> response) {
                        if(response.hasErrors()){
                            Log.i("EventStart ERROR", response.errors().get(0).message());
                            goToErrorFragment(response.errors().get(0).message());
                        } else {
                            Log.i("EventStart", response.data().eventStart().id());
                            goToEventDetailsFragment(response.data().eventStart().id());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.i("onFailure EventStart", "XXXXXX");
                    }
                });
    }

    public void stop(){
        EventStopMutation eventStoptMutation = EventStopMutation.builder()
                .id(eventId)
                .build();

        Storage.provideApolloClient(Objects.requireNonNull(getActivity()))
                .mutate(eventStoptMutation)
                .enqueue(new ApolloCall.Callback<EventStopMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<EventStopMutation.Data> response) {
                        if(response.hasErrors()){
                            Log.i("EventStart ERROR", response.errors().get(0).message());
                            goToErrorFragment(response.errors().get(0).message());
                        } else {
                            Log.i("EventStart", response.data().eventStop().id());
                            goToEventDetailsFragment(response.data().eventStop().id());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.i("onFailure EventStop", "XXXXXX");
                    }
                });
    }

    public void goToErrorFragment(String message) {
        Fragment errorFragment = ErrorFragment.newInstance(message);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.events_fragment_container, errorFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void goToEventDetailsFragment(String id) {
        Fragment eventDetails = EventDetailsFragment.newInstance(id);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.events_fragment_container, eventDetails);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}