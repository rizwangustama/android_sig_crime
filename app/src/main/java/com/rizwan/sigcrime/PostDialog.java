package com.rizwan.sigcrime;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rizwan.sigcrime.dao.Kriminal;
import com.rizwan.sigcrime.utilities.UtilClass;
import com.rizwan.sigcrime.utilities.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostDialog extends Dialog implements
		android.view.View.OnClickListener {

	public Activity c;
	public ImageButton btn_photo, btn_video, btn_close;
	public TextView textView_kejahatan, textView_title, textView_kecamatan,text_content, textView_date;
	LinearLayout content_text;
	ScrollView content_image;
	ImageView imageView1,imageView2,imageView3,imageView4;

	Kriminal kriminal;

	public void setKriminal(Kriminal kriminal) {
		this.kriminal = kriminal;
	}

	public PostDialog(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.c = a;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = 600;

		setCanceledOnTouchOutside(false);
		setContentView(R.layout.dialog_post);

		content_text = findViewById(R.id.content_text);
		content_image = findViewById(R.id.content_image);
		content_text.setVisibility(View.VISIBLE);
		content_image.setVisibility(View.GONE);
		textView_kecamatan = findViewById(R.id.post_kecamatan);
		textView_kejahatan = findViewById(R.id.post_kejahatan);
		textView_title = findViewById(R.id.post_title);
		text_content = findViewById(R.id.et_post);
		textView_date = findViewById(R.id.post_date);

		btn_photo = findViewById(R.id.bt_content_text);
		btn_video = findViewById(R.id.bt_img);
		btn_close = findViewById(R.id.bt_close);

		textView_kejahatan.setText(kriminal.getKlasifikasi_kriminal());
		textView_kecamatan.setText(kriminal.getKecamatan());
		textView_title.setText(kriminal.getTitle());
		text_content.setText(kriminal.getKasus_kriminal());

		String dateString = kriminal.getTanggal_kejadian() + " "+ kriminal.getWaktu_kejadian();
		String crimeDate = UtilClass.stringToDate(dateString, "yyyy-MM-dd HH:mm:ss","dd MMMM yyyy, HH:mm");
		textView_date.setText(crimeDate);

		btn_close.setOnClickListener(this);
		btn_video.setOnClickListener(this);
		btn_photo.setOnClickListener(this);

		imageView1 = findViewById(R.id.imageView1);
		imageView2 = findViewById(R.id.imageView2);
		imageView3 = findViewById(R.id.imageView3);
		imageView4 = findViewById(R.id.imageView4);

		String serverURL= UtilClass.serverUri + "Assets/api/appscript/postnews/postimages.php";
		StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				Log.d("PostDialog", "onResponse: " + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONArray datas = jsonObject.getJSONArray("data");
					if (datas.get(0) != null) {
						JSONObject img = datas.getJSONObject(0);
						String imageURI = UtilClass.serverUri+"Assets/api/appscript/postnews/lampiran/"+img.getString("filename");
						Picasso.with(c)
								.load(imageURI)
								.centerCrop()
								.noFade()
								.resize(700,340)
								.into(imageView1);
					}
					if (datas.get(1) != null) {
						JSONObject img = datas.getJSONObject(1);
						String imageURI = UtilClass.serverUri+"Assets/api/appscript/postnews/lampiran/"+img.getString("filename");
						Picasso.with(c)
								.load(imageURI)
								.centerCrop()
								.noFade()
								.resize(700,340)
								.into(imageView2);
					}
					if (datas.get(2) != null) {
						JSONObject img = datas.getJSONObject(2);
						String imageURI = UtilClass.serverUri+"Assets/api/appscript/postnews/lampiran/"+img.getString("filename");
						Picasso.with(c)
								.load(imageURI)
								.centerCrop()
								.noFade()
								.resize(700,340)
								.into(imageView3);
					}
					if (datas.get(3) != null) {
						JSONObject img = datas.getJSONObject(3);
						String imageURI = UtilClass.serverUri+"Assets/api/appscript/postnews/lampiran/"+img.getString("filename");
						Picasso.with(c)
								.load(imageURI)
								.centerCrop()
								.noFade()
								.resize(700,340)
								.into(imageView4);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}){
			@Override
			protected Map<String,String> getParams(){
				Map<String,String> params = new HashMap<String, String>();
				params.put("id", String.valueOf(kriminal.getId()));

				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");
				return params;
			}
		};
		VolleySingleton.getInstance(c).addToRequestQueue(stringRequest);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_content_text:
				content_text.setVisibility(View.VISIBLE);
				content_image.setVisibility(View.GONE);
				break;
			case R.id.bt_img:
				content_text.setVisibility(View.GONE);
				content_image.setVisibility(View.VISIBLE);
				break;
			case R.id.bt_close:
				dismiss();
				break;
			default:
				break;
		}

	}
}
