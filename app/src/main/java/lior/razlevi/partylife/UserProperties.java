package lior.razlevi.partylife;

public class UserProperties {
    private  String userPhone;
    private String fullName;
    private String uid;
    private String profileImage;

    public UserProperties(String userPhone, String uid, String fullName, String profileImage) {
        this.userPhone = userPhone;
        this.uid = uid;
        this.fullName = fullName;
        this.profileImage = profileImage;
    }
    public UserProperties(){

    }
    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUid() {
        return uid;
    }
    public String getProfileImage() {
        return profileImage;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }



    @Override
    public String toString() {
        return "UserProperties{" +
                "userPhone=" + userPhone +
                ", uid='" + uid + '\'' +
                '}';
    }

}
