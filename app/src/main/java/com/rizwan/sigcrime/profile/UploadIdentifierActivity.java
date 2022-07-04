package com.rizwan.sigcrime.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rizwan.sigcrime.AccountActivity;
import com.rizwan.sigcrime.LoginActivity;
import com.rizwan.sigcrime.MainActivity;
import com.rizwan.sigcrime.R;
import com.rizwan.sigcrime.SplashActivity;
import com.rizwan.sigcrime.dao.UserData;
import com.rizwan.sigcrime.dao.Users;
import com.rizwan.sigcrime.utilities.ImageFilePath;
import com.rizwan.sigcrime.utilities.UtilClass;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.rizwan.sigcrime.utilities.VolleySingleton;
import com.theartofdev.edmodo.cropper.CropImage;

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

import static com.rizwan.sigcrime.utilities.UtilClass.userID;
import static com.rizwan.sigcrime.utilities.UtilClass.userLogin;

public class UploadIdentifierActivity extends AppCompatActivity {

	AppCompatButton button;
	ImageView imageView;
	private ConstraintLayout constraintLayout;

	private static final int SELECT_FILE = 1;
	HttpEntity resEntity;
	String realPath ="?";
	ProgressDialog progressDialog;

	private void doLogin(){
		String serverURL= UtilClass.serverUri + "Assets/api/userlogin.php";
		StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.i("UploadKTP", "onResponse: " + response);
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




						Handler handler = new Handler();

						final Runnable r = new Runnable() {
							public void run() {
								Intent mainIntent = new Intent(UploadIdentifierActivity.this, MainActivity.class);
								startActivity(mainIntent);
								finishAffinity();
							}
						};
						handler.postDelayed(r, 1000);

					}else{
						Handler handler = new Handler();

						final Runnable r = new Runnable() {
							public void run() {
								Intent mainIntent = new Intent(UploadIdentifierActivity.this, LoginActivity.class);
								startActivity(mainIntent);
								finishAffinity();
							}
						};
						handler.postDelayed(r, 1000);

					}
				} catch (JSONException e) {
					e.printStackTrace();
					Snackbar snackbar=Snackbar.make(UploadIdentifierActivity.this,constraintLayout,
							e.getMessage(),Snackbar.LENGTH_SHORT);
					snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
					snackbar.show();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("UploadKTP", "onErrorResponse: " + error.getMessage());
				Snackbar snackbar=Snackbar.make(UploadIdentifierActivity.this,constraintLayout,
						error.getMessage(),Snackbar.LENGTH_SHORT);
				snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
				snackbar.show();
			}
		}){
			@Override
			protected Map<String,String> getParams(){
				Map<String,String> params = new HashMap<String, String>();
				params.put("username", userLogin.getEmail());
				params.put("password",userLogin.getPass());
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
		VolleySingleton.getInstance(UploadIdentifierActivity.this).addToRequestQueue(stringRequest);
	}

	public void openGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Select Image "), SELECT_FILE);
	}

	private void doFileUpload(String path){
		File file1 = new File(path);
		String urlString = UtilClass.serverUri+"Assets/api/identifier/identifierupload.php";
		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urlString);
			FileBody bin1 = new FileBody(file1);
			StringBody stringBody = new StringBody(String.valueOf(UtilClass.userLogin.getId()));
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
							if (progressDialog.isShowing()) progressDialog.dismiss();
							UtilClass.userLogin.setFilename(bin1.getFilename());
							Toast.makeText(getApplicationContext(),"Upload Complete.", Toast.LENGTH_LONG).show();
							doLogin();
						} catch (Exception e) {
							e.printStackTrace();
							if (progressDialog.isShowing()) progressDialog.dismiss();
							Toast.makeText(getApplicationContext(),"Upload Gagal.\n" + e.getMessage(), Toast.LENGTH_LONG).show();
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
		setContentView(R.layout.activity_upload_identifier);

		progressDialog = new ProgressDialog(UploadIdentifierActivity.this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setTitle("Uploading Image");
		progressDialog.setMessage("Uploading Profile Image");
		progressDialog.setCancelable(false);

		constraintLayout = findViewById(R.id.container_upload_ktp);
		imageView = findViewById(R.id.imageView5);
		button = findViewById(R.id.button2);

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openGallery();
			}
		});

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!realPath.equals("?")){
					progressDialog.show();
					Thread thread=new Thread(new Runnable(){
						public void run(){
							doFileUpload(realPath);
//							runOnUiThread(new Runnable(){
//								public void run() {
//									if(progressDialog.isShowing())
//										progressDialog.dismiss();
//
//									Snackbar snackbar=Snackbar.make(UploadIdentifierActivity.this,constraintLayout,
//											"Upload Complete",Snackbar.LENGTH_SHORT);
//									snackbar.getView().setBackgroundColor(ContextCompat.getColor(UploadIdentifierActivity.this, R.color.colorPrimary));
//									snackbar.setTextColor(ContextCompat.getColor(UploadIdentifierActivity.this, R.color.white));
//									snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
//
//									snackbar.show();
//
//									Handler handler = new Handler();
//									final Runnable r = new Runnable() {
//										public void run() {
//											Intent intent = new Intent(UploadIdentifierActivity.this, AccountActivity.class);
//											startActivity(intent);
//											finishAffinity();
//										}
//									};
//									handler.postDelayed(r, 1000);
//								}
//							});
						}
					});
					thread.start();
				}else{
					openGallery();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
			Uri uri =data.getData();
			CropImage.activity(uri)
					.start(this);

		}else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				Uri uri =data.getData();
				Uri resultUri = result.getUri();
				this.realPath = ImageFilePath.getPath(UploadIdentifierActivity.this, resultUri);
				Log.i("UploadKTP", "onActivityResult: file path : " + realPath);

				imageView.setImageURI(resultUri);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}

		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(UploadIdentifierActivity.this, MainActivity.class);
		startActivity(intent);
		finishAffinity();
		super.onBackPressed();
	}
}