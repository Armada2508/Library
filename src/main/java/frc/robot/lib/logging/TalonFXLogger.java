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
    private static boolean selfRefresh = true;

    public TalonFXLogger() {
        super(TalonFX.class);
    }

    @Override
    protected void update(EpilogueBackend dataLogger, TalonFX talon) {
        var signals = talonFXSignals.computeIfAbsent(talon, TalonFXSignals::new);
        if (selfRefresh) {
            var all = signals.allSignals();
            BaseStatusSignal.refreshAll(all.toArray(new BaseStatusSignal[all.size()]));
        }
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

    /**
     * Disables the TalonFXLogger from automatically refreshing each TalonFX individually refreshes them all at once
     * periodically determined by the period and offset provided.
     * @param robot The robot to add the callback to
     * @param period The rate at which the TalonFXs should be refreshed
     * @param offset The offset from the main loop at which this refresh should occur
     */
    public static void refreshAllLoggedTalonFX(TimedRobot robot, Time period, Time offset) {
        selfRefresh = false;
        robot.addPeriodic(() -> {
            List<BaseStatusSignal> signals = new ArrayList<>(); // cache this
            talonFXSignals.values().forEach(s -> signals.addAll(s.allSignals()));
            if (signals.size() > 0) {
                BaseStatusSignal.refreshAll(signals.toArray(new BaseStatusSignal[signals.size()]));
            }
        }, period, offset);
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

    public List<BaseStatusSignal> allSignals() {
        // TODO Use reflection and cache the list by making this a normal class ? Don't need to do that in the constructor.., could do everything in the constructor
        // List<BaseStatusSignal> allSignals = new ArrayList<>();
        // for (var comp : getClass().getRecordComponents()) {
        //     try {
        //         Method method = getClass().getDeclaredMethod(comp.getName());
        //         var signal = method.invoke(this);
        //         allSignals.add((BaseStatusSignal) signal);
        //     } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        //         e.printStackTrace();
        //     }
        // }
        // return allSignals;
        return List.of(
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
