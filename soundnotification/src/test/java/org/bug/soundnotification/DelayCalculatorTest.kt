package org.bug.soundnotification

import android.content.SharedPreferences
import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DelayCalculatorTest {

    @ParameterizedTest
    @MethodSource("generationParams")
    fun testCalculatedDelays(
        initialDelay: Int,
        @IncrementType increment: Int,
        @LimitType limit: Int,
        expected: IntArray
    ) {
        assertEquals(GENERATED_DELAYS, expected.size)

        val prefs = TestPrefs(initialDelay, increment, limit)
        val calc = DelayCalculator(prefs)

        val actual = IntArray(GENERATED_DELAYS)
        for (i in 0 until GENERATED_DELAYS) {
            actual[i] = calc.nextDelay()
        }

        assertArrayEquals(expected, actual)
    }

    companion object {
        private const val GENERATED_DELAYS = 10

        @MethodSource
        @JvmStatic
        fun generationParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    2,
                    NO_INCREMENT,
                    NO_LIMIT,
                    intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2)
                ),  //ConstantProgressionNoLimit
                Arguments.of(
                    2,
                    NO_INCREMENT,
                    THREE_NOTIFICATIONS,
                    intArrayOf(2, 2, 2, 0, 0, 0, 0, 0, 0, 0)
                ),  //ConstantProgressionRepeatLimit
                Arguments.of(
                    2,
                    NO_INCREMENT,
                    FIFTEEN_MINUTES,
                    intArrayOf(2, 2, 2, 2, 2, 2, 2, 0, 0, 0)
                ),  //ConstantProgressionTimeLimit
                Arguments.of(
                    2,
                    THREE_MINUTES,
                    NO_LIMIT,
                    intArrayOf(2, 5, 8, 11, 14, 17, 20, 23, 26, 29)
                ),  //LinearProgressionNoLimit
                Arguments.of(
                    2,
                    THREE_MINUTES,
                    THREE_NOTIFICATIONS,
                    intArrayOf(2, 5, 8, 0, 0, 0, 0, 0, 0, 0)
                ),  //LinearProgressionRepeatLimit
                Arguments.of(
                    2,
                    THREE_MINUTES,
                    FIFTEEN_MINUTES,
                    intArrayOf(2, 5, 8, 0, 0, 0, 0, 0, 0, 0)
                ),  //LinearProgressionTimeLimit
                Arguments.of(
                    2,
                    DOUBLE,
                    NO_LIMIT,
                    intArrayOf(2, 4, 8, 16, 32, 64, 128, 256, 512, 1024)
                ),  //DoubleIntervalNoLimit
                Arguments.of(
                    2,
                    DOUBLE,
                    NINE_NOTIFICATIONS,
                    intArrayOf(2, 4, 8, 16, 32, 64, 128, 256, 512, 0)
                ),  //DoubleIntervalRepeatLimit
                Arguments.of(
                    2,
                    DOUBLE,
                    FIFTEEN_MINUTES,
                    intArrayOf(2, 4, 8, 0, 0, 0, 0, 0, 0, 0)
                ),  //DoubleIntervalTimeLimit
                Arguments.of(1, FIB, NO_LIMIT, intArrayOf(1, 1, 2, 3, 5, 8, 13, 21, 34, 55)),  //FibonacciNoLimit
                Arguments.of(
                    1,
                    FIB,
                    NINE_NOTIFICATIONS,
                    intArrayOf(1, 1, 2, 3, 5, 8, 13, 21, 34, 0)
                ),  //FibonacciRepeatLimit
                Arguments.of(1, FIB, FIFTEEN_MINUTES, intArrayOf(1, 1, 2, 3, 5, 0, 0, 0, 0, 0))  //FibonacciTimeLimit
            )
        }

        @BeforeAll
        @JvmStatic
        fun init() {
            mockkStatic(Log::class)
            every { Log.v(any(), any()) } returns 0
            every { Log.d(any(), any()) } returns 0
            every { Log.i(any(), any()) } returns 0
            every { Log.e(any(), any()) } returns 0
        }
    }

    private class TestPrefs(val initialDelay: Int, val increment: Int, val limit: Int): SharedPreferences {
        override fun contains(key: String?): Boolean {
            TODO("Not yet implemented")
        }

        override fun getBoolean(key: String?, defValue: Boolean): Boolean {
            TODO("Not yet implemented")
        }

        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            TODO("Not yet implemented")
        }

        override fun getInt(key: String?, defValue: Int): Int {
            TODO("Not yet implemented")
        }

        override fun getAll(): MutableMap<String, *> {
            TODO("Not yet implemented")
        }

        override fun edit(): SharedPreferences.Editor {
            TODO("Not yet implemented")
        }

        override fun getLong(key: String?, defValue: Long): Long {
            TODO("Not yet implemented")
        }

        override fun getFloat(key: String?, defValue: Float): Float {
            TODO("Not yet implemented")
        }

        override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
            TODO("Not yet implemented")
        }

        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            TODO("Not yet implemented")
        }

        override fun getString(key: String?, defValue: String?): String? {
            when(key) {
                "increment_list" -> return increment.toString()
                "limit_list" -> return limit.toString()
                "start_list" -> return initialDelay.toString()
            }
            return ""
        }

    }
}