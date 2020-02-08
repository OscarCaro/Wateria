package com.example.wateria.DataStructures;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.wateria.R;

import java.util.ArrayList;
import java.util.Arrays;

public class IconGenerator {

    private ArrayList<Drawable> list;

    public IconGenerator(Context context){

        list = new ArrayList<Drawable>(Arrays.asList(

                context.getResources().getDrawable(R.drawable.ic_cactus_1),
                context.getResources().getDrawable(R.drawable.ic_cactus_2),
                context.getResources().getDrawable(R.drawable.ic_cactus_3),
                context.getResources().getDrawable(R.drawable.ic_cactus_4),
                context.getResources().getDrawable(R.drawable.ic_cactus_5),
                context.getResources().getDrawable(R.drawable.ic_cactus_6),
                context.getResources().getDrawable(R.drawable.ic_cactus_7_concara),

                context.getResources().getDrawable(R.drawable.ic_common_1),
                context.getResources().getDrawable(R.drawable.ic_common_2_snakeplant),
                context.getResources().getDrawable(R.drawable.ic_common_3_sansevieria),
                context.getResources().getDrawable(R.drawable.ic_common_4_hanging),
                context.getResources().getDrawable(R.drawable.ic_common_5_spiderplant),
                context.getResources().getDrawable(R.drawable.ic_common_6_ivy),
                context.getResources().getDrawable(R.drawable.ic_common_7_bamboo),
                context.getResources().getDrawable(R.drawable.ic_common_8_monstera),
                context.getResources().getDrawable(R.drawable.ic_common_9_monsteraleaf),
                context.getResources().getDrawable(R.drawable.ic_common_10),

                context.getResources().getDrawable(R.drawable.ic_flower_1_red),
                context.getResources().getDrawable(R.drawable.ic_flower_2_orange),
                context.getResources().getDrawable(R.drawable.ic_flower_3_yellow),
                context.getResources().getDrawable(R.drawable.ic_flower_4_two),
                context.getResources().getDrawable(R.drawable.ic_flower_5),
                context.getResources().getDrawable(R.drawable.ic_flower_6_rose),

                context.getResources().getDrawable(R.drawable.ic_propagation_1),
                context.getResources().getDrawable(R.drawable.ic_propagation_2),
                context.getResources().getDrawable(R.drawable.ic_propagation_3),

                context.getResources().getDrawable(R.drawable.ic_tree_1_bush),
                context.getResources().getDrawable(R.drawable.ic_tree_2_dracaena),
                context.getResources().getDrawable(R.drawable.ic_tree_3_joshuatree_jade),
                context.getResources().getDrawable(R.drawable.ic_tree_4_palm),
                context.getResources().getDrawable(R.drawable.ic_tree_5_pine),
                context.getResources().getDrawable(R.drawable.ic_tree_6_bonsai),

                context.getResources().getDrawable(R.drawable.ic_veggies_1_lettuce),
                context.getResources().getDrawable(R.drawable.ic_veggies_2_carrot),
                context.getResources().getDrawable(R.drawable.ic_veggies_3_onion),
                context.getResources().getDrawable(R.drawable.ic_veggies_4_onion2),
                context.getResources().getDrawable(R.drawable.ic_veggies_5_garlic),
                context.getResources().getDrawable(R.drawable.ic_veggies_6_general),
                context.getResources().getDrawable(R.drawable.ic_veggies_7_tomato),
                context.getResources().getDrawable(R.drawable.ic_veggies_8_eggplant),
                context.getResources().getDrawable(R.drawable.ic_veggies_9_greenpepper),
                context.getResources().getDrawable(R.drawable.ic_veggies_10_redpepper),
                context.getResources().getDrawable(R.drawable.ic_veggies_11_avocado),
                context.getResources().getDrawable(R.drawable.ic_veggies_12_strawberry)

        ));
    }

    public Drawable getDrawable(int idx){
        return list.get(idx);
    }

    public int getIdx(Drawable drawable){
        return list.indexOf(drawable);
    }

    public int getSize(){
        return list.size();
    }


}
