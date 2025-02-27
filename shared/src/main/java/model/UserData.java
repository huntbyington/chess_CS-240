package model;

public record UserData(String username, String password, String email) {

    // Constructor for username and password only
    public UserData(String username, String password) {
        this(username, password, ""); // Default email to empty string
    }

    // Constructor for username, password, and email
    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
