package frc.robot.lib.logging;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.epilogue.CustomLoggerFor;
import edu.wpi.first.epilogue.logging.ClassSpecificLogger;
import edu.wpi.first.epilogue.logging.EpilogueBackend;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.wpilibj.TimedRobot;

@CustomLoggerFor(TalonFX.class)
public class TalonFXLogger extends ClassSpecificLogger<TalonFX> {

    private static List<BaseStatusSignal> allSignals = new ArrayList<>();
    private static List<TalonFX> trackedTalons = new ArrayList<>();

    public TalonFXLogger() {
        super(TalonFX.class);
    }

    @Override
    protected void update(EpilogueBackend dataLogger, TalonFX talon) {
        if (!trackedTalons.contains(talon)) {
            addTalonSignals(talon);
            trackedTalons.add(talon);
        }
        dataLogger.log("Device ID", talon.getDeviceID());
        dataLogger.log("Has Reset Occurred", talon.hasResetOccurred());
        dataLogger.log("Connected", talon.isConnected());
        dataLogger.log("Bridge Output", talon.getBridgeOutput(false).getValue());
        dataLogger.log("Control Mode", talon.getControlMode(false).getValue());
        dataLogger.log("Rotor Polarity", talon.getAppliedRotorPolarity(false).getValue());
        dataLogger.log("Fwd Limit Switch", talon.getForwardLimit(false).getValue());
        dataLogger.log("Rev Limit Switch", talon.getReverseLimit(false).getValue());
        dataLogger.log("Fwd Soft Limit Switch", talon.getFault_ForwardSoftLimit(false).getValue());
        dataLogger.log("Rev Soft Limit Switch", talon.getFault_ReverseSoftLimit(false).getValue());
        dataLogger.log("Position (Rots)", talon.getPosition(false).getValueAsDouble());
        dataLogger.log("Velocity (Rots\\s)", talon.getVelocity(false).getValueAsDouble());
        dataLogger.log("Acceleration (Rots\\s^2)", talon.getAcceleration(false).getValueAsDouble());
        dataLogger.log("Closed Loop Reference", talon.getClosedLoopReference(false).getValueAsDouble());
        dataLogger.log("Closed Loop Reference Slope", talon.getClosedLoopReferenceSlope(false).getValueAsDouble());
        dataLogger.log("Closed Loop Slot", talon.getClosedLoopSlot(false).getValue());
        dataLogger.log("Supply Voltage (V)", talon.getSupplyVoltage(false).getValueAsDouble());
        dataLogger.log("Motor Voltage (V)", talon.getMotorVoltage(false).getValueAsDouble());
        dataLogger.log("Supply Current (A)", talon.getSupplyCurrent(false).getValueAsDouble());
        dataLogger.log("Torque Current (A)", talon.getTorqueCurrent(false).getValueAsDouble());
        dataLogger.log("Device Temperature (C)", talon.getDeviceTemp(false).getValueAsDouble());
        dataLogger.log("Firmware Version", talon.getVersion(false).getValue());
    }

    /**
     * This must be called for your TalonFXs to be logged properly.
     * Refreshes all of the TalonFX at once, periodically determined by the period and offset provided.
     * @param robot The robot to add the callback to
     * @param period The rate at which the TalonFXs should be refreshed
     * @param offset The offset from the main loop at which this refresh should occur
     */
    public static void refreshAllLoggedTalonFX(TimedRobot robot, Time period, Time offset) {
        robot.addPeriodic(() -> {
            if (allSignals.size() > 0) {
                BaseStatusSignal.refreshAll(allSignals.toArray(new BaseStatusSignal[allSignals.size()]));
            }
        }, period, offset);
    }

    /**
     * Adds a talon's signals to be refreshed on a periodic loop since this logger doesn't refresh signals when directly logging them
     */
    private static void addTalonSignals(TalonFX talon) {
        allSignals.addAll(List.of(
            talon.getBridgeOutput(),
            talon.getControlMode(),
            talon.getAppliedRotorPolarity(),
            talon.getForwardLimit(),
            talon.getReverseLimit(),
            talon.getFault_ForwardSoftLimit(),
            talon.getFault_ReverseSoftLimit(),
            talon.getPosition(),
            talon.getVelocity(),
            talon.getAcceleration(),
            talon.getClosedLoopReference(),
            talon.getClosedLoopReferenceSlope(),
            talon.getClosedLoopSlot(),
            talon.getSupplyVoltage(),
            talon.getMotorVoltage(),
            talon.getSupplyCurrent(),
            talon.getTorqueCurrent(),
            talon.getDeviceTemp(),
            talon.getVersion()
        ));
    }

}

