package frc.robot.subsystems;

import java.util.HashMap;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.Example;
import frc.robot.lib.logging.Loggable;
import frc.robot.lib.logging.NTLogger;
import frc.robot.lib.motionmagic.MotionMagicCommand;
import frc.robot.lib.motionmagic.MotionMagicSubsystem;

public class ExampleSubsystem extends SubsystemBase implements MotionMagicSubsystem, Loggable {

    private TalonFX talon = new TalonFX(-1);

    public ExampleSubsystem() {
        NTLogger.register(this);
        configMotors(Example.talonConfig);
    }

    @Override
    public void configMotors(TalonFXConfiguration config) {
        talon.configFactoryDefault();
        talon.configAllSettings(config);
        talon.setNeutralMode(NeutralMode.Brake);
    }

    @Override
    public void configMotionMagic(double velocity, double acceleration) {
        talon.configMotionCruiseVelocity(velocity);
        talon.configMotionAcceleration(acceleration);
    }

    @Override
    public void stop() {
        talon.neutralOutput();
    }

    @Override
    public void setPosition(double position) {
        talon.set(TalonFXControlMode.MotionMagic, position);
    }

    @Override
    public double getSensorPosition() {
        return talon.getSelectedSensorPosition();
    }

    @Override
    public double getSensorTarget() {
        return talon.getClosedLoopTarget();
    }

    /**
     * @param position imaginary units
     * @param velocity imaginary units
     * @param acceleration imaginary units
     */
    @Override
    public Command doMotionMagic(double position, double velocity, double acceleration) {
        return new MotionMagicCommand(position, velocity, acceleration, this);
    }

    @Override
    public Map<String, Object> log() {
        Map<String, Object> map = new HashMap<>();
        map.put("Test1", false);
        map.put("Test2", 0.2);
        map.put("Test3", 6);
        map.put("Test4", "woo");
        map.put("Test5", new byte[]{3, 4});
        map.put("Test6", new PIDController(0, 0, 0));
        return map;
    }

}
