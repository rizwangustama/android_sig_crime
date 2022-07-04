package com.rizwan.sigcrime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rizwan.sigcrime.adapter.SearchUpdateListAdapter;
import com.rizwan.sigcrime.dao.Kriminal;
import com.rizwan.sigcrime.report.ReportActivity;
import com.rizwan.sigcrime.utilities.Constants;
import com.rizwan.sigcrime.utilities.UtilClass;
import com.rizwan.sigcrime.utilities.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

	private boolean mLocationPermissionGranted = false;
	private GoogleMap mMap;
	private String TAG ="MapsActivity";
	private ArrayList<Kriminal> item = new ArrayList<>();
	private SearchUpdateListAdapter searchUpdateListAdapter;

	EditText editTextSearch;

	ImageButton btnHome, btnMap, btnPost, btnAccount;
	RecyclerView recyclerviewSearch ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		if (UtilClass.kmlKecamatan!="?"){
//			retrieveFileFromUrl("Test1.kml");
//			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//					.findFragmentById(R.id.map);
//			mapFragment.getMapAsync(this);
		}else{
//			Intent intent = new Intent(MapsActivity.this, MainActivity.class);
//			startActivity(intent);
//			onDestroy();
		}

		recyclerviewSearch = findViewById(R.id.recyclerview_map_search);
		recyclerviewSearch.setHasFixedSize(true);

		btnHome = findViewById(R.id.home_button);
		btnMap = findViewById(R.id.map_button);
		btnPost = findViewById(R.id.post_button);
		btnAccount = findViewById(R.id.acount_button);
		editTextSearch = findViewById(R.id.search_text);
		editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					searchCrimeData(editTextSearch.getText().toString());
					InputMethodManager imm = (InputMethodManager) MapsActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
					//Find the currently focused view, so we can grab the correct window token from it.
					View view = MapsActivity.this.getCurrentFocus();
					//If no view currently has focus, create a new one, just so we can grab a window token from it
					if (view == null) {
						view = new View(MapsActivity.this);
					}
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
					handled = true;
				}
				return handled;
			}
		});

		btnHome.setColorFilter(ContextCompat.getColor(MapsActivity.this,R.color.grey_40), PorterDuff.Mode.SRC_IN);
		btnMap.setColorFilter(ContextCompat.getColor(MapsActivity.this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
		btnPost.setColorFilter(ContextCompat.getColor(MapsActivity.this,R.color.grey_40), PorterDuff.Mode.SRC_IN);
		btnAccount.setColorFilter(ContextCompat.getColor(MapsActivity.this,R.color.grey_40), PorterDuff.Mode.SRC_IN);

		btnHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MapsActivity.this, MainActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});

		btnMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		btnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (UtilClass.userLogin.getUser_verified().equals("true") || UtilClass.userLogin.getUser_verified().equals("True") ) {
						Intent intent = new Intent(MapsActivity.this, ReportActivity.class);
						finishAffinity();
						startActivity(intent);
					} else {
						Toast.makeText(MapsActivity.this, "Anda belum bisa membuat post berita", Toast.LENGTH_SHORT).show();
					}
				}catch (Exception ex){
					Toast.makeText(MapsActivity.this, "Anda belum bisa membuat post berita", Toast.LENGTH_SHORT).show();
				}
			}
		});

		btnAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MapsActivity.this, AccountActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});

	}

	private void searchCrimeData(String toString) {
		if (item.size()>0) {
			item.clear();
		recyclerviewSearch.setAdapter(null);
			;}
		String serverURL= UtilClass.serverUri + "Assets/api/crimerecordsearch.php";
		StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL, new Response.Listener<String>() {
			@SuppressLint("PotentialBehaviorOverride")
			@Override
			public void onResponse(String response) {
				Log.d(TAG, "onResponse: " + response);
				try {
					mMap.clear();
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("crime_record");
					Log.d(TAG, "onResponse: " + jsonArray);
					for (int i = 0; i < jsonArray.length(); i++){
						Kriminal kriminal = new Kriminal();
						kriminal.setId(jsonArray.getJSONObject(i).getInt("id"));
						kriminal.setId_kecamatan(jsonArray.getJSONObject(i).getInt("id_kecamatan"));
						kriminal.setId_klassifikasi_kriminal(jsonArray.getJSONObject(i).getInt("id_klassifikasi_kriminal"));
						kriminal.setKasus_kriminal(jsonArray.getJSONObject(i).getString("kasus_kriminal"));
						kriminal.setTanggal_kejadian(jsonArray.getJSONObject(i).getString("tanggal_kejadian"));
						kriminal.setWaktu_kejadian(jsonArray.getJSONObject(i).getString("waktu_kejadian"));
						kriminal.setAlamat(jsonArray.getJSONObject(i).getString("alamat"));
						kriminal.setMap_lat(jsonArray.getJSONObject(i).getString("map_lat"));
						kriminal.setMap_lang(jsonArray.getJSONObject(i).getString("map_lang"));
						kriminal.setCreate_by(Integer.parseInt(jsonArray.getJSONObject(i).getString("create_by")));
						kriminal.setCreated(jsonArray.getJSONObject(i).getString("created"));
						kriminal.setIs_verified(jsonArray.getJSONObject(i).getString("is_verified"));
						kriminal.setTitle(jsonArray.getJSONObject(i).getString("title"));

						kriminal.setKlasifikasi_kriminal(jsonArray.getJSONObject(i).getString("klasifikasi_kriminal"));
						kriminal.setKecamatan(jsonArray.getJSONObject(i).getString("kecamatan"));
						kriminal.setUser_full_name(jsonArray.getJSONObject(i).getString("full_name"));
						kriminal.setFilename(jsonArray.getJSONObject(i).getString("filename"));

						LatLng temp = new LatLng(Double.parseDouble(kriminal.getMap_lat()), Double.parseDouble(kriminal.getMap_lang()));
						mMap.addMarker(new MarkerOptions()
								.position(temp)
								.title( kriminal.getTitle() )
								.snippet(UtilClass.stringToDate(kriminal.getCreated(), "yyyy-MM-dd HH:mm:ss","dd MMMM yyyy, HH:mm"))
						);
						item.add(kriminal);
					}
					if (item.size()>-1){
						LatLng latLng = new LatLng(Double.parseDouble(item.get(0).getMap_lat()), Double.parseDouble(item.get(0).getMap_lang()));
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13),500,null);
					}

					searchUpdateListAdapter = new SearchUpdateListAdapter(MapsActivity.this,item);
					searchUpdateListAdapter.setmOnItemClickListener(new SearchUpdateListAdapter.OnItemClickListener() {
						@Override
						public void onItemClick(View view, Kriminal obj, int position) {
							PostDialog postDialog = new PostDialog(MapsActivity.this);
							postDialog.setKriminal(obj);
							postDialog.show();
						}
					});
					recyclerviewSearch.setAdapter(searchUpdateListAdapter);
					Log.d(TAG, "onResponse Array: " + jsonArray.length());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(TAG, "onErrorResponse error 1: " + error.getMessage());
			}
		}){
			@Override
			protected Map<String,String> getParams(){
				Map<String,String> params = new HashMap<String, String>();
				params.put("search",toString);

				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");
				return params;
			}
		};

		VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
	}

	// TODO: 6/2/2022 LOAD DATA
	private void LoadData(){
		if(item.size()>1){
			item.clear();
			recyclerviewSearch.setAdapter(null);
		}
		String serverURL= UtilClass.serverUri + "Assets/api/crimerecord.php";
		StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL, new Response.Listener<String>() {
			@SuppressLint("PotentialBehaviorOverride")
			@Override
			public void onResponse(String response) {
				Log.d(TAG, "onResponse: " + response);
				try {
					mMap.clear();
					boolean result = false;
					String msg = "";
					JSONObject jsonObject = new JSONObject(response);
					result = jsonObject.getBoolean("result");
					msg = jsonObject.getString("msg");
					if (result){
						JSONArray jsonArray = jsonObject.getJSONArray("crime_record");
						for (int i = 0; i < jsonArray.length(); i++){
							Kriminal kriminal = new Kriminal();
							kriminal.setId(jsonArray.getJSONObject(i).getInt("id"));
							kriminal.setId_kecamatan(jsonArray.getJSONObject(i).getInt("id_kecamatan"));
							kriminal.setId_klassifikasi_kriminal(jsonArray.getJSONObject(i).getInt("id_klassifikasi_kriminal"));
							kriminal.setKasus_kriminal(jsonArray.getJSONObject(i).getString("kasus_kriminal"));
							kriminal.setTanggal_kejadian(jsonArray.getJSONObject(i).getString("tanggal_kejadian"));
							kriminal.setWaktu_kejadian(jsonArray.getJSONObject(i).getString("waktu_kejadian"));
							kriminal.setAlamat(jsonArray.getJSONObject(i).getString("alamat"));
							kriminal.setMap_lat(jsonArray.getJSONObject(i).getString("map_lat"));
							kriminal.setMap_lang(jsonArray.getJSONObject(i).getString("map_lang"));
							kriminal.setCreate_by(Integer.parseInt(jsonArray.getJSONObject(i).getString("create_by")));
							kriminal.setCreated(jsonArray.getJSONObject(i).getString("created"));
							kriminal.setIs_verified(jsonArray.getJSONObject(i).getString("is_verified"));
							kriminal.setTitle(jsonArray.getJSONObject(i).getString("title"));

							kriminal.setKlasifikasi_kriminal(jsonArray.getJSONObject(i).getString("klasifikasi_kriminal"));
							kriminal.setKecamatan(jsonArray.getJSONObject(i).getString("kecamatan"));
							kriminal.setUser_full_name(jsonArray.getJSONObject(i).getString("full_name"));
							kriminal.setFilename(jsonArray.getJSONObject(i).getString("filename"));

							LatLng temp = new LatLng(Double.parseDouble(kriminal.getMap_lat()), Double.parseDouble(kriminal.getMap_lang()));
							mMap.addMarker(new MarkerOptions()
									.position(temp)
									.title( kriminal.getTitle() )
									.snippet(UtilClass.stringToDate(kriminal.getCreated(), "yyyy-MM-dd HH:mm:ss","dd MMMM yyyy, HH:mm"))
							);
							item.add(kriminal);
						}
						searchUpdateListAdapter = new SearchUpdateListAdapter(MapsActivity.this,item);
						searchUpdateListAdapter.setmOnItemClickListener(new SearchUpdateListAdapter.OnItemClickListener() {
							@Override
							public void onItemClick(View view, Kriminal obj, int position) {
								PostDialog postDialog = new PostDialog(MapsActivity.this);
								postDialog.setKriminal(obj);
								postDialog.show();
							}
						});
						recyclerviewSearch.setAdapter(searchUpdateListAdapter);
					}else{
						Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(TAG, "onErrorResponse error 1: " + error.getMessage());
			}
		}){
			@Override
			protected Map<String,String> getParams(){
				Map<String,String> params = new HashMap<String, String>();
				params.put("kecamatan",UtilClass.kecamatanID);

				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");
				return params;
			}
		};

		VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@SuppressLint("PotentialBehaviorOverride")
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setOnMarkerClickListener(this);


		// Add a marker in Sydney and move the camera

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			getLocationPermission();
			return;
		}
		if (!UtilClass.kecamatanID.equals("?")){
			LatLng latLng = new LatLng(Double.parseDouble(UtilClass.kecLat), Double.parseDouble(UtilClass.kecLang));
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13),500,null);
			LoadData();
		}else{
			LatLng latLng = new LatLng(-6.93334, 106.92203);
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13),500,null);
			searchCrimeData("");
		}

		mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(@NonNull Marker marker) {
				Integer x = 0;
				for (Integer i=0; i<item.size(); i++){
					if (Double.parseDouble(item.get(i).getMap_lat()) == marker.getPosition().latitude &&
							Double.parseDouble(item.get(i).getMap_lang()) == marker.getPosition().longitude){
						x= i;
						break ;
					}
				}

				PostDialog postDialog = new PostDialog(MapsActivity.this);
				postDialog.setKriminal(item.get(x));
				postDialog.show();
			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (checkMapServices()) {
			if (!mLocationPermissionGranted) {
				getLocationPermission();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult: called.");
		switch (requestCode) {
			case Constants.PERMISSIONS_REQUEST_ENABLE_GPS: {
				if (!mLocationPermissionGranted) {
					getLocationPermission();
				}
			}
		}
	}

	private boolean checkMapServices() {
		if (isServicesOK()) {
			if (isMapsEnabled()) {
				return true;
			}
		}
		return false;
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(enableGpsIntent, Constants.PERMISSIONS_REQUEST_ENABLE_GPS);
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	public boolean isMapsEnabled() {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
			return false;
		}
		return true;
	}

	private void getLocationPermission() {
		/*
		 * Request location permission, so that we can get the location of the
		 * device. The result of the permission request is handled by a callback,
		 * onRequestPermissionsResult.
		 */
		if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			mLocationPermissionGranted = true;
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
		}

		if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions((Activity) MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
		}
	}

	public boolean isServicesOK() {
		Log.d(TAG, "isServicesOK: checking google services version");

		int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

		if (available == ConnectionResult.SUCCESS) {
			//everything is fine and the user can make map requests
			Log.d(TAG, "isServicesOK: Google Play Services is working");
			return true;
		} else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
			//an error occured but we can resolve it
			Log.d(TAG, "isServicesOK: an error occured but we can fix it");
			Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, Constants.ERROR_DIALOG_REQUEST);
			dialog.show();
		} else {
			Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		UtilClass.kecamatanID="?";
	}

	@Override
	public boolean onMarkerClick(@NonNull Marker marker) {
		Log.d(TAG, "onMarkerClick: ");

		return false;
	}
}