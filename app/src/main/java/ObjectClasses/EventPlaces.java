//The EventPlaces object allows users to create a new event with the listed attributes and then upload it to the FirebaseFirestore database.
//Kiaan Upamanyu

package ObjectClasses;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class EventPlaces {
    private String eventName;
    private String placeName;
    private String placeDate;
    private GeoPoint placeGeoPoint;
    private String startTime;
    private String startLocation;
    private GeoPoint startLocationGeoPoint;
    private Boolean hasEventStarted;


    public EventPlaces() {
        this.eventName = eventName;
        this.placeName = placeName;
        this.placeDate = placeDate;
        this.placeGeoPoint = placeGeoPoint;
        this.startTime = startTime;
        this.startLocation = startLocation;
        this.hasEventStarted = hasEventStarted;
    }

    public EventPlaces(String eventName, String placeName, String placeDate, GeoPoint placeGeoPoint, String startTime, String startLocation, GeoPoint startLocationGeoPoint, Boolean hasEventStarted) {
        this.eventName = eventName;
        this.placeName = placeName;
        this.placeDate = placeDate;
        this.placeGeoPoint = placeGeoPoint;
        this.startTime = startTime;
        this.startLocation = startLocation;
        this.startLocationGeoPoint = startLocationGeoPoint;
        this.hasEventStarted = hasEventStarted;
    }

    public Boolean getHasEventStarted() {
        return hasEventStarted;
    }

    public void setHasEventStarted(Boolean hasEventStarted) {
        this.hasEventStarted = hasEventStarted;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public GeoPoint getStartLocationGeoPoint() {
        return startLocationGeoPoint;
    }

    public void setStartLocationGeoPoint(GeoPoint startLocationGeoPoint) { this.startLocationGeoPoint = startLocationGeoPoint; }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceDate() {
        return placeDate;
    }

    public void setPlaceDate(String placeDate) {
        this.placeDate = placeDate;
    }

    public GeoPoint getPlaceGeoPoint() {
        return placeGeoPoint;
    }

    public void setPlaceGeoPoint(GeoPoint placeGeoPoint) {
        this.placeGeoPoint = placeGeoPoint;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

}