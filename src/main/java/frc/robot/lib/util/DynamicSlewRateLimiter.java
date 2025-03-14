package frc.robot.lib.util;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathSharedStore;
import edu.wpi.first.math.MathUtil;


/**
* A class that limits the rate of change of an input value. Can have different rate limits for 
* getting farther from zero (increasing) and getting closer to zero (decreasing). Useful for 
* controlling something like robot acceleration and deceleration.
*/
public class DynamicSlewRateLimiter {

    private final DoubleSupplier increasingRateLimit;
    private final DoubleSupplier decreasingRateLimit;
    private double prevVal;
    private double prevTime;
    
    /**
    * Creates a new DynamicSlewRateLimiter with the given increasing and decreasing rate limits.
    * Increasing is how fast the input can get farther from zero, Decreasing is how fast the input can get closer to zero.
    * The rate limits are only magnitudes.
    * @param increasingRateLimit The rate-of-change limit when the input is increasing, in units per
    *     second. This is expected to be positive. How quickly the input can get farther from zero.
    * @param decreasingRateLimit The rate-of-change limit when the input is decreasing, in units per
    *     second. This is expected to be positive. How quickly the input can get closer to zero.
    */
    public DynamicSlewRateLimiter(DoubleSupplier increasingRateLimit, DoubleSupplier decreasingRateLimit) {
        this.increasingRateLimit = increasingRateLimit;
        this.decreasingRateLimit = decreasingRateLimit;
        prevVal = 0;
        prevTime = MathSharedStore.getTimestamp();
    }

    /**
    * Creates a new DynamicSlewRateLimiter with the given increasing and decreasing rate limits.
    * Increasing is how fast the input can get farther from zero, Decreasing is how fast the input can get closer to zero.
    * The rate limits are only magnitudes.
    * @param increasingRateLimit The rate-of-change limit when the input is increasing, in units per
    *     second. This is expected to be positive. How quickly the input can get farther from zero.
    * @param decreasingRateLimit The rate-of-change limit when the input is decreasing, in units per
    *     second. This is expected to be positive. How quickly the input can get closer to zero.
    */
    public DynamicSlewRateLimiter(double increasingRateLimit, double decreasingRateLimit) {
        this(() -> increasingRateLimit, () -> decreasingRateLimit);
        if (increasingRateLimit < 0 || decreasingRateLimit < 0) {
            throw new IllegalArgumentException("Rate limits can't be negative! Increasing: "
            + increasingRateLimit + ", Decreasing: " + decreasingRateLimit);
        }
    }
    
    /**
    * Filters the input to limit its slew rate.
    *
    * @param input The input value whose slew rate is to be limited.
    * @return The filtered value, which will not change faster than the slew rate.
    */
    public double calculate(double input) {
        double currentTime = MathSharedStore.getTimestamp();
        double elapsedTime = currentTime - prevTime;
        double sign = Math.signum(prevVal);

        double increasing = increasingRateLimit.getAsDouble();
        double decreasing = decreasingRateLimit.getAsDouble();
        if (increasing < 0 || decreasing < 0) { // Maybe not great to throw here but it fundamentally breaks the class
            throw new IllegalArgumentException("Rate limits can't be negative! Increasing: "
            + increasing + ", Decreasing: " + decreasing);
        }

        double positiveRateLimit = increasing;
        double negativeRateLimit = decreasing;
        // Flip the positive and negative limits so that decreasing still means towards zero and increasing still means away.
        if (sign < 0) { 
            positiveRateLimit = decreasing;
            negativeRateLimit = increasing;
        } 
        prevVal +=
        MathUtil.clamp(
            input - prevVal,
            -negativeRateLimit * elapsedTime,
            positiveRateLimit * elapsedTime);
        prevTime = currentTime;
        return prevVal;
    }
    
    /**
    * Returns the value last calculated by the DynamicSlewRateLimiter.
    *
    * @return The last value.
    */
    public double lastValue() {
        return prevVal;
    }
    
    /**
    * Resets the slew rate limiter to the specified value; ignores the rate limit when doing so.
    *
    * @param value The value to reset to.
    */
    public void reset(double value) {
        prevVal = value;
        prevTime = MathSharedStore.getTimestamp();
    }
    
}