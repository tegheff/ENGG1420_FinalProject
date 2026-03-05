
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserManager userManager = new UserManager();
        UserManagementUI ui = new UserManagementUI(sc, userManager);

        // Simple runner just for 2.1 demo
        ui.run();
    }
}