package com.rizwan.sigcrime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.rizwan.sigcrime.utilities.UtilClass.userID;
import static com.rizwan.sigcrime.utilities.UtilClass.userLogin;

public class SplashActivity extends AppCompatActivity {
	private static String TAG = "SplashActivity";

	ConstraintLayout constraintLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		constraintLayout = findViewById(R.id.splash_container);

		SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
				UtilClass.prefKeyUserID, Context.MODE_PRIVATE);
		UtilClass.userID = sharedPref.getString(UtilClass.prefKeyUserID,"?");
		String userEmail = sharedPref.getString("prefKeyUserEmail","?");
		String userPass = sharedPref.getString("prefKeyUserPass","?");
		if (userID.equals("?")){
			Intent wellcomeIntent = new Intent(this, WellcomeActivity.class);
			startActivity(wellcomeIntent);
			this.finishAffinity();
		}
		else{
			String serverURL= UtilClass.serverUri + "Assets/api/userlogin.php";
			StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL, new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.i(TAG, "onResponse: " + response);
					boolean result=false;
					String msg = "";
					try {
						JSONObject jsonObject = new JSONObject(response);

						result = jsonObject.getBoolean("result");
						msg = jsonObject.getString("msg");

						JSONObject userObject = jsonObject.getJSONObject("user_data");

						if (result ){
							userLogin = new Users();
							userLogin.setId(userObject.getInt("id"));
							userLogin.setUser(userObject.getString("user"));
							userLogin.setPass(userObject.getString("pass"));
							userLogin.setEmail(userObject.getString("email"));
							userLogin.setFull_name(userObject.getString("full_name"));
							userLogin.setIs_admin(userObject.getString("is_admin"));
							userLogin.setIs_admin(userObject.getString("is_admin"));
							userLogin.setUser_verified(userObject.getString("user_verified"));
							userLogin.setFilename(userObject.getString("filename"));
							userID = userObject.getString("id");

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
							userLogin.setUserData(userData);


							Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
							startActivity(mainIntent);
							SplashActivity.this.finishAffinity();

						}else{
							Snackbar snackbar=Snackbar.make(SplashActivity.this,constraintLayout,
									msg,Snackbar.LENGTH_SHORT);
							snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
							snackbar.show();

							Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
							startActivity(mainIntent);
							SplashActivity.this.finishAffinity();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Snackbar snackbar=Snackbar.make(SplashActivity.this,constraintLayout,
								e.getMessage(),Snackbar.LENGTH_SHORT);
						snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
						snackbar.show();
					}

				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Log.d(TAG, "onErrorResponse: " + error.getMessage());
					Snackbar snackbar=Snackbar.make(SplashActivity.this,constraintLayout,
							error.getMessage(),Snackbar.LENGTH_SHORT);
					snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
					snackbar.show();
				}
			}){
				@Override
				protected Map<String,String> getParams(){
					Map<String,String> params = new HashMap<String, String>();
					params.put("username",userEmail);
					params.put("password",userPass);
					params.put("id", UtilClass.userID);

					return params;
				}

				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					Map<String,String> params = new HashMap<String, String>();
					params.put("Content-Type","application/x-www-form-urlencoded");
					return params;
				}
			};
			VolleySingleton.getInstance(SplashActivity.this).addToRequestQueue(stringRequest);
		}
	}
}