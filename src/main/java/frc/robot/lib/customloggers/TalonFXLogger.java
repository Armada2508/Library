package frc.robot.lib.customloggers;

import java.util.HashMap;
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
import edu.wpi.first.units.measure.Voltage;

@CustomLoggerFor(TalonFX.class)
public class TalonFXLogger extends ClassSpecificLogger<TalonFX> {

    private static Map<TalonFX, TalonFXSignals> talonFXSignals = new HashMap<>();

    public TalonFXLogger() {
        super(TalonFX.class);
    }

    @Override
    protected void update(EpilogueBackend dataLogger, TalonFX talon) {
        var signals = talonFXSignals.computeIfAbsent(talon, TalonFXSignals::new);
        signals.refresh();
        dataLogger.log("Device ID", talon.getDeviceID());
        dataLogger.log("Has Reset Occurred", talon.hasResetOccurred());
        dataLogger.log("Control Mode", signals.controlMode().getValue());
        dataLogger.log("Rotor Polarity", signals.appliedRotorPolarity().getValue());
        dataLogger.log("Fwd Limit Switch", signals.forwardLimit().getValue());
        dataLogger.log("Rev Limit Switch", signals.reverseLimit().getValue());
        dataLogger.log("Position (Rots)", signals.position().getValueAsDouble());
        dataLogger.log("Velocity (Rots\\s)", signals.velocity().getValueAsDouble());
        dataLogger.log("Acceleration (Rots\\s^2)", signals.acceleration().getValueAsDouble());
        dataLogger.log("Closed Loop Reference", signals.closedLoopReference().getValueAsDouble());
        dataLogger.log("Closed Loop Slot", signals.closedLoopSlot().getValue());
        dataLogger.log("Supply Voltage (V)", signals.supplyVoltage().getValueAsDouble());
        dataLogger.log("Motor Voltage (V)", signals.motorVoltage().getValueAsDouble());
        dataLogger.log("Supply Current (A)", signals.supplyCurrent().getValueAsDouble());
        dataLogger.log("Torque Current (A)", signals.torqueCurrent().getValueAsDouble());
        dataLogger.log("Device Temperature (C)", signals.deviceTemp().getValueAsDouble());
        dataLogger.log("Firmware Version", signals.version().getValue());
    }

}

record TalonFXSignals(
    StatusSignal<ControlModeValue> controlMode,
    StatusSignal<AppliedRotorPolarityValue> appliedRotorPolarity,
    StatusSignal<ForwardLimitValue> forwardLimit,
    StatusSignal<ReverseLimitValue> reverseLimit,
    StatusSignal<Angle> position,
    StatusSignal<AngularVelocity> velocity,
    StatusSignal<AngularAcceleration> acceleration,
    StatusSignal<Double> closedLoopReference,
    StatusSignal<Integer> closedLoopSlot,
    StatusSignal<Voltage> supplyVoltage,
    StatusSignal<Voltage> motorVoltage,
    StatusSignal<Current> supplyCurrent,
    StatusSignal<Current> torqueCurrent,
    StatusSignal<Temperature> deviceTemp,
    StatusSignal<Integer> version
){
    public TalonFXSignals(TalonFX talon) {
        this(
            talon.getControlMode(),
            talon.getAppliedRotorPolarity(),
            talon.getForwardLimit(),
            talon.getReverseLimit(),
            talon.getPosition(),
            talon.getVelocity(),
            talon.getAcceleration(),
            talon.getClosedLoopReference(),
            talon.getClosedLoopSlot(),
            talon.getSupplyVoltage(),
            talon.getMotorVoltage(),
            talon.getSupplyCurrent(),
            talon.getTorqueCurrent(),
            talon.getDeviceTemp(),
            talon.getVersion()
        );
    }

    public void refresh() {
        BaseStatusSignal.refreshAll(
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