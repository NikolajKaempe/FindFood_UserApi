package firebaseFiles;

/**
 * Created by Kaempe on 01-05-2017.
 */
public class User
{
    private String role;
    private String publisherName;

    public User(){}

    public User(String role,String publisherName){
        this.role = role;
        this.publisherName = publisherName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }
}
