package com.example.popularmovies.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "favourites")
public class FavouritesEntry {

    @PrimaryKey (autoGenerate = true)
    private int id;
    private int movieId;
    private String title;
    private String releaseDate;
    private String overview;
    private String posterUrl;
    private double voteAverage;

    @Ignore
    public FavouritesEntry(int movieId, String title, String releaseDate, String overview, String posterUrl, double voteAverage) {
        this.movieId = movieId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.posterUrl = posterUrl;
        this.voteAverage = voteAverage;
    }

    public FavouritesEntry(int id, int movieId, String title, String releaseDate, String overview, String posterUrl, double voteAverage) {
        this.id = id;
        this.movieId = movieId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.posterUrl = posterUrl;
        this.voteAverage = voteAverage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }


    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public double getVoteAverage() {
        return voteAverage;
    }
}
