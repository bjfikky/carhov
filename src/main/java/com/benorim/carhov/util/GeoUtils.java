package com.benorim.carhov.util;

/**
 * Utility class for geographical calculations.
 */
public class GeoUtils {
    
    private static final double EARTH_RADIUS_MILES = 3958.8; // Earth radius in miles
    
    /**
     * Calculate the distance between two points using the Haversine formula.
     * This formula provides the great-circle distance between two points on a sphere.
     *
     * @param lat1 Latitude of point 1 in degrees
     * @param lon1 Longitude of point 1 in degrees
     * @param lat2 Latitude of point 2 in degrees
     * @param lon2 Longitude of point 2 in degrees
     * @return Distance in miles between the two points
     */
    public static double calculateDistanceInMiles(double lat1, double lon1, double lat2, double lon2) {
        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        // Haversine formula
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_MILES * c;
    }
    
    /**
     * Check if a location is within a specified radius of another location.
     *
     * @param lat1 Latitude of point 1 in degrees
     * @param lon1 Longitude of point 1 in degrees
     * @param lat2 Latitude of point 2 in degrees
     * @param lon2 Longitude of point 2 in degrees
     * @param radiusInMiles The radius in miles
     * @return true if the distance between points is less than or equal to the radius
     */
    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radiusInMiles) {
        double distance = calculateDistanceInMiles(lat1, lon1, lat2, lon2);
        return distance <= radiusInMiles;
    }
}