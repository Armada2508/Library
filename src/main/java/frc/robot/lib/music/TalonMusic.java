package frc.robot.lib.music;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.music.Orchestra;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class TalonMusic {

    private static final Orchestra orchestra = new Orchestra();
    private static final String startupSong = Filesystem.getDeployDirectory().getAbsolutePath() + "/PacmanMelody.chrp";
    private static final List<Subsystem> subsystems = new ArrayList<>();

    public static void addTalonFX(Subsystem currentSubsystem, TalonFX... talons) {
        if (talons == null) throw new IllegalArgumentException("Talons are null.");
        for (TalonFX talonFX : talons) {
            orchestra.addInstrument(talonFX);
            subsystems.add(currentSubsystem);
        }
    }
    
    public static void playStartupTune() {
        if (orchestra.isPlaying()) {
            orchestra.stop();
        }
        orchestra.loadMusic(startupSong);
        Subsystem[] systems = subsystems.toArray(new Subsystem[subsystems.size()]);
        SequentialCommandGroup group = new SequentialCommandGroup(
            new InstantCommand(orchestra::play),
            new WaitUntilCommand(() -> !orchestra.isPlaying())
        );
        group.addRequirements(systems);
        group.schedule();
    }

    public static void stopPlaying() {
        orchestra.stop();
    }

}
