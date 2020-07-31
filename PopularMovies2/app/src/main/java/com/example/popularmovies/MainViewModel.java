package com.example.popularmovies;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmovies.database.FavouritesDatabase;
import com.example.popularmovies.database.FavouritesEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavouritesEntry>> favourites;

    public MainViewModel(@NonNull Application application) {
        super(application);
        FavouritesDatabase database = FavouritesDatabase.getInstance(this.getApplication());
        favourites = database.favouritesDao().loadAllFavourites();
    }

    public LiveData<List<FavouritesEntry>> getFavourites() {
        return favourites;
    }
}
