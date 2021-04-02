package com.andrijag.allocation.controlers;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.MyEvent;


public class LocationEventsActivity extends AppCompatActivity implements EventsFragment.OnListFragmentInteractionListener {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location_events);

    String locationId = getIntent().getStringExtra("LOCATION_ID");

    Fragment locationEventFragment = LocationEventsFragment.newInstance(locationId);
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(R.id.location_events_fragment_container, locationEventFragment);
    fragmentTransaction.commit();
  }


  @Override
  public void onListFragmentInteraction(MyEvent item) {

      goToStartStopFragment(item.getId(), item.getCanStart(), item.getCanStop());
  }

  public void goToStartStopFragment(String id, Boolean canStart, Boolean canStop) {
    Fragment startStopFragment = StartStopEventFragment.newInstance(id, canStart, canStop);
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.location_events_fragment_container, startStopFragment);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }
}
