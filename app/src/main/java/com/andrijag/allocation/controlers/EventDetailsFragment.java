package com.andrijag.allocation.controlers;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrijag.allocation.R;
import com.andrijag.allocation.models.MyEvent;
import com.andrijag.allocation.models.Storage;

public class EventDetailsFragment extends Fragment {

  MyEvent event;

  private TextView mTime;
  private TextView mLocation;
  private TextView mTitle;
  private TextView mInformation;
  private TextView mHeaderTitle;
  private ImageView mBackArrowBtn;
  private Button mManualStartStopButton;


  public EventDetailsFragment() {
  }

  public static EventDetailsFragment newInstance() {
    return new EventDetailsFragment();
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
    mLocation = view.findViewById(R.id.event_location);
    mInformation = view.findViewById(R.id.event_information);
    mManualStartStopButton = view.findViewById(R.id.manual_start_stop_btn);
    mTitle = view.findViewById(R.id.event_title);
    mHeaderTitle = view.findViewById(R.id.header_title);

    mBackArrowBtn = view.findViewById(R.id.back_arrow_btn);
    mBackArrowBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getActivity().finish();
      }
    });

    setView(Storage.get(getContext()).getEvent());

    return view;
  }

  public void setView(final MyEvent event) {
    mTime.setText(event.getStart());
    mLocation.setText(event.getLocation());
    mTitle.setText(event.getTitle());
    mInformation.setText(event.getDescription());
    mTitle.setText(event.getTitle());
    mHeaderTitle.setText(event.getLocation()+" - "+event.getTitle());
    if (event.getCanStart()) {
      mManualStartStopButton.setText(R.string.start);
      mManualStartStopButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          goToManualStartStopFragment(event.getId(), true);
        }
      });
    }
    if (event.getCanStop()) {
      mManualStartStopButton.setText(R.string.stop);
      mManualStartStopButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          goToManualStartStopFragment(event.getId(), false);

        }
      });
    }


  }

  public void goToManualStartStopFragment(String eventId, Boolean isStart) {
    Fragment manualStartStopFragment = ManualStartStopFragment.newInstance(eventId, isStart);
    FragmentManager fragmentManager = getParentFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.events_details_container, manualStartStopFragment);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }


}
