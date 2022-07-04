package com.rizwan.sigcrime.report;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.rizwan.sigcrime.MainActivity;
import com.rizwan.sigcrime.R;
import com.rizwan.sigcrime.utilities.ImageFilePath;
import com.rizwan.sigcrime.utilities.UtilClass;
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

import java.io.File;
import java.io.IOException;

import static com.rizwan.sigcrime.utilities.UtilClass.userLogin;

public class ReportGalleryActivity extends AppCompatActivity {

	ImageView reportImage1,reportImage2,reportImage3,reportImage4;

	private static final int SELECT_FILE1 = 1;
	private static final int SELECT_FILE2 = 2;
	private static final int SELECT_FILE3 = 3;
	private static final int SELECT_FILE4 = 4;

	String pathFile1,pathFile2,pathFile3,pathFile4;
	HttpEntity resEntity;
	ProgressDialog progressDialog;
	AppCompatButton button2;

	private void doFileUpload(String path){
		File file1 = new File(path);
		String urlString = UtilClass.serverUri+"Assets/api/appscript/postnews/lampiranupload.php";
		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urlString);
			FileBody bin1 = new FileBody(file1);
			StringBody stringBody = new StringBody(UtilClass.NewsCreatID);
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("uploadedfile1", bin1);
			reqEntity.addPart("id",stringBody);
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
							userLogin.setFilename(bin1.getFilename());
							Toast.makeText(getApplicationContext(),"Upload Complete.", Toast.LENGTH_LONG).show();
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
		setContentView(R.layout.activity_report_gallery);

		if (UtilClass.NewsCreatID.equals("?")){
			Toast.makeText(this, "Error Post not found", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(ReportGalleryActivity.this, MainActivity.class);
			startActivity(intent);
			finishAffinity();
		}

		progressDialog = new ProgressDialog(ReportGalleryActivity.this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setTitle("Uploading Image");
		progressDialog.setMessage("Uploading Profile Image");
		progressDialog.setCancelable(false);
		button2 = findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReportGalleryActivity.this, MainActivity.class);
				startActivity(intent);
				finishAffinity();
			}
		});

		reportImage1 =findViewById(R.id.report_Image1);
		reportImage2 =findViewById(R.id.report_Image2);
		reportImage3 =findViewById(R.id.report_Image3);
		reportImage4 =findViewById(R.id.report_Image4);

		reportImage1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "Select Image "), SELECT_FILE1);
			}
		});

		reportImage2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "Select Image "), SELECT_FILE2);
			}
		});

		reportImage3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "Select Image "), SELECT_FILE3);
			}
		});

		reportImage4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "Select Image "), SELECT_FILE4);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_FILE1 && resultCode == RESULT_OK && data != null && data.getData() != null){
			Uri uri = data.getData();
			CropImage.activity(uri)
					.start(this);
			pathFile1 = ImageFilePath.getPath(ReportGalleryActivity.this, data.getData());
			if (null != pathFile1 && !pathFile1.isEmpty()) {
				progressDialog.show();
				Thread thread=new Thread(new Runnable(){
					public void run(){
						doFileUpload(pathFile1);
						runOnUiThread(new Runnable(){
							public void run() {
								if(progressDialog.isShowing())
									progressDialog.dismiss();
								try {
									Bitmap bitmap = null;
									bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
									reportImage1.setImageBitmap(bitmap);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
				thread.start();
			}
		}

		if (requestCode == SELECT_FILE2 && resultCode == RESULT_OK && data != null && data.getData() != null){
			Uri uri = data.getData();
			pathFile2 = ImageFilePath.getPath(ReportGalleryActivity.this, data.getData());
			if (null != pathFile2 && !pathFile2.isEmpty()) {
				progressDialog.show();
				Thread thread=new Thread(new Runnable(){
					public void run(){
						doFileUpload(pathFile2);
						runOnUiThread(new Runnable(){
							public void run() {
								if(progressDialog.isShowing())
									progressDialog.dismiss();
								try {
									Bitmap bitmap = null;
									bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
									reportImage2.setImageBitmap(bitmap);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
				thread.start();
			}
		}

		if (requestCode == SELECT_FILE3 && resultCode == RESULT_OK && data != null && data.getData() != null){
			Uri uri = data.getData();
			pathFile3 = ImageFilePath.getPath(ReportGalleryActivity.this, data.getData());
			if (null != pathFile3 && !pathFile3.isEmpty()) {
				progressDialog.show();
				Thread thread=new Thread(new Runnable(){
					public void run(){
						doFileUpload(pathFile3);
						runOnUiThread(new Runnable(){
							public void run() {
								if(progressDialog.isShowing())
									progressDialog.dismiss();
								try {
									Bitmap bitmap = null;
									bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
									reportImage3.setImageBitmap(bitmap);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
				thread.start();
			}
		}

		if (requestCode == SELECT_FILE4 && resultCode == RESULT_OK && data != null && data.getData() != null){
			Uri uri = data.getData();
			pathFile4 = ImageFilePath.getPath(ReportGalleryActivity.this, data.getData());
			if (null != pathFile4 && !pathFile4.isEmpty()) {
				progressDialog.show();
				Thread thread=new Thread(new Runnable(){
					public void run(){
						doFileUpload(pathFile4);
						runOnUiThread(new Runnable(){
							public void run() {
								if(progressDialog.isShowing())
									progressDialog.dismiss();
								try {
									Bitmap bitmap = null;
									bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
									reportImage4.setImageBitmap(bitmap);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
				thread.start();
			}
		}
	}

	@Override
	public void onBackPressed() {
		UtilClass.NewsCreatID="?";
		super.onBackPressed();
	}
}