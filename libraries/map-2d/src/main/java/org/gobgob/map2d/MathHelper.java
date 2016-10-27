package org.gobgob.map2d;


public class MathHelper {
	
	public static double modulo(double mod, double angle){
		angle= ((angle>mod/2.0)?angle-mod:angle);
		angle=((angle<-mod/2.0)?angle+mod:angle);
		return angle;
	}
	
	// angle in radians
	public static double modulo2PI(float angle){
		return modulo(2*Math.PI, angle);
	}

	
	public static double square(double x){
		return x*x;
	}
	
	public static double bind(double n, double min, double max){
		return (((n)<(min))?(min):(((n)>(max))?(max):(n)));
	}
	
	public static double deg2rad(double d) {
		  return d * (Math.PI/180);
	}
	
	public static float rad2deg(double r) {
		  return (float) r * 180.0f / (float) Math.PI;
	}
	
	/** Return the distance between two Points defined in WGS84
	 * @param toLatitude, toLongitude: latitude and longitude of the first point
	 * @param fromLatitude, fromLongitude: latitude and longitude of the second point
	 */
	public static double getDistanceToPointWGS84(double toLatitude, double toLongitude, double fromLatitude, double fromLongitude) {
		
		return Math.acos(Math.sin(toLatitude * Math.PI/180) * Math.sin(fromLatitude * Math.PI/180) + 
				Math.cos(toLatitude * Math.PI/180) * Math.cos(fromLatitude * Math.PI/180) *
				Math.cos(toLongitude * Math.PI/180-fromLongitude * Math.PI/180)) * 6371000;
	}
	
}
