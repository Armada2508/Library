package frc.robot.lib.util.GearRatios;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;

public class GearRatio {
    /**
     * Internal running total of the current ratio 
     * */ 
    private double ratio = 1; // Start with raw motor or sensor rotations

    /**
     * 
     * @param stageRatio
     * @return
     */
    public GearRatio into(double stageRatio) {
        ratio *= stageRatio;
        return this;
    }

    /**
     * 
     * @return
     */
    public double getSensorToMechanismRatio() {
        return ratio;
    }

    /**
     * 
     * @return
     */
    public double getMechanismToSensorRatio() {
        return 1.0 / ratio;
    }

    /**
     * 
     * @param inputRotations
     * @return
     */
    public Angle getMechanismAngleFromSensor(Angle inputRotations) {
        return inputRotations.times(ratio);
    }

    /**
     * 
     * @param inputRotations
     * @return
     */
    public Angle getSensorAngleFromMechanism(Angle inputRotations) {
        return inputRotations.div(ratio);
    }

    /**
     * 
     * @param inputAngularVelocity
     * @return
     */
    public AngularVelocity getMechanismAngularVelocityFromSensor(AngularVelocity inputAngularVelocity) {
        return inputAngularVelocity.times(ratio);
    }

    /**
     * 
     * @param inputAngularVelocity
     * @return
     */
    public AngularVelocity getSensorAngularVelocityFromMechanism(AngularVelocity inputAngularVelocity) {
        return inputAngularVelocity.div(ratio);
    }

    /**
     * 
     * @param inputAngularAcceleration
     * @return
     */
    public AngularAcceleration getMechanismAngularAccelerationFromSensor(AngularAcceleration inputAngularAcceleration) {
        return inputAngularAcceleration.times(ratio);
    }

    /**
     * 
     * @param inputAngularAcceleration
     * @return
     */
    public AngularAcceleration getSensorAngularAccelerationFromMechanism(AngularAcceleration inputAngularAcceleration) {
        return inputAngularAcceleration.div(ratio);
    }

    //TODO: Handle angular to linear conversions and vice versa (see intake on Dreadnought)
}

