package com.example.popularmovies.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavouritesEntry.class}, version = 1, exportSchema = false)
public abstract class FavouritesDatabase extends RoomDatabase {
    private static final String LOG_TAG = FavouritesDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favouriteslist";
    private static FavouritesDatabase sInstance;

    public static FavouritesDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        FavouritesDatabase.class,
                        FavouritesDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract FavouritesDao favouritesDao();
}
