package com.andrijag.allocation.controlers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.andrijag.allocation.EventQuery;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.MyEvent;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EventDetailsActivity extends AppCompatActivity {

  String eventId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_event_details);

    String eventId = getIntent().getStringExtra("EVENT_ID");
    getDetails(eventId);
//    goToEventDetailsFragment(eventId);

  }

  public void getDetails(String eventId) {
    EventQuery eventQuery = EventQuery.builder()
      .id(eventId)
      .build();
    Storage.provideApolloClient(Objects.requireNonNull(this))
      .query(eventQuery)
      .enqueue(new ApolloCall.Callback<EventQuery.Data>() {
        @Override
        public void onResponse(@NotNull Response<EventQuery.Data> response) {
          if (response.hasErrors()) {
            Log.i("EventStart ERROR", response.errors().get(0).message());
            goToErrorFragment(response.errors().get(0).message());
          } else {
            MyEvent event = new MyEvent();
            event.setStart(Storage.convertDateTimeFormat("yyyy-MM-dd HH:mm:ss", "HH:mm", response.data().event().start()));
            event.setEnd(Storage.convertDateTimeFormat("yyyy-MM-dd HH:mm:ss", "HH:mm", response.data().event().end()));
            event.setTitle(response.data().event().title());
            event.setLocation(response.data().event().location());
            event.setClientName(response.data().event().clientName());
            event.setId(response.data().event().id());
            event.setCanStart(response.data().event().canStart());
            event.setCanStop(response.data().event().canStop());
            Storage.get(getApplicationContext()).setEvent(event);
            goToEventDetailsFragment();
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
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.events_details_container, errorFragment);
    fragmentTransaction.commit();
  }

  public void goToEventDetailsFragment() {
    Fragment eventDetails = EventDetailsFragment.newInstance();
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.events_details_container, eventDetails);
//    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }


}
