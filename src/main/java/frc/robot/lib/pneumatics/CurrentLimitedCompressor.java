package frc.robot.lib.pneumatics;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Seconds;

import edu.wpi.first.units.Current;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Time;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class CurrentLimitedCompressor extends Compressor {

    private double currentTime = 0;
    private final double maxAmps;
    private final double timeToTrip;
    private static int numberInstances = 0;

    public CurrentLimitedCompressor(int module, PneumaticsModuleType moduleType, double maxAmps, double timeToTrip) {
        super(module, moduleType);
        this.maxAmps = maxAmps;
        this.timeToTrip = timeToTrip;
        numberInstances++;
    }

    public CurrentLimitedCompressor(int module, PneumaticsModuleType moduleType, Measure<Current> maxAmps, Measure<Time> timeToTrip) {
        this(module, moduleType, maxAmps.in(Amps), timeToTrip.in(Seconds));
    }

    public void check(double timeStep) {
        double current = getCurrent();
        if (current > maxAmps) {
            currentTime += timeStep;
            if (currentTime > timeToTrip) {
                System.out.println("Disabled " + numberInstances + " Compressor! Current: " + current);
                disable();
            }
        } else {
            currentTime = 0;
        }
    }

}
