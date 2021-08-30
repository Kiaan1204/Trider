//UserLocation object of a user is updated consatantly based on the data obtained from the phone on which the user's account has been logged in.
//Kiaan Upamanyu

package ObjectClasses;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {
    public GeoPoint geo_Point;
    public @ServerTimestamp Date time_stamp;
    public User user;

    public UserLocation(GeoPoint geo_Point, Date time_stamp, User user) {
        this.geo_Point = geo_Point;
        this.time_stamp = time_stamp;
        this.user = user;
    }
    public UserLocation() {
        this.geo_Point = geo_Point;
        this.time_stamp = time_stamp;
        this.user = user;
    }

    public GeoPoint getGeo_Point() {
        return geo_Point;
    }

    public void setGeo_Point(GeoPoint geo_Point) {
        this.geo_Point = geo_Point;
    }

    public Date getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
