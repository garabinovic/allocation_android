package com.andrijag.allocation.controlers;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrijag.allocation.EventStartMutation;
import com.andrijag.allocation.EventStopMutation;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ManualStartStopFragment extends Fragment {

  private static final String IS_START = "is_start";
  private static final String EVENT_ID = "event_id";

  EditText mEditDescription;
  Button mSubmit;
  Boolean mIsStart;
  String mEventId;
  String mDescription;

  public ManualStartStopFragment() {}

  public static ManualStartStopFragment newInstance(String eventId, Boolean isStart) {
    ManualStartStopFragment fragment = new ManualStartStopFragment();
    Bundle args = new Bundle();
    args.putString(EVENT_ID, eventId);
    args.putBoolean(IS_START, isStart);
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mIsStart = getArguments().getBoolean(IS_START);
      mEventId = getArguments().getString(EVENT_ID);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_manual_start_stop, container, false);
    mEditDescription = view.findViewById(R.id.description_edit_field);
    mSubmit = view.findViewById(R.id.submit_btn);
    mSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mDescription = mEditDescription.getText().toString();
        if(mIsStart){
          start();
        } else {
          stop();
        }
        Toast.makeText(getActivity(), "Manual", Toast.LENGTH_SHORT).show();
      }
    });

    return view;
  }

  public void start(){
    EventStartMutation eventStartMutation = EventStartMutation.builder()
      .id(mEventId)
      .description(mDescription)
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
            getActivity().finish();
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
      .id(mEventId)
      .description(mDescription)
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

            getActivity().finish();
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

//  public void goToEventsActivity() {
//    Intent intent = new Intent(getContext(), EventsActivity.class);
//    startActivity(intent);
//    getActivity().finish();
//
//  }



}
