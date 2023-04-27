package frc.robot.subsystems;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import ntannotate.Loggable;
import ntannotate.NTLogger;

public class ExampleSubsystem extends SubsystemBase implements Loggable {

    public ExampleSubsystem() {
        NTLogger.register(this);
    }

    @Override
    public Map<String, NetworkTableValue> log() {
        Map<String, NetworkTableValue> map = new HashMap<>();
        map.put("Test1", NetworkTableValue.makeBoolean(false));
        map.put("Test2", NetworkTableValue.makeDouble(0.2));
        map.put("Test3", NetworkTableValue.makeInteger(6));
        map.put("Test4", NetworkTableValue.makeString("woo"));
        map.put("Test5", NetworkTableValue.makeRaw(new byte[]{3, 4}));
        return map;
    }
    
}
