package com.rizwan.sigcrime;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rizwan.sigcrime.adapter.KecamatanListAdapter;
import com.rizwan.sigcrime.adapter.NewsUpdateListAdapter;
import com.rizwan.sigcrime.dao.Kecamatan;
import com.rizwan.sigcrime.dao.Kriminal;
import com.rizwan.sigcrime.utilities.UtilClass;
import com.rizwan.sigcrime.utilities.VolleySingleton;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
	private static String TAG = "HomeFragment";

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	private RecyclerView recyclerView,recyclerViewNews;
	private KecamatanListAdapter kecamatanAdapter;
	private NewsUpdateListAdapter newsUpdateListAdapter;
	private TextView counter_kejahatan, wellcome_label;
	private ImageView picture_profile;
	MaterialRippleLayout acount_button;
	private int counterKejahatan = 0;
	ArrayList<Kecamatan> item = new ArrayList<>();
	ArrayList<Kriminal> itemCrime = new ArrayList<>();

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	public HomeFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment HomeFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static HomeFragment newInstance(String param1, String param2) {
		HomeFragment fragment = new HomeFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		counter_kejahatan = view.findViewById(R.id.counter_kejahatan);
		picture_profile = view.findViewById(R.id.image_account);
		wellcome_label = view.findViewById(R.id.wellcome_label);
		acount_button = view.findViewById(R.id.cardview_acount);
		acount_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeFragment.this.getActivity(), AccountActivity.class);
				HomeFragment.this.getActivity().finishAffinity();
				HomeFragment.this.getActivity().startActivity(intent);
			}
		});

		// Add the following lines to create RecyclerView
		recyclerView = view.findViewById(R.id.list_kecamatan);
		recyclerView.setHasFixedSize(true);
		recyclerViewNews = view.findViewById(R.id.list_berita);
		recyclerViewNews.setHasFixedSize(true);

		return view;
	}

	@Override
	public void onResume() {
		counterKejahatan = 0;
		item.clear();
		itemCrime.clear();

		if (!UtilClass.userID.equals("?")){
			wellcome_label.setText(getActivity().getString(R.string.wellcome)+ " " + UtilClass.userLogin.getFull_name());
			if (!UtilClass.userLogin.getFilename().equals("?")){

				String imageURI = UtilClass.serverUri+"Assets/image/userprofile/"+UtilClass.userLogin.getFilename();
				Picasso.with(getContext())
						.load(imageURI)
						.centerCrop()
						.noFade()
						.resize(100,100)
						.into(picture_profile);
			}else{
				picture_profile.setImageResource(R.mipmap.ic_sig_crime_round);
			}
		}else{
			picture_profile.setImageResource(R.mipmap.ic_sig_crime_round);
		}


		String serverURL= UtilClass.serverUri + "Assets/api/dashdata.php";
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(serverURL, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				String string_response = String.valueOf(response);

				try {
					JSONArray jsonArray1= new JSONArray(string_response);
					counterKejahatan = Integer.valueOf(jsonArray1.getJSONObject(0).getString("crime_record"));
					counter_kejahatan.setText(String.valueOf(counterKejahatan));
					if (item.size()>0) item.clear();
					JSONArray jsonArray2= new JSONArray(string_response);
					JSONArray kecJson = new JSONArray(String.valueOf(jsonArray2.getJSONArray(1)));
					for (int i = 0; i < kecJson.length(); i++){
						Kecamatan kecamatan = new Kecamatan();
						kecamatan.setId(Integer.parseInt(kecJson.getJSONObject(i).getString("id")));
						kecamatan.setName(kecJson.getJSONObject(i).getString("name"));
						kecamatan.setAlamat(kecJson.getJSONObject(i).getString("alamat"));
						kecamatan.setMap_lat(kecJson.getJSONObject(i).getString("map_lat"));
						kecamatan.setMap_long(kecJson.getJSONObject(i).getString("map_long"));
						kecamatan.setGeo_file(kecJson.getJSONObject(i).getString("geo_file"));
						kecamatan.setFilename(kecJson.getJSONObject(i).getString("filename"));
						item.add(kecamatan);
					}

					kecamatanAdapter = new KecamatanListAdapter(HomeFragment.this.getContext(),item);
					kecamatanAdapter.setmOnItemClickListener(new KecamatanListAdapter.OnItemClickListener() {
						@Override
						public void onItemClick(View view, Kecamatan obj, int position) {
							UtilClass.kmlKecamatan = obj.getGeo_file();
							UtilClass.kecamatanID =String.valueOf( obj.getId());
							UtilClass.kecLat = obj.getMap_lat();
							UtilClass.kecLang = obj.getMap_long();
							if (UtilClass.kmlKecamatan != "?") {
								Intent mapIntent= new Intent(HomeFragment.this.getActivity(),MapsActivity.class);
								HomeFragment.this.startActivity(mapIntent);
							}else{

							}
							Log.d(TAG, "onItemClick: " + UtilClass.kmlKecamatan);
						}
					});
					recyclerView.setAdapter(kecamatanAdapter);

					if (itemCrime.size()>0) itemCrime.clear();
					JSONArray jsonArray3= new JSONArray(string_response);
					JSONArray crimeJson = new JSONArray(String.valueOf(jsonArray3.getJSONArray(2)));
					for (int i = 0; i < crimeJson.length(); i++){
						Kriminal kriminal = new Kriminal();
						kriminal.setId(crimeJson.getJSONObject(i).getInt("id"));
						kriminal.setId_kecamatan(crimeJson.getJSONObject(i).getInt("id_kecamatan"));
						kriminal.setId_klassifikasi_kriminal(crimeJson.getJSONObject(i).getInt("id_klassifikasi_kriminal"));
						kriminal.setKasus_kriminal(crimeJson.getJSONObject(i).getString("kasus_kriminal"));
						kriminal.setTanggal_kejadian(crimeJson.getJSONObject(i).getString("tanggal_kejadian"));
						kriminal.setWaktu_kejadian(crimeJson.getJSONObject(i).getString("waktu_kejadian"));
						kriminal.setAlamat(crimeJson.getJSONObject(i).getString("alamat"));
						kriminal.setMap_lat(crimeJson.getJSONObject(i).getString("map_lat"));
						kriminal.setMap_lang(crimeJson.getJSONObject(i).getString("map_lang"));
						kriminal.setCreate_by(Integer.parseInt(crimeJson.getJSONObject(i).getString("create_by")));
						kriminal.setCreated(crimeJson.getJSONObject(i).getString("created"));
						kriminal.setIs_verified(crimeJson.getJSONObject(i).getString("is_verified"));
						kriminal.setTitle(crimeJson.getJSONObject(i).getString("title"));

						kriminal.setKlasifikasi_kriminal(crimeJson.getJSONObject(i).getString("klasifikasi_kriminal"));
						kriminal.setKecamatan(crimeJson.getJSONObject(i).getString("kecamatan"));
						kriminal.setUser_full_name(crimeJson.getJSONObject(i).getString("full_name"));
						kriminal.setFilename(crimeJson.getJSONObject(i).getString("filename"));

						itemCrime.add(kriminal);
					}

					newsUpdateListAdapter = new NewsUpdateListAdapter(HomeFragment.this.getActivity(),itemCrime);
					newsUpdateListAdapter.setmOnItemClickListener(new NewsUpdateListAdapter.OnItemClickListener() {
						@Override
						public void onItemClick(View view, Kriminal obj, int position) {
							PostDialog postDialog = new PostDialog(HomeFragment.this.getActivity());
							postDialog.setKriminal(obj);
							postDialog.show();
						}
					});
					recyclerViewNews.setAdapter(newsUpdateListAdapter);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
		super.onResume();
	}
}