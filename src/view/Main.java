package view;

//import demo1.view.PhapSuKhacCheView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhapSuKhacCheView().setVisible(true));
    }
}
