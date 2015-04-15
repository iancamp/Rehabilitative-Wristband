package client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Henry on 4/15/2015.
 */
public class Summary extends JPanel {
    JFrame frame;


    int startingWidth = 600;
    int startingHeight = 400;

    public Summary(JFrame frame){
        this.frame = frame;


        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(startingWidth, startingHeight);
        frame.setResizable(true);
        frame.setAlwaysOnTop(false);
        frame.setVisible(true);
        frame.setTitle("Summary");
        frame.setBackground(Color.white);
    }

    @Override
    public void paint (Graphics g) {
        g.setFont(new Font("Courier New", Font.PLAIN, 20));
        g.drawString("Some Stuff",30,50);
    }
}
