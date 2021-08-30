//A cluster marker object is used to allow the user's chosen bike icon to show on the map.
// The ClusterMarker gets updated whenever the user location changes.
//Kiaan Upamanyu


package ObjectClasses;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
public class ClusterMarker implements ClusterItem {
   private LatLng position;
   private String title;
   private String snippet;
   private int iconPicture;
   private User user;

   public ClusterMarker(LatLng position, String title, String snippet, int iconPicture, User user) {
      this.position = position;
      this.title = title;
      this.snippet = snippet;
      this.iconPicture = iconPicture;
      this.user = user;
   }
   public ClusterMarker() {
      this.position = position;
      this.title = title;
      this.snippet = snippet;
      this.iconPicture = iconPicture;
      this.user = user;
   }

   @NonNull
   @Override
   public LatLng getPosition() {
      return position;
   }

   public void setPosition(LatLng position) {
      this.position = position;
   }

   @Nullable
   @Override
   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Nullable
   @Override
   public String getSnippet() {
      return snippet;
   }

   public void setSnippet(String snippet) {
      this.snippet = snippet;
   }

   public int getIconPicture() {
      return iconPicture;
   }

   public void setIconPicture(int iconPicture) {
      this.iconPicture = iconPicture;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }
}
