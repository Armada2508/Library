package frc.robot.lib.drive;

import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * Contains helpful methods to build functionality for driving.
 */
public class DriveHelper {

    /**
     * Prevent this class from being instantiated.
     */
    private DriveHelper() {}

    /**
     * Accounts for deadband with a smooth transition between the two states.
     * {@link} https://www.chiefdelphi.com/uploads/default/original/3X/b/a/ba7ccfd90bac0934e374dd4459d813cee2903942.pdf
     * @param deadband - should be positive
     * @return Returns a new value accounting for deadband that matches the sign of {@code val}
     */
    public static double processDeadband(double val, double deadband) {
        double newVal = val;
        if(Math.abs(val) < deadband) {
            newVal = 0;
        }
        else {
            // Point slope form Y = M(X-X0)+Y0.
            newVal = /*M*/ (1 / (1 - deadband)) * /*X-X0*/(val + (-Math.signum(val) * deadband));
        }
        return newVal;
    }

    /**
     * Squares a value and retains its original sign.
     */
    public static double squareInput(double val) {
        return Math.signum(val) * (val * val);
    }

    /**
     * Applies constant curvature, {@link DifferentialDrive#curvatureDriveIK}.
     */
    public static double constantCurvature(double speed, double turn, double trim) {
        return turn * speed + trim;
    }

    /**
     * Scales two values so that neither value exceeds one.
     * Useful for normalizing left and right speeds or voltages.
     */
    public static Pair<Double, Double> normalizeValues(double first, double second){
        double factor = 1;
        if (first > 1) {
            factor = 1 / first;
        } 
        else if (second > 1) {
            factor = 1 / second;
        }
        return Pair.of(first * factor, second * factor);
    }

}
