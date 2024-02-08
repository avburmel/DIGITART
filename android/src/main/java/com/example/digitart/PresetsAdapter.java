package com.example.digitart;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PresetsAdapter extends RecyclerView.Adapter<PresetsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Presets> presets;

    PresetsAdapter(Context context, List<Presets> presets) {
        this.presets = presets;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public PresetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_presets, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PresetsAdapter.ViewHolder holder, int position) {
        Presets state = presets.get(position);
        holder.nameView.setText(state.getName());
        holder.nameView.setTextColor(state.getColor());
    }

    @Override
    public int getItemCount() {
        return presets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        ViewHolder(View view){
            super(view);
            nameView = view.findViewById(R.id.name);
        }
    }
}
