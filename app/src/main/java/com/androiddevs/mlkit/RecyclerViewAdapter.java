package com.androiddevs.mlkit;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    List<Pair<Integer, String>> elements;
    Context context;

    public RecyclerViewAdapter(Context context, List<Pair<Integer, String>> elements) {
        this.context = context;
        this.elements = elements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycler_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rlColor.setBackgroundColor(elements.get(position).first);
        holder.tvContourType.setText(elements.get(position).second);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlColor;
        TextView tvContourType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlColor = itemView.findViewById(R.id.rlColor);
            tvContourType = itemView.findViewById(R.id.tvContourType);
        }
    }
}
