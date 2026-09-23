# Tailor Management — Android project

This project is a native Android starter for the tailor-management app discussed in chat.

## Design
- App controls are English.
- Measurement labels are Urdu.
- Cutting View keeps the exact order and visual concept from the supplied reference.
- Numbers are shown on the LEFT and Urdu labels on the RIGHT.
- Customer photo is supported.
- Measurements can be edited.
- Daman has two selectable options: `دامن چورس` / `دامن گول`.
- Measurement 6 supports a selectable option and keeps its measurement value.
- Data is stored locally on the device using SharedPreferences/JSON.

## Open
Open the `TailorManagement` folder in Android Studio and let Gradle sync.

If Android Studio asks for a Gradle JDK, choose the bundled JDK 17.

## Main flow
Customers → Customer Profile → Measurements → Cutting View

The sample customer is included so the Cutting View is immediately visible.

## Important
The reference screenshot was used only as the visual target. The app is source code and can be customized further.
