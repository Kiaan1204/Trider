//PolyLine data object calls on the google directions api to allow the user to set a path from start location to end location.
//Kiaan Upamanyu

package ObjectClasses;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

public class PolyLineData {

    private Polyline polyline;
    private DirectionsLeg leg;

public PolyLineData(Polyline polyline, DirectionsLeg leg) {
    this.polyline = polyline;
    this.leg = leg;
}

public Polyline getPolyline() {
    return polyline;
}

public void setPolyline(Polyline polyline) {
    this.polyline = polyline;
}

public DirectionsLeg getLeg() {
    return leg;
}

public void setLeg(DirectionsLeg leg) {
    this.leg = leg;
}

@Override
public String toString() {
    return "PolylineData{" +
            "polyline=" + polyline +
            ", leg=" + leg +
            '}';
}
}
