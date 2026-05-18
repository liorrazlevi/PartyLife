package lior.razlevi.partylife;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Party {
    private String partyId;
    private String name;
    private String location;
    private String date;
    private String time;
    private String age;
    private String dressCode;
    private String phone;
    private String imageString; 
    private String creatorId;
    private String parking;
    private String fullAddress;

    public Party() {}

    public Party(String partyId, String name, String location, String date, String time, String age, String dressCode, String phone,
                 String imageString, String creatorId, String parking, String fullAddress) {
        this.partyId = partyId;
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
        this.age = age;
        this.dressCode = dressCode;
        this.phone = phone;
        this.imageString = imageString;
        this.creatorId = creatorId;
        this.parking = parking;
        this.fullAddress = fullAddress;
    }

    public String getPartyId() { return partyId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getAge() { return age; }
    public String getDressCode() { return dressCode; }
    public String getPhone() { return phone; }
    public String getFullAddress() { return fullAddress; }
    
    // פעולה זו מחזירה את ה-Bitmap עבור Glide
    public Bitmap bringPartyImage() {
        return convertStringToBitmap(imageString); 
    }
    
    public String getImageString() { return imageString; }
    public String getCreatorId() { return creatorId; }
    public String getParking() { return parking; }

    public void setPartyId(String partyId) { this.partyId = partyId; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setAge(String age) { this.age = age; }
    public void setDressCode(String dressCode) { this.dressCode = dressCode; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setImageString(String imageString) { this.imageString = imageString; } 
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setParking(String parking) { this.parking = parking; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    
    // הפעולה המתוקנת להמרת הסטרינג ל-Bitmap
    public Bitmap convertStringToBitmap(String imageString) {
        if (imageString != null && !imageString.isEmpty()) {
            try {
                // המרה מ-Base64 למערך בתים
                byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                // יצירת Bitmap מהבתים
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    public String toString() {


       return this.partyId + " " + this.name + " " + this.location + " " + this.date + " " + this.time + " " + this.age + " " + this.dressCode + " " + this.phone + " "  + " " +
               this.creatorId + " " + this.parking + " " + this.fullAddress;





    }
}
