package frc.robot.lib.logging;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.epilogue.CustomLoggerFor;
import edu.wpi.first.epilogue.logging.ClassSpecificLogger;
import edu.wpi.first.epilogue.logging.DataLogger;

@CustomLoggerFor(TalonFX.class)
public class TalonFXLogger extends ClassSpecificLogger<TalonFX> {

    public TalonFXLogger() {
        super(TalonFX.class);
    }

    @Override
    protected void update(DataLogger dataLogger, TalonFX talon) {
        dataLogger.log("Device ID", talon.getDeviceID());
        dataLogger.log("Control Mode", talon.getControlMode().getValue());
        dataLogger.log("Rotor Polarity", talon.getAppliedRotorPolarity().getValue());
        dataLogger.log("Fwd Limit Switch", talon.getForwardLimit().getValue());
        dataLogger.log("Rev Limit Switch", talon.getReverseLimit().getValue());
        dataLogger.log("Position (Rots)", talon.getPosition().getValueAsDouble());
        dataLogger.log("Velocity (Rots\\s)", talon.getVelocity().getValueAsDouble());
        dataLogger.log("Acceleration (Rots\\s^2)", talon.getAcceleration().getValueAsDouble());
        dataLogger.log("Closed Loop Target", talon.getClosedLoopReference().getValueAsDouble());
        dataLogger.log("Closed Loop Slot", talon.getClosedLoopSlot().getValue());
        dataLogger.log("Supply Voltage (V)", talon.getSupplyVoltage().getValueAsDouble());
        dataLogger.log("Motor Voltage (V)", talon.getMotorVoltage().getValueAsDouble());
        dataLogger.log("Supply Current (A)", talon.getSupplyCurrent().getValueAsDouble());
        dataLogger.log("Torque Current (A)", talon.getTorqueCurrent().getValueAsDouble());
        dataLogger.log("Device Temperature (C)", talon.getDeviceTemp().getValueAsDouble());
        dataLogger.log("Firmware Version", talon.getVersion().getValue());
        dataLogger.log("Has Reset Occurred", talon.hasResetOccurred());
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
