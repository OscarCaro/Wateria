# Legacy v1.6 fixtures

These files capture the SharedPreferences and plant JSON inputs accepted by Wateria
v1.6. They are migration inputs, not examples of the desired new Room schema.

The characterization tests intentionally document both valid behavior and defects:

- Invalid whole JSON becomes an empty list.
- Non-object array entries and invalid calendar dates escape error handling.
- Names and watering frequencies are not validated during decoding.
- Unknown drawable integers are accepted until Android tries to render them.
- Derived day counts are mutable snapshots.

The revamp migration must reuse these fixtures while asserting the repaired outcomes
defined in `docs/architecture/legacy-v1-migration-contract.md`. Tests that describe a
legacy defect should be retained as historical evidence until a replacement migration
test covers the same input.

`all_icons.json` contains exactly one plant for each of the 44 frozen v1.6 drawable
IDs. Do not regenerate those integers from a later build.
