package org.bug.soundnotification

import android.content.SharedPreferences
import android.util.Log
import java.io.Serializable
import kotlin.math.abs

/**
 * This is where the magic happens
 */
class DelayCalculator(prefs: SharedPreferences) : Serializable {
    private var increment = 0
    private var limit = 0
    private var totalRepeats = 1
    private var totalDelays = 0
    private var next = 0
    private var previous = 0

    init {
        increment = prefs.getString("increment_list", "0")!!.toInt()
        limit = prefs.getString("limit_list", "0")!!.toInt()
        totalDelays = prefs.getString("start_list", "2")!!.toInt()

        next = totalDelays
    }

    fun nextDelay(): Int {
        Log.d(javaClass.name, "Calc: $this")
        var delay = next
        if (limit == 0 ||
            limit > 0 && totalDelays <= limit ||
            limit < 0 && totalRepeats <= abs(limit)
        ) {
            when (increment) {
                DOUBLE -> next *= 2
                FIB -> {
                    val tmp = next
                    next += previous
                    previous = tmp
                }
                else -> next += increment
            }
            totalDelays += next
            totalRepeats++
        } else {
            delay = 0
        }

        Log.d(javaClass.name, "Returning $delay")
        return delay
    }

    override fun toString() =
        "DelayCalculator(increment=$increment, limit=$limit, totalRepeats=$totalRepeats, totalDelays=$totalDelays, next=$next, previous=$previous)"
}