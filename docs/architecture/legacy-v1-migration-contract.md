# Wateria v1.6 Legacy Migration Contract

Status: proposed normative input contract for the revamp  
Source baseline: master/develop v1.6.0, version code 10

## 1. Purpose

Wateria v1.6 stores plants and settings in default SharedPreferences. Plant icons
are persisted as generated Android integer resource IDs. Those integers are not
stable across a resource rebuild, so this document freezes their v1.6 meaning.

Phase 1 must convert this document into executable migration fixtures before any
plant drawable is renamed, removed, regenerated, or reordered.

## 2. Legacy preference keys

| Key | Type | Default | Destination |
|---|---|---|---|
| `plantlistkey` | JSON array string | absent/empty list | Room `plants` table |
| `notif_enabled` | Boolean | `true` | `reminders_enabled` |
| `notif_hour` | Int | `18` | `reminder_hour` |
| `notif_minute` | Int | `0` | `reminder_minute` |
| `notif_repetition` | Int hours | `1` | `snooze_duration_minutes` |
| `first_time` | Boolean | legacy first-run semantics | `onboarding_version_completed` |
| `tip_idx` | Int | `0` | `next_tip_index` |
| `last_day` | Int day-of-year | absent | `last_tip_epoch_day` when safely inferable |

The old `last_day` does not contain a year. Migration must not invent a confident
historical date. If it cannot be mapped unambiguously relative to migration time,
leave `last_tip_epoch_day` absent and allow the normal tip scheduler to restart.

## 3. Legacy plant JSON

`plantlistkey` contains a JSON array. Each valid object uses:

```json
{
  "name": "Monstera",
  "icon": 2131230849,
  "day": 17,
  "month": 7,
  "year": 2026,
  "wat_freq": 5
}
```

Fields:

| Field | Type | Meaning |
|---|---|---|
| `name` | String | User-entered plant name |
| `icon` | Int | v1.6 Android drawable resource ID |
| `day` | Int | Next-watering day of month |
| `month` | Int | Next-watering month, 1 through 12 |
| `year` | Int | Next-watering year |
| `wat_freq` | Int | Watering interval in days |

No stable ID, creation timestamp, update timestamp, last-watered date, or history
exists in the legacy payload.

## 4. Frozen v1.6 icon mapping

The following IDs were verified to be identical in the v1.6 debug and release
resource symbol tables.

| Legacy ID | Stable key | v1.6 drawable |
|---|---|---|
| `0x7f080071` | `cactus_01` | `ic_cactus_1` |
| `0x7f080072` | `cactus_02` | `ic_cactus_2` |
| `0x7f080073` | `cactus_03` | `ic_cactus_3` |
| `0x7f080074` | `cactus_04` | `ic_cactus_4` |
| `0x7f080075` | `cactus_05` | `ic_cactus_5` |
| `0x7f080076` | `cactus_06` | `ic_cactus_6` |
| `0x7f080077` | `cactus_07` | `ic_cactus_7_concara` |
| `0x7f080079` | `common_01` | `ic_common_1` |
| `0x7f08007a` | `common_10` | `ic_common_10` |
| `0x7f08007b` | `common_02_snake_plant` | `ic_common_2_snakeplant` |
| `0x7f08007c` | `common_03_sansevieria` | `ic_common_3_sansevieria` |
| `0x7f08007d` | `common_04_hanging` | `ic_common_4_hanging` |
| `0x7f08007e` | `common_05_spider_plant` | `ic_common_5_spiderplant` |
| `0x7f08007f` | `common_06_ivy` | `ic_common_6_ivy` |
| `0x7f080080` | `common_07_bamboo` | `ic_common_7_bamboo` |
| `0x7f080081` | `common_08_monstera` | `ic_common_8_monstera` |
| `0x7f080082` | `common_09_monstera_leaf` | `ic_common_9_monsteraleaf` |
| `0x7f080083` | `flower_01_red` | `ic_flower_1_red` |
| `0x7f080084` | `flower_02_orange` | `ic_flower_2_orange` |
| `0x7f080085` | `flower_03_yellow` | `ic_flower_3_yellow` |
| `0x7f080086` | `flower_04_pair` | `ic_flower_4_two` |
| `0x7f080087` | `flower_05` | `ic_flower_5` |
| `0x7f080088` | `flower_06_rose` | `ic_flower_6_rose` |
| `0x7f080091` | `propagation_01` | `ic_propagation_1` |
| `0x7f080092` | `propagation_02` | `ic_propagation_2` |
| `0x7f080093` | `propagation_03` | `ic_propagation_3` |
| `0x7f080094` | `tree_01_bush` | `ic_tree_1_bush` |
| `0x7f080095` | `tree_02_dracaena` | `ic_tree_2_dracaena` |
| `0x7f080096` | `tree_03_joshua_jade` | `ic_tree_3_joshuatree_jade` |
| `0x7f080097` | `tree_04_palm` | `ic_tree_4_palm` |
| `0x7f080098` | `tree_05_pine` | `ic_tree_5_pine` |
| `0x7f080099` | `tree_06_bonsai` | `ic_tree_6_bonsai` |
| `0x7f08009a` | `vegetable_10_red_pepper` | `ic_veggies_10_redpepper` |
| `0x7f08009b` | `vegetable_11_avocado` | `ic_veggies_11_avocado` |
| `0x7f08009c` | `vegetable_12_strawberry` | `ic_veggies_12_strawberry` |
| `0x7f08009d` | `vegetable_01_lettuce` | `ic_veggies_1_lettuce` |
| `0x7f08009e` | `vegetable_02_carrot` | `ic_veggies_2_carrot` |
| `0x7f08009f` | `vegetable_03_onion` | `ic_veggies_3_onion` |
| `0x7f0800a0` | `vegetable_04_onion` | `ic_veggies_4_onion2` |
| `0x7f0800a1` | `vegetable_05_garlic` | `ic_veggies_5_garlic` |
| `0x7f0800a2` | `vegetable_06_general` | `ic_veggies_6_general` |
| `0x7f0800a3` | `vegetable_07_tomato` | `ic_veggies_7_tomato` |
| `0x7f0800a4` | `vegetable_08_eggplant` | `ic_veggies_8_eggplant` |
| `0x7f0800a5` | `vegetable_09_green_pepper` | `ic_veggies_9_greenpepper` |

Stable keys become persistence values and may not be renamed casually. The drawable
used to render a key may change without changing the stored semantic identity.

## 5. Migration algorithm

1. Read migration state.
2. If `complete`, do nothing.
3. Set state to `in_progress`.
4. Read the raw legacy preferences without modifying them.
5. Parse `plantlistkey` as an array.
6. For each array position, validate and convert the object independently.
7. Generate a deterministic UUID using array index plus the raw legacy fields so a
   retry produces the same identity, including for duplicate plants.
8. Map the icon integer using the frozen table.
9. Convert the date to `LocalDate` and then Room epoch-day storage.
10. Insert/upsert converted plants inside a Room transaction.
11. Convert preferences to DataStore.
12. Read back Room and DataStore to verify expected counts and values.
13. Record non-sensitive migration counts.
14. Set migration version to 1 and state to `complete`.
15. Leave all legacy preference keys untouched through at least one stable release.

Room and DataStore cannot share one physical transaction. Idempotent plant IDs,
upserts, a migration state machine, and read-back verification make interruption
and retry safe.

## 6. Repair and failure rules

| Legacy condition | Required behavior |
|---|---|
| Preference absent | Treat as a legitimate empty installation |
| Empty JSON array | Migrate zero plants successfully |
| Whole value is not a JSON array | Mark migration failed; retain value; do not show overwriteable empty state |
| Non-object array entry | Record failed-entry count; retain raw legacy payload |
| Missing or non-string name | Preserve entry with a localized recovery name and warning count |
| Blank legacy name | Preserve verbatim until edited |
| Unknown icon ID | Use explicit `unknown_legacy` fallback and preserve plant |
| Invalid date | Repair to migration date plus validated watering frequency; record repair count |
| Frequency below 1 | Clamp to 1; record repair count |
| Frequency above 40 | Clamp to 40; record repair count |
| Duplicate plant values | Preserve every array entry with a distinct deterministic ID |
| Migration interrupted | Retry idempotently; do not duplicate records |
| Existing new database plus incomplete marker | Reconcile using deterministic IDs and read-back verification |

No raw record, name, or JSON payload may be sent to logs, analytics, or crash-report
custom keys.

## 7. Settings conversion

- `notif_enabled` maps directly to `reminders_enabled`.
- Valid `notif_hour` is 0 through 23; otherwise use 18.
- Valid `notif_minute` is 0 through 59; otherwise use 0.
- Valid `notif_repetition` is 1 through 23 hours; convert to minutes.
- `first_time=false` means the legacy onboarding has been shown and maps to the
  current parity onboarding version being complete.
- `first_time=true` or absence leaves onboarding incomplete.
- Normalize `tip_idx` modulo the current tip count when non-negative; otherwise use 0.
- Treat `last_day` conservatively because the year was not stored.

## 8. Required Phase 1 fixtures

- Empty preferences.
- One valid plant for every one of the 44 icon IDs.
- Multiple plants with identical values at different array indices.
- Names with surrounding whitespace, empty strings, Unicode, and 50+ characters.
- Minimum and maximum valid dates/frequencies.
- Leap-day date.
- Invalid month/day/year combinations.
- Unknown icon value.
- Missing field and wrong-type permutations.
- Malformed whole JSON.
- Valid plants surrounding a malformed entry.
- Every valid and invalid settings boundary.
- Interrupted migration after Room write and before DataStore completion.
- Repeated migration execution after completion.

## 9. Removal gate

Legacy reading and the frozen integer mapping may be removed only after:

- At least one stable production version containing the migration has completed rollout.
- Migration success/failure counts show an acceptable result.
- The rollback window has closed.
- A later migration explicitly removes retained legacy preferences.

Until that gate is met, resource cleanup must not remove the executable mapping even
after all UI code has switched to semantic icon keys.
