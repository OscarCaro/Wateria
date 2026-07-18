#!/usr/bin/env bash

set -euo pipefail

repo_root="$(git rev-parse --show-toplevel)"
legacy_ref="${WATERIA_LEGACY_REF:-develop}"
legacy_apk_override="${WATERIA_LEGACY_APK:-}"
debug_package="com.wateria.debug"
test_package="com.wateria.debug.test"
runner="${test_package}/androidx.test.runner.AndroidJUnitRunner"
temporary_directory="$(mktemp -d "${TMPDIR:-/tmp}/wateria-phase7a.XXXXXX")"

cleanup() {
    if [[ -n "${temporary_directory:-}" && -d "${temporary_directory}" ]]; then
        rm -rf "${temporary_directory}"
    fi
}
trap cleanup EXIT

if [[ -n "${ANDROID_SERIAL:-}" ]]; then
    adb_command=(adb -s "${ANDROID_SERIAL}")
else
    adb_command=(adb)
fi

run_instrumentation() {
    local scenario="$1"
    local test_class="$2"
    local simple_test_class="${test_class##*.}"
    local output_file="${temporary_directory}/${scenario}-${simple_test_class}.log"
    "${adb_command[@]}" shell am instrument -w -r \
        -e scenario "${scenario}" \
        -e class "${test_class}" \
        "${runner}" | tee "${output_file}"
    if grep -q '^FAILURES!!!' "${output_file}" || ! grep -q '^OK (' "${output_file}"; then
        echo "Instrumentation failed: ${scenario} / ${test_class}" >&2
        return 1
    fi
}

seed_legacy_preferences() {
    local scenario="$1"
    local fixture_xml="${temporary_directory}/${scenario}.xml"
    local remote_fixture="/data/local/tmp/wateria-${scenario}-preferences.xml"
    local plant_payload=""

    case "${scenario}" in
        malformed)
            plant_payload="$(<"${repo_root}/app/src/test/resources/legacy/malformed.json")"
            ;;
        all_icons)
            plant_payload="$(<"${repo_root}/app/src/test/resources/legacy/all_icons.json")"
            ;;
        representative)
            plant_payload='[{"name":"Living room Monstera","icon":2131230849,"day":20,"month":7,"year":2026,"wat_freq":7},{"name":"Áloe","icon":2131230833,"day":22,"month":7,"year":2026,"wat_freq":10}]'
            ;;
        empty)
            ;;
        *)
            echo "Unknown Phase 7A scenario: ${scenario}" >&2
            exit 1
            ;;
    esac

    {
        printf '%s\n' "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>" '<map>'
        if [[ -n "${plant_payload}" ]]; then
            printf '%s' '    <string name="plantlistkey">'
            printf '%s' "${plant_payload}" | sed -e 's/&/\&amp;/g' -e 's/</\&lt;/g' -e 's/>/\&gt;/g'
            printf '%s\n' '</string>'
        fi
        if [[ "${scenario}" == "representative" ]]; then
            printf '%s\n' \
                '    <boolean name="notif_enabled" value="true" />' \
                '    <int name="notif_hour" value="18" />' \
                '    <int name="notif_minute" value="0" />' \
                '    <int name="notif_repetition" value="1" />' \
                '    <boolean name="first_time" value="false" />' \
                '    <int name="tip_idx" value="4" />' \
                '    <int name="last_day" value="198" />'
        fi
        printf '%s\n' '</map>'
    } >"${fixture_xml}"

    "${adb_command[@]}" shell am force-stop "${debug_package}"
    "${adb_command[@]}" push "${fixture_xml}" "${remote_fixture}" >/dev/null
    "${adb_command[@]}" shell run-as "${debug_package}" mkdir -p shared_prefs
    "${adb_command[@]}" shell run-as "${debug_package}" \
        cp "${remote_fixture}" "shared_prefs/${debug_package}_preferences.xml"
    "${adb_command[@]}" shell run-as "${debug_package}" \
        chmod 600 "shared_prefs/${debug_package}_preferences.xml"
    "${adb_command[@]}" shell rm "${remote_fixture}"
}

uninstall_debug_package_if_present() {
    local package_name="$1"
    if "${adb_command[@]}" shell pm path "${package_name}" | grep -q '^package:'; then
        "${adb_command[@]}" uninstall "${package_name}" >/dev/null
    fi
}

if [[ "$("${adb_command[@]}" get-state)" != "device" ]]; then
    echo "No ready Android device. Start an emulator or set ANDROID_SERIAL." >&2
    exit 1
fi

api_level="$("${adb_command[@]}" shell getprop ro.build.version.sdk | tr -d '\r')"
device_name="$("${adb_command[@]}" shell getprop ro.product.model | tr -d '\r')"
echo "Validating on ${device_name}, API ${api_level}"

if [[ -n "${legacy_apk_override}" ]]; then
    legacy_apk="${legacy_apk_override}"
else
    legacy_source="${temporary_directory}/legacy"
    mkdir -p "${legacy_source}"
    git -C "${repo_root}" archive "${legacy_ref}" | tar -x -C "${legacy_source}"
    if [[ -f "${repo_root}/local.properties" ]]; then
        cp "${repo_root}/local.properties" "${legacy_source}/local.properties"
    fi
    "${legacy_source}/gradlew" -p "${legacy_source}" :app:assembleDebug
    legacy_apk="${legacy_source}/app/build/outputs/apk/debug/app-debug.apk"
fi

"${repo_root}/gradlew" -p "${repo_root}" :app:assembleDebug :app:assembleDebugAndroidTest
current_apk="${repo_root}/app/build/outputs/apk/debug/app-debug.apk"
test_apk="${repo_root}/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"

for required_apk in "${legacy_apk}" "${current_apk}" "${test_apk}"; do
    if [[ ! -f "${required_apk}" ]]; then
        echo "Missing APK: ${required_apk}" >&2
        exit 1
    fi
done

scenarios=(malformed empty all_icons representative)
for scenario in "${scenarios[@]}"; do
    echo "Running Phase 7A scenario: ${scenario}"
    uninstall_debug_package_if_present "${test_package}"
    uninstall_debug_package_if_present "${debug_package}"

    "${adb_command[@]}" install "${legacy_apk}" >/dev/null
    seed_legacy_preferences "${scenario}"
    "${adb_command[@]}" install -r "${current_apk}" >/dev/null
    "${adb_command[@]}" install "${test_apk}" >/dev/null

    if [[ "${scenario}" == "representative" ]]; then
        run_instrumentation \
            "${scenario}" \
            "com.wateria.revamp.migration.LegacyUpgradeUiTest"
    fi
    run_instrumentation \
        "${scenario}" \
        "com.wateria.revamp.migration.LegacyUpgradeStorageTest"
    if [[ "${scenario}" == "malformed" ]]; then
        run_instrumentation \
            "${scenario}" \
            "com.wateria.revamp.migration.LegacyUpgradeUiTest"
    fi
done

echo "Phase 7A passed on ${device_name}, API ${api_level}."
