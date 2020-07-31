package com.example.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

// Creates Movie objects which will be displayed on the Main and Movie Details screen
public class Movie implements Parcelable {
    private int mId;
    private String mTitle;
    private String mReleaseDate;
    private String mOverview;
    private String mPosterPath;
    private double mVoteAverage;
    private ArrayList<String> mTrailerUrls;
    private ArrayList<String> mReviews;

    public Movie(int id, String title, String releaseDate, String overview, String posterPath, double voteAverage) {
        mId = id;
        mTitle = title;
        mReleaseDate = releaseDate;
        mOverview = overview;
        mPosterPath = posterPath;
        mVoteAverage = voteAverage;
    }

    protected Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mOverview = in.readString();
        mPosterPath = in.readString();
        mVoteAverage = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mOverview);
        dest.writeString(mPosterPath);
        dest.writeDouble(mVoteAverage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getMovieId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public ArrayList<String> getTrailerUrls() {
        return mTrailerUrls;
    }

    public void setTrailerUrls(ArrayList<String> urls) {
        mTrailerUrls = urls;
    }

    public void setReviews(ArrayList<String> reviews) {
        mReviews = reviews;
    }
}

