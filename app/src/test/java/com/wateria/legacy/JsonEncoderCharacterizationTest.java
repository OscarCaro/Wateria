package com.wateria.legacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.wateria.DataStructures.Plant;
import com.wateria.Utils.JsonEncoder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;

public class JsonEncoderCharacterizationTest {

    @Test
    public void readsFixtureContainingAll44LegacyIcons() {
        ArrayList<Plant> plants = JsonEncoder.readPlantList(LegacyFixtureLoader.load("all_icons.json"));

        assertEquals(44, plants.size());
        assertEquals("cactus_01", plants.get(0).getPlantName());
        assertEquals(0x7f080071, plants.get(0).getIconId());
        assertEquals("vegetable_09_green_pepper", plants.get(43).getPlantName());
        assertEquals(0x7f0800a5, plants.get(43).getIconId());
        assertEquals(LocalDate.of(2026, 7, 17), plants.get(43).getNextWateringDate());
        assertEquals(5, plants.get(43).getWateringFrequency());
    }

    @Test
    public void writerUsesOnlyTheSixLegacyPlantFields() throws Exception {
        ArrayList<Plant> plants = new ArrayList<>();
        Plant plant = new Plant("Monstera", 0x7f080081, 7, LocalDate.of(2026, 8, 9));
        plant.setDaysRemaining(99);
        plants.add(plant);

        JSONObject encoded = new JSONArray(JsonEncoder.writePlantList(plants)).getJSONObject(0);

        assertEquals(6, encoded.length());
        assertEquals("Monstera", encoded.getString("name"));
        assertEquals(0x7f080081, encoded.getInt("icon"));
        assertEquals(9, encoded.getInt("day"));
        assertEquals(8, encoded.getInt("month"));
        assertEquals(2026, encoded.getInt("year"));
        assertEquals(7, encoded.getInt("wat_freq"));
        assertFalse(encoded.has("daysRemaining"));
        assertFalse(encoded.has("id"));
    }

    @Test
    public void objectMissingARequiredFieldIsSkippedWhileNeighborsSurvive() {
        ArrayList<Plant> plants = JsonEncoder.readPlantList(LegacyFixtureLoader.load("missing_field_between_valid.json"));

        assertEquals(2, plants.size());
        assertEquals("Before", plants.get(0).getPlantName());
        assertEquals("After", plants.get(1).getPlantName());
    }

    @Test
    public void malformedWholePayloadCurrentlyBecomesAnEmptyList() {
        ArrayList<Plant> plants = JsonEncoder.readPlantList(LegacyFixtureLoader.load("malformed.json"));

        assertTrue(plants.isEmpty());
    }

    @Test
    public void nonObjectArrayEntryCurrentlyCrashesParsing() {
        assertThrows(
                NullPointerException.class,
                () -> JsonEncoder.readPlantList(LegacyFixtureLoader.load("non_object_entry.json"))
        );
    }

    @Test
    public void invalidCalendarDateCurrentlyEscapesJsonErrorHandling() {
        assertThrows(
                DateTimeException.class,
                () -> JsonEncoder.readPlantList(LegacyFixtureLoader.load("invalid_date.json"))
        );
    }

    @Test
    public void legacyNamesAreReadVerbatimAndDuplicatesArePreserved() {
        ArrayList<Plant> plants = JsonEncoder.readPlantList(LegacyFixtureLoader.load("unusual_names.json"));

        assertEquals(6, plants.size());
        assertEquals("  Fern  ", plants.get(0).getPlantName());
        assertEquals("", plants.get(1).getPlantName());
        assertEquals("Áloe 🌱", plants.get(2).getPlantName());
        assertTrue(plants.get(3).getPlantName().length() > 50);
        assertEquals("Duplicate", plants.get(4).getPlantName());
        assertEquals("Duplicate", plants.get(5).getPlantName());
    }

    @Test
    public void legacyReaderAcceptsOutOfRangeFrequenciesWithoutValidation() {
        ArrayList<Plant> plants = JsonEncoder.readPlantList(LegacyFixtureLoader.load("out_of_range_frequencies.json"));

        assertEquals(2, plants.size());
        assertEquals(-2, plants.get(0).getWateringFrequency());
        assertEquals(99, plants.get(1).getWateringFrequency());
    }

    @Test
    public void legacyReaderAcceptsUnknownIconIntegers() {
        ArrayList<Plant> plants = JsonEncoder.readPlantList(LegacyFixtureLoader.load("unknown_icon.json"));

        assertEquals(1, plants.size());
        assertEquals(123456789, plants.get(0).getIconId());
    }

    @Test
    public void validLeapDayIsReadSuccessfully() {
        ArrayList<Plant> plants = JsonEncoder.readPlantList(LegacyFixtureLoader.load("leap_day.json"));

        assertEquals(1, plants.size());
        assertEquals(LocalDate.of(2024, 2, 29), plants.get(0).getNextWateringDate());
    }

    @Test
    public void otherInvalidCalendarComponentsAlsoEscapeJsonErrorHandling() {
        assertThrows(
                DateTimeException.class,
                () -> JsonEncoder.readPlantList(LegacyFixtureLoader.load("invalid_month.json"))
        );
        assertThrows(
                DateTimeException.class,
                () -> JsonEncoder.readPlantList(LegacyFixtureLoader.load("invalid_year.json"))
        );
    }

    @Test
    public void wrongTypedFieldIsSkippedWhileValidNeighborsSurvive() {
        ArrayList<Plant> plants = JsonEncoder.readPlantList(LegacyFixtureLoader.load("wrong_type_between_valid.json"));

        assertEquals(2, plants.size());
        assertEquals("Before", plants.get(0).getPlantName());
        assertEquals("After", plants.get(1).getPlantName());
    }

    @Test
    public void emptyLegacyArrayProducesAnEmptyList() {
        assertTrue(JsonEncoder.readPlantList(LegacyFixtureLoader.load("empty.json")).isEmpty());
    }
}
