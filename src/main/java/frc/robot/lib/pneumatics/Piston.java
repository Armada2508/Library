package frc.robot.lib.pneumatics;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Convenience class for controlling a piston with two single solenoids.
 */
public class Piston {

    private final Solenoid extend;
    private final Solenoid retract;

    /**
     * Creates a new piston.
     * @param extendSolenoid The solenoid connected to the piston that causes it to extend.
     * @param retractSolenoid The solenoid connected to the piston that causes it to retract.
     */
    public Piston(Solenoid extendSolenoid, Solenoid retractSolenoid) {
        extend = extendSolenoid;
        retract = retractSolenoid;
    }

    /**
     * Extends the piston.
     */
    public void extend() {
        extend.set(true);
        retract.set(false);
    }

    /**
     * Retracts the piston.
     */
    public void retract() {
        extend.set(false);
        retract.set(true);
    }

    /**
     * Turns off all solenoids.
     */
    public void disable() {
        extend.set(false);
        retract.set(false);
    }

    /**
     * Returns if the piston is currently extended.
     * @return True if the piston is extended or false if it isn't.
     */
    public boolean isExtended() {
        return extend.get() && !retract.get();
    }

}
