//User object allows a user to create a new account
//Kiaan Upamanyu

package ObjectClasses;

public class User {
    String UserEmail;
    String UserName;
    String UserPhone;
    String UserID;
    String selectedBike;



//
    public User(String userEmail, String userName, String userPhone, String userID, String selectedBike) {
        this.UserEmail = userEmail;
        this.UserName = userName;
        this.UserPhone = userPhone;
        this.UserID = userID;
        this.selectedBike = selectedBike;
    }
    public User() {
        this.UserEmail = UserEmail;
        this.UserName = UserName;
        this.UserPhone = UserPhone;
        this.UserID = UserID;
        this.selectedBike = selectedBike;
    }
    public String getSelectedBike() {
        return selectedBike;
    }

    public void setSelectedBike(String selectedBike) {
        this.selectedBike = selectedBike;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

}
