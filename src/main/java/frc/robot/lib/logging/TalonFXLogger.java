package frc.robot.lib.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.AppliedRotorPolarityValue;
import com.ctre.phoenix6.signals.ControlModeValue;
import com.ctre.phoenix6.signals.ForwardLimitValue;
import com.ctre.phoenix6.signals.ReverseLimitValue;

import edu.wpi.first.epilogue.CustomLoggerFor;
import edu.wpi.first.epilogue.logging.ClassSpecificLogger;
import edu.wpi.first.epilogue.logging.EpilogueBackend;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.TimedRobot;

@CustomLoggerFor(TalonFX.class)
public class TalonFXLogger extends ClassSpecificLogger<TalonFX> {

    private static Map<TalonFX, TalonFXSignals> talonFXSignals = new HashMap<>();

    public TalonFXLogger() {
        super(TalonFX.class);
    }

    @Override
    protected void update(EpilogueBackend dataLogger, TalonFX talon) {
        var signals = talonFXSignals.computeIfAbsent(talon, TalonFXSignals::new);
        dataLogger.log("Device ID", talon.getDeviceID());
        dataLogger.log("Has Reset Occurred", talon.hasResetOccurred());
        dataLogger.log("Connected", talon.isConnected());
        dataLogger.log("Control Mode", signals.controlMode.getValue());
        dataLogger.log("Rotor Polarity", signals.appliedRotorPolarity.getValue());
        dataLogger.log("Fwd Limit Switch", signals.forwardLimit.getValue());
        dataLogger.log("Rev Limit Switch", signals.reverseLimit.getValue());
        dataLogger.log("Position (Rots)", signals.position.getValueAsDouble());
        dataLogger.log("Velocity (Rots\\s)", signals.velocity.getValueAsDouble());
        dataLogger.log("Acceleration (Rots\\s^2)", signals.acceleration.getValueAsDouble());
        dataLogger.log("Closed Loop Reference", signals.closedLoopReference.getValueAsDouble());
        dataLogger.log("Closed Loop Slot", signals.closedLoopSlot.getValue());
        dataLogger.log("Supply Voltage (V)", signals.supplyVoltage.getValueAsDouble());
        dataLogger.log("Motor Voltage (V)", signals.motorVoltage.getValueAsDouble());
        dataLogger.log("Supply Current (A)", signals.supplyCurrent.getValueAsDouble());
        dataLogger.log("Torque Current (A)", signals.torqueCurrent.getValueAsDouble());
        dataLogger.log("Device Temperature (C)", signals.deviceTemp.getValueAsDouble());
        dataLogger.log("Firmware Version", signals.version.getValue());
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
            List<BaseStatusSignal> signals = new ArrayList<>(); // cache this
            talonFXSignals.values().forEach(s -> signals.addAll(s.allSignals));
            if (signals.size() > 0) {
                BaseStatusSignal.refreshAll(signals.toArray(new BaseStatusSignal[signals.size()]));
            }
        }, period, offset);
    }

}

class TalonFXSignals {
    
    public final StatusSignal<ControlModeValue> controlMode;
    public final StatusSignal<AppliedRotorPolarityValue> appliedRotorPolarity;
    public final StatusSignal<ForwardLimitValue> forwardLimit;
    public final StatusSignal<ReverseLimitValue> reverseLimit;
    public final StatusSignal<Angle> position;
    public final StatusSignal<AngularVelocity> velocity;
    public final StatusSignal<AngularAcceleration> acceleration;
    public final StatusSignal<Double> closedLoopReference;
    public final StatusSignal<Integer> closedLoopSlot;
    public final StatusSignal<Voltage> supplyVoltage;
    public final StatusSignal<Voltage> motorVoltage;
    public final StatusSignal<Current> supplyCurrent;
    public final StatusSignal<Current> torqueCurrent;
    public final StatusSignal<Temperature> deviceTemp;
    public final StatusSignal<Integer> version;
    public final List<BaseStatusSignal> allSignals;

    public TalonFXSignals(TalonFX talon) {
        this.controlMode = talon.getControlMode();
        this.appliedRotorPolarity = talon.getAppliedRotorPolarity();
        this.forwardLimit = talon.getForwardLimit();
        this.reverseLimit = talon.getReverseLimit();
        this.position = talon.getPosition();
        this.velocity = talon.getVelocity();
        this.acceleration = talon.getAcceleration();
        this.closedLoopReference = talon.getClosedLoopReference();
        this.closedLoopSlot = talon.getClosedLoopSlot();
        this.supplyVoltage = talon.getSupplyVoltage();
        this.motorVoltage = talon.getMotorVoltage();
        this.supplyCurrent = talon.getSupplyCurrent();
        this.torqueCurrent = talon.getTorqueCurrent();
        this.deviceTemp = talon.getDeviceTemp();
        this.version = talon.getVersion();
        allSignals = List.of(
            controlMode,
            appliedRotorPolarity,
            forwardLimit,
            reverseLimit,
            position,
            velocity,
            acceleration,
            closedLoopReference,
            closedLoopSlot,
            supplyVoltage,
            motorVoltage,
            supplyCurrent,
            torqueCurrent,
            deviceTemp,
            version
        );
    }

}
