package com.example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.model.Movie;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private Movie mCurrentMovie;
    private ArrayList<String> mTrailerKeys;
    private TrailerAdapterOnClickHandler mClickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(String trailerKey);
    }

    public TrailerAdapter(Movie movie) {
        mCurrentMovie = movie;
    }

    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.trailer_view, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, final int position) {
        LinearLayout trailerLayout = holder.itemView.findViewById(R.id.trailer_view_linear_layout);
        TextView trailerTextView = holder.itemView.findViewById(R.id.movie_detail_trailer_text);
        trailerTextView.append(" " + (position + 1));
        final String trailerKey = mCurrentMovie.getTrailerUrls().get(position);
        trailerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerKey));

                Context context = view.getContext();
                context.startActivity(trailerIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mTrailerKeys == null) {
            return 0;
        }
        return mTrailerKeys.size();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TrailerAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.v("CLICK POS: ", "" + position);

            String trailerKey = mTrailerKeys.get(position);
            mClickHandler.onClick(trailerKey);
        }
    }

    public void setTrailerData(ArrayList<String> trailerKeys) {
        mTrailerKeys = trailerKeys;
        notifyDataSetChanged();
    }
}











