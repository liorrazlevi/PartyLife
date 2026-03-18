package lior.razlevi.partylife;

public class Party {
    private String partyId;
    private String name;
    private String location;
    private String date;
    private String time;
    private String age;
    private String dressCode;
    private String phone;
    private String imageUrl;
    private String creatorId;
    private String parking;

    // חובה: קונסטרקטור ריק בשביל Firebase
    public Party() {}

    // Getters
    public String getPartyId() { return partyId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getAge() { return age; }
    public String getDressCode() { return dressCode; }
    public String getPhone() { return phone; }
    public String getImageUrl() { return imageUrl; }
    public String getCreatorId() { return creatorId; }
    public String getParking() { return parking; }

    // Setters - חובה כדי ש-Firebase יוכל למלא את האובייקט בנתונים
    public void setPartyId(String partyId) { this.partyId = partyId; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setAge(String age) { this.age = age; }
    public void setDressCode(String dressCode) { this.dressCode = dressCode; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setParking(String parking) { this.parking = parking; }
}
