package com.andrijag.allocation.controlers;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andrijag.allocation.EventQuery;
import com.andrijag.allocation.EventStopMutation;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.MyEvent;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EventDetailsFragment extends Fragment {

//  private static final String EVENT_ID = "eventId";
//  private String eventId;
//  private TextView idText;
//  View view;
  MyEvent event;



  TextView mTime;
  TextView mLocation;
  TextView mEventTitle;
  TextView mInformation;
  Button mManualStartStopButton;



  public EventDetailsFragment() {}

  public static EventDetailsFragment newInstance() {
    EventDetailsFragment fragment = new EventDetailsFragment();
//    Bundle args = new Bundle();
//    args.putString(EVENT_ID, eventId);
//    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_event_details, container, false);
    mTime = view.findViewById(R.id.event_time);
    mEventTitle = view.findViewById(R.id.e_title);
    mLocation = view.findViewById(R.id.event_location);
    mInformation = view.findViewById(R.id.event_information);
    mManualStartStopButton = view.findViewById(R.id.manual_start_stop_btn);

    setView(Storage.get(getContext()).getEvent());



//        idText = view.findViewById(R.id.idText);
//        idText.setText(eventId);
    // Inflate the layout for this fragment
    return view;
  }

  public void setView(final MyEvent event){
    mTime.setText(event.getStart());
    mLocation.setText(event.getLocation());
    mEventTitle.setText(event.getTitle());
    mInformation.setText(event.getDescription());
    if(event.getCanStart()){
      mManualStartStopButton.setText(R.string.start);
      mManualStartStopButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            Toast.makeText(getActivity(), "STOP NOW", Toast.LENGTH_SHORT).show();
          goToManualStartStopFragment(event.getId(), true);
        }
      });
    }
    if(event.getCanStop()){
      mManualStartStopButton.setText(R.string.stop);
      mManualStartStopButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//          Toast.makeText(getActivity(), "START NOW", Toast.LENGTH_SHORT).show();
          goToManualStartStopFragment(event.getId(), false);

        }
      });
    }


  }

  public void goToManualStartStopFragment(String eventId, Boolean isStart) {
    Fragment manualStartStopFragment = ManualStartStopFragment.newInstance(eventId,isStart);
    FragmentManager fragmentManager = getParentFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.events_details_container, manualStartStopFragment);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }



}
