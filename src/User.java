
public abstract class User {
    private final String userId;
    private final String name;
    private final String email;

    public User(String userId, String name, String email) {
        this.userId = userId.trim();
        this.name = name.trim();
        this.email = email.trim();
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public abstract String getType();

    @Override
    public String toString() {
        return userId + " | " + name + " | " + email + " | " + getType();
    }
}