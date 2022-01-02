package tm;

/**
 * @author : D.D.Sandaruwan <dulanjayasandaruwan@gmail.com>
 * @Since : 30/05/2021
 **/
public class ToDo {

    private String id;
    private String description;
    private String user_id;

    public ToDo() {
    }

    public ToDo(String id, String description, String user_id) {
        this.id = id;
        this.description = description;
        this.user_id = user_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getUser_id() {
        return user_id;
    }

    @Override
    public String toString() {
        return description;
    }
}
