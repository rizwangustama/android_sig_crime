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

import com.rizwan.sigcrime.AccountActivity;
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

public class ProfileUpdateActivity extends AppCompatActivity  {
	private static String TAG="ProfileUpdateActivity";

	ProgressDialog progressDialog;

	TextInputEditText input_sername,input_fullname,input_tempat_lahir,input_tgl_lahir,input_password;
	AppCompatButton button,button2;
	private ConstraintLayout constraintLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_update);

		progressDialog= new ProgressDialog(this);
		progressDialog.setTitle("Update User Data");
		progressDialog.setMessage("Updating data to database");
		progressDialog.setCanceledOnTouchOutside(false);

		constraintLayout = findViewById(R.id.update_profile_container);

		input_sername = findViewById(R.id.input_sername);
		input_fullname = findViewById(R.id.input_fullname);
		input_tempat_lahir = findViewById(R.id.input_tempat_lahir);
		input_tgl_lahir = findViewById(R.id.input_tgl_lahir);
		input_password = findViewById(R.id.input_password);

		button = findViewById(R.id.button);
		button2 = findViewById(R.id.updatebutton_profile);


		input_sername.setText(UtilClass.userLogin.getUser());
		input_fullname.setText(UtilClass.userLogin.getFull_name());
		input_tempat_lahir.setText(UtilClass.userLogin.getUserData().getTempat_lahir());
		input_tgl_lahir.setText(UtilClass.userLogin.getUserData().getTanggal_lahir());
		input_password.setText(UtilClass.userLogin.getPass());

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProfileUpdateActivity.this, AccountActivity.class);
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
				String serverURL= UtilClass.serverUri + "Assets/api/appscript/profileupdate/updateuserdata.php";
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

								Snackbar snackbar=Snackbar.make(ProfileUpdateActivity.this,constraintLayout,
										msg,Snackbar.LENGTH_SHORT);
								snackbar.getView().setBackgroundColor(ContextCompat.getColor(ProfileUpdateActivity.this, R.color.colorPrimary));
								snackbar.setTextColor(ContextCompat.getColor(ProfileUpdateActivity.this, R.color.white));
								snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
								snackbar.show();


								Handler handler = new Handler();

								final Runnable r = new Runnable() {
									public void run() {
										Intent intent = new Intent(ProfileUpdateActivity.this, InformasiActivity.class);
										startActivity(intent);
										finishAffinity();
									}
								};
								handler.postDelayed(r, 1000);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Snackbar snackbar=Snackbar.make(ProfileUpdateActivity.this,constraintLayout,
									e.getMessage(),Snackbar.LENGTH_SHORT);
							snackbar.getView().setBackgroundColor(ContextCompat.getColor(ProfileUpdateActivity.this, R.color.colorAlertBackground));
							snackbar.setTextColor(ContextCompat.getColor(ProfileUpdateActivity.this, R.color.grey_40));
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
						Snackbar snackbar=Snackbar.make(ProfileUpdateActivity.this,constraintLayout,
								error.getMessage(),Snackbar.LENGTH_SHORT);
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
						params.put("user",input_sername.getText().toString());
						params.put("full_name",input_fullname.getText().toString());
						params.put("tempat_lahir", input_tempat_lahir.getText().toString());
						params.put("tgl_lahir", input_tgl_lahir.getText().toString());
						params.put("pass",input_password.getText().toString());
						params.put("email",UtilClass.userLogin.getEmail());
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
				VolleySingleton.getInstance(ProfileUpdateActivity.this).addToRequestQueue(stringRequest);
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(ProfileUpdateActivity.this, AccountActivity.class);
		startActivity(intent);
		finishAffinity();

		super.onBackPressed();
	}
}