package familla.mipl.familla.activity;

public class Data_Model {

    // Getter and Setter model for recycler view items
    private String title;
    private int image;
    private String count;

    private String id;

    public Data_Model(String title, int image, String count, String id) {

        this.title = title;
        this.count = count;
        this.id = id;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    public int getImage() {
        return image;
    }
}