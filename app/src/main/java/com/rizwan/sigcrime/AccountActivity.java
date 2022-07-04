package com.rizwan.sigcrime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rizwan.sigcrime.dao.Users;
import com.rizwan.sigcrime.profile.ProfileUpdateActivity;
import com.rizwan.sigcrime.report.ReportActivity;
import com.rizwan.sigcrime.utilities.ImageFilePath;
import com.rizwan.sigcrime.utilities.UtilClass;
import com.rizwan.sigcrime.utilities.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.rizwan.sigcrime.utilities.UtilClass.userID;
import static com.rizwan.sigcrime.utilities.UtilClass.userLogin;

public class AccountActivity extends AppCompatActivity {
	private static String TAG="AccountActivity";
	SharedPreferences sharedPref;
	CircleImageView picture_profile;
	ImageButton btnHome, btnMap, btnPost, btnAccount;
	TextView textView_fullname, textView_email, textView_report_count, getTextView_report_verified;
	private ConstraintLayout constraintLayout;

	private static final int SELECT_FILE = 1;
	HttpEntity resEntity;
	ProgressDialog progressDialog;

	public void openGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Select Image "), SELECT_FILE);
	}


	private void doFileUpload(String path){
		File file1 = new File(path);
		String urlString = UtilClass.serverUri+"Assets/image/uploaderprofile.php";
		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urlString);
			FileBody bin1 = new FileBody(file1);
			StringBody stringBody = new StringBody(String.valueOf(userLogin.getId()));
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("uploadedfile1", bin1);
			reqEntity.addPart("userid",stringBody);
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();
			final String response_str = EntityUtils.toString(resEntity);
			if (resEntity != null) {
				Log.i("RESPONSE", String.valueOf(response_str));
				runOnUiThread(new Runnable(){
					public void run() {
						try {
							if(progressDialog.isShowing())
								progressDialog.dismiss();
							userLogin.setFilename(bin1.getFilename());
							Toast.makeText(getApplicationContext(),"Upload Complete.", Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							if(progressDialog.isShowing())
								progressDialog.dismiss();
							e.printStackTrace();
							Toast.makeText(getApplicationContext(),"Upload Gagal. \n" + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		}
		catch (Exception ex){
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (userID.equals("?")){
			Intent wellcomeIntent = new Intent(this, LoginActivity.class);
			this.finishAffinity();
			startActivity(wellcomeIntent);
		}
		setContentView(R.layout.activity_account);

		progressDialog = new ProgressDialog(AccountActivity.this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setTitle("Uploading Image");
		progressDialog.setMessage("Uploading Profile Image");
		progressDialog.setCancelable(false);


		constraintLayout = findViewById(R.id.layout_container);

		sharedPref = getApplicationContext().getSharedPreferences(
				UtilClass.prefKeyUserID, Context.MODE_PRIVATE);
		picture_profile = findViewById(R.id.circleImageView);
		picture_profile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openGallery();
			}
		});

		textView_email = findViewById(R.id.acc_email);
		textView_fullname = findViewById(R.id.acc_fullname);
		textView_report_count = findViewById(R.id.report_count);
		getTextView_report_verified = findViewById(R.id.report_verfied);

		btnHome = findViewById(R.id.home_button);
		btnMap = findViewById(R.id.map_button);
		btnPost = findViewById(R.id.post_button);
		btnAccount = findViewById(R.id.acount_button);

		btnHome.setColorFilter(ContextCompat.getColor(AccountActivity.this,R.color.grey_40), PorterDuff.Mode.SRC_IN);
		btnMap.setColorFilter(ContextCompat.getColor(AccountActivity.this,R.color.grey_40), PorterDuff.Mode.SRC_IN);
		btnPost.setColorFilter(ContextCompat.getColor(AccountActivity.this,R.color.grey_40), PorterDuff.Mode.SRC_IN);
		btnAccount.setColorFilter(ContextCompat.getColor(AccountActivity.this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


		AppCompatButton buttonLogOut = findViewById(R.id.button2);
		buttonLogOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(UtilClass.prefKeyUserID,"?");
				UtilClass.userID="?";
				editor.apply();
				UtilClass.userLogin = new Users();
				Intent intent = new Intent(AccountActivity.this, MainActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});

		AppCompatButton buttonUpdate = findViewById(R.id.button);
		buttonUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountActivity.this, ProfileUpdateActivity.class);
				startActivity(intent);
				finishAffinity();
			}
		});


		btnHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountActivity.this, MainActivity.class);
				finishAffinity();
				startActivity(intent);
			}
		});

		btnMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mapIntent= new Intent(AccountActivity.this,MapsActivity.class);
				AccountActivity.this.startActivity(mapIntent);
			}
		});

		btnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (UtilClass.userLogin.getUser_verified().equals("true") || UtilClass.userLogin.getUser_verified().equals("True")) {
						Intent intent = new Intent(AccountActivity.this, ReportActivity.class);
						finishAffinity();
						startActivity(intent);
					} else {
						Toast.makeText(AccountActivity.this, "Anda belum bisa membuat post berita", Toast.LENGTH_SHORT).show();
					}
				}catch (Exception ex){
					Toast.makeText(AccountActivity.this, "Anda belum bisa membuat post berita", Toast.LENGTH_SHORT).show();
				}
			}
		});

		btnAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		UtilClass.userID = sharedPref.getString(UtilClass.prefKeyUserID,"?");

		if (!UtilClass.userID.equals("?")){
			String accountStatus="";
			if (UtilClass.userLogin.getUser_verified().equals("true") || UtilClass.userLogin.getUser_verified().equals("True")){
				accountStatus="Verified";
			}else{
				accountStatus="Not Verified";
			}
			textView_fullname.setText(UtilClass.userLogin.getFull_name() +" - "+ accountStatus);
			textView_email.setText(UtilClass.userLogin.getEmail());

		}else{
			picture_profile.setImageResource(R.mipmap.ic_sig_crime_round);
		}

		if (!UtilClass.userID.equals("?") && UtilClass.userLogin.getFilename() != null && !UtilClass.userLogin.getFilename().equals("?")){

			String imageURI = UtilClass.serverUri+"Assets/image/userprofile/"+UtilClass.userLogin.getFilename();
			Picasso.with(this)
					.load(imageURI)
					.centerCrop()
					.noFade()
					.resize(100,100)
					.into(picture_profile);
		}else{
			picture_profile.setImageResource(R.mipmap.ic_sig_crime_round);
		}


	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!UtilClass.userID.equals("?")){
			String serverURL= UtilClass.serverUri + "Assets/api/usercountreport.php";
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
							textView_report_count.setText(userObject.getString("Total_Laporan"));
							getTextView_report_verified.setText(userObject.getString("Total_Laporan_Verfied"));

						}else{
							Snackbar snackbar=Snackbar.make(AccountActivity.this,constraintLayout,
									msg,Snackbar.LENGTH_SHORT);
							snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
							snackbar.getView().setBackgroundColor(ContextCompat.getColor(AccountActivity.this, R.color.colorPrimary));
							snackbar.setTextColor(ContextCompat.getColor(AccountActivity.this, R.color.textPrimary));
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
					Snackbar snackbar=Snackbar.make(AccountActivity.this,constraintLayout,
							error.getMessage(),Snackbar.LENGTH_SHORT);
					snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
					snackbar.show();
				}
			}){
				@Override
				protected Map<String,String> getParams(){
					Map<String,String> params = new HashMap<String, String>();
					params.put("id", String.valueOf(userLogin.getId()));

					return params;
				}

				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					Map<String,String> params = new HashMap<String, String>();
					params.put("Content-Type","application/x-www-form-urlencoded");
					return params;
				}
			};
			VolleySingleton.getInstance(AccountActivity.this).addToRequestQueue(stringRequest);
		}else{
			textView_report_count.setText("0");
			getTextView_report_verified.setText("0");
		}

		if (!UtilClass.userID.equals("?") && UtilClass.userLogin.getFilename() != null && !UtilClass.userLogin.getFilename().equals("?")){

			String imageURI = UtilClass.serverUri+"Assets/image/userprofile/"+UtilClass.userLogin.getFilename();
			Picasso.with(this)
					.load(imageURI)
					.centerCrop()
					.noFade()
					.resize(100,100)
					.into(picture_profile);
		}else{
			picture_profile.setImageResource(R.mipmap.ic_sig_crime_round);
		}
	}

	@Override
	public void onBackPressed() {

		if (progressDialog.isShowing()){
			Toast.makeText(this, "Sedang proses upload", Toast.LENGTH_SHORT).show();
		}else{
			Intent intent = new Intent(AccountActivity.this, MainActivity.class);
			startActivity(intent);
			finishAffinity();
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
			Uri uri = data.getData();
			CropImage.activity(uri)
					.start(this);
		} else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				Uri resultUri = result.getUri();
				String realPath = ImageFilePath.getPath(AccountActivity.this, resultUri);
				Log.i(TAG, "onActivityResult: file path : " + realPath);
			if (null != realPath && !realPath.isEmpty()) {
				progressDialog.show();
				Thread thread=new Thread(new Runnable(){
					public void run(){
						doFileUpload(realPath);
						runOnUiThread(new Runnable(){
							public void run() {
								if(progressDialog.isShowing())
									progressDialog.dismiss();
								try {
									Bitmap bitmap = null;
									bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
									picture_profile.setImageBitmap(bitmap);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
				thread.start();
			}
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Exception error = result.getError();
			}
		}
		else {
			Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
		}

	}

}