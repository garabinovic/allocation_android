package com.andrijag.allocation.controlers;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrijag.allocation.EventByLocationIdQuery;
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

public class LocationEventsFragment extends Fragment {

  private EventsFragment.OnListFragmentInteractionListener mListener;

  private static final String ID_LOCATION = "idLocation";
  private String mIdLocation;
  private RecyclerView recyclerView;
  private final List<MyEvent> events = new ArrayList<MyEvent>();

  private TextView mHeaderTitle;
  private ImageView mBackArrowBtn;

  public LocationEventsFragment() {}

  public static LocationEventsFragment newInstance(String idLocation) {
    LocationEventsFragment fragment = new LocationEventsFragment();
    Bundle args = new Bundle();
    args.putString(ID_LOCATION, idLocation);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      mIdLocation = getArguments().getString(ID_LOCATION);
    }

    getLocationEvents(mIdLocation);
  }

  @Override
  public void onResume() {
    super.onResume();
    recyclerView.setAdapter(new MyEventRecyclerViewAdapter(events, mListener));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    if (container != null) {
      container.removeAllViews(); // Inflate the layout for this fragment
    }

    View view = inflater.inflate(R.layout.fragment_location_events, container, false);

    mHeaderTitle = view.findViewById(R.id.header_title);
    mHeaderTitle.setText("Location ID: "+mIdLocation);

    mBackArrowBtn = view.findViewById(R.id.back_arrow_btn);
    mBackArrowBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getActivity().finish();
      }
    });

    recyclerView = view.findViewById(R.id.list);
    Context context = view.getContext();
    recyclerView.setLayoutManager(new LinearLayoutManager(context));

    return view;
  }

  private void getLocationEvents(String id) {
    EventByLocationIdQuery eventByLocationIdQuery = EventByLocationIdQuery.builder()
      .locationId(id)
      .build();
    Storage.provideApolloClient(getContext())
      .query(eventByLocationIdQuery)
      .enqueue(new ApolloCall.Callback<EventByLocationIdQuery.Data>() {
        @Override
        public void onResponse(@NotNull Response<EventByLocationIdQuery.Data> response) {

          if (response.hasErrors()) {
            Log.i("LOCATON  ERROR", response.errors().get(0).message());
            goToErrorFragment(response.errors().get(0).message());
          } else {
            Log.i("MY EVENTS", response.data().eventByLocationId().toString());

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
              event.setTitle(response.data().eventByLocationId().get(i).title());
              events.add(event);
            }

            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
              @Override
              public void run() {
                // Stuff that updates the UI
                recyclerView.setAdapter(new MyEventRecyclerViewAdapter(events, mListener));
              }
            });

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
    FragmentManager fragmentManager = getParentFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.location_events_fragment_container, errorFragment);
    fragmentTransaction.commit();
  }


  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof EventsFragment.OnListFragmentInteractionListener) {
      mListener = (EventsFragment.OnListFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
        + " must implement OnListFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnListFragmentInteractionListener {
    void onListFragmentInteraction(MyEvent item);
  }

}
