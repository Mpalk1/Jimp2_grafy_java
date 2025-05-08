import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args){
        System.out.println("Hello World");

        JFrame window = new JFrame("Jimp2 Grafy");
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(1600, 1000);
        window.setLocationRelativeTo(null);




        window.setVisible(true); // to zawsze na koncu
    }
}
