package com.andrijag.allocation.controlers;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrijag.allocation.EventByDateForUserQuery;
import com.andrijag.allocation.R;
import com.andrijag.allocation.controlers.dummy.DummyContent;
import com.andrijag.allocation.controlers.dummy.DummyContent.DummyItem;
import com.andrijag.allocation.models.MyEvent;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventsFragment extends Fragment {

  private OnListFragmentInteractionListener mListener;

  private TextView mDateString;
  private RecyclerView recyclerView;

  private Date currentDate;

  final List<MyEvent> events = new ArrayList<MyEvent>();

  public EventsFragment() {
  }

  @Override
  public void onResume() {
    super.onResume();
    recyclerView.setAdapter(new MyEventRecyclerViewAdapter(events, mListener));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    currentDate = new Date();
    getEvents(Storage.getDateString(currentDate, "yyyy-MM-dd"));
  }

  private void getEvents(String dateString) {
    EventByDateForUserQuery eventsQuery = EventByDateForUserQuery.builder()
      .date(dateString)
      .build();
    Storage.provideApolloClient(Objects.requireNonNull(getContext()))
      .query(eventsQuery)
      .enqueue(new ApolloCall.Callback<EventByDateForUserQuery.Data>() {
        @Override
        public void onResponse(@NotNull Response<EventByDateForUserQuery.Data> response) {
          assert response.data() != null;
//          Log.i("MY EVENTS", response.data().eventByDateForUser().toString());
          events.clear();

          for (int i = 0; i < response.data().eventByDateForUser().size(); i++) {
            MyEvent event = new MyEvent();
            event.setId(response.data().eventByDateForUser().get(i).id());
            event.setStart(Storage.convertDateTimeFormat("yyyy-MM-dd HH:mm:ss", "HH:mm", response.data().eventByDateForUser().get(i).start()));
            event.setEnd(Storage.convertDateTimeFormat("yyyy-MM-dd HH:mm:ss", "HH:mm", response.data().eventByDateForUser().get(i).end()));
            event.setTitle(response.data().eventByDateForUser().get(i).title());
            event.setLocation(response.data().eventByDateForUser().get(i).location());
            event.setClientName(response.data().eventByDateForUser().get(i).clientName());
            event.setTitle(response.data().eventByDateForUser().get(i).title());
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


        @Override
        public void onFailure(@NotNull ApolloException e) {
          Log.e("ERROR - onFailure", e.toString());
        }
      });

  }

  public void getEventsFromDay(int i) {
    currentDate = Storage.getDay(currentDate, i);
    mDateString.setText(Storage.getDateString(currentDate, "MMMM dd"));
    getEvents(Storage.getDateString(currentDate, "YYYY-MM-dd"));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    if (container != null) {
      container.removeAllViews(); // Inflate the layout for this fragment
    }

    View view = inflater.inflate(R.layout.fragment_events_list, container, false);

    ImageView mNextBtn = view.findViewById(R.id.next);
    mNextBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getEventsFromDay(1);
      }
    });

    ImageView mPrevBtn = view.findViewById(R.id.previous);
    mPrevBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getEventsFromDay(-1);
      }
    });

    mDateString = view.findViewById(R.id.dateString);
    mDateString.setText(Storage.getDateString(currentDate, "MMMM dd"));

    // Set the adapter

    recyclerView = view.findViewById(R.id.list);
//    Context context = view.getContext();
//    if (mColumnCount <= 1) {
//      recyclerView.setLayoutManager(new LinearLayoutManager(context));
//    } else {
//      recyclerView.setLayoutManager(new GridLayoutManager());
//    }

    return view;
  }


  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnListFragmentInteractionListener) {
      mListener = (OnListFragmentInteractionListener) context;
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
