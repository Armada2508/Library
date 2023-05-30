package frc.robot.lib.motionmagic;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class MotionMagicCommand extends CommandBase {

    private double targetPosition;
    private double velocity;
    private double acceleration;
    private MotionMagicSubsystem subsystem;
    private final double allowedSensorError = 0.05;

    public MotionMagicCommand(double targetPosition, double velocity, double accerleration, MotionMagicSubsystem subsystem) {
        this.targetPosition = targetPosition;
        this.velocity = velocity;
        this.acceleration = accerleration;
        this.subsystem = subsystem;
    }

    @Override
    public void initialize() {
        subsystem.configMotionMagic(velocity, acceleration);
        subsystem.setPosition(targetPosition);
    }

    @Override
    public void execute() {}

    @Override
    public void end(boolean interrupted) {
        subsystem.stop();
    }

    @Override
    public boolean isFinished() {
        double current = subsystem.getSensorPosition();
        double target = subsystem.getSensorTarget();
        return Math.abs(target-current) < allowedSensorError;
    }
    
}
