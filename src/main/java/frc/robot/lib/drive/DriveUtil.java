package frc.robot.lib.drive;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * Contains helpful methods to build functionality for driving.
 */
public class DriveUtil {

    /**
     * Prevent this class from being instantiated.
     */
    private DriveUtil() {}

    /**
     * Accounts for deadband with a smooth transition between the two states.
     * @param x - value to use
     * @param deadband - should be positive
     * @param smoothing - how quickly the curve should reach  {@code x}, 1 means it will reach {@code x} when {@code x} = 1.
     * Greater than 1 it will reach {@code x} sooner.
     * @return Returns a new value accounting for deadband that matches the sign of {@code x} and will
     * never have a larger magnitude than {@code x}
     */
    public static double processDeadband(double x, double deadband, double smoothing) {
        double positiveX = Math.abs(x);
        if (positiveX < deadband) {
            return 0;
        }
        else {
        return MathUtil.clamp((smoothing / (1 - deadband)) * (positiveX - deadband), 0, positiveX) * Math.signum(x);
        }
    }

    /**
     * Squares a value and retains its original sign.
     */
    public static double squareInput(double x) {
        return Math.signum(x) * (x * x);
    }

    /**
     * Applies constant curvature, {@link DifferentialDrive#curvatureDriveIK}.
     */
    public static double constantCurvature(double speed, double turn, double trim) {
        return Math.abs(speed) * turn + trim;
    }

    /**
     * Scales two values so that neither value exceeds -1 or 1.
     * Useful for normalizing left and right speeds or voltages.
     */
    public static Pair<Double, Double> normalizeValues(double first, double second){
        double factor = 1;
        double max = Math.max(Math.abs(first), Math.abs(second));
        if (max > 1) {
            factor = 1 / max;
        }
        return Pair.of(first * factor, second * factor);
    }

}
