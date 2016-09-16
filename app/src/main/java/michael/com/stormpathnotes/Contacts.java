package michael.com.stormpathnotes;

/**
 * Created by Mikhail on 9/13/16.
 */
public class Contacts {

    private String firstName;
    private String lastName;
    private String email;

    public Contacts(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("%s", firstName + " " + lastName + " " + email);
    }
}
