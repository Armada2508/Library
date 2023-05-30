package frc.robot;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.driving.DriveCommand;
import frc.robot.lib.music.TalonMusic;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ExampleSubsystem;

public class RobotContainer {

    private final Joystick joystick = new Joystick(0);
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
        new JoystickButton(joystick, 1).onTrue(exampleSubsystem.doMotionMagic(0, 0, 0));
        // Stop Everything
        new JoystickButton(joystick, 11).onTrue(Commands.runOnce(this::stopEverything));
    }

    public Command getAutoCommand() {
       return new InstantCommand();
    }

    // void logSubsystems() {
    //     try {
    //         final Field fieldIndex = SequentialCommandGroup.class.getDeclaredField("m_currentCommandIndex");
    //         fieldIndex.setAccessible(true);
    //         final Field fieldCommands = SequentialCommandGroup.class.getDeclaredField("m_commands");
    //         fieldCommands.setAccessible(true);
    //         SubsystemBase loggerSubsystem = new SubsystemBase() {};
    //         loggerSubsystem.setDefaultCommand(Commands.run(() -> {
    //             System.out.println("\nDEBUG: Subsystem Logger");
    //             for (int i = 0; i < subsystems.length; i++) {
    //                 String name = "None";
    //                 Command command = subsystems[i].getCurrentCommand();
    //                 if (command != null) {
    //                     name = command.getName();
    //                     if (command instanceof SequentialCommandGroup) {
    //                         try {
    //                             @SuppressWarnings("unchecked")
    //                             List<Command> list = (List<Command>) fieldCommands.get(command);
    //                             name += " - " + list.get(fieldIndex.getInt(command)).getName();
    //                         } catch (IllegalArgumentException | IllegalAccessException e) {
    //                             e.printStackTrace();
    //                         }
    //                     }
    //                 }
    //                 System.out.println(subsystems[i].getName() + ": " + name);
    //             }
    //         }, loggerSubsystem));
    //     } catch (NoSuchFieldException | SecurityException e) {
    //         e.printStackTrace();
    //     }
    // }

}
