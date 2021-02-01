package com.andrijag.allocation.controlers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrijag.allocation.R;

public class ErrorFragment extends Fragment {


    private static final String ERROR_MESSAGE = "errorMessage";

    private String message;
    private TextView msgText;

    public ErrorFragment() {}

    public static ErrorFragment newInstance(String message) {
        ErrorFragment fragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString(ERROR_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(ERROR_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        msgText = view.findViewById(R.id.msgText);
        msgText.setText(message);
        return view;
    }
}