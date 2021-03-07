package com.wateria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.wateria.Activities.MainActivity;
import com.wateria.DataStructures.Plant;
import com.wateria.DataStructures.PlantList;

import java.lang.ref.WeakReference;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private PlantList plantList;
    WeakReference<Context> mContextWeakReference;

    public RecyclerViewAdapter (Context currentContext, PlantList myPlantList){
        this.plantList = myPlantList;
        this.mContextWeakReference = new WeakReference<Context>(currentContext);
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        Context context = mContextWeakReference.get();
        if (context != null) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_view_layout, parent, false);

            return new ViewHolder(itemView, context);
        }

        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPlantName;
        public TextView textViewDaysRemaining;
        public TextView textViewStringDays;
        public ImageView imageViewIcon;
        public AppCompatImageButton buttonWatering;

        public ViewHolder (View v, final Context context){
            super(v);

            textViewPlantName = v.findViewById(R.id.text_name);
            textViewDaysRemaining = v.findViewById(R.id.text_number);
            textViewStringDays = v.findViewById(R.id.text_days);
            imageViewIcon = v.findViewById(R.id.image);
            buttonWatering = v.findViewById(R.id.button);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) context).onRowClicked(getAdapterPosition());
                }
            });

            buttonWatering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) context).onWateringButtonClicked(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, int position){
        Context context = mContextWeakReference.get();

        if (context == null) {
            return;
        }

        Plant currentPlant = plantList.get(position);

        String plantName = currentPlant.getPlantName();
        holder.textViewPlantName.setText(plantName);

        int daysRemaining = currentPlant.getDaysRemaining();
        holder.textViewDaysRemaining.setText(Integer.toString(daysRemaining));

        String days = context.getResources().getQuantityString(R.plurals.days, daysRemaining);
        holder.textViewStringDays.setText(days);

        holder.imageViewIcon.setImageDrawable(currentPlant.getIcon());
    }

    @Override
    public int getItemCount(){
        return plantList.getSize();
    }
}
