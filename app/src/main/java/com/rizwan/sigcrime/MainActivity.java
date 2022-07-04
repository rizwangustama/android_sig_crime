package com.rizwan.sigcrime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rizwan.sigcrime.report.ReportActivity;
import com.rizwan.sigcrime.utilities.UtilClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
		LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private static final String TAG = MainActivity.class.getSimpleName();

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;


	private FragmentManager fragmentManager;
	HomeFragment homeFragment;
	ImageButton btnHome, btnMap, btnPost, btnAccount;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PermissionListener permissionlistener = new PermissionListener() {
			@Override
			public void onPermissionGranted() {
//				Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPermissionDenied(List<String> deniedPermissions) {
				Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
			}
		};

		TedPermission.create()
				.setPermissionListener(permissionlistener)
				.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
				.setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION)
				.check();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				// The next two lines tell the new client that “this” current class will handle connection stuff
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				//fourth line adds the LocationServices API endpoint from GooglePlayServices
				.addApi(LocationServices.API)
				.build();

		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(10 * 1000)        // 10 seconds, in milliseconds
				.setFastestInterval(1 * 1000); // 1 second, in milliseconds

		fragmentManager = getSupportFragmentManager();
		btnHome = findViewById(R.id.home_button);
		btnMap = findViewById(R.id.map_button);
		btnPost = findViewById(R.id.post_button);
		btnAccount = findViewById(R.id.acount_button);

		btnHome.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
		btnMap.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey_40), PorterDuff.Mode.SRC_IN);
		btnPost.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey_40), PorterDuff.Mode.SRC_IN);
		btnAccount.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.grey_40), PorterDuff.Mode.SRC_IN);

		btnHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		btnMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
				MainActivity.this.startActivity(mapIntent);
			}
		});

		btnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (UtilClass.userLogin.getUser_verified().equals("true") || UtilClass.userLogin.getUser_verified().equals("True")) {
						Intent intent = new Intent(MainActivity.this, ReportActivity.class);
						finishAffinity();
						startActivity(intent);
					} else {
						Toast.makeText(MainActivity.this, "Anda belum bisa membuat post berita", Toast.LENGTH_SHORT).show();
					}
				}catch (Exception ex){
					Toast.makeText(MainActivity.this, "Anda belum bisa membuat post berita", Toast.LENGTH_SHORT).show();
				}
			}
		});

		btnAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AccountActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});

		initFragment();
	}

	private void initFragment() {
		homeFragment = new HomeFragment();
		fragmentManager.beginTransaction().replace(R.id.mfragment, homeFragment);
		btnHome.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onLocationChanged(@NonNull Location location) {
		UtilClass.currentLatitude = String.valueOf(location.getLatitude());
		UtilClass.currentLongitude = String.valueOf(location.getLongitude());
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

		if (location == null) {
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

		} else {
			//If everything went fine lets get latitude and longitude
			UtilClass.currentLatitude = String.valueOf(location.getLatitude());
			UtilClass.currentLongitude = String.valueOf(location.getLongitude());
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
		}
	}
}