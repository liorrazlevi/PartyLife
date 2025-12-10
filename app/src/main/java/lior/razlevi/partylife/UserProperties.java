package lior.razlevi.partylife;

public class UserProperties {
    private  Integer userPhone;
    private String uid;

    public UserProperties(Integer userPhone, String uid) {
        this.userPhone = userPhone;
        this.uid = uid;
    }
    public UserProperties(){

    }
    public Integer getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Integer userPhone) {
        this.userPhone = userPhone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "UserProperties{" +
                "userPhone=" + userPhone +
                ", uid='" + uid + '\'' +
                '}';
    }

}
