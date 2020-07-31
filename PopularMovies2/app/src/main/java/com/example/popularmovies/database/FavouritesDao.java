package com.example.popularmovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavouritesDao {

    @Query ("SELECT * FROM favourites ORDER BY id")
    LiveData<List<FavouritesEntry>> loadAllFavourites();

    @Insert
    void insertFavourite(FavouritesEntry favouritesEntry);

    @Delete
    void deleteFavourite(FavouritesEntry favouritesEntry);
}
