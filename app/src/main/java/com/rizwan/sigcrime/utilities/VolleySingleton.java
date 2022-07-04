package com.rizwan.sigcrime.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
	private static VolleySingleton mInstance = null;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private static Context mCtx;

	private VolleySingleton(Context context){
		mCtx = context;
		mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
		mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
			private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
			public void putBitmap(String url, Bitmap bitmap) {
				mCache.put(url, bitmap);
			}
			public Bitmap getBitmap(String url) {
				return mCache.get(url);
			}
		});
	}

	public static synchronized  VolleySingleton getInstance(Context context){
		if(mInstance == null){
			mInstance = new VolleySingleton(context);
		}
		return mInstance;
	}

	public RequestQueue getRequestQueue(){
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

	public ImageLoader getImageLoader(){
		return this.mImageLoader;
	}
}
