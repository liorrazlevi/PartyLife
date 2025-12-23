package lior.razlevi.partylife;

public class UserProperties {
    private  String userPhone;
    private String fullName;
    private String uid;

    public UserProperties(String userPhone, String uid, String fullName) {
        this.userPhone = userPhone;
        this.uid = uid;
        this.fullName = fullName;
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

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    @Override
    public String toString() {
        return "UserProperties{" +
                "userPhone=" + userPhone +
                ", uid='" + uid + '\'' +
                '}';
    }

}
