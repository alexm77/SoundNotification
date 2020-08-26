package org.bug.soundnotification

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import androidx.preference.Preference.OnPreferenceChangeListener
import java.lang.ClassCastException


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
        checkAndRequestPermission()
    }

    override fun onStart() {
        super.onStart()
        Log.d(javaClass.name, "Start")
        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        Log.d(javaClass.name, "Enabled listeners [$enabledListeners]")
        val notificationsEnabled = enabledListeners.contains(NotificationListener::class.java.name)
        if (!notificationsEnabled) {
            Log.d(javaClass.name, "Notification permission not granted, asking")
            AlertDialog.Builder(this)
                .setMessage(R.string.permission_text)
                .setPositiveButton("OK") { _, _ ->
                    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
                .show()
        } else {
            Log.d(javaClass.name, "Notification permission already granted")

        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val volOverrideListener =
            OnPreferenceChangeListener { _, value ->
                findPreference<SeekBarPreference>("vol_override_level")?.isEnabled =
                    value.toString().toBoolean()
                true
            }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_general)

            bindPreferenceSummaryToValue(findPreference("start_list"))
            bindPreferenceSummaryToValue(findPreference("increment_list"))
            bindPreferenceSummaryToValue(findPreference("limit_list"))

            bindListener(findPreference<CheckBoxPreference>("vol_override"), volOverrideListener)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            ServiceBootstrap.startService(requireContext())
        }

        companion object {
            private val preferenceSummaryToValueListener =
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
                    }
                    true
                }

            private fun bindPreferenceSummaryToValue(preference: ListPreference?) {
                bindListener(preference, preferenceSummaryToValueListener)
            }

            private fun bindListener(preference: Preference?, listener: OnPreferenceChangeListener) {
                preference?.let {
                    preference.onPreferenceChangeListener = listener

                    // hacky, but whatever
                    try {
                        listener.onPreferenceChange(
                            preference,
                            PreferenceManager.getDefaultSharedPreferences(preference.context)
                                .getString(preference.key, "")
                        )
                    } catch (e: ClassCastException) {
                        listener.onPreferenceChange(
                            preference,
                            PreferenceManager.getDefaultSharedPreferences(preference.context)
                                .getBoolean(preference.key, false)
                        )

                    }
                }
            }
        }
    }
}
