package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.database.FavouritesDatabase;
import com.example.popularmovies.database.FavouritesEntry;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    private Movie mSelectedMovie;

    private ImageView mPosterImageView;
    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;
    private TextView mOverviewTextView;
    private TextView mNoTrailersTextView;
    private TrailerAdapter mTrailerAdapter;
    private ImageButton mFavouriteButton;

    private FavouritesDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Get correct movie clicked on from intent
        mSelectedMovie = getIntent().getParcelableExtra("movie");
        mFavouriteButton = findViewById(R.id.movie_detail_favourite_button);

        // Set up database and ViewModel so that data persists
        mDb = FavouritesDatabase.getInstance(getApplicationContext());
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavourites().observe(this, new Observer<List<FavouritesEntry>>() {
            @Override
            public void onChanged(final List<FavouritesEntry> favouritesEntries) {
                // Load favourites data and button functionality
                loadFavouriteSettings(favouritesEntries);
                mFavouriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleFavourite(favouritesEntries);
                    }
                });
            }
        });

        mPosterImageView = findViewById(R.id.movie_detail_image);
        mTitleTextView = findViewById(R.id.movie_detail_title);
        mReleaseDateTextView = findViewById(R.id.movie_detail_release_date);
        mVoteAverageTextView = findViewById(R.id.movie_detail_vote_average);
        mOverviewTextView = findViewById(R.id.movie_detail_overview);
        mNoTrailersTextView = findViewById(R.id.movie_detail_no_trailers);

        loadMovieData(mSelectedMovie);
    }

    // Load all data to be displayed for the chosen movie in Movie Details view
    public void loadMovieData(Movie movie) {
        // Load movie poster
        final String ROOT_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
        String posterUrl = ROOT_POSTER_URL + movie.getPosterPath();
        Picasso.get().load(posterUrl).into(mPosterImageView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        new FetchTrailersTask().execute(mSelectedMovie);
        new FetchReviewsTask().execute(mSelectedMovie);

        // Set up RecyclerView to hold trailers
        RecyclerView recyclerView = findViewById(R.id.trailer_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(mSelectedMovie);
        recyclerView.setAdapter(mTrailerAdapter);

        // Populate all other text views
        mTitleTextView.setText(movie.getTitle());
        mReleaseDateTextView.setText(movie.getReleaseDate());
        mVoteAverageTextView.setText(String.valueOf(movie.getVoteAverage()));
        mOverviewTextView.setText(movie.getOverview());
    }

    public class FetchTrailersTask extends AsyncTask<Movie, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Movie... movies) {
            // Fetch the selected movie's trailers from API
            Movie movie = movies[0];
            final String ROOT_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
            String trailerUrl = ROOT_MOVIE_URL + movie.getMovieId() + "/videos?api_key=" + getString(R.string.api_key);
            return JsonUtils.parseTrailers(trailerUrl);
        }

        @Override
        protected void onPostExecute(ArrayList<String> trailerUrls) {
            // If there are trailers available, display these in a RecyclerView
            if (trailerUrls != null) {
                mSelectedMovie.setTrailerUrls(trailerUrls);
                mTrailerAdapter.setTrailerData(trailerUrls);
            }
            // If there are no trailers, display a message informing user
            else {
                mNoTrailersTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<Movie, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Movie... movies) {
            // Fetch the selected movie's reviews from the API
            Movie movie = movies[0];
            final String ROOT_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
            String reviewUrl = ROOT_MOVIE_URL + movie.getMovieId() + "/reviews?api_key=" + getString(R.string.api_key);
            return JsonUtils.parseReviews(reviewUrl);
        }

        @Override
        protected void onPostExecute(ArrayList<String> reviews) {
            // If reviews exist, append them to review textview
            if (reviews != null) {
                mSelectedMovie.setReviews(reviews);
                TextView reviewTextView = findViewById(R.id.movie_detail_reviews_text);
                // If no reviews exist, inform user
                if (reviews.size() == 0) {
                    reviewTextView.append(getString(R.string.no_reviews));
                } else {
                    for (int i = 0; i < reviews.size(); i++) {
                        reviewTextView.append(reviews.get(i) + "\n\n");
                    }
                }
            }
        }
    }

    // Load initial state of film (favourited or not) and change star icon accordingly
    public void loadFavouriteSettings(List<FavouritesEntry> favourites) {
        boolean isFavourite = false;

        //Find if movie is in favourites
        for (int i = 0; i < favourites.size(); i++) {
            if (favourites.get(i).getMovieId() == mSelectedMovie.getMovieId()) {
                isFavourite = true;
                break;
            }
        }
        if (isFavourite) {
            mFavouriteButton.setImageResource(R.drawable.outline_star_white_36);
            mFavouriteButton.setTag("Favourited");
        } else {
            mFavouriteButton.setImageResource(R.drawable.outline_star_outline_white_36);
            mFavouriteButton.setTag("Not favourited");
        }
    }

    // Switch states between favourited and not favourited, changing icon
    public void toggleFavourite(final List<FavouritesEntry> favouritesEntries) {
        if (mFavouriteButton.getTag().equals("Not favourited") || mFavouriteButton.getTag().equals("")) {
            mFavouriteButton.setImageResource(R.drawable.outline_star_white_36);
            mFavouriteButton.setTag("Favourited");

            // If not a favourite, create a new FavouritesEntry to add to database
            final FavouritesEntry favouritesEntryToAdd = new FavouritesEntry(mSelectedMovie.getMovieId(),
                    mSelectedMovie.getTitle(),
                    mSelectedMovie.getReleaseDate(),
                    mSelectedMovie.getOverview(),
                    mSelectedMovie.getPosterPath(),
                    mSelectedMovie.getVoteAverage());
            // Add favourite off main thread
            FavouritesExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb = FavouritesDatabase.getInstance(getApplicationContext());
                    mDb.favouritesDao().insertFavourite(favouritesEntryToAdd);

                }
            });
        }
        else {
            // If movie is already a favourite, unfavourite it including swapping icon
            mFavouriteButton.setImageResource(R.drawable.outline_star_outline_white_36);
            mFavouriteButton.setTag("Not favourited");
            //Search for movie in favourites
            for (int i = 0; i < favouritesEntries.size(); i++) {
                // If it exists, delete it using Executor off main thread
                if (favouritesEntries.get(i).getMovieId() == mSelectedMovie.getMovieId()) {
                    final FavouritesEntry favourite = favouritesEntries.get(i);
                    FavouritesExecutor.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.favouritesDao().deleteFavourite(favourite);
                        }
                    });
                    break;
                }
            }
        }
    }
}