package org.bug.soundnotification

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager


/**
 * See [ Android Design:
 * Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 *
 *
 * Copied from online tutorial, a lot of this stuff can probably look nicer
 */
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkAndRequestPermission()
    }

    override fun onStart() {
        super.onStart()
        Log.d(javaClass.name, "Start")
        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        val notificationsEnabled =
            Settings.Secure.getString(contentResolver, "enabled_notification_listeners").contains(packageName)
        if (!notificationsEnabled) {
            AlertDialog.Builder(this)
                .setMessage("Please give me permission")
                .setPositiveButton("OK") { _, _ ->
                    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
                .show()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setupSimplePreferencesScreen()
        }

        /**
         * Shows the simplified settings UI if the device configuration if the device configuration
         * dictates that a simplified, single-pane UI should be shown.
         */
        private fun setupSimplePreferencesScreen() {
            if (isSimplePreferences(requireContext())) {

                // In the simplified UI, fragments are not used at all and we instead
                // use the older PreferenceActivity APIs.

                // Add 'general' preferences.
                addPreferencesFromResource(R.xml.pref_general)
                bindPreferenceSummaryToValue(findPreference("start_list"))
                bindPreferenceSummaryToValue(findPreference("increment_list"))
                bindPreferenceSummaryToValue(findPreference("limit_list"))
            }
        }

        /**
         * {@inheritDoc}
         */
//    override fun onIsMultiPane(): Boolean {
//        return isXLargeTablet(this) && !isSimplePreferences(this)
//    }

        /**
         * {@inheritDoc}
         */
//    @TargetApi(Build.VERSION_CODES.P)
//    override fun onBuildHeaders(target: List<Header>) {
//        if (!isSimplePreferences(this)) {
//            loadHeadersFromResource(R.xml.pref_headers, target)
//        }
//    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            ServiceBootstrap.startService(requireContext())
        }

        /**
         * This fragment shows general preferences only. It is used when the activity is showing a
         * two-pane settings UI.
         */
        @TargetApi(Build.VERSION_CODES.P)
        class GeneralPreferenceFragment : PreferenceFragmentCompat() {
            override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
                super.onCreate(savedInstanceState)
                setPreferencesFromResource(R.xml.pref_general, rootKey)
                bindPreferenceSummaryToValue(findPreference("start_list"))
                bindPreferenceSummaryToValue(findPreference("increment_list"))
                bindPreferenceSummaryToValue(findPreference("limit_list"))
            }
        }

        companion object {
            /**
             * Determines whether to always show the simplified settings UI, where settings are presented in
             * a single list. When false, settings are shown as a master/detail two-pane view on tablets.
             * When true, a single pane is shown on tablets.
             */
            private const val ALWAYS_SIMPLE_PREFS = false

            /**
             * A preference value change listener that updates the preference's summary to reflect its new
             * value.
             */
            private val sBindPreferenceSummaryToValueListener =
                OnPreferenceChangeListener { preference, value ->
                    val stringValue = value.toString()
                    if (preference is ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        val index = preference.findIndexOfValue(stringValue)

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                            if (index >= 0) preference.entries[index] else null
                        )
                    } else {
                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.summary = stringValue
                    }
                    true
                }

            /**
             * Helper method to determine if the device has an extra-large screen. For example, 10" tablets
             * are extra-large.
             */
            private fun isXLargeTablet(context: Context): Boolean {
                return (context.resources.configuration.screenLayout
                        and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
            }

            /**
             * Determines whether the simplified settings UI should be shown. This is true if this is forced
             * via [.ALWAYS_SIMPLE_PREFS], or the device doesn't have newer APIs like [ ], or the device doesn't have an extra-large screen. In these cases, a
             * single-pane "simplified" settings UI should be shown.
             */
            private fun isSimplePreferences(context: Context): Boolean {
                return (ALWAYS_SIMPLE_PREFS || !isXLargeTablet(context))
            }

            /**
             * Binds a preference's summary to its value. More specifically, when the preference's value is
             * changed, its summary (line of text below the preference title) is updated to reflect the
             * value. The summary is also immediately updated upon calling this method. The exact display
             * format is dependent on the type of preference.
             *
             * @see .sBindPreferenceSummaryToValueListener
             */
            private fun bindPreferenceSummaryToValue(preference: Preference?) {
                if (preference != null) {
                    // Set the listener to watch for value changes.
                    preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

                    // Trigger the listener immediately with the preference's
                    // current value.
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(
                        preference,
                        PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, "")
                    )
                }
            }
        }
    }
}
