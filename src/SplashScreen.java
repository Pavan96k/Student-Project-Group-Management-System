import javax.swing.*;
import java.awt.*;

public class SplashScreen {
    public static void main(String[] args) {
        // Create splash window
        JWindow splash = new JWindow();

        // Load image
        ImageIcon icon = new ImageIcon(SplashScreen.class.getResource("/images/splash4.jpg"));
        JLabel label = new JLabel(icon);
        splash.getContentPane().add(label, BorderLayout.CENTER);

        // Set window size same as image
        splash.setSize(icon.getIconWidth(), icon.getIconHeight());
        splash.setLocationRelativeTo(null);

        // Show splash
        splash.setVisible(true);

        try {
            Thread.sleep(2500); // 2.5 seconds delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        splash.dispose(); // close splash

        // Open your main Login window
        LoginApp.main(null);
    }
}
