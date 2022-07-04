package com.rizwan.sigcrime.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.rizwan.sigcrime.R;
import com.rizwan.sigcrime.dao.UserData;
import com.rizwan.sigcrime.dao.Users;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InformasiActivity extends AppCompatActivity {

	private static String TAG="InformasiActivity";

	Spinner spinner_jk, spinner_st_kawin;
	TextInputEditText input_agama,input_pekerjaan,input_kewarganegaraan;
	AppCompatButton button,button2;
	String tmp_jk, tmp_st_kawin;

	ProgressDialog progressDialog;
	private ConstraintLayout constraintLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informasi);

		progressDialog= new ProgressDialog(this);
		progressDialog.setTitle("Update User Data");
		progressDialog.setMessage("Updating data to database");
		progressDialog.setCanceledOnTouchOutside(false);

		constraintLayout = findViewById(R.id.private_info_container);

		spinner_jk = findViewById(R.id.spinner_jk);
		spinner_st_kawin = findViewById(R.id.spinner_st_kawin);
		input_agama = findViewById(R.id.input_agama);
		input_pekerjaan = findViewById(R.id.input_pekerjaan);
		input_kewarganegaraan = findViewById(R.id.input_kewarganegaraan);

		button = findViewById(R.id.button);
		button2 = findViewById(R.id.button2);

		input_kewarganegaraan.setText(UtilClass.userLogin.getUserData().getKewarganegaraan());
		input_pekerjaan.setText(UtilClass.userLogin.getUserData().getPekerjaan());
		input_agama.setText(UtilClass.userLogin.getUserData().getAgama());

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.array_jk, android.R.layout.simple_spinner_dropdown_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner_jk.setAdapter(adapter);
		spinner_jk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				tmp_jk = adapter.getItem(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
				R.array.array_st_kawin, android.R.layout.simple_spinner_dropdown_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner_st_kawin.setAdapter(adapter2);
		spinner_st_kawin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				tmp_st_kawin = adapter2.getItem(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		try {
			spinner_st_kawin.setSelection(adapter2.getPosition(UtilClass.userLogin.getUserData().getSt_kawin()));
		}catch (Exception ex){
			spinner_st_kawin.setSelection(0);
		}

		try {
			spinner_jk.setSelection(adapter.getPosition(UtilClass.userLogin.getUserData().getJenis_kelamin()));
		}catch (Exception ex){
			spinner_jk.setSelection(0);
		}


		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InformasiActivity.this, ProfileUpdateActivity.class);
				startActivity(intent);
				finishAffinity();
			}
		});

		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!progressDialog.isShowing()){
					progressDialog.show();
				}
				String serverURL= UtilClass.serverUri + "Assets/api/appscript/profileupdate/updateuserinformation.php";
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

							JSONObject userObject = jsonObject.getJSONObject("user_data");
							if (result ){
								UtilClass.userLogin = new Users();
								UtilClass.userLogin.setId(userObject.getInt("id"));
								UtilClass.userLogin.setUser(userObject.getString("user"));
								UtilClass.userLogin.setPass(userObject.getString("pass"));
								UtilClass.userLogin.setEmail(userObject.getString("email"));
								UtilClass.userLogin.setFull_name(userObject.getString("full_name"));
								UtilClass.userLogin.setIs_admin(userObject.getString("is_admin"));
								UtilClass.userLogin.setIs_admin(userObject.getString("is_admin"));
								UtilClass.userLogin.setUser_verified(userObject.getString("user_verified"));
								UtilClass.userLogin.setFilename(userObject.getString("filename"));
								UtilClass.userID = userObject.getString("id");

								JSONObject userDetailObject = jsonObject.getJSONObject("user_detail");
								UserData userData = new UserData();
								userData.setUser_id(userDetailObject.getInt("id"));
								userData.setNik(userDetailObject.getString("nik"));
								userData.setUser_id(userDetailObject.getInt("user_id"));
								userData.setName(userDetailObject.getString("name"));
								userData.setTempat_lahir(userDetailObject.getString("tempat_lahir"));
								userData.setTanggal_lahir(userDetailObject.getString("tanggal_lahir"));
								userData.setJenis_kelamin(userDetailObject.getString("jenis_kelamin"));
								userData.setAlamat(userDetailObject.getString("alamat"));
								userData.setRt_rw(userDetailObject.getString("rt_rw"));
								userData.setKel_desa(userDetailObject.getString("kel_desa"));
								userData.setKecamatan(userDetailObject.getString("kecamatan"));
								userData.setAgama(userDetailObject.getString("agama"));
								userData.setSt_kawin(userDetailObject.getString("st_kawin"));
								userData.setPekerjaan(userDetailObject.getString("pekerjaan"));
								userData.setKewarganegaraan(userDetailObject.getString("kewarganegaraan"));
								userData.setExp(userDetailObject.getString("exp"));
								userData.setFilename(userDetailObject.getString("filename"));
								UtilClass.userLogin.setUserData(userData);

								Snackbar snackbar=Snackbar.make(InformasiActivity.this,constraintLayout,
										msg,Snackbar.LENGTH_SHORT);
								snackbar.getView().setBackgroundColor(ContextCompat.getColor(InformasiActivity.this, R.color.colorPrimary));
								snackbar.setTextColor(ContextCompat.getColor(InformasiActivity.this, R.color.white));
								snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
								snackbar.show();

								Handler handler = new Handler();

								final Runnable r = new Runnable() {
									public void run() {
										Intent intent = new Intent(InformasiActivity.this, AlamatActivity.class);
										startActivity(intent);
										finishAffinity();
									}
								};
								handler.postDelayed(r, 1000);

							}
						} catch (JSONException e) {
							e.printStackTrace();
							Snackbar snackbar=Snackbar.make(InformasiActivity.this,constraintLayout,
									e.getMessage(),Snackbar.LENGTH_SHORT);
							snackbar.getView().setBackgroundColor(ContextCompat.getColor(InformasiActivity.this, R.color.colorAlertBackground));
							snackbar.setTextColor(ContextCompat.getColor(InformasiActivity.this, R.color.grey_40));
							snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
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
						Snackbar snackbar=Snackbar.make(InformasiActivity.this,constraintLayout,
								error.getMessage(),Snackbar.LENGTH_SHORT);
						snackbar.getView().setBackgroundColor(ContextCompat.getColor(InformasiActivity.this, R.color.colorAlertBackground));
						snackbar.setTextColor(ContextCompat.getColor(InformasiActivity.this, R.color.grey_40));
						snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
						snackbar.show();
						if (progressDialog.isShowing()){
							progressDialog.dismiss();
						}
					}
				}){
					@Override
					protected Map<String,String> getParams(){
						Map<String,String> params = new HashMap<String, String>();
						params.put("jk",tmp_jk);
						params.put("agama",input_agama.getText().toString());
						params.put("st_kawin", tmp_st_kawin);
						params.put("pekerjaan",input_pekerjaan.getText().toString());
						params.put("kewarganegaraan",input_kewarganegaraan.getText().toString());
						params.put("userid",String.valueOf(UtilClass.userLogin.getId()));

						return params;
					}

					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String,String> params = new HashMap<String, String>();
						params.put("Content-Type","application/x-www-form-urlencoded");
						return params;
					}
				};
				VolleySingleton.getInstance(InformasiActivity.this).addToRequestQueue(stringRequest);
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(InformasiActivity.this, ProfileUpdateActivity.class);
		startActivity(intent);
		finishAffinity();

		super.onBackPressed();
	}
}