package org.bug.soundnotification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class DelayCalculatorTest {
	private static final int TEST_REPEATS = 10;

	public static void genericTest(int start, int increment, int limit, int[] expected) {
		assertNotNull(expected);
		assertEquals(TEST_REPEATS, expected.length);

		int[] actual = new int[TEST_REPEATS];
		DelayCalculator calc = new DelayCalculator();

		calc.init(start, increment, limit);
		for (int i = 0; i < TEST_REPEATS; i++) {
			actual[i] = calc.nextDelay();
		}

		assertTrue(Arrays.equals(expected, actual));
	}

	@Test
	public void testConstantProgressionNoLimit() {
		genericTest(2, 0, 0, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2});
	}

	@Test
	public void testConstantProgressionRepeatLimit() {
		genericTest(2, 0, -3, new int[]{2, 2, 2, 0, 0, 0, 0, 0, 0, 0});
	}

	@Test
	public void testConstantProgressionTimeLimit() {
		genericTest(2, 0, 15, new int[]{2, 2, 2, 2, 2, 2, 2, 0, 0, 0});
	}

	@Test
	public void testLinearProgressionNoLimit() {
		genericTest(2, 3, 0, new int[]{2, 5, 8, 11, 14, 17, 20, 23, 26, 29});
	}

	@Test
	public void testLinearProgressionRepeatLimit() {
		genericTest(2, 3, -3, new int[]{2, 5, 8, 0, 0, 0, 0, 0, 0, 0});
	}

	@Test
	public void testLinearProgressionTimeLimit() {
		genericTest(2, 3, 15, new int[]{2, 5, 8, 0, 0, 0, 0, 0, 0, 0});
	}

	@Test
	public void testDoubleIntervalNoLimit() {
		genericTest(2, -1, 0, new int[]{2, 4, 8, 16, 32, 64, 128, 256, 512, 1024});
	}

	@Test
	public void testDoubleIntervalRepeatLimit() {
		genericTest(2, -1, -5, new int[]{2, 4, 8, 16, 32, 0, 0, 0, 0, 0});
	}

	@Test
	public void testDoubleIntervalTimeLimit() {
		genericTest(2, -1, 15, new int[]{2, 4, 8, 0, 0, 0, 0, 0, 0, 0});
	}

	@Test
	public void testFibonacciNoLimit() {
		genericTest(1, -2, 0, new int[]{1, 1, 2, 3, 5, 8, 13, 21, 34, 55});
	}

	@Test
	public void testFibonacciRepeatLimit() {
		genericTest(1, -2, -5, new int[]{1, 1, 2, 3, 5, 0, 0, 0, 0, 0});
	}

	@Test
	public void testFibonacciTimeLimit() {
		genericTest(1, -2, 15, new int[]{1, 1, 2, 3, 5, 0, 0, 0, 0, 0});
	}
}
