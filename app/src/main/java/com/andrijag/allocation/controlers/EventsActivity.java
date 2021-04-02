package com.andrijag.allocation.controlers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrijag.allocation.BuildConfig;
import com.andrijag.allocation.EventByDateForUserQuery;
import com.andrijag.allocation.EventsQuery;
import com.andrijag.allocation.LoginMutation;
import com.andrijag.allocation.R;
import com.andrijag.allocation.controlers.dummy.DummyContent;
import com.andrijag.allocation.models.MyEvent;
import com.andrijag.allocation.models.Storage;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.sql.Types.TIMESTAMP;

public class EventsActivity extends AppCompatActivity implements EventsFragment.OnListFragmentInteractionListener {

  //////////////////////////////////////

  private static final String TAG = EventsActivity.class.getSimpleName();

  // location last updated time
  private String mLastUpdateTime;

  // location updates interval - 20sec
  private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 20000;

  // fastest updates interval - 10 sec
  // location updates will be received if another app is requesting the locations
  // than your app can handle
  private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

  private static final int REQUEST_CHECK_SETTINGS = 100;

  // bunch of location related apis
  private FusedLocationProviderClient mFusedLocationClient;
  private SettingsClient mSettingsClient;
  private LocationRequest mLocationRequest;
  private LocationSettingsRequest mLocationSettingsRequest;
  private LocationCallback mLocationCallback;
  private Location mCurrentLocation;

  // boolean flag to toggle the ui
  private Boolean mRequestingLocationUpdates;

  public FloatingActionButton scanFab;
  public Boolean isScanFab = true;
  public BottomAppBar bottomAppBar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_events);

    scanFab = findViewById(R.id.scanFab);
    scanFab.setImageResource(R.drawable.btn);

    scanFab.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (isScanFab) {
          openScanner();
        } else {
          getSupportFragmentManager().popBackStackImmediate();
          Toast.makeText(getApplicationContext(), "close", Toast.LENGTH_SHORT).show();

        }
      }
    });

    bottomAppBar = findViewById(R.id.bar);

    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.events_fragment_container);

    if (fragment == null) {
      fragment = new EventsFragment();
      fm.beginTransaction()
        .add(R.id.events_fragment_container, fragment)
        .commit();
    }


//    // LOCATION FUNCTIONALITY !!!!!!!!!!!!!!!!!!
//    // DON't DELETE THIS
//    // UNCOMMENT ON RESUME and ON PAUSE and ON SAVE INSTANT STATES TOO
//    // initialize the necessary libraries
//    init();
//
//    // restore the values from saved instance state
//    restoreValuesFromBundle(savedInstanceState);
  }

  private void init() {
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    mSettingsClient = LocationServices.getSettingsClient(this);

    mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        // location is received
        mCurrentLocation = locationResult.getLastLocation();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        updateLocationUI();
      }
    };

    mRequestingLocationUpdates = false;

    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
    mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
    builder.addLocationRequest(mLocationRequest);
    mLocationSettingsRequest = builder.build();

    startLocation();
  }

  /**
   * Restoring values from saved instance state
   */
  private void restoreValuesFromBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey("is_requesting_updates")) {
        mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
      }

      if (savedInstanceState.containsKey("last_known_location")) {
        mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
      }

      if (savedInstanceState.containsKey("last_updated_on")) {
        mLastUpdateTime = savedInstanceState.getString("last_updated_on");
      }
    }

    updateLocationUI();
  }


  /**
   * Update the UI displaying the location data
   * and toggling the buttons
   */
  private void updateLocationUI() {
    if (mCurrentLocation != null) {
//            Toast.makeText(this, "Lat: " + mCurrentLocation.getLatitude() + ", " +
//                    "Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();


//            txtLocationResult.setText(
//                    "Lat: " + mCurrentLocation.getLatitude() + ", " +
//                            "Lng: " + mCurrentLocation.getLongitude()
//            );

//            // giving a blink animation on TextView
//            txtLocationResult.setAlpha(0);
//            txtLocationResult.animate().alpha(1).setDuration(300);
//
//            // location last updated time
//            txtUpdatedOn.setText("Last updated on: " + mLastUpdateTime);
    }
  }

//  @Override
//  public void onSaveInstanceState(@NotNull Bundle outState) {
//    super.onSaveInstanceState(outState);
//    outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
//    outState.putParcelable("last_known_location", mCurrentLocation);
//    outState.putString("last_updated_on", mLastUpdateTime);
//
//  }


  /**
   * Starting location updates
   * Check whether location settings are satisfied and then
   * location updates will be requested
   */
  private void startLocationUpdates() {
    mSettingsClient
      .checkLocationSettings(mLocationSettingsRequest)
      .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
        @SuppressLint("MissingPermission")
        @Override
        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
          Log.i(TAG, "All location settings are satisfied.");

          Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

          //noinspection MissingPermission
          mFusedLocationClient.requestLocationUpdates(mLocationRequest,
            mLocationCallback, Looper.myLooper());

          updateLocationUI();
        }
      })
      .addOnFailureListener(this, new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          int statusCode = ((ApiException) e).getStatusCode();
          switch (statusCode) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
              Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                "location settings ");
              try {
                // Show the dialog by calling startResolutionForResult(), and check the
                // result in onActivityResult().
                ResolvableApiException rae = (ResolvableApiException) e;
                rae.startResolutionForResult(EventsActivity.this, REQUEST_CHECK_SETTINGS);
              } catch (IntentSender.SendIntentException sie) {
                Log.i(TAG, "PendingIntent unable to execute request.");
              }
              break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
              String errorMessage = "Location settings are inadequate, and cannot be " +
                "fixed here. Fix in Settings.";
              Log.e(TAG, errorMessage);

              Toast.makeText(EventsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
          }

          updateLocationUI();
        }
      });
  }

  public void startLocation() {
    // Requesting ACCESS_FINE_LOCATION using Dexter library
    Dexter.withActivity(this)
      .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
      .withListener(new PermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {
          mRequestingLocationUpdates = true;
          startLocationUpdates();
        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse response) {
          if (response.isPermanentlyDenied()) {
            // open device settings when the permission is
            // denied permanently
            openSettings();
          }
        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
          token.continuePermissionRequest();
        }
      }).check();
  }

  public void stopLocation() {
    mRequestingLocationUpdates = false;
    stopLocationUpdates();
  }

  public void stopLocationUpdates() {
    // Removing location updates
    mFusedLocationClient
      .removeLocationUpdates(mLocationCallback)
      .addOnCompleteListener(this, new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
          Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
        }
      });
  }

  public void showLastKnownLocation() {
    if (mCurrentLocation != null) {
      Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
        + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // Check for the integer request code originally supplied to startResolutionForResult().
    if (requestCode == REQUEST_CHECK_SETTINGS) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          Log.e(TAG, "User agreed to make required location settings changes.");
          // Nothing to do. startLocationupdates() gets called in onResume again.
          break;
        case Activity.RESULT_CANCELED:
          Log.e(TAG, "User chose not to make required location settings changes.");
          mRequestingLocationUpdates = false;
          break;
      }
    }
  }

  private void openSettings() {
    Intent intent = new Intent();
    intent.setAction(
      Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package",
      BuildConfig.APPLICATION_ID, null);
    intent.setData(uri);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

//  @Override
//  public void onResume() {
//    super.onResume();
//
//    // Resuming location updates depending on button state and
//    // allowed permissions
//    if (mRequestingLocationUpdates && checkPermissions()) {
//      startLocationUpdates();
//    }
//    updateLocationUI();
//  }

  private boolean checkPermissions() {
    int permissionState = ActivityCompat.checkSelfPermission(this,
      Manifest.permission.ACCESS_FINE_LOCATION);
    return permissionState == PackageManager.PERMISSION_GRANTED;
  }


//  @Override
//  protected void onPause() {
//    super.onPause();
//
//    if (mRequestingLocationUpdates) {
//      // pausing location updates
//      stopLocationUpdates();
//    }
//  }

  public void openScanner() {
    Fragment qcr = new QcReaderFragment();
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.events_fragment_container, qcr);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();

  }

  @Override
  public void onListFragmentInteraction(MyEvent item) {
    goToEventDetailsActivity(item.getId());
  }

  public void goToEventDetailsActivity(String id) {
    Intent intent = new Intent(this, EventDetailsActivity.class);
    intent.putExtra("EVENT_ID", id);
    startActivity(intent);
  }
}

