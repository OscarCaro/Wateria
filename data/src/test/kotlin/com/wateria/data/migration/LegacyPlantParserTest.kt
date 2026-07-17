package com.wateria.data.migration

import com.wateria.domain.model.PlantIcon
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class LegacyPlantParserTest {
    private val parser = LegacyPlantParser()
    private val migrationDate = LocalDate.of(2026, 7, 17)

    @Test
    fun `all 44 frozen resource ids map to semantic icon keys`() {
        val result =
            parser.parse(
                loadLegacyFixture("all_icons.json"),
                migrationDate,
                "Recovered"
            ).asSuccess()

        assertEquals(44, result.plants.size)
        assertEquals(44, result.plants.map { it.icon.key }.toSet().size)
        assertEquals(PlantIcon.knownKeys, result.plants.map { it.icon.key }.toSet())
        assertEquals(0, result.repairedPlantCount)
    }

    @Test
    fun `Phase 1 fixtures migrate with the approved repair and failure counts`() {
        val cases =
            listOf(
                FixtureExpectation("empty.json", plants = 0, repaired = 0, failed = 0),
                FixtureExpectation("leap_day.json", plants = 1, repaired = 0, failed = 0),
                FixtureExpectation("unusual_names.json", plants = 6, repaired = 0, failed = 0),
                FixtureExpectation("invalid_date.json", plants = 1, repaired = 1, failed = 0),
                FixtureExpectation("invalid_month.json", plants = 1, repaired = 1, failed = 0),
                FixtureExpectation("invalid_year.json", plants = 1, repaired = 1, failed = 0),
                FixtureExpectation(
                    "out_of_range_frequencies.json",
                    plants = 2,
                    repaired = 2,
                    failed = 0
                ),
                FixtureExpectation("unknown_icon.json", plants = 1, repaired = 1, failed = 0),
                FixtureExpectation(
                    "missing_field_between_valid.json",
                    plants = 3,
                    repaired = 1,
                    failed = 0
                ),
                FixtureExpectation(
                    "wrong_type_between_valid.json",
                    plants = 3,
                    repaired = 1,
                    failed = 0
                ),
                FixtureExpectation("non_object_entry.json", plants = 2, repaired = 0, failed = 1)
            )

        cases.forEach { expectation ->
            val result =
                parser
                    .parse(loadLegacyFixture(expectation.name), migrationDate, "Recovered")
                    .asSuccess()
            assertEquals(expectation.name, expectation.plants, result.plants.size)
            assertEquals(expectation.name, expectation.repaired, result.repairedPlantCount)
            assertEquals(expectation.name, expectation.failed, result.failedPlantCount)
        }

        val unusualNames =
            parser.parse(
                loadLegacyFixture("unusual_names.json"),
                migrationDate,
                "Recovered"
            ).asSuccess()
        assertEquals("  Fern  ", unusualNames.plants[0].name)
        assertEquals("", unusualNames.plants[1].name)
        assertEquals("Áloe 🌱", unusualNames.plants[2].name)
    }

    @Test
    fun `duplicate values retain distinct deterministic ids across retries`() {
        val duplicate = validPlant(name = "Same", icon = 0x7f080071)
        val payload = JSONArray().put(duplicate).put(JSONObject(duplicate.toString())).toString()

        val first = parser.parse(payload, migrationDate, "Recovered").asSuccess()
        val retry = parser.parse(payload, migrationDate.plusDays(1), "Recovered").asSuccess()

        assertEquals(first.plants.map { it.id }, retry.plants.map { it.id })
        assertNotEquals(first.plants[0].id, first.plants[1].id)
    }

    @Test
    fun `repairs invalid records independently and preserves blank names`() {
        val payload =
            JSONArray()
                .put(validPlant(name = "", icon = 0x7f080071))
                .put("not an object")
                .put(
                    JSONObject()
                        .put("icon", -1)
                        .put("day", 31)
                        .put("month", 2)
                        .put("year", 2026)
                        .put("wat_freq", 99)
                )

        val result = parser.parse(payload.toString(), migrationDate, "Recovered").asSuccess()

        assertEquals(2, result.plants.size)
        assertEquals("", result.plants[0].name)
        assertEquals("Recovered", result.plants[1].name)
        assertEquals(PlantIcon.UnknownLegacy, result.plants[1].icon)
        assertEquals(40, result.plants[1].wateringInterval.days)
        assertEquals(migrationDate.plusDays(40), result.plants[1].nextWateringDate)
        assertEquals(1, result.repairedPlantCount)
        assertEquals(1, result.failedPlantCount)
    }

    @Test
    fun `malformed whole payload fails instead of becoming an empty migration`() {
        assertEquals(
            LegacyPlantParseResult.WholePayloadFailure,
            parser.parse(loadLegacyFixture("malformed.json"), migrationDate, "Recovered")
        )
    }

    private fun loadLegacyFixture(name: String): String {
        val resource = requireNotNull(javaClass.classLoader?.getResourceAsStream("legacy/$name"))
        return resource.bufferedReader().use { reader -> reader.readText() }
    }

    private fun validPlant(name: String, icon: Int): JSONObject = JSONObject()
        .put("name", name)
        .put("icon", icon)
        .put("day", 20)
        .put("month", 7)
        .put("year", 2026)
        .put("wat_freq", 5)

    private fun LegacyPlantParseResult.asSuccess(): LegacyPlantParseResult.Success =
        this as LegacyPlantParseResult.Success

    private data class FixtureExpectation(
        val name: String,
        val plants: Int,
        val repaired: Int,
        val failed: Int
    )
}
