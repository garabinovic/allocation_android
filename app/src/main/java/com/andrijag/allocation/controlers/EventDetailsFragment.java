package com.andrijag.allocation.controlers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrijag.allocation.EventQuery;
import com.andrijag.allocation.EventStopMutation;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EventDetailsFragment extends Fragment {

    private static final String EVENT_ID = "eventId";
    private String eventId;
    private TextView idText;
    View view;


    public EventDetailsFragment() {}

    public static EventDetailsFragment newInstance(String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(EVENT_ID);
            getDetails();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event_details, container, false);

        idText = view.findViewById(R.id.idText);
        idText.setText(eventId);
        // Inflate the layout for this fragment
        return view;
    }

    public void getDetails() {
        EventQuery eventQuery = EventQuery.builder()
                .id(eventId)
                .build();

        Storage.provideApolloClient(Objects.requireNonNull(getActivity()))
                .query(eventQuery)
                .enqueue(new ApolloCall.Callback<EventQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<EventQuery.Data> response) {
                        if(response.hasErrors()){
                            Log.i("EventStart ERROR", response.errors().get(0).message());
//                            goToErrorFragment(response.errors().get(0).message());
                        } else {
                            Log.i("EventStart", response.data().event().id());
//                            goToEventDetailsFragment(response.data().eventStop().id());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.i("onFailure EventStop", "XXXXXX");
                    }
                });
    }
}
