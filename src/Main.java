import GUI.MainFrame;
import db.MongoConnection;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        MongoConnection.init();
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
