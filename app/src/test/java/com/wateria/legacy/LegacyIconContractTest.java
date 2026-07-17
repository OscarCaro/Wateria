package com.wateria.legacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.wateria.R;

import org.json.JSONArray;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LegacyIconContractTest {

    @Test
    public void currentResourcesMatchFrozenV16Ids() {
        assertEquals(0x7f080071, R.drawable.ic_cactus_1);
        assertEquals(0x7f080072, R.drawable.ic_cactus_2);
        assertEquals(0x7f080073, R.drawable.ic_cactus_3);
        assertEquals(0x7f080074, R.drawable.ic_cactus_4);
        assertEquals(0x7f080075, R.drawable.ic_cactus_5);
        assertEquals(0x7f080076, R.drawable.ic_cactus_6);
        assertEquals(0x7f080077, R.drawable.ic_cactus_7_concara);
        assertEquals(0x7f080079, R.drawable.ic_common_1);
        assertEquals(0x7f08007a, R.drawable.ic_common_10);
        assertEquals(0x7f08007b, R.drawable.ic_common_2_snakeplant);
        assertEquals(0x7f08007c, R.drawable.ic_common_3_sansevieria);
        assertEquals(0x7f08007d, R.drawable.ic_common_4_hanging);
        assertEquals(0x7f08007e, R.drawable.ic_common_5_spiderplant);
        assertEquals(0x7f08007f, R.drawable.ic_common_6_ivy);
        assertEquals(0x7f080080, R.drawable.ic_common_7_bamboo);
        assertEquals(0x7f080081, R.drawable.ic_common_8_monstera);
        assertEquals(0x7f080082, R.drawable.ic_common_9_monsteraleaf);
        assertEquals(0x7f080083, R.drawable.ic_flower_1_red);
        assertEquals(0x7f080084, R.drawable.ic_flower_2_orange);
        assertEquals(0x7f080085, R.drawable.ic_flower_3_yellow);
        assertEquals(0x7f080086, R.drawable.ic_flower_4_two);
        assertEquals(0x7f080087, R.drawable.ic_flower_5);
        assertEquals(0x7f080088, R.drawable.ic_flower_6_rose);
        assertEquals(0x7f080091, R.drawable.ic_propagation_1);
        assertEquals(0x7f080092, R.drawable.ic_propagation_2);
        assertEquals(0x7f080093, R.drawable.ic_propagation_3);
        assertEquals(0x7f080094, R.drawable.ic_tree_1_bush);
        assertEquals(0x7f080095, R.drawable.ic_tree_2_dracaena);
        assertEquals(0x7f080096, R.drawable.ic_tree_3_joshuatree_jade);
        assertEquals(0x7f080097, R.drawable.ic_tree_4_palm);
        assertEquals(0x7f080098, R.drawable.ic_tree_5_pine);
        assertEquals(0x7f080099, R.drawable.ic_tree_6_bonsai);
        assertEquals(0x7f08009a, R.drawable.ic_veggies_10_redpepper);
        assertEquals(0x7f08009b, R.drawable.ic_veggies_11_avocado);
        assertEquals(0x7f08009c, R.drawable.ic_veggies_12_strawberry);
        assertEquals(0x7f08009d, R.drawable.ic_veggies_1_lettuce);
        assertEquals(0x7f08009e, R.drawable.ic_veggies_2_carrot);
        assertEquals(0x7f08009f, R.drawable.ic_veggies_3_onion);
        assertEquals(0x7f0800a0, R.drawable.ic_veggies_4_onion2);
        assertEquals(0x7f0800a1, R.drawable.ic_veggies_5_garlic);
        assertEquals(0x7f0800a2, R.drawable.ic_veggies_6_general);
        assertEquals(0x7f0800a3, R.drawable.ic_veggies_7_tomato);
        assertEquals(0x7f0800a4, R.drawable.ic_veggies_8_eggplant);
        assertEquals(0x7f0800a5, R.drawable.ic_veggies_9_greenpepper);
    }

    @Test
    public void allIconFixtureCoversEveryFrozenIdExactlyOnce() throws Exception {
        Map<Integer, String> contract = LegacyIconContract.stableKeysByLegacyId();
        JSONArray plants = new JSONArray(LegacyFixtureLoader.load("all_icons.json"));
        Set<Integer> fixtureIds = new HashSet<>();

        for (int index = 0; index < plants.length(); index++) {
            int iconId = plants.getJSONObject(index).getInt("icon");
            assertTrue("Unknown fixture icon ID: " + iconId, contract.containsKey(iconId));
            assertTrue("Duplicate fixture icon ID: " + iconId, fixtureIds.add(iconId));
        }

        assertEquals(44, contract.size());
        assertEquals(contract.keySet(), fixtureIds);
        assertEquals(44, new HashSet<>(contract.values()).size());
    }
}
