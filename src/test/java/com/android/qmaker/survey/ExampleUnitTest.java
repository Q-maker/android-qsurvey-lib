package com.android.qmaker.survey;

import com.istat.freedev.processor.Process;
import com.qmaker.survey.core.engines.QSurvey;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testBitwise() {
        int state = QSurvey.SurveyStateListener.STATE_PREPARED;
        int bitWise = QSurvey.SurveyStateListener.STATE_FINISH & state;
        assertEquals(state, bitWise);
    }

    @Test
    public void testBitwise2() {
        boolean result = isAIncludeInB(Process.STATE_DROPPED, Process.STATE_FLAG_FINISHED);
        assertTrue(result);
    }

    @Test
    public void testFinishIncludeInDropped() {
        boolean result = isAIncludeInB(Process.STATE_FLAG_FINISHED, Process.STATE_DROPPED);
        assertTrue(result);
    }

    @Test
    public void testBitwise3() {
        int state = Process.STATE_PROCESSING;
        int bitWise = Process.STATE_FLAG_FINISHED & state;
        assertEquals(state, bitWise);
    }

    @Test
    public void testBitwise4() {
        boolean result = isAIncludeInB(Process.STATE_STARTED, Process.STATE_PROCESSING);
        assertTrue(result);
    }

    private boolean isAIncludeInB(int A, int B) {
        int bitAnd = A & B;
        return A == bitAnd;
    }

    @Test
    public void testBitwise5() {
        int state = 0x00000003;
        int contol = 0x00000007;
        int bitWise = contol | state;
        int bitWiseInv = contol & state;
        assertEquals(state, bitWiseInv);
    }
}