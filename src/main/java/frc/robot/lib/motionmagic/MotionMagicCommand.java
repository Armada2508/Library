package frc.robot.lib.motionmagic;

import edu.wpi.first.wpilibj2.command.Command;

public class MotionMagicCommand extends Command {

    private final double targetPosition;
    private final double velocity;
    private final double acceleration;
    private final double allowedError;
    private final MotionMagicSubsystem subsystem;
    
    public MotionMagicCommand(double targetPosition, double velocity, double acceleration, double allowedError, MotionMagicSubsystem subsystem) {
        this.targetPosition = targetPosition;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.allowedError = allowedError;
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
        double current = subsystem.getPosition();
        double target = subsystem.getTarget();
        return Math.abs(target-current) < allowedError;
    }
    
}
