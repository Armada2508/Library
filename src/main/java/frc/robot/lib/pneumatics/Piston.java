package frc.robot.lib.pneumatics;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

public class Piston {

    private final Solenoid extend;
    private final Solenoid retract;
    private boolean extendInverted = false;
    private boolean retractInverted = false;

    /**
     * Creates a new Piston Object
     * @param retractSolenoid The ID of the solenoid connected to the piston that causes it to retract
     * @param extendSolenoid The ID of the solenoid connected to the piston that causes it to extend
     */
    public Piston(int retractSolenoid, int extendSolenoid) {
        extend = new Solenoid(PneumaticsModuleType.CTREPCM, extendSolenoid);
        retract = new Solenoid(PneumaticsModuleType.CTREPCM, retractSolenoid);
    }

    /**
     * Sets if the extend solenoid is inverted
     * @param inverted If the extend solenoid is inverted
     */
    public void extendInverted(boolean inverted) {
        extendInverted = inverted;
    }


    /**
     * Sets if the retract solenoid is inverted
     * @param inverted If the retract solenoid is inverted
     */
    public void retractInverted(boolean inverted) {
        retractInverted = inverted;
    }

    /**
     * Extend the Piston
     */
    public void extend() {
        extend.set(!extendInverted);
        retract.set(retractInverted);
    }

    /**
     * Retract the Piston
     */
    public void retract() {
        extend.set(extendInverted);
        retract.set(!retractInverted);
    }

    /**
     * Vent the Piston
     */
    public void vent() {
        extend.set(extendInverted);
        retract.set(retractInverted);
    }

    /**
     * Turn off all solenoids
     */
    public void disable() {
        extend.set(false);
        retract.set(false);
    }

    public boolean isExtended() {
        return extend.get() && !retract.get();
    }

}