package com.vaidebike;

import com.google.android.gms.maps.model.LatLng;

public interface LatLngInterpolator {
    LatLng interpolate(float fraction, LatLng a, LatLng b);
    class Spherical implements LatLngInterpolator {
        /* From github.com/googlemaps/android-maps-utils */
        @Override
        public LatLng interpolate(float fraction, LatLng from, LatLng to) {
            // http://en.wikipedia.org/wiki/Slerp
            double fromLat = Math.toRadians(from.latitude);
            double fromLng = Math.toRadians(from.longitude);
            double toLat = Math.toRadians(to.latitude);
            double toLng = Math.toRadians(to.longitude);
            double cosFromLat = Math.cos(fromLat);
            double cosToLat = Math.cos(toLat);
            // Computes Spherical interpolation coefficients.
            double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
            double sinAngle = Math.sin(angle);
            if (sinAngle < 1E-6) {
                return from;
            }
            double a = Math.sin((1 - fraction) * angle) / sinAngle;
            double b = Math.sin(fraction * angle) / sinAngle;
            // Converts from polar to vector and interpolate.
            double x = a * cosFromLat * Math.cos(fromLng) + b * cosToLat * Math.cos(toLng);
            double y = a * cosFromLat * Math.sin(fromLng) + b * cosToLat * Math.sin(toLng);
            double z = a * Math.sin(fromLat) + b * Math.sin(toLat);
            // Converts interpolated vector back to polar.
            double lat = Math.atan2(z, Math.sqrt(x * x + y * y));
            double lng = Math.atan2(y, x);
            return new LatLng(Math.toDegrees(lat), Math.toDegrees(lng));
        }
        private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
            // Haversine's formula
            double dLat = fromLat - toLat;
            double dLng = fromLng - toLng;
            return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(dLat / 2), 2) +
                    Math.cos(fromLat) * Math.cos(toLat) * Math.pow(Math.sin(dLng / 2), 2)));
        }
    }
}