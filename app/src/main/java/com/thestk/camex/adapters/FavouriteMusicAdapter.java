package com.thestk.camex.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thestk.camex.DynamicImageView;
import com.thestk.camex.R;
import com.thestk.camex.models.DataModel;

import java.util.List;

import butterknife.Unbinder;


/**
 * Created by Neba on 08-Oct-17.
 */

public class FavouriteMusicAdapter extends RecyclerView.Adapter<FavouriteMusicAdapter.MyViewHolder> {


    private String type;
    private List<DataModel> musicList;
    private Activity activity;
    // private DBHelper dbHelper;
    private Unbinder unbinder;

    private CustomRecyclerOnClick customRecyclerOnClick = null;


    public FavouriteMusicAdapter(Activity activity, List<DataModel> musicList, CustomRecyclerOnClick customRecyclerOnClick) {
        this.activity = activity;
        this.musicList = musicList;
        this.customRecyclerOnClick = customRecyclerOnClick;
    }

    @Override
    public FavouriteMusicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_helper_cardview, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavouriteMusicAdapter.MyViewHolder holder, final int position) {

        final DataModel helper = musicList.get(position);
        holder.music_title.setText(helper.getTitle() + "\n" + helper.getArtist_actors());
        holder.type.setText(helper.getType());
        holder.thumbnail.setAspectRatio(helper.getAspect_ratio());
        // loading BuyerHelper cover using Glide library

        String imageUrl = activity.getString(R.string.imageUrl) + "/img/" + helper.getImage();
        Log.e("url", imageUrl);
        Picasso.with(activity).load(imageUrl).fit()
                .error(R.drawable.image_placeholder)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.thumbnail);


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customRecyclerOnClick != null) {
                    customRecyclerOnClick.onCustomClick(position, holder.thumbnail);
                }

            }
        });

        Log.e("activity bounded", "bound view " + position);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    /**
     * onclicklisterner for recycler view
     */
    public interface CustomRecyclerOnClick {
        void onCustomClick(int position, ImageView imageView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public DynamicImageView thumbnail;
        public View view;
        public TextView music_title;
        public TextView type;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            music_title = (TextView) view.findViewById(R.id.music_title);
            type = (TextView) view.findViewById(R.id.tv_type);
            thumbnail = (DynamicImageView) view.findViewById(R.id.thumbnail);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.rlClick);

        }
    }

}