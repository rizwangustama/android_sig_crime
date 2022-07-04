package com.rizwan.sigcrime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rizwan.sigcrime.R;
import com.rizwan.sigcrime.R;
import com.rizwan.sigcrime.dao.Kriminal;
import com.rizwan.sigcrime.utilities.UtilClass;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsUpdateListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<Kriminal> items = new ArrayList<>();
	private Context ctx;
	private OnItemClickListener mClickListener;

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder vh;
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_update, parent, false);
		vh = new ViewHolder(v);
		return vh;
	}

	public interface OnItemClickListener {
		void onItemClick(View view, Kriminal obj, int position);
	}

	public void setmOnItemClickListener(final OnItemClickListener mOnItemClickListener){
		this.mClickListener = mOnItemClickListener;
	}

	public NewsUpdateListAdapter(Context context, List<Kriminal> items){
		this.items = items;
		ctx = context;
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		// each data item is just a string in this case
		public ImageView image;
		public TextView item_kec_name,item_kasus,item_date, item_title;
		public MaterialRippleLayout itemContainer;

		public ViewHolder(View v) {
			super(v);
			image = (ImageView) v.findViewById(R.id.image_news);
			item_kec_name = (TextView) v.findViewById(R.id.kecamatan);
			item_title =(TextView) v.findViewById(R.id.title);
			item_kasus = (TextView) v.findViewById(R.id.klasifikasi_kasus);
			item_date = (TextView) v.findViewById(R.id.crime_date);
			itemContainer = v.findViewById(R.id.itemCrime);

			itemContainer.setOnClickListener(this);
		}
		@Override
		public void onClick(View view) {
			if (mClickListener != null) mClickListener.onItemClick(view, items.get(getAdapterPosition()),getAdapterPosition());
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof NewsUpdateListAdapter.ViewHolder) {
			NewsUpdateListAdapter.ViewHolder view = (NewsUpdateListAdapter.ViewHolder) holder;
			final Kriminal o = items.get(position);
			view.item_kec_name.setText(o.getKecamatan());
			view.item_kasus.setText(o.getKlasifikasi_kriminal());
			view.item_title.setText(o.getTitle());
			String dateString = o.getTanggal_kejadian() + " "+ o.getWaktu_kejadian();
			String crimeDate = UtilClass.stringToDate(dateString, "yyyy-MM-dd HH:mm:ss","dd MMMM yyyy, HH:mm");
			view.item_date.setText(crimeDate);

			String imageURI = UtilClass.serverUri+"Assets/api/appscript/postnews/lampiran/"+o.getFilename();
			Picasso.with(ctx)
					.load(imageURI)
					.centerCrop()
					.noFade()
					.resize(100,100)
					.into(view.image);

		}
	}

	@Override
	public int getItemCount() {
		return items.size();
	}
}
