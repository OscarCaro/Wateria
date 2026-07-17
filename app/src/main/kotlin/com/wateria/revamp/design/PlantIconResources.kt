package com.wateria.revamp.design

import androidx.annotation.DrawableRes
import com.wateria.R
import com.wateria.domain.model.PlantIcon

enum class PlantIconCategory {
    COMMON,
    FLOWER,
    CACTUS,
    TREE,
    PROPAGATION,
    VEGETABLE
}

data class PlantIconOption(
    val icon: PlantIcon,
    @param:DrawableRes val drawableRes: Int,
    val category: PlantIconCategory
)

val plantIconOptions: List<PlantIconOption> =
    listOf(
        option("common_01", R.drawable.ic_common_1, PlantIconCategory.COMMON),
        option(
            "common_02_snake_plant",
            R.drawable.ic_common_2_snakeplant,
            PlantIconCategory.COMMON
        ),
        option(
            "common_03_sansevieria",
            R.drawable.ic_common_3_sansevieria,
            PlantIconCategory.COMMON
        ),
        option("common_04_hanging", R.drawable.ic_common_4_hanging, PlantIconCategory.COMMON),
        option(
            "common_05_spider_plant",
            R.drawable.ic_common_5_spiderplant,
            PlantIconCategory.COMMON
        ),
        option("common_06_ivy", R.drawable.ic_common_6_ivy, PlantIconCategory.COMMON),
        option("common_07_bamboo", R.drawable.ic_common_7_bamboo, PlantIconCategory.COMMON),
        option("common_08_monstera", R.drawable.ic_common_8_monstera, PlantIconCategory.COMMON),
        option(
            "common_09_monstera_leaf",
            R.drawable.ic_common_9_monsteraleaf,
            PlantIconCategory.COMMON
        ),
        option("common_10", R.drawable.ic_common_10, PlantIconCategory.COMMON),
        option("flower_01_red", R.drawable.ic_flower_1_red, PlantIconCategory.FLOWER),
        option("flower_02_orange", R.drawable.ic_flower_2_orange, PlantIconCategory.FLOWER),
        option("flower_03_yellow", R.drawable.ic_flower_3_yellow, PlantIconCategory.FLOWER),
        option("flower_04_pair", R.drawable.ic_flower_4_two, PlantIconCategory.FLOWER),
        option("flower_05", R.drawable.ic_flower_5, PlantIconCategory.FLOWER),
        option("flower_06_rose", R.drawable.ic_flower_6_rose, PlantIconCategory.FLOWER),
        option("cactus_01", R.drawable.ic_cactus_1, PlantIconCategory.CACTUS),
        option("cactus_02", R.drawable.ic_cactus_2, PlantIconCategory.CACTUS),
        option("cactus_03", R.drawable.ic_cactus_3, PlantIconCategory.CACTUS),
        option("cactus_04", R.drawable.ic_cactus_4, PlantIconCategory.CACTUS),
        option("cactus_05", R.drawable.ic_cactus_5, PlantIconCategory.CACTUS),
        option("cactus_06", R.drawable.ic_cactus_6, PlantIconCategory.CACTUS),
        option("cactus_07", R.drawable.ic_cactus_7_concara, PlantIconCategory.CACTUS),
        option("tree_01_bush", R.drawable.ic_tree_1_bush, PlantIconCategory.TREE),
        option("tree_02_dracaena", R.drawable.ic_tree_2_dracaena, PlantIconCategory.TREE),
        option("tree_03_joshua_jade", R.drawable.ic_tree_3_joshuatree_jade, PlantIconCategory.TREE),
        option("tree_04_palm", R.drawable.ic_tree_4_palm, PlantIconCategory.TREE),
        option("tree_05_pine", R.drawable.ic_tree_5_pine, PlantIconCategory.TREE),
        option("tree_06_bonsai", R.drawable.ic_tree_6_bonsai, PlantIconCategory.TREE),
        option("propagation_01", R.drawable.ic_propagation_1, PlantIconCategory.PROPAGATION),
        option("propagation_02", R.drawable.ic_propagation_2, PlantIconCategory.PROPAGATION),
        option("propagation_03", R.drawable.ic_propagation_3, PlantIconCategory.PROPAGATION),
        option(
            "vegetable_01_lettuce",
            R.drawable.ic_veggies_1_lettuce,
            PlantIconCategory.VEGETABLE
        ),
        option("vegetable_02_carrot", R.drawable.ic_veggies_2_carrot, PlantIconCategory.VEGETABLE),
        option("vegetable_03_onion", R.drawable.ic_veggies_3_onion, PlantIconCategory.VEGETABLE),
        option("vegetable_04_onion", R.drawable.ic_veggies_4_onion2, PlantIconCategory.VEGETABLE),
        option("vegetable_05_garlic", R.drawable.ic_veggies_5_garlic, PlantIconCategory.VEGETABLE),
        option(
            "vegetable_06_general",
            R.drawable.ic_veggies_6_general,
            PlantIconCategory.VEGETABLE
        ),
        option("vegetable_07_tomato", R.drawable.ic_veggies_7_tomato, PlantIconCategory.VEGETABLE),
        option(
            "vegetable_08_eggplant",
            R.drawable.ic_veggies_8_eggplant,
            PlantIconCategory.VEGETABLE
        ),
        option(
            "vegetable_09_green_pepper",
            R.drawable.ic_veggies_9_greenpepper,
            PlantIconCategory.VEGETABLE
        ),
        option(
            "vegetable_10_red_pepper",
            R.drawable.ic_veggies_10_redpepper,
            PlantIconCategory.VEGETABLE
        ),
        option(
            "vegetable_11_avocado",
            R.drawable.ic_veggies_11_avocado,
            PlantIconCategory.VEGETABLE
        ),
        option(
            "vegetable_12_strawberry",
            R.drawable.ic_veggies_12_strawberry,
            PlantIconCategory.VEGETABLE
        )
    )

private val optionsByKey = plantIconOptions.associateBy { option -> option.icon.key }

fun PlantIcon.toDrawableRes(): Int = optionsByKey[key]?.drawableRes ?: R.drawable.ic_common_1

private fun option(
    key: String,
    @DrawableRes drawableRes: Int,
    category: PlantIconCategory
): PlantIconOption = PlantIconOption(PlantIcon.fromKey(key), drawableRes, category)
