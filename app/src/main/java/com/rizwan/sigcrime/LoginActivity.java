package com.rizwan.sigcrime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import static com.rizwan.sigcrime.utilities.UtilClass.prefKeyUserID;
import static com.rizwan.sigcrime.utilities.UtilClass.userID;
import static com.rizwan.sigcrime.utilities.UtilClass.userLogin;


public class LoginActivity extends AppCompatActivity {

	private static String TAG="LoginActivity";

	TextInputEditText inputEmail, inputPassword;
	AppCompatButton btnLogin, btnSkip;
	ConstraintLayout constraintLayout;

	SharedPreferences sharedPref;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		sharedPref = getApplicationContext().getSharedPreferences(
				UtilClass.prefKeyUserID, Context.MODE_PRIVATE);

		constraintLayout = findViewById(R.id.login_container);

		inputEmail = findViewById(R.id.input_email);
		inputPassword = findViewById(R.id.input_password);

		btnLogin = findViewById(R.id.button);
		btnSkip = findViewById(R.id.buttonskip);

		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
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

							if (result ){
								JSONObject userObject = jsonObject.getJSONObject("user_data");
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


								SharedPreferences.Editor editor = sharedPref.edit();
								editor.putString(prefKeyUserID, userID);
								editor.putString("prefKeyUserEmail", userLogin.getEmail());
								editor.putString("prefKeyUserPass", userLogin.getPass());
								editor.apply();

								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								finishAffinity();
								startActivity(intent);

							}else{
								Snackbar snackbar=Snackbar.make(LoginActivity.this,constraintLayout,
										msg,Snackbar.LENGTH_SHORT);
								snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
								snackbar.getView().setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary));
								snackbar.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.textPrimary));
								snackbar.setAction("Dashboard", new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent = new Intent(LoginActivity.this, MainActivity.class);
										finishAffinity();
										startActivity(intent);
									}
								});
								snackbar.show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d(TAG, "onErrorResponse: " + error.getMessage());
						Snackbar snackbar=Snackbar.make(LoginActivity.this,constraintLayout,
								error.getMessage(),Snackbar.LENGTH_SHORT);
						snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
						snackbar.show();
					}
				}){
					@Override
					protected Map<String,String> getParams(){
						Map<String,String> params = new HashMap<String, String>();
						params.put("username",inputEmail.getText().toString());
						params.put("password",inputPassword.getText().toString());
						params.put("id", "");

						return params;
					}

					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String,String> params = new HashMap<String, String>();
						params.put("Content-Type","application/x-www-form-urlencoded");
						return params;
					}
				};
				VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
			}
		});

		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		finishAffinity();
		startActivity(intent);
		super.onBackPressed();
	}
}