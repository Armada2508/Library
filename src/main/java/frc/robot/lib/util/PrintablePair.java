package frc.robot.lib.util;

import edu.wpi.first.math.Pair;

public class PrintablePair<A, B> extends Pair<A, B> {
    
    public PrintablePair(A a, B b) {
        super(a, b);
    }

    @Override
    public String toString() {
        return "(" + getFirst() + ", " + getSecond() + ")";
    }

}
