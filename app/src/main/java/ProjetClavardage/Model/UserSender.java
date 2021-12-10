package ProjetClavardage.Model;

public class UserSender extends Thread {

    private UserManager userManager;

    public static int SLEEP_TIME = 5000;

    public UserSender(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(SLEEP_TIME);
                this.userManager.sender(true);
                //sleep(SLEEP_TIME);
                this.userManager.users_update();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
