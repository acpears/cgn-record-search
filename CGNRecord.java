// Course number:       COMP5511
// Instructor:          Prof. Bipin C. DESAI
// Assignment number:   04
// Question: 6
// Submitted by:        Group 11
// -
// Group members:
// -
// ID           Name    Last Name       Email                           Group Leader
// 40003312	    Sima	NOPARAST        s_nopara@encs.concordia.ca      [ ]
// 40046477	    Matthew	MORGAN          m_rgan@encs.concordia.ca        [ ]
// 40181490	    Boris	NIJIKOVSKY      b_nijiko@encs.concordia.ca      [ ]
// 40181988	    Adam	PEARSON         a_ears@encs.concordia.ca        [*]

/* 
CLASS DESCRIPTION: CGN Record
    - Data structure for a CGN Record
    - All indexes in program reference CGN Records created during the csv parsing process
*/

public class CGNRecord {

    private final String id; // 5 letter ID of the record
    private final String name; // Name of the record
    private final String latitude; // Latitude of the record
    private final String longitude; // Longitude of the record
    private final int lat; // Integer format for latitude
    private final int lon; // Integer format for longitude


    public CGNRecord(String id, String name, String lat, String lon) {
        this.id = id.toLowerCase(); // Store the id as lower case
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.lat = latlonToInt(lat); 
        this.lon = latlonToInt(lon);
        
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getLat() {
        return this.lat;
    }

    public int getLon() {
        return this.lon;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public static int latlonToInt(String s){
        return (int) (Double.parseDouble(s) * 10000000);
    }

    public static String latlonToString(int i){
        return String.valueOf( (double) i / 10000000) ;
    }

    public static int hashId(String id) {
        return 31 * 17 + id.toLowerCase().hashCode();
    }

    @Override
    public int hashCode() {
        // That's the recommended way of producing a hashcode for Objects (rather than primitives)
        // according to the "Effective Java" book by Joshua Bloch
        return 31 * 17 + this.id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CGNRecord that = (CGNRecord) o;
        return id.equals(that.id);
    }
}