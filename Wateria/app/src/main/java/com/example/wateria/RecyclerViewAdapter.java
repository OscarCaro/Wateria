package com.example.wateria;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wateria.DataStructures.Plant;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<Plant> plantList;
    //private Context context;
    WeakReference<Context> mContextWeakReference;

    public RecyclerViewAdapter (Context currentContext, ArrayList<Plant> myPlantList){
        plantList = myPlantList;
        this.mContextWeakReference = new WeakReference<Context>(currentContext);
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        Context context = mContextWeakReference.get();if (context != null) {

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

        final Plant currentPlant = plantList.get(position);

        final String plantName = String.valueOf(currentPlant.getPlantName());
        holder.textViewPlantName.setText(plantName);

        final String daysRemaining = Integer.toString(currentPlant.getDaysRemaining());
        holder.textViewDaysRemaining.setText(daysRemaining);

        int numberOfDays = Integer.parseInt(daysRemaining);
        final String days = context.getResources().getQuantityString(R.plurals.days, numberOfDays);
        holder.textViewStringDays.setText(days);

        int codeImage = currentPlant.getImageCode();
        switch (codeImage) {
            case 101:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cactus_1));
                break;
            case 102:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cactus_2));
                break;
            case 103:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cactus_3));
                break;
            case 104:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cactus_4));
                break;
            case 105:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cactus_5));
                break;
            case 106:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cactus_6));
                break;
            case 107:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cactus_7_concara));
                break;
            case 201:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_1));
                break;
            case 202:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_2_snakeplant));
                break;
            case 203:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_3_sansevieria));
                break;
            case 204:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_4_hanging));
                break;
            case 205:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_5_spiderplant));
                break;
            case 206:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_6_ivy));
                break;
            case 207:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_7_bamboo));
                break;
            case 208:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_8_monstera));
                break;
            case 209:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_9_monsteraleaf));
                break;
            case 210:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_10));
                break;
            case 301:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flower_1_red));
                break;
            case 302:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flower_2_orange));
                break;
            case 303:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flower_3_yellow));
                break;
            case 304:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flower_4_two));
                break;
            case 305:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flower_5));
                break;
            case 306:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flower_6_rose));
                break;
            case 401:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_propagation_1));
                break;
            case 402:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_propagation_2));
                break;
            case 403:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_propagation_3));
                break;
            case 501:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tree_1_bush));
                break;
            case 502:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tree_2_dracaena));
                break;
            case 503:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tree_3_joshuatree_jade));
                break;
            case 504:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tree_4_palm));
                break;
            case 505:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tree_5_pine));
                break;
            case 506:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tree_6_bonsai));
                break;
            case 601:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_1_lettuce));
                break;
            case 602:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_2_carrot));
                break;
            case 603:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_3_onion));
                break;
            case 604:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_4_onion2));
                break;
            case 605:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_5_garlic));
                break;
            case 606:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_6_general));
                break;
            case 607:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_7_tomato));
                break;
            case 608:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_8_eggplant));
                break;
            case 609:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_9_greenpepper));
                break;
            case 610:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_10_redpepper));
                break;
            case 611:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_11_avocado));
                break;
            case 612:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_veggies_12_strawberry));
                break;
            default:
                holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_common_10));
                break;
        }
    }

    @Override
    public int getItemCount(){
        return plantList.size();
    }
}
