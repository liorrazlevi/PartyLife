package lior.razlevi.partylife;

/**
 * מחלקת מודל המייצגת אורח במסיבה.
 *   משמשת לאחסון ושליפת נתוני אישורי הגעה מה-Firebase.
 */
public class Guest {
    private String name;
    private String status;
    private String picture; // מחרוזת Base64 המייצגת את תמונת הפרופיל של האורח

    /**
     * בנאי ריק הנדרש על ידי Firebase כדי להמיר את הנתונים מהדאטה-בייס לאובייקט Java.
     */
    public Guest() {}

    /**
     * בנאי ליצירת אובייקט אורח חדש עם פרטים התחלתיים.
     */
    public Guest(String name, String status, String picture) {
        this.name = name;
        this.status = status;
        this.picture = picture;

    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public String toString() {
        return "Name: " + name + ", Status: " + status + ", Picture: " + picture.substring(0, Math.min(picture.length(), 10));
    }
}
