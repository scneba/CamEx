package com.thestk.camex.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Neba on 07-Oct-17.
 */

public class DataModel implements Parcelable {
    public static final Creator<DataModel> CREATOR = new Creator<DataModel>() {
        @Override
        public DataModel createFromParcel(Parcel in) {
            return new DataModel(in);
        }

        @Override
        public DataModel[] newArray(int size) {
            return new DataModel[size];
        }
    };
    private int id;
    private int data_id;
    private String type;
    private String title;
    private String artist_actors;
    private String music_genre;
    private String synop_desc;
    private String contacts;
    private String address;
    private String image;
    private String youtube_id;
    private int views;
    private float rating;
    private int likes;
    private String update_views;
    private double longitude;
    private double latitude;
    private String created_at;
    private int total_ratings;
    private String user_has_rated;
    private String user_has_liked;
    private int user_rating;
    private float aspect_ratio;


    public DataModel() {
    }

    protected DataModel(Parcel in) {
        id = in.readInt();
        data_id = in.readInt();
        type = in.readString();
        title = in.readString();
        artist_actors = in.readString();
        music_genre = in.readString();
        synop_desc = in.readString();
        contacts = in.readString();
        address = in.readString();
        image = in.readString();
        aspect_ratio = in.readFloat();
        youtube_id = in.readString();
        views = in.readInt();
        rating = in.readInt();
        likes = in.readInt();
        update_views = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        created_at = in.readString();
        total_ratings = in.readInt();
        user_has_rated = in.readString();
        user_has_liked = in.readString();
        user_rating = in.readInt();

    }

    public static Creator<DataModel> getCREATOR() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(data_id);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(artist_actors);
        dest.writeString(music_genre);
        dest.writeString(synop_desc);
        dest.writeString(contacts);
        dest.writeString(address);
        dest.writeString(image);
        dest.writeFloat(aspect_ratio);
        dest.writeString(youtube_id);
        dest.writeInt(views);
        dest.writeFloat(rating);
        dest.writeInt(likes);
        dest.writeString(update_views);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(created_at);
        dest.writeInt(total_ratings);
        dest.writeString(user_has_rated);
        dest.writeString(user_has_liked);
        dest.writeInt(user_rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //setter for music data
    public void setMusicData(int id, int music_id, String type, String title, String artist_actors, String music_genre, String youtube_id,
                             String image, float aspect_ratio, int views, float rating, int total_ratings, int likes
            , String update_views, String created_at, String user_has_liked, String user_has_rated, int user_rating) {
        this.id = id;
        this.data_id = music_id;
        this.type = type;
        this.title = title;
        this.music_genre = music_genre;
        this.artist_actors = artist_actors;
        this.youtube_id = youtube_id;
        this.views = views;
        this.rating = rating;
        this.total_ratings = total_ratings;
        this.likes = likes;
        this.image = image;
        this.aspect_ratio = aspect_ratio;
        this.update_views = update_views;
        this.created_at = created_at;
        this.user_has_rated = user_has_rated;
        this.user_has_liked = user_has_liked;
        this.user_rating = user_rating;


    }
    //getters

    //setter for music data
    public void setMovieData(int id, int movie_id, String type, String title, String artist_actors, String synopsis, String contact, String music_genre, String youtube_id,
                             String image, float aspect_ratio, int views, float rating, int total_ratings, int likes
            , String update_views, String created_at, String user_has_liked, String user_has_rated, int user_rating) {
        this.id = id;
        this.data_id = movie_id;
        this.type = type;
        this.title = title;
        this.music_genre = music_genre;
        this.artist_actors = artist_actors;
        this.synop_desc = synopsis;
        this.contacts = contact;
        this.youtube_id = youtube_id;
        this.views = views;
        this.rating = rating;
        this.total_ratings = total_ratings;
        this.likes = likes;
        this.image = image;
        this.aspect_ratio = aspect_ratio;
        this.update_views = update_views;
        this.created_at = created_at;
        this.user_has_rated = user_has_rated;
        this.user_has_liked = user_has_liked;
        this.user_rating = user_rating;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getData_id() {
        return data_id;
    }

    public void setData_id(int data_id) {
        this.data_id = data_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist_actors() {
        return artist_actors;
    }

    public void setArtist_actors(String artist_actors) {
        this.artist_actors = artist_actors;
    }

    public String getMusic_genre() {
        return music_genre;
    }

    public void setMusic_genre(String music_genre) {
        this.music_genre = music_genre;
    }

    public String getSynop_desc() {
        return synop_desc;
    }

    public void setSynop_desc(String synop_desc) {
        this.synop_desc = synop_desc;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public float getRating() {
        return rating;

    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getTotal_ratings() {
        return total_ratings;
    }

    public void setTotal_ratings(int total_ratings) {
        this.total_ratings = total_ratings;
    }

    public String getUser_has_rated() {
        return user_has_rated;
    }

    public void setUser_has_rated(String user_has_rated) {
        this.user_has_rated = user_has_rated;
    }

    public String getUser_has_liked() {
        return user_has_liked;
    }

    public void setUser_has_liked(String user_has_liked) {
        this.user_has_liked = user_has_liked;
    }

    public int getUser_rating() {
        return user_rating;
    }

    public void setUser_rating(int user_rating) {
        this.user_rating = user_rating;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUpdate_views() {
        return update_views;
    }

    public void setUpdate_views(String update_views) {
        this.update_views = update_views;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Float getAspect_ratio() {
        return this.aspect_ratio;
    }

    public void setAspect_ratio(Float aspect_ratio) {
        this.aspect_ratio = aspect_ratio;
    }
}
