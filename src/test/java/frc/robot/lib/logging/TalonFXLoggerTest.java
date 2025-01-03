package frc.robot.lib.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TalonFXLoggerTest {

    @Test
    /**
     * This is really just a reminder that if the number of signals in this class changes, the allSignals() method
     * must be changed as well.
     */
    void testTalonFXSignalsLength() {
        assertEquals(15, TalonFXSignals.class.getRecordComponents().length);
    }

}
