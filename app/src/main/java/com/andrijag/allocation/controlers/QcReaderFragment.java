package com.andrijag.allocation.controlers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andrijag.allocation.EventByLocationIdQuery;
import com.andrijag.allocation.EventsQuery;
import com.andrijag.allocation.R;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class QcReaderFragment extends Fragment {

  private static final int REQUEST_CAMERA_PERMISSION = 201;

  private SurfaceView surfaceView;
  private TextView txtBarcodeValue;
  private CameraSource cameraSource;
  private Button btnAction;
  private String intentData = "";

  private boolean isScanned = false;

  public QcReaderFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    ((EventsActivity) Objects.requireNonNull(getActivity())).scanFab.setImageResource(R.drawable.btn_2);
    ((EventsActivity) Objects.requireNonNull(getActivity())).isScanFab = false;

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_qc_reader, container, false);
    txtBarcodeValue = view.findViewById(R.id.txtBarcodeValue);
    surfaceView = view.findViewById(R.id.surfaceView);
//        btnAction = view.findViewById(R.id.btnAction);

    return view;

  }

  private void initialiseDetectorsAndSources() {

    Toast.makeText(getActivity(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

    BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getActivity())
      .setBarcodeFormats(Barcode.ALL_FORMATS)
      .build();

    cameraSource = new CameraSource.Builder(Objects.requireNonNull(getActivity()), barcodeDetector)
      .setRequestedPreviewSize(1920, 1080)
      .setAutoFocusEnabled(true) //you should add this feature
      .build();

    surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
      @Override
      public void surfaceCreated(SurfaceHolder holder) {
        try {
          if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraSource.start(surfaceView.getHolder());
          } else {
            ActivityCompat.requestPermissions(getActivity(), new
              String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
          }

        } catch (IOException e) {
          e.printStackTrace();
        }

      }

      @Override
      public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      }

      @Override
      public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
      }
    });

    barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
      @Override
      public void release() {
        Toast.makeText(getActivity(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> barcodes = detections.getDetectedItems();
        if (barcodes.size() != 0) {
//                    btnAction.setText("CHECK DATA");
          intentData = barcodes.valueAt(0).displayValue;
          try {
            JSONObject qcObj = new JSONObject(intentData);
            if (qcObj.has("locationId")) {
              if(!isScanned){
                isScanned = true;
                Log.i("REZULTAT", qcObj.getString("locationId"));
                Intent intent = new Intent(getContext(), LocationEventsActivity.class);
                intent.putExtra("LOCATION_ID", qcObj.getString("locationId"));
                startActivity(intent);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
//              Objects.requireNonNull(getActivity()).finish();
              }


//                            getEventByLocationId(qcObj.getString("locationId"));

            } else {
              goToErrorFragment("No Location ID on QC");
            }

          } catch (JSONException e) {
            e.printStackTrace();
          }
//                    txtBarcodeValue.setText(intentData);
          // ovde se salje locationid da se dobije ebent alo ga ima
          // ukoliko se dobije event otbaraju se detalji sa aktivnim start dugmetom
          // ukoliko se dobije greska otbara se fragment za gresku sa ofgovarajucim tekstom
        }
      }
    });
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initialiseDetectorsAndSources();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    cameraSource.release();
    ((EventsActivity) Objects.requireNonNull(getActivity())).scanFab.setImageResource(R.drawable.btn);
    ((EventsActivity) Objects.requireNonNull(getActivity())).isScanFab = true;

  }

//    public void getEventByLocationId(String id){
//        EventByLocationIdQuery eventByLocationIdQuery = EventByLocationIdQuery.builder()
//                .locationId(id)
//                .build();
//        Storage.provideApolloClient(Objects.requireNonNull(getActivity()))
//                .query(eventByLocationIdQuery)
//                .enqueue(new ApolloCall.Callback<EventByLocationIdQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<EventByLocationIdQuery.Data> response) {
//                        if(response.hasErrors()){
//                            Log.i("LOCATON  ERROR", response.errors().get(0).message());
//                            goToErrorFragment(response.errors().get(0).message());
//                        } else {
//                            Log.i("LOCATON ID RESPONSE", response.data().eventByLocationId().location());
//
//                            goToStartStopFragment(
//                                    response.data().eventByLocationId().id(),
//                                    response.data().eventByLocationId().canStart(),
//                                    response.data().eventByLocationId().canStop()
//                                    );
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//                        Log.i("ERROR ID RESPONSE", e.getMessage());
//
//                    }
//                });
//
//    }

  public void goToErrorFragment(String message) {
    Fragment errorFragment = ErrorFragment.newInstance(message);
    FragmentManager fragmentManager = getParentFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    fragmentTransaction.replace(R.id.events_fragment_container, errorFragment);
//        fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }

//  public void goToStartStopFragment(String id, Boolean canStart, Boolean canStop) {
//    Fragment startStopFragment = StartStopEventFragment.newInstance(id, canStart, canStop);
//    FragmentManager fragmentManager = getParentFragmentManager();
//    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//    fragmentTransaction.replace(R.id.events_fragment_container, startStopFragment);
////        fragmentTransaction.addToBackStack(null);
//    fragmentTransaction.commit();
//
//  }


}
