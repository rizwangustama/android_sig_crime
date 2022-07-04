package com.rizwan.sigcrime.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.rizwan.sigcrime.MainActivity;
import com.rizwan.sigcrime.R;
import com.rizwan.sigcrime.dao.Kecamatan;
import com.rizwan.sigcrime.dao.KlasifikasiKriminal;
import com.rizwan.sigcrime.dao.Kriminal;
import com.rizwan.sigcrime.utilities.UtilClass;
import com.rizwan.sigcrime.utilities.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.rizwan.sigcrime.utilities.UtilClass.userLogin;

public class ReportActivity extends AppCompatActivity implements
		LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
	private static String TAG="ReportActivity";

	ProgressDialog progressDialog;

	Spinner spinnerjk, spinnerkecamatan;
	TextView textViewDate;
	TextInputEditText post_title,post_content;
	AppCompatButton button;
	ImageButton buttonClose;
	private ConstraintLayout constraintLayout;

	List<Kecamatan> kecamatanList =new ArrayList<>();
	List<String> kecamatanNameList =new ArrayList<>();
	int kecamatanIndex = -1;

	List<KlasifikasiKriminal> klasifikasiKriminalList =new ArrayList<>();
	List<String> klasifikasiKriminalNameList =new ArrayList<>();
	int klasifikasiKriminalIndex = -1;

	Kriminal postkriminal;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);

		PermissionListener permissionlistener = new PermissionListener() {
			@Override
			public void onPermissionGranted() {
//				Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPermissionDenied(List<String> deniedPermissions) {
				Toast.makeText(ReportActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(ReportActivity.this, MainActivity.class);
				startActivity(intent);
				finishAffinity();
			}
		};

		TedPermission.create()
				.setPermissionListener(permissionlistener)
				.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
				.setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		String currentDateandTime = sdf.format(new Date());

		progressDialog= new ProgressDialog(this);
		progressDialog.setTitle("Post Berita");
		progressDialog.setMessage("Menyimpan data to database");
		progressDialog.setCanceledOnTouchOutside(false);

		constraintLayout = findViewById(R.id.report_container);

		spinnerjk = findViewById(R.id.spinner);
		spinnerkecamatan = findViewById(R.id.spinner2);
		textViewDate = findViewById(R.id.post_date);
		post_title = findViewById(R.id.et_post_title);
		post_content = findViewById(R.id.et_post);
		button = findViewById(R.id.button_posting);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!progressDialog.isShowing()) progressDialog.show();

				postkriminal = new Kriminal();
				postkriminal.setTitle(post_title.getText().toString());
				postkriminal.setKasus_kriminal(post_content.getText().toString());
				postkriminal.setId_kecamatan(kecamatanList.get(kecamatanIndex).getId());
				postkriminal.setId_klassifikasi_kriminal(klasifikasiKriminalList.get(klasifikasiKriminalIndex).getId());
				postkriminal.setTanggal_kejadian(UtilClass.stringToDate(currentDateandTime,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"));
				postkriminal.setWaktu_kejadian(UtilClass.stringToDate(currentDateandTime,"yyyy-MM-dd HH:mm:ss","HH:mm:ss"));
				postkriminal.setCreate_by(userLogin.getId());
				postkriminal.setCreated(currentDateandTime);
				postkriminal.setAlamat("");
				postkriminal.setMap_lang(UtilClass.currentLongitude);
				postkriminal.setMap_lat(UtilClass.currentLatitude);
				postkriminal.setIs_verified("false");


				String serverURL= UtilClass.serverUri + "Assets/api/appscript/postnews/addnewpost.php";
				StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						if (progressDialog.isShowing()) progressDialog.dismiss();
						Log.i(TAG, "onResponse: " + response);
						boolean result=false;
						String msg = "";
						try {
							JSONObject jsonObject = new JSONObject(response);

							result = jsonObject.getBoolean("result");
							msg = jsonObject.getString("msg");
							UtilClass.NewsCreatID = jsonObject.getString("lastID");

							if (result ){

								Dialog successDialog =new Dialog(ReportActivity.this);
								successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
								successDialog.setContentView(R.layout.create_post_success_layout);
								successDialog.setCancelable(false);

								WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
								lp.copyFrom(successDialog.getWindow().getAttributes());
								lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
								lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

								((AppCompatButton) successDialog.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										successDialog.dismiss();
										Intent intent = new Intent(ReportActivity.this, MainActivity.class);
										startActivity(intent);
										finishAffinity();
									}
								});


								((AppCompatButton) successDialog.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										successDialog.dismiss();
										Intent intent = new Intent(ReportActivity.this, ReportGalleryActivity.class);
										startActivity(intent);
									}
								});

								successDialog.show();

							}else{
								Dialog failedDialog =new Dialog(ReportActivity.this);
								failedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
								failedDialog.setContentView(R.layout.create_post_failed_layout);
								failedDialog.setCancelable(false);

								WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
								lp.copyFrom(failedDialog.getWindow().getAttributes());
								lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
								lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

								((AppCompatButton) failedDialog.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										failedDialog.dismiss();
									}
								});

								failedDialog.show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							if (progressDialog.isShowing()) progressDialog.dismiss();
							Snackbar snackbar=Snackbar.make(ReportActivity.this,constraintLayout,
									e.getMessage(),Snackbar.LENGTH_SHORT);
							snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
							snackbar.show();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d(TAG, "onErrorResponse: " + error.getMessage());
						if (progressDialog.isShowing()) progressDialog.dismiss();
						Snackbar snackbar=Snackbar.make(ReportActivity.this,constraintLayout,
								error.getMessage(),Snackbar.LENGTH_SHORT);
						snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
						snackbar.show();
					}
				}){
					@Override
					protected Map<String,String> getParams(){
						Map<String,String> params = new HashMap<String, String>();
						params.put("id_kecamatan",String.valueOf(postkriminal.getId_kecamatan()));
						params.put("id_klassifikasi_kriminal",String.valueOf(postkriminal.getId_klassifikasi_kriminal()));
						params.put("kasus_kriminal", postkriminal.getKasus_kriminal());
						params.put("tanggal_kejadian",postkriminal.getTanggal_kejadian());
						params.put("waktu_kejadian",postkriminal.getWaktu_kejadian());
						params.put("alamat",postkriminal.getAlamat());
						params.put("map_lat", postkriminal.getMap_lat());
						params.put("map_lang",postkriminal.getMap_lang());
						params.put("create_by",String.valueOf(postkriminal.getCreate_by()));
						params.put("created", postkriminal.getCreated());
						params.put("is_verified", postkriminal.getIs_verified());
						params.put("title",postkriminal.getTitle());

						return params;
					}

					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String,String> params = new HashMap<String, String>();
						params.put("Content-Type","application/x-www-form-urlencoded");
						return params;
					}
				};
				VolleySingleton.getInstance(ReportActivity.this).addToRequestQueue(stringRequest);
			}
		});

		buttonClose = findViewById(R.id.bt_close);
		buttonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReportActivity.this, MainActivity.class);
				startActivity(intent);
				finishAffinity();
			}
		});

		textViewDate.setText(UtilClass.stringToDate(currentDateandTime,"yyyy-MM-dd HH:mm:ss","dd MMMM yyyy HH:mm"));

	}

	@Override
	protected void onResume() {
		String serverURL= UtilClass.serverUri + "Assets/api/postprepare.php";
		StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.i(TAG, "onResponse: "+ response);
				boolean result=false;
				String msg = "";
				try {
					JSONObject jsonObject = new JSONObject(response);
					result = jsonObject.getBoolean("result");
					msg = jsonObject.getString("msg");

					if (result ){
						if ( kecamatanNameList.size()>0){
							kecamatanNameList.clear();
						}
						JSONArray jsonArrayKecamatan = jsonObject.getJSONArray("kecamatan");
						for (int i=0; i<jsonArrayKecamatan.length(); i++) {
							Kecamatan kecamatan = new Kecamatan();
							kecamatan.setId(jsonArrayKecamatan.getJSONObject(i).getInt("id"));
							kecamatan.setName(jsonArrayKecamatan.getJSONObject(i).getString("name"));
							kecamatanList.add(kecamatan);
							kecamatanNameList.add(kecamatan.getName());
						}
						if ( klasifikasiKriminalNameList.size()>0){
							klasifikasiKriminalNameList.clear();
						}
						JSONArray jsonArrayKlasifikasiKriminal = jsonObject.getJSONArray("jenis_kriminal");
						for (int i=0; i<jsonArrayKlasifikasiKriminal.length(); i++) {
							KlasifikasiKriminal klasifikasiKriminal = new KlasifikasiKriminal();
							klasifikasiKriminal.setId(jsonArrayKlasifikasiKriminal.getJSONObject(i).getInt("id"));
							klasifikasiKriminal.setName(jsonArrayKlasifikasiKriminal.getJSONObject(i).getString("name"));
							klasifikasiKriminalList.add(klasifikasiKriminal);
							klasifikasiKriminalNameList.add(klasifikasiKriminal.getName());
						}

						ArrayAdapter<String> klasifikasiKriminalAdapter = new ArrayAdapter<>
								(ReportActivity.this, android.R.layout.simple_spinner_dropdown_item,klasifikasiKriminalNameList);
						klasifikasiKriminalAdapter.setDropDownViewResource
								(android.R.layout.simple_spinner_dropdown_item);
						spinnerjk.setAdapter(klasifikasiKriminalAdapter);
						spinnerjk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
								klasifikasiKriminalIndex = position;
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {

							}
						});

						ArrayAdapter<String> kecamatanAdapter = new ArrayAdapter<>
								(ReportActivity.this, android.R.layout.simple_spinner_dropdown_item,kecamatanNameList);
						kecamatanAdapter.setDropDownViewResource
								(android.R.layout.simple_spinner_dropdown_item);
						spinnerkecamatan.setAdapter(kecamatanAdapter);
						spinnerkecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
								kecamatanIndex = position;
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {

							}
						});
					}else{
						Snackbar snackbar=Snackbar.make(ReportActivity.this,constraintLayout,
								msg,Snackbar.LENGTH_SHORT);
						snackbar.getView().setBackgroundColor(ContextCompat.getColor(ReportActivity.this, R.color.colorAlertBackground));
						snackbar.setTextColor(ContextCompat.getColor(ReportActivity.this, R.color.grey_40));
						snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
						snackbar.setAction("Coba Lagi", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								ReportActivity.super.onResume();
							}
						});
						snackbar.show();
					}
					if (progressDialog.isShowing()){
						progressDialog.dismiss();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Snackbar snackbar=Snackbar.make(ReportActivity.this,constraintLayout,
							e.getMessage(),Snackbar.LENGTH_SHORT);
					snackbar.getView().setBackgroundColor(ContextCompat.getColor(ReportActivity.this, R.color.colorAlertBackground));
					snackbar.setTextColor(ContextCompat.getColor(ReportActivity.this, R.color.grey_40));
					snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
					snackbar.setAction("Coba Lagi", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ReportActivity.super.onResume();
						}
					});
					snackbar.show();
				}
				if (progressDialog.isShowing()){
					progressDialog.dismiss();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.i(TAG, "onErrorResponse: " + error.getMessage());
				Snackbar snackbar=Snackbar.make(ReportActivity.this,constraintLayout,
						error.getMessage(),Snackbar.LENGTH_SHORT);
				snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
				snackbar.show();
				if (progressDialog.isShowing()){
					progressDialog.dismiss();
				}
			}
		});
		VolleySingleton.getInstance(ReportActivity.this).addToRequestQueue(stringRequest);

		mGoogleApiClient.connect();
		super.onResume();
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

	@Override
	public void onLocationChanged(@NonNull Location location) {
		UtilClass.currentLatitude = String.valueOf(location.getLatitude());
		UtilClass.currentLongitude = String.valueOf(location.getLongitude());
	}
}