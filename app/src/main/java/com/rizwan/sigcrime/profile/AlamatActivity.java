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

import com.rizwan.sigcrime.MainActivity;
import com.rizwan.sigcrime.R;
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

public class AlamatActivity extends AppCompatActivity {
	private static String TAG="AlamataActivity";
	TextInputEditText input_alamat,input_rtrw,input_desa,input_kecamatan;
	AppCompatButton button, button2;

	ProgressDialog progressDialog;
	private ConstraintLayout constraintLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alamat);

		progressDialog= new ProgressDialog(this);
		progressDialog.setTitle("Update User Data");
		progressDialog.setMessage("Updating data to database");
		progressDialog.setCanceledOnTouchOutside(false);

		constraintLayout = findViewById(R.id.alamat_container);

		input_alamat = findViewById(R.id.input_alamat);
		input_rtrw = findViewById(R.id.input_rtrw);
		input_desa = findViewById(R.id.input_desa);
		input_kecamatan = findViewById(R.id.input_kecamatan);

		button = findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AlamatActivity.this, InformasiActivity.class);
				startActivity(intent);
				finishAffinity();
			}
		});
		button2 = findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (button2.getText().toString().equals("Dashboard")){
					Intent intent = new Intent(AlamatActivity.this, MainActivity.class);
					startActivity(intent);
					finishAffinity();
				}else{
					if (!progressDialog.isShowing()){
						progressDialog.show();
					}
					String serverURL= UtilClass.serverUri + "Assets/api/appscript/profileupdate/updateuseraddress.php";
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

									Handler handler = new Handler();

									final Runnable r = new Runnable() {
										public void run() {
											Intent intent = new Intent(AlamatActivity.this, UploadIdentifierActivity.class);
											startActivity(intent);
											finishAffinity();
										}
									};
									handler.postDelayed(r, 1000);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								Snackbar snackbar=Snackbar.make(AlamatActivity.this,constraintLayout,
										e.getMessage(),Snackbar.LENGTH_SHORT);
								snackbar.getView().setBackgroundColor(ContextCompat.getColor(AlamatActivity.this, R.color.colorAlertBackground));
								snackbar.setTextColor(ContextCompat.getColor(AlamatActivity.this, R.color.grey_40));
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
							Snackbar snackbar=Snackbar.make(AlamatActivity.this,constraintLayout,
									error.getMessage(),Snackbar.LENGTH_SHORT);
							snackbar.getView().setBackgroundColor(ContextCompat.getColor(AlamatActivity.this, R.color.colorAlertBackground));
							snackbar.setTextColor(ContextCompat.getColor(AlamatActivity.this, R.color.grey_40));
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
							params.put("alamat",input_alamat.getText().toString());
							params.put("rtrw",input_rtrw.getText().toString());
							params.put("desa", input_desa.getText().toString());
							params.put("kecamatan",input_kecamatan.getText().toString());
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
					VolleySingleton.getInstance(AlamatActivity.this).addToRequestQueue(stringRequest);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		input_alamat.setText(UtilClass.userLogin.getUserData().getAlamat());
		input_rtrw.setText(UtilClass.userLogin.getUserData().getRt_rw());
		input_desa.setText(UtilClass.userLogin.getUserData().getKel_desa());
		input_kecamatan.setText(UtilClass.userLogin.getUserData().getKecamatan());
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(AlamatActivity.this, InformasiActivity.class);
		startActivity(intent);
		finishAffinity();
		super.onBackPressed();
	}
}