package com.benorim.carhov.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class GeoUtilsTest {

    private static final double DELTA = 0.01; // Tolerance for floating point comparisons

    @Test
    void calculateDistanceInMiles_SamePoint_ShouldReturnZero() {
        // Same coordinates should return distance of 0
        double lat = 40.7128;
        double lon = -74.0060;
        
        double distance = GeoUtils.calculateDistanceInMiles(lat, lon, lat, lon);
        
        assertEquals(0.0, distance);
    }

    @ParameterizedTest
    @CsvSource({
        // lat1, lon1, lat2, lon2, expectedMiles
        "40.7128, -74.0060, 34.0522, -118.2437, 2445.59", // New York to Los Angeles
        "51.5074, -0.1278, 48.8566, 2.3522, 213.48",      // London to Paris
        "35.6762, 139.6503, 22.3193, 114.1694, 1786.85",  // Tokyo to Hong Kong
        "33.9249, -118.4051, 37.6213, -122.3790, 338.83", // LAX to SFO
        "0.0, 0.0, 0.0, 90.0, 6218.47"                    // Equator points 90 degrees apart
    })
    void calculateDistanceInMiles_KnownDistances_ShouldMatchExpected(
            double lat1, double lon1, double lat2, double lon2, double expectedMiles) {
        
        double calculatedDistance = GeoUtils.calculateDistanceInMiles(lat1, lon1, lat2, lon2);
        
        assertEquals(expectedMiles, calculatedDistance, DELTA); // Higher tolerance for known distances
    }
    
    @Test
    void calculateDistanceInMiles_ShouldBeSymmetric() {
        // Distance should be the same regardless of direction
        double lat1 = 40.7128;
        double lon1 = -74.0060;
        double lat2 = 34.0522;
        double lon2 = -118.2437;
        
        double distance1 = GeoUtils.calculateDistanceInMiles(lat1, lon1, lat2, lon2);
        double distance2 = GeoUtils.calculateDistanceInMiles(lat2, lon2, lat1, lon1);
        
        assertEquals(distance1, distance2, DELTA);
    }
    
    @Test
    void calculateDistanceInMiles_InvalidCoordinates_ShouldNotThrowException() {
        // Test with extreme coordinates
        double distance = GeoUtils.calculateDistanceInMiles(90.0, 180.0, -90.0, -180.0);
        
        // Just verify no exception is thrown and result is a valid number
        assertFalse(Double.isNaN(distance));
        assertTrue(distance > 0);
    }
    
    @Test
    void isWithinRadius_ExactlyOnRadius_ShouldReturnTrue() {
        // Setup coordinates that are exactly 10 miles apart
        double lat1 = 40.7128;
        double lon1 = -74.0060;
        
        // Using an approximate coordinate ~10 miles away
        double lat2 = 40.8694; // Roughly north of first point
        double lon2 = -74.0060;
        
        // Calculate actual distance to determine radius
        double actualDistance = GeoUtils.calculateDistanceInMiles(lat1, lon1, lat2, lon2);
        
        // Check if point is within its exact distance radius
        boolean result = GeoUtils.isWithinRadius(lat1, lon1, lat2, lon2, actualDistance);
        
        assertTrue(result);
    }
    
    @Test
    void isWithinRadius_OutsideRadius_ShouldReturnFalse() {
        // New York coordinates
        double lat1 = 40.7128;
        double lon1 = -74.0060;
        
        // Los Angeles coordinates
        double lat2 = 34.0522;
        double lon2 = -118.2437;
        
        // Known distance is ~2450 miles, so use a smaller radius
        boolean result = GeoUtils.isWithinRadius(lat1, lon1, lat2, lon2, 1000.0);
        
        assertFalse(result);
    }
    
    @Test
    void isWithinRadius_InsideRadius_ShouldReturnTrue() {
        // New York coordinates
        double lat1 = 40.7128;
        double lon1 = -74.0060;
        
        // Newark, NJ coordinates (close to NY)
        double lat2 = 40.7357;
        double lon2 = -74.1724;
        
        // These points are about 10 miles apart, so use 15 mile radius
        boolean result = GeoUtils.isWithinRadius(lat1, lon1, lat2, lon2, 15.0);
        
        assertTrue(result);
    }
    
    @Test
    void isWithinRadius_ZeroRadius_OnlyReturnsTrueForSamePoint() {
        double lat = 40.7128;
        double lon = -74.0060;
        
        // Same point with zero radius should be true
        assertTrue(GeoUtils.isWithinRadius(lat, lon, lat, lon, 0.0));
        
        // Different point with zero radius should be false
        assertFalse(GeoUtils.isWithinRadius(lat, lon, lat + 0.001, lon, 0.0));
    }
    
    @Test
    void isWithinRadius_NegativeRadius_ShouldReturnFalse() {
        // Even for the same point, negative radius should return false
        double lat = 40.7128;
        double lon = -74.0060;
        
        boolean result = GeoUtils.isWithinRadius(lat, lon, lat, lon, -1.0);
        
        assertFalse(result);
    }
} 