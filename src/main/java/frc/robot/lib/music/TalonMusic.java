package frc.robot.lib.music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ctre.phoenix6.Orchestra;
import com.ctre.phoenix6.configs.AudioConfigs;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;

public class TalonMusic {

    private static final Orchestra orchestra = new Orchestra();
    private static final AudioConfigs audioConfigs = new AudioConfigs().withAllowMusicDurDisable(true);
    private static final List<Subsystem> subsystems = new ArrayList<>();
    private static final String startupSong = "PacmanMelody.chrp";
    private static int trackNumber = 0;

    /**
     * Adds talons to the class's internal orchestra, also modifies their audio configs to allow music when disabled.
     * @param currentSubsystem to add for music requirements
     * @param talons to use in orchestra
     */
    public static void addTalonFX(Subsystem currentSubsystem, TalonFX... talons) {
        Objects.requireNonNull(currentSubsystem);
        Objects.requireNonNull(talons);
        for (TalonFX talonFX : talons) {
            talonFX.getConfigurator().apply(audioConfigs);
            orchestra.addInstrument(talonFX, trackNumber);
            trackNumber++;
        }
        subsystems.add(currentSubsystem);
    }
    
    public static void playStartupTune() {
        playTune(startupSong);
    }

    /**
     * Plays a .chrp file on the talonFXs that have been added to the orchestra.
     * @param fileName file to play, we assume it's in the deploy directory
     */
    public static void playTune(String fileName) {
        if (orchestra.isPlaying()) {
            orchestra.stop();
        }
        orchestra.loadMusic(Filesystem.getDeployDirectory().getAbsolutePath() + File.separator + fileName);
        Subsystem[] requirements = subsystems.toArray(new Subsystem[subsystems.size()]);
        Commands.runOnce(orchestra::play, requirements)
            .andThen(Commands.waitUntil(() -> !orchestra.isPlaying()))
            .ignoringDisable(true)
            .schedule();
    }

    public static void stopPlaying() {
        orchestra.stop();
    }

}
