package com.wateria;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wateria.Activities.MainActivity;
import com.wateria.DataStructures.Plant;
import com.wateria.DataStructures.PlantList;
import com.wateria.Database.DatabaseInstance;
import com.wateria.Database.PlantDatabase;
import com.wateria.Database.PlantEntity;
import com.wateria.Utils.GlideApp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SpeciesRecyclerViewAdapter extends RecyclerView.Adapter<SpeciesRecyclerViewAdapter.ViewHolder> {

    private List<PlantEntity> plantEntities;
    private Thread dbLoaderThread;
    private StorageReference firebaseStorageRef;

    WeakReference<Context> mContextWeakReference;

    public SpeciesRecyclerViewAdapter (Context currentContext){
        this.mContextWeakReference = new WeakReference<Context>(currentContext);

        //TODO: do on Activity because this is called every time the dialog shows up
        dbLoaderThread =  new Thread(new Runnable() {
            @Override
            public void run() {
                PlantDatabase database = DatabaseInstance.getInstance(mContextWeakReference.get());
                plantEntities = database.plantDao().getAll();
            }
        });
        dbLoaderThread.start();

        firebaseStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public SpeciesRecyclerViewAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        Context context = mContextWeakReference.get();
        if (context != null) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.species_adapter_view_layout, parent, false);
            return new ViewHolder(itemView, context);
        }

        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameTextView;
        private TextView secNamesTextView;

        public ViewHolder (View v, final Context context){
            super(v);

            imageView = v.findViewById(R.id.image);
            nameTextView = v.findViewById(R.id.name);
            secNamesTextView = v.findViewById(R.id.secondary_names);

            // TODO: onclick listeners
        }
    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, int position){
        Context context = mContextWeakReference.get();

        if (context == null) {
            return;
        }

        try {
            dbLoaderThread.join();
        } catch (InterruptedException e) {
            Toast.makeText(mContextWeakReference.get(), "Error loading data", Toast.LENGTH_SHORT).show();
            return;
        }

        holder.nameTextView.setText(plantEntities.get(position).name);
        holder.secNamesTextView.setText(plantEntities.get(position).url);


        StorageReference imageRef = firebaseStorageRef.child(plantEntities.get(position).url);

        GlideApp.with(context)
                .load(imageRef)
                .placeholder(R.drawable.ic_flower_1_red)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount(){
        try {
            dbLoaderThread.join();
            return plantEntities.size();
        } catch (InterruptedException e) {
            Toast.makeText(mContextWeakReference.get(), "Error loading data", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }
}
