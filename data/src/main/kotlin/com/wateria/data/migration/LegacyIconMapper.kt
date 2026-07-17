@file:Suppress("MagicNumber")

package com.wateria.data.migration

import com.wateria.domain.model.PlantIcon

object LegacyIconMapper {
    private val stableKeysByLegacyId =
        mapOf(
            0x7f080071 to "cactus_01",
            0x7f080072 to "cactus_02",
            0x7f080073 to "cactus_03",
            0x7f080074 to "cactus_04",
            0x7f080075 to "cactus_05",
            0x7f080076 to "cactus_06",
            0x7f080077 to "cactus_07",
            0x7f080079 to "common_01",
            0x7f08007a to "common_10",
            0x7f08007b to "common_02_snake_plant",
            0x7f08007c to "common_03_sansevieria",
            0x7f08007d to "common_04_hanging",
            0x7f08007e to "common_05_spider_plant",
            0x7f08007f to "common_06_ivy",
            0x7f080080 to "common_07_bamboo",
            0x7f080081 to "common_08_monstera",
            0x7f080082 to "common_09_monstera_leaf",
            0x7f080083 to "flower_01_red",
            0x7f080084 to "flower_02_orange",
            0x7f080085 to "flower_03_yellow",
            0x7f080086 to "flower_04_pair",
            0x7f080087 to "flower_05",
            0x7f080088 to "flower_06_rose",
            0x7f080091 to "propagation_01",
            0x7f080092 to "propagation_02",
            0x7f080093 to "propagation_03",
            0x7f080094 to "tree_01_bush",
            0x7f080095 to "tree_02_dracaena",
            0x7f080096 to "tree_03_joshua_jade",
            0x7f080097 to "tree_04_palm",
            0x7f080098 to "tree_05_pine",
            0x7f080099 to "tree_06_bonsai",
            0x7f08009a to "vegetable_10_red_pepper",
            0x7f08009b to "vegetable_11_avocado",
            0x7f08009c to "vegetable_12_strawberry",
            0x7f08009d to "vegetable_01_lettuce",
            0x7f08009e to "vegetable_02_carrot",
            0x7f08009f to "vegetable_03_onion",
            0x7f0800a0 to "vegetable_04_onion",
            0x7f0800a1 to "vegetable_05_garlic",
            0x7f0800a2 to "vegetable_06_general",
            0x7f0800a3 to "vegetable_07_tomato",
            0x7f0800a4 to "vegetable_08_eggplant",
            0x7f0800a5 to "vegetable_09_green_pepper"
        )

    val legacyIds: Set<Int> = stableKeysByLegacyId.keys

    fun map(legacyId: Int): PlantIcon =
        stableKeysByLegacyId[legacyId]?.let(PlantIcon::fromKey) ?: PlantIcon.UnknownLegacy
}
