package com.andrijag.allocation.controlers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.andrijag.allocation.EventByLocationIdQuery;
import com.andrijag.allocation.EventsQuery;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.MyEvent;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocationEventsActivity extends AppCompatActivity implements EventsFragment.OnListFragmentInteractionListener {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location_events);

    String locationId = getIntent().getStringExtra("LOCATION_ID");

    getLocationEvents(locationId);

  }

  private void getLocationEvents(String id) {
//    ovde zvati evete za predmetnu lokaciju
    EventByLocationIdQuery eventByLocationIdQuery = EventByLocationIdQuery.builder()
      .locationId(id)
      .build();
    Storage.provideApolloClient(this)
      .query(eventByLocationIdQuery)
      .enqueue(new ApolloCall.Callback<EventByLocationIdQuery.Data>() {
        @Override
        public void onResponse(@NotNull Response<EventByLocationIdQuery.Data> response) {

          if (response.hasErrors()) {
            Log.i("LOCATON  ERROR", response.errors().get(0).message());
            goToErrorFragment(response.errors().get(0).message());
          } else {
            Log.i("MY EVENTS", response.data().eventByLocationId().toString());
            List<MyEvent> events = new ArrayList<MyEvent>();
            ;
            for (int i = 0; i < response.data().eventByLocationId().size(); i++) {
              MyEvent event = new MyEvent();

              event.setStart(Storage.convertDateTimeFormat("yyyy-MM-dd HH:mm:ss", "HH:mm", response.data().eventByLocationId().get(i).start()));
              event.setEnd(Storage.convertDateTimeFormat("yyyy-MM-dd HH:mm:ss", "HH:mm", response.data().eventByLocationId().get(i).end()));
              event.setTitle(response.data().eventByLocationId().get(i).title());
              event.setLocation(response.data().eventByLocationId().get(i).location());
              event.setClientName(response.data().eventByLocationId().get(i).clientName());
              event.setId(response.data().eventByLocationId().get(i).id());
              event.setCanStart(response.data().eventByLocationId().get(i).canStart());
              event.setCanStop(response.data().eventByLocationId().get(i).canStop());
//              Log.i("MY ZZZZ", String.valueOf(response.data().eventByLocationId().get(i)));
              events.add(event);
            }
            Storage.get(getApplicationContext()).setMyEvents(events);
//                        Log.i("EVO IHHHHHHH", events.toString());

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.events_fragment_container);

            if (fragment == null) {
              fragment = new EventsFragment();
              fm.beginTransaction()
                .add(R.id.events_fragment_container, fragment)
                .commit();
            }
          }

        }

        @Override
        public void onFailure(@NotNull ApolloException e) {
          Log.i("ERROR ID RESPONSE", Objects.requireNonNull(e.getMessage()));
        }
      });

  }

  public void goToErrorFragment(String message) {
    Fragment errorFragment = ErrorFragment.newInstance(message);
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.events_fragment_container, errorFragment);
//        fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }

//  @Override
//  public void onBackPressed() {
//    finish();
//  }

  @Override
  public void onListFragmentInteraction(MyEvent item) {

      goToStartStopFragment(item.getId(), item.getCanStart(), item.getCanStop());
  }

  public void goToStartStopFragment(String id, Boolean canStart, Boolean canStop) {

//    Toast.makeText(this, id.toString(), Toast.LENGTH_SHORT).show();
    Fragment startStopFragment = StartStopEventFragment.newInstance(id, canStart, canStop);
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    fragmentTransaction.replace(R.id.events_fragment_container, startStopFragment);
//        fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();

  }
}
