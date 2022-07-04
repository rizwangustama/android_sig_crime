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
import com.rizwan.sigcrime.dao.Kecamatan;
import com.rizwan.sigcrime.utilities.UtilClass;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class KecamatanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private List<Kecamatan> items = new ArrayList<>();
	private Context ctx;
	private OnItemClickListener mClickListener;

	public interface OnItemClickListener {
		void onItemClick(View view, Kecamatan obj, int position);
	}

	public void setmOnItemClickListener(final OnItemClickListener mOnItemClickListener){
		this.mClickListener = mOnItemClickListener;
	}

	public KecamatanListAdapter(Context context, List<Kecamatan> items){
		this.items = items;
		ctx = context;
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		// each data item is just a string in this case
		public ImageView image;
		public TextView item_kec_name;
		public MaterialRippleLayout itemContainer;

		public ViewHolder(View v) {
			super(v);
			image = (ImageView) v.findViewById(R.id.map_img);
			item_kec_name = (TextView) v.findViewById(R.id.item_kec_name);
			itemContainer = v.findViewById(R.id.itemKecamatan);
			itemContainer.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mClickListener != null) mClickListener.onItemClick(v, items.get(getAdapterPosition()),getAdapterPosition());
		}
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder vh;
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img_kecamatan, parent, false);
		vh = new ViewHolder(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ViewHolder) {
			ViewHolder view = (ViewHolder) holder;
			final Kecamatan o = items.get(position);
			view.item_kec_name.setText(o.getName());
			if ( o.getFilename() != null && !o.getFilename().equals("")){

				String imageURI = UtilClass.serverUri+"Assets/image/kecamatan/"+o.getFilename();
				Picasso.with(ctx)
						.load(imageURI)
						.centerCrop()
						.noFade()
						.resize(100,100)
						.into(view.image);
			}else{
				view.image.setImageResource(R.mipmap.ic_sig_crime_round);
			}
//			view.image.setImageResource(R.drawable.ic_map_1);
//			view.image.setColorFilter(ContextCompat.getColor(ctx.getApplicationContext(),R.color.colorPrimary));
		}
	}

	@Override
	public int getItemCount() {
		return items.size();
	}
}
