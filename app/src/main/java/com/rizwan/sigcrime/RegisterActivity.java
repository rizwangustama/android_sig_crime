package com.rizwan.sigcrime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

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

public class RegisterActivity extends AppCompatActivity {

	private static final String TAG = "RegisterActivity";
	TextInputEditText inp_name, inp_email, inp_pass, inp_sername;
	AppCompatButton btn_regis;
	Button btn_skip;
	SharedPreferences sharedPref;
	ConstraintLayout constraintLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		sharedPref = getApplicationContext().getSharedPreferences(
				UtilClass.prefKeyUserID, Context.MODE_PRIVATE);

		constraintLayout = findViewById(R.id.register_container);
		inp_sername= findViewById(R.id.input_username);
		inp_email= findViewById(R.id.input_email);
		inp_name= findViewById(R.id.input_name);
		inp_pass= findViewById(R.id.input_password);

		btn_regis = findViewById(R.id.button);
		btn_skip = findViewById(R.id.buttonskip);

		btn_regis.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String serverURL= UtilClass.serverUri + "Assets/api/userregister.php";
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

								Dialog successDialog =new Dialog(RegisterActivity.this);
								successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
								successDialog.setContentView(R.layout.register_success_layout);
								successDialog.setCancelable(true);

								WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
								lp.copyFrom(successDialog.getWindow().getAttributes());
								lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
								lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

								((AppCompatButton) successDialog.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										SharedPreferences.Editor editor = sharedPref.edit();
										editor.putString(prefKeyUserID, userID);
										editor.putString("prefKeyUserEmail", userLogin.getEmail());
										editor.putString("prefKeyUserPass", userLogin.getPass());
										editor.apply();
										successDialog.dismiss();
									}
								});

								successDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
									@Override
									public void onDismiss(DialogInterface dialog) {
										Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
										finishAffinity();
										startActivity(intent);
									}
								});

								successDialog.show();

							}else{
								Dialog failedDialog =new Dialog(RegisterActivity.this);
								failedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
								failedDialog.setContentView(R.layout.register_failed_layout);
								failedDialog.setCancelable(true);

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
							Snackbar snackbar=Snackbar.make(RegisterActivity.this,constraintLayout,
									e.getMessage(),Snackbar.LENGTH_SHORT);
							snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
							snackbar.show();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d(TAG, "onErrorResponse: " + error.getMessage());
						Snackbar snackbar=Snackbar.make(RegisterActivity.this,constraintLayout,
								error.getMessage(),Snackbar.LENGTH_SHORT);
						snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
						snackbar.show();
					}
				}){
					@Override
					protected Map<String,String> getParams(){
						Map<String,String> params = new HashMap<String, String>();
						params.put("user",inp_sername.getText().toString());
						params.put("pass",inp_pass.getText().toString());
						params.put("email", inp_email.getText().toString());
						params.put("full_name",inp_name.getText().toString());

						return params;
					}

					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {
						Map<String,String> params = new HashMap<String, String>();
						params.put("Content-Type","application/x-www-form-urlencoded");
						return params;
					}
				};
				VolleySingleton.getInstance(RegisterActivity.this).addToRequestQueue(stringRequest);
			}
		});

		btn_skip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
		finishAffinity();
		startActivity(intent);
		super.onBackPressed();
	}
}