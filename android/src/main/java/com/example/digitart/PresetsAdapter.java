package com.example.digitart;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PresetsAdapter extends RecyclerView.Adapter<PresetsAdapter.ViewHolder> {

    interface OnPresetClickListener{
        void onPresetClick(Presets preset);
    }
    private final PresetsAdapter.OnPresetClickListener onClickListener;
    private final LayoutInflater inflater;
    private final List<Presets> presets;

    PresetsAdapter(Context context, List<Presets> presets, PresetsAdapter.OnPresetClickListener onClickListener) {
        this.onClickListener = onClickListener;
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
        Presets preset = presets.get(position);
        holder.nameView.setText(preset.getName());
        holder.nameView.setTextColor(preset.getColor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onPresetClick(preset);
            }
        });
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
