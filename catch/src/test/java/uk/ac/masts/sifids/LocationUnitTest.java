package uk.ac.masts.sifids;

import org.junit.Test;

import uk.ac.masts.sifids.entities.CatchLocation;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LocationUnitTest {
    @Test
    public void icesSquares_CalculatedCorrectly() throws Exception {
        assertEquals(null, CatchLocation.getIcesRectangle(35.9, 0));
        assertEquals(null, CatchLocation.getIcesRectangle(85.6, 0));
        assertEquals(null, CatchLocation.getIcesRectangle(50, -44.1));
        assertEquals(null, CatchLocation.getIcesRectangle(50, 68.6));
        assertEquals("37F3", CatchLocation.getIcesRectangle(54.1, 3.1));
        assertEquals("45G0", CatchLocation.getIcesRectangle(58.2, 10.51));
        assertEquals("45A0", CatchLocation.getIcesRectangle(58.2, -43.51));
    }
}