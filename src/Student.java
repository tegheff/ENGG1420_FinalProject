public class Student extends User {
    public Student(String userId, String name, String email) {
        super(userId, name, email);
    }

    @Override
    public String getType() {
        return "Student";
    }
}