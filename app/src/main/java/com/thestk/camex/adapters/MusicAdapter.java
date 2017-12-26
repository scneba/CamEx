package com.thestk.camex.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thestk.camex.DynamicImageView;
import com.thestk.camex.HelperMethods;
import com.thestk.camex.R;
import com.thestk.camex.models.DataModel;

import java.util.List;

import butterknife.Unbinder;

/**
 * Created by Neba on 08-Oct-17.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {


    private String type;
    private List<DataModel> musicList;
    private Activity activity;
    private Genre genre;
    // private DBHelper dbHelper;
    private Unbinder unbinder;

    private CustomRecyclerOnClick customRecyclerOnClick = null;


    public MusicAdapter(Activity activity, List<DataModel> musicList, Genre genre, CustomRecyclerOnClick customRecyclerOnClick) {
        this.activity = activity;
        this.musicList = musicList;
        this.type = type;
        this.genre = genre;
        this.customRecyclerOnClick = customRecyclerOnClick;
    }

    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_cardview, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MusicAdapter.MyViewHolder holder, final int position) {

        final DataModel helper = musicList.get(position);
        holder.music_title.setText(helper.getTitle() + "\n" + helper.getArtist_actors());
        holder.music_likes.setText(HelperMethods.getStringViewsFromInt(helper.getLikes(), activity.getString(R.string.likes)));
        holder.music_views.setText(HelperMethods.getStringViewsFromInt(helper.getViews(), activity.getString(R.string.views)));
        holder.number_starred.setText(HelperMethods.getStringViewsFromInt(helper.getTotal_ratings(), ""));
        holder.ratingBar.setRating(helper.getRating());
        holder.thumbnail.setAspectRatio(helper.getAspect_ratio());


        // loading BuyerHelper cover using Glide library

        String imageUrl = activity.getString(R.string.imageUrl) + "/img/" + helper.getImage();
        Log.e("url", imageUrl);
        Picasso.with(activity).load(imageUrl).fit()
                .error(R.drawable.image_placeholder)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customRecyclerOnClick != null) {
                    customRecyclerOnClick.onCustomClick(position, holder.thumbnail);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public static enum Genre {
        GOSPEL, SECULAR, TRADITIONAL;
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
        public TextView music_views;
        public TextView music_likes;
        public TextView number_starred;
        public RatingBar ratingBar;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            music_title = (TextView) view.findViewById(R.id.music_title);
            music_likes = (TextView) view.findViewById(R.id.music_likes);
            music_views = (TextView) view.findViewById(R.id.music_views);
            number_starred = (TextView) view.findViewById(R.id.number_starred);
            thumbnail = (DynamicImageView) view.findViewById(R.id.thumbnail);
            ratingBar = (RatingBar) view.findViewById(R.id.rating);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.rlClick);

        }
    }


}