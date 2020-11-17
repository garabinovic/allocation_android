package com.andrijag.allocation.controlers;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrijag.allocation.R;
import com.andrijag.allocation.controlers.EventsFragment.OnListFragmentInteractionListener;
import com.andrijag.allocation.controlers.dummy.DummyContent;
import com.andrijag.allocation.controlers.dummy.DummyContent.DummyItem;
import com.andrijag.allocation.models.MyEvent;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {

    private final List<MyEvent> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyEventRecyclerViewAdapter(List<MyEvent> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String time = mValues.get(position).getFrom()+"-"+mValues.get(position).getTo();
        holder.mTime.setText(time);
        holder.mEventTitle.setText(mValues.get(position).getTitle());
        holder.mClientName.setText(mValues.get(position).getClientName());
        holder.mLocation.setText(mValues.get(position).getLocation());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTime;
        public final TextView mEventTitle;
        public final TextView mClientName;
        public final TextView mLocation;
        public MyEvent mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTime = (TextView) view.findViewById(R.id.event_time);
            mEventTitle = (TextView) view.findViewById(R.id.event_title);
            mClientName = (TextView) view.findViewById(R.id.event_client_name);
            mLocation = (TextView) view.findViewById(R.id.event_location);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mEventTitle.getText() + "'";
        }
    }
}
