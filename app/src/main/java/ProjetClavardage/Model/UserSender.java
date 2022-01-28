package ProjetClavardage.Model;

/**
 * UserSender est un Thread qui gère la mise a jour toutes les 3 secondes des utilisateurs connectés via un appel aux fonctions sender et user_update de UserManager
 */
public class UserSender extends Thread {

    private UserManager userManager;

    public static int SLEEP_TIME = 1500;

    public UserSender(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(SLEEP_TIME);
                this.userManager.sender(true);
                sleep(SLEEP_TIME);
                this.userManager.users_update();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
