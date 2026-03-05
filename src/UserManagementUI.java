
import java.util.List;
import java.util.Scanner;

public class UserManagementUI {
    private final Scanner sc;
    private final UserManager userManager;

    public UserManagementUI(Scanner sc, UserManager userManager) {
        this.sc = sc;
        this.userManager = userManager;
    }

    public void run() {
        while (true) {
            System.out.println("\n=== 2.1 User Management ===");
            System.out.println("1) Add User");
            System.out.println("2) View User Details");
            System.out.println("3) List All Users");
            System.out.println("0) Back");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> addUser();
                case "2" -> viewUserDetails();
                case "3" -> listAllUsers();
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addUser() {
        System.out.print("Enter userId: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("userId cannot be empty.");
            return;
        }
        if (userManager.getUserById(id) != null) {
            System.out.println("That userId already exists.");
            return;
        }

        System.out.print("Enter name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter email: ");
        String email = sc.nextLine().trim();

        System.out.println("Select user type:");
        System.out.println("1) Student");
        System.out.println("2) Staff");
        System.out.println("3) Guest");
        System.out.print("Type: ");
        String type = sc.nextLine().trim();

        User user;
        switch (type) {
            case "1" -> user = new Student(id, name, email);
            case "2" -> user = new Staff(id, name, email);
            case "3" -> user = new Guest(id, name, email);
            default -> {
                System.out.println("Invalid type.");
                return;
            }
        }

        boolean ok = userManager.addUser(user);
        System.out.println(ok ? "User created." : "Failed to create user.");
    }

    private void viewUserDetails() {
        System.out.print("Enter userId to view: ");
        String id = sc.nextLine().trim();

        User user = userManager.getUserById(id);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\n--- User Details ---");
        System.out.println("User ID: " + user.getUserId());
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Type: " + user.getType());
        System.out.println("Bookings summary: (available in Booking module later)");
    }

    private void listAllUsers() {
        List<User> users = userManager.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }

        System.out.println("\n--- All Users ---");
        System.out.println("userId | name | email | type");
        System.out.println("------------------------------------------");
        for (User u : users) {
            System.out.println(u);
        }
    }
}