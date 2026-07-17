package com.wateria.domain.model

@JvmInline
value class PlantIcon private constructor(val key: String) {
    companion object {
        const val UNKNOWN_LEGACY_KEY = "unknown_legacy"

        val knownKeys: Set<String> =
            setOf(
                "cactus_01",
                "cactus_02",
                "cactus_03",
                "cactus_04",
                "cactus_05",
                "cactus_06",
                "cactus_07",
                "common_01",
                "common_10",
                "common_02_snake_plant",
                "common_03_sansevieria",
                "common_04_hanging",
                "common_05_spider_plant",
                "common_06_ivy",
                "common_07_bamboo",
                "common_08_monstera",
                "common_09_monstera_leaf",
                "flower_01_red",
                "flower_02_orange",
                "flower_03_yellow",
                "flower_04_pair",
                "flower_05",
                "flower_06_rose",
                "propagation_01",
                "propagation_02",
                "propagation_03",
                "tree_01_bush",
                "tree_02_dracaena",
                "tree_03_joshua_jade",
                "tree_04_palm",
                "tree_05_pine",
                "tree_06_bonsai",
                "vegetable_10_red_pepper",
                "vegetable_11_avocado",
                "vegetable_12_strawberry",
                "vegetable_01_lettuce",
                "vegetable_02_carrot",
                "vegetable_03_onion",
                "vegetable_04_onion",
                "vegetable_05_garlic",
                "vegetable_06_general",
                "vegetable_07_tomato",
                "vegetable_08_eggplant",
                "vegetable_09_green_pepper"
            )

        val UnknownLegacy: PlantIcon = PlantIcon(UNKNOWN_LEGACY_KEY)

        fun fromKey(key: String): PlantIcon =
            if (key in knownKeys) PlantIcon(key) else UnknownLegacy
    }
}
