package frc.robot.lib.drive;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

/**
 * A drive command with good bit of functionality. 
 */
public class ButterySmoothDriveCommand extends Command {

    private DoubleSupplier joystickSpeed;
    private DoubleSupplier joystickTurn;
    private DoubleSupplier joystickTrim;
    private BooleanSupplier joystickSlow;
    private SlewRateLimiter limiter;
    private BiConsumer<Double, Double> speedConsumer;
    private Runnable onEnd;
    private DriveConfig config;

    public ButterySmoothDriveCommand(DoubleSupplier joystickSpeed, DoubleSupplier joystickTurn, DoubleSupplier joystickTrim,  BooleanSupplier joystickSlow, DriveConfig config, 
        BiConsumer<Double, Double> speedConsumer, Runnable onEnd, Subsystem subsystem) {
        this.joystickSpeed = joystickSpeed;
        this.joystickTurn = joystickTurn;
        this.joystickTrim = joystickTrim;
        this.joystickSlow = joystickSlow;
        this.speedConsumer = speedConsumer;
        this.onEnd = onEnd;
        this.config = config;
        limiter = new SlewRateLimiter(config.slewRate);
        addRequirements(subsystem);
    }

    @Override
    public void execute() {
        double speed = joystickSpeed.getAsDouble();
        double turn = joystickTurn.getAsDouble();
        double trim = joystickTrim.getAsDouble();
        // Deadband
        speed = processDeadband(speed);
        turn = processDeadband(turn); 
        trim = processDeadband(trim); 
        // Slow Mode
        if (joystickSlow.getAsBoolean()) {
            speed = config.slowSpeed * speed;
            turn = config.slowSpeed * turn;
            trim = config.slowSpeed * trim;
        }
        // Square the inputs
        if (config.squareInputs) {
            speed = Math.signum(speed) * (speed * speed);
            turn = Math.signum(turn) * (turn * turn);        
            trim = Math.signum(trim) * (trim * trim);   
        }
        // Speed Adjusting and Slew Rate Limiting 
        speed *= config.speedAdjustment;
        turn *= config.turnAdjustment;
        trim *= config.trimAdjustment;
        speed = limiter.calculate(speed);
        // Constant Curvature, WPILib DifferentialDrive#curvatureDriveIK
        turn = turn * speed + trim; 

        double powerFactor = normalizeSpeed((speed - turn), (speed + turn));

        double leftSpeed = (speed - turn) * powerFactor;
        double rightSpeed = (speed + turn) * powerFactor;
        speedConsumer.accept(leftSpeed, rightSpeed);
    }

    /**
     * {@link} https://www.chiefdelphi.com/uploads/default/original/3X/b/a/ba7ccfd90bac0934e374dd4459d813cee2903942.pdf
     */
    private double processDeadband(double val) {
        double newVal = val;
        if(Math.abs(val) < config.joystickDeadband) {
            newVal = 0;
        }
        else {
            // Point slope form Y = M(X-X0)+Y0.
            newVal = /*M*/ (1 / (1 - config.joystickDeadband)) * /*X-X0*/(val + (-Math.signum(val) * config.joystickDeadband));
        }
        return newVal;
    }

    private double normalizeSpeed(double left, double right){
        double p = 1;
        if (left > 1) {
            p = 1/left;
        } 
        else if (right > 1) {
            p = 1/right;
        }
        return p;
    }

    @Override
    public void end(boolean interrupted) {
        onEnd.run();
    }

    public record DriveConfig(
        double speedAdjustment, 
        double turnAdjustment, 
        double trimAdjustment, 
        double slowSpeed, 
        boolean squareInputs, 
        double slewRate, 
        double joystickDeadband
    ) {}

}
