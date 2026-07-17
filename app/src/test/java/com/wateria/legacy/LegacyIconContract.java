package com.wateria.legacy;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class LegacyIconContract {

    private LegacyIconContract() {
    }

    static Map<Integer, String> stableKeysByLegacyId() {
        Map<Integer, String> icons = new LinkedHashMap<>();
        icons.put(0x7f080071, "cactus_01");
        icons.put(0x7f080072, "cactus_02");
        icons.put(0x7f080073, "cactus_03");
        icons.put(0x7f080074, "cactus_04");
        icons.put(0x7f080075, "cactus_05");
        icons.put(0x7f080076, "cactus_06");
        icons.put(0x7f080077, "cactus_07");
        icons.put(0x7f080079, "common_01");
        icons.put(0x7f08007a, "common_10");
        icons.put(0x7f08007b, "common_02_snake_plant");
        icons.put(0x7f08007c, "common_03_sansevieria");
        icons.put(0x7f08007d, "common_04_hanging");
        icons.put(0x7f08007e, "common_05_spider_plant");
        icons.put(0x7f08007f, "common_06_ivy");
        icons.put(0x7f080080, "common_07_bamboo");
        icons.put(0x7f080081, "common_08_monstera");
        icons.put(0x7f080082, "common_09_monstera_leaf");
        icons.put(0x7f080083, "flower_01_red");
        icons.put(0x7f080084, "flower_02_orange");
        icons.put(0x7f080085, "flower_03_yellow");
        icons.put(0x7f080086, "flower_04_pair");
        icons.put(0x7f080087, "flower_05");
        icons.put(0x7f080088, "flower_06_rose");
        icons.put(0x7f080091, "propagation_01");
        icons.put(0x7f080092, "propagation_02");
        icons.put(0x7f080093, "propagation_03");
        icons.put(0x7f080094, "tree_01_bush");
        icons.put(0x7f080095, "tree_02_dracaena");
        icons.put(0x7f080096, "tree_03_joshua_jade");
        icons.put(0x7f080097, "tree_04_palm");
        icons.put(0x7f080098, "tree_05_pine");
        icons.put(0x7f080099, "tree_06_bonsai");
        icons.put(0x7f08009a, "vegetable_10_red_pepper");
        icons.put(0x7f08009b, "vegetable_11_avocado");
        icons.put(0x7f08009c, "vegetable_12_strawberry");
        icons.put(0x7f08009d, "vegetable_01_lettuce");
        icons.put(0x7f08009e, "vegetable_02_carrot");
        icons.put(0x7f08009f, "vegetable_03_onion");
        icons.put(0x7f0800a0, "vegetable_04_onion");
        icons.put(0x7f0800a1, "vegetable_05_garlic");
        icons.put(0x7f0800a2, "vegetable_06_general");
        icons.put(0x7f0800a3, "vegetable_07_tomato");
        icons.put(0x7f0800a4, "vegetable_08_eggplant");
        icons.put(0x7f0800a5, "vegetable_09_green_pepper");
        return Collections.unmodifiableMap(icons);
    }
}
