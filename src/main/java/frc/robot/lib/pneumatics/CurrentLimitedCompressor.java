package frc.robot.lib.pneumatics;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class CurrentLimitedCompressor extends Compressor {
    
    private double currentTime = 0;
    private final int maxAmps;
    private final double timeToTrip;
    private static int numberInstances = 0;

    public CurrentLimitedCompressor(int module, PneumaticsModuleType moduleType, int maxAmps, double timeToTrip) {
        super(module, moduleType);
        this.maxAmps = maxAmps;
        this.timeToTrip = timeToTrip;
        numberInstances++;
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
