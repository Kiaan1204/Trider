//A groupObjectClass allows a user to create a new group and assign a group name and group code to allow user to join the group.
//Kiaan Upamanyu

package ObjectClasses;

public class GroupObjectClass {
    String groupCode;
    String groupName;

    public GroupObjectClass(String groupCode, String groupName) {
        this.groupCode = groupCode;
        this.groupName = groupName;
    }
    public GroupObjectClass() {
        this.groupCode = groupCode;
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
