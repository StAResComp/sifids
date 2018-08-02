package uk.ac.masts.sifids;

import org.junit.Test;

import java.util.Map;

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
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.001));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.1));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.2));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.3));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.4));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.5));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.6));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.7));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.8));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.9));
        assertEquals("49E9", CatchLocation.getIcesRectangle(60.2, -0.999));
        assertNotEquals("41E11", CatchLocation.getIcesRectangle(56.333, -2.8));
    }

    @Test
    public void icesSquareBounds_CalculatedCorrectly() throws Exception {
        Map bounds = CatchLocation.getIcesRectangleBounds(60.2, -0.5);
        assertEquals(60.0, bounds.get(CatchLocation.LOWER_LAT));
        assertEquals(60.5, bounds.get(CatchLocation.UPPER_LAT));
        assertEquals(-1.0, bounds.get(CatchLocation.LOWER_LONG));
        assertEquals(-0.0, bounds.get(CatchLocation.UPPER_LONG));
    }

    @Test
    public void latitudeComponents_CalculatedCorrectly() throws Exception {
        CatchLocation loc = new CatchLocation();
        loc.setLatitude(12.4);
        assertEquals('N', loc.getLatitudeDirection());
        assertEquals(12, loc.getLatitudeDegrees());
        assertEquals(24, loc.getLatitudeMinutes());
        assertEquals("12 24 N", loc.getLatitudeString());
        loc.setLatitude(-5.68);
        assertEquals('S', loc.getLatitudeDirection());
        assertEquals(5, loc.getLatitudeDegrees());
        assertEquals(41, loc.getLatitudeMinutes());
        assertEquals("05 41 S", loc.getLatitudeString());
    }

    @Test
    public void latitude_SetCorrectlyFromDegMinDir() throws Exception {
        CatchLocation loc = new CatchLocation();
        loc.setLatitude(12, 24, 'N');
        assertEquals(12.4, loc.getLatitude(), 0.01);
        loc.setLatitude(5, 41, 'S');
        assertEquals(-5.68, loc.getLatitude(), 0.01);
    }

    @Test
    public void longitudeComponents_CalculatedCorrectly() throws Exception {
        CatchLocation loc = new CatchLocation();
        loc.setLongitude(12.4);
        assertEquals('E', loc.getLongitudeDirection());
        assertEquals(12, loc.getLongitudeDegrees());
        assertEquals(24, loc.getLongitudeMinutes());
        assertEquals("12 24 E", loc.getLongitudeString());
        loc.setLongitude(-5.68);
        assertEquals('W', loc.getLongitudeDirection());
        assertEquals(5, loc.getLongitudeDegrees());
        assertEquals(41, loc.getLongitudeMinutes());
        assertEquals("05 41 W", loc.getLongitudeString());
    }

    @Test
    public void longitude_SetCorrectlyFromDegMinDir() throws Exception {
        CatchLocation loc = new CatchLocation();
        loc.setLongitude(12, 24, 'E');
        assertEquals(12.4, loc.getLongitude(), 0.01);
        loc.setLongitude(5, 41, 'W');
        assertEquals(-5.68, loc.getLongitude(), 0.01);
    }
}