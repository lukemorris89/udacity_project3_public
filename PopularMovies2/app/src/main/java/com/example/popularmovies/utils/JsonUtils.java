package com.example.popularmovies.utils;

import android.util.Log;

import com.example.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class JsonUtils {
    public static final String LOG_TAG = JsonUtils.class.getSimpleName();

    private JsonUtils() {
    }

    // Create new ArrayList of Movie objects from the JSON retrieved from the String stringUrl provided.
    public static ArrayList<Movie> parseMovies(String stringUrl) {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            // Build the URL to be queried using stringUrl input
            URL url = NetworkUtils.buildUrl(stringUrl);
            // Make a HTTP request to the url's endpoint and parse the data into a JSON object
            JSONObject resultsReturned = NetworkUtils.makeHttpRequest(url);
            JSONArray root = resultsReturned.optJSONArray("results");
            for (int i = 0; i < root.length(); i++) {
                JSONObject movieObject = root.optJSONObject(i);
                String title = movieObject.optString("original_title");
//            Release Date is returned as format "YYYY-MM-dd" and so this is formatted to a more
//            human-readable format
                int movieId = movieObject.optInt("id");
                String releaseDateRaw = movieObject.optString("release_date");
                String releaseDate = dateFormat(releaseDateRaw);
                String overview = movieObject.optString("overview");
                String posterPath = movieObject.optString("poster_path");
                double voteAverage = movieObject.optDouble("vote_average");

                // Movie object created using retrieved data
                Movie movie = new Movie(movieId, title, releaseDate, overview, posterPath, voteAverage);
                movies.add(movie);
            }
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving movies: ", e);
        }
        return movies;
    }

    // Create new ArrayList of trailer Strings from the JSON retrieved from the String stringUrl provided.
    public static ArrayList<String> parseTrailers(String stringUrl) {
        ArrayList<String> trailers = new ArrayList<>();
        try {
            // Build the URL to be queried using stringUrl input
            URL url = NetworkUtils.buildUrl(stringUrl);
            // Make a HTTP request to the url's endpoint and parse the data into a JSON object
            JSONObject resultsReturned = NetworkUtils.makeHttpRequest(url);
            JSONArray root = resultsReturned.optJSONArray("results");
            for (int i = 0; i < root.length(); i++) {
                JSONObject movieObject = root.optJSONObject(i);
                String trailerKey = movieObject.optString("key");
                trailers.add(trailerKey);
            }
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving trailers: ", e);
        }
        return trailers;
    }

    // Create new ArrayList of review Strings from the JSON retrieved from the String stringUrl provided.
    public static ArrayList<String> parseReviews(String stringUrl) {
        ArrayList<String> reviews = new ArrayList<>();
        try {
            // Build the URL to be queried using stringUrl input
            URL url = NetworkUtils.buildUrl(stringUrl);
            // Make a HTTP request to the url's endpoint and parse the data into a JSON object
            JSONObject resultsReturned = NetworkUtils.makeHttpRequest(url);
            JSONArray root = resultsReturned.optJSONArray("results");
            for (int i = 0; i < root.length(); i++) {
                JSONObject movieObject = root.optJSONObject(i);
                String reviewContent = movieObject.optString("content");
                reviews.add(reviewContent);
            }
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving trailers: ", e);
        }
        return reviews;
    }

//    Format Release Date from "YYYY-MM-dd" to "d MMM YYYY"
    private static String dateFormat(String dateString) {
        String formattedDate = "";
        Locale locale = Locale.ENGLISH;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", locale).parse(dateString);
            return new SimpleDateFormat("dd MMM yyyy", locale).format(date);
        }
        catch (ParseException e) {
            Log.e(LOG_TAG, "Unable to parse date: ", e);
        }
        return formattedDate;
    }
}

