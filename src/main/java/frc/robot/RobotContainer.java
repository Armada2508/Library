package frc.robot;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.commands.driving.DriveCommand;
import frc.robot.lib.controller.SmartJoystick;
import frc.robot.lib.music.TalonMusic;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ExampleSubsystem;

public class RobotContainer {

    private final SmartJoystick joystick = new SmartJoystick(0);
	private final WPI_PigeonIMU pigeon = new WPI_PigeonIMU(Constants.pigeonID);
    private final DriveSubsystem driveSubsystem = new DriveSubsystem(pigeon);
    private final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();

    public RobotContainer() {
        pigeon.setYaw(0);
        driveSubsystem.setDefaultCommand(new DriveCommand(() -> -joystick.getRawAxis(1), () -> -joystick.getRawAxis(0),  () -> -joystick.getRawAxis(2), () -> joystick.getRawButton(12), true, driveSubsystem)); // default to driving from joystick input
        configureButtons();
    }

    public void stopEverything() {
        TalonMusic.stopPlaying();
        CommandScheduler.getInstance().cancelAll();
        driveSubsystem.stop();
    }

    private void configureButtons() {
        joystick.onTrue(1, exampleSubsystem.doMotionMagic(0, 0, 0));
        joystick.onTrue(11, Commands.runOnce(this::stopEverything));
    }

    public Command getAutoCommand() {
       return new InstantCommand();
    }

}
