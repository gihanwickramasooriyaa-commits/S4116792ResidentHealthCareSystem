# Refactoring Report (Milestone → Final)

## Summarised changes
- **Logging made consistent**: every mutating action logs staff **name + id/username**.
- **Null-guards & validation**: added defensive checks for invalid bed IDs; centralised input checks in `Validators`.
- **Separation of concerns**: CareHome handles domain logic; `view.App` only orchestrates dialogs and calls model.
- **Method extractions**: dialog handlers (`addResidentHere`, `addPrescriptionFor`, `markDoseAdministered`, etc.).
- **Naming & readability**: clearer variable names; comments; consistent formatting.
- **Shift compliance clarity**: per-day aggregation using `EnumMap<DayOfWeek,Long>`.

## Smells addressed
- **Long methods (UI)** → extracted helper methods; context-menu per-role.
- **Inconsistent logging** → unified format.
- **Error messaging** → meaningful messages shown in alerts rather than console.

## Possible future improvements
- Real database (JDBC) with DAO layer; replace serialization.
- FXML for declarative UI.
- More unit tests around discharge CSV parsing and admin logs.
