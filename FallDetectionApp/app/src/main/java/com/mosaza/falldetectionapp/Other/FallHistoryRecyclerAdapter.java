package com.mosaza.falldetectionapp.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mosaza.falldetectionapp.Classes.Fall;
import com.mosaza.falldetectionapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class FallHistoryRecyclerAdapter extends RecyclerView.Adapter<FallHistoryRecyclerAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewDate, textViewLatitude, textViewLongitude;

        public ViewHolder(View itemView){
            super(itemView);
            textViewDate = itemView.findViewById(R.id.list_item_fall_date);
            textViewLatitude = itemView.findViewById(R.id.list_item_fall_latitude);
            textViewLongitude = itemView.findViewById(R.id.list_item_fall_longitude);
        }
    }

    private List<Fall> fallList;

    public FallHistoryRecyclerAdapter(List<Fall> fallList){
        this.fallList = fallList;
    }

    @NonNull
    @Override
    public FallHistoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View AdviceView = inflater.inflate(R.layout.list_item_fall_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(AdviceView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FallHistoryRecyclerAdapter.ViewHolder holder, int position) {
        final Fall current = fallList.get(position);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = dateFormat.format(current.getFallDate());
        holder.textViewDate.setText(date);
        holder.textViewLatitude.setText(("Latitude: " + current.getLatitude()));
        holder.textViewLongitude.setText(("Longitude: " + current.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return fallList.size();
    }
}
