package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.database.FavouritesEntry;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private static final String ROOT_MAIN_URL = "https://api.themoviedb.org/3/movie/";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private String apiKey;
    private String mOrderBy;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingBar;
    private TextView mErrorTextView;
    private TextView mNoFavouritesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.api_key);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingBar = findViewById(R.id.loading_bar);
        mErrorTextView = findViewById(R.id.tv_error_empty);
        mNoFavouritesTextView = findViewById(R.id.tv_no_favourites);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mOrderBy = preferences.getString("order_by", getString(R.string.settings_order_by_popularity_value));

        if (!NetworkUtils.isOnline(this)) {
            mLoadingBar.setVisibility(View.INVISIBLE);
            mErrorTextView.setText(getText(R.string.no_network_connection));
        }
        else {
            loadMovieData();
        }
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Intent intentViewDetails = new Intent(context, MovieDetailsActivity.class);
        intentViewDetails.putExtra("movie", movie);
        startActivity(intentViewDetails);
    }

    public class FetchMoviePostersTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... string) {
            String mUrlString = Uri.parse(ROOT_MAIN_URL).buildUpon()
                    .appendPath(mOrderBy)
                    .appendQueryParameter("api_key", apiKey)
                    .build()
                    .toString();
            return JsonUtils.parseMovies(mUrlString);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            mLoadingBar.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMoviePostersView();
                mMovieAdapter.setMovieData(movies);
            }
            else {
                showErrorMessage();
            }
        }
    }

    private void loadMovieData() {
        if (apiKey.equals("")) {
            showErrorMessage();
            mLoadingBar.setVisibility(View.INVISIBLE);
            mErrorTextView.setText(R.string.api_missing);
        }
        else {
            showMoviePostersView();
            if (mOrderBy.equals("show_favourites")) {
                mLoadingBar.setVisibility(View.INVISIBLE);
                setUpViewModel();
            }
            else {
                new FetchMoviePostersTask().execute();
            }
        }
    }

    private void showMoviePostersView() {
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intentSettings = new Intent(this, SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpViewModel() {
        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavourites().observe(this, new Observer<List<FavouritesEntry>>() {
            @Override
            public void onChanged(List<FavouritesEntry> favouritesEntries) {
                viewModel.getFavourites().removeObserver(this);
                Log.d(LOG_TAG, "Querying database for favourites.");
                loadFavourites(favouritesEntries);
            }
        });
    }

    // Loads received Favourites into RecyclerView to be displayed on main screen
    private void loadFavourites(List<FavouritesEntry> favouritesEntries) {
        // Create ArrayList for storing all favourites
        ArrayList<Movie> movies = new ArrayList<>();
        // For each favourite returned, create a new movie object and pass it to the adapter
        for (int i = 0; i < favouritesEntries.size(); i++) {
            FavouritesEntry favouritesEntry = favouritesEntries.get(i);
            Movie movie = new Movie(favouritesEntry.getMovieId(),
                    favouritesEntry.getTitle(),
                    favouritesEntry.getReleaseDate(),
                    favouritesEntry.getOverview(),
                    favouritesEntry.getPosterUrl(),
                    favouritesEntry.getVoteAverage());
            movies.add(movie);
        }
        // If no favourites have been added, inform user
        if (movies.size() == 0) {
            mNoFavouritesTextView.setVisibility(View.VISIBLE);
        }
        // Otherwise show all favourites
        else {
            mNoFavouritesTextView.setVisibility(View.INVISIBLE);
            mMovieAdapter.setMovieData(movies);
        }
    }
}
