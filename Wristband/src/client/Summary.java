package client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Henry on 4/15/2015.
 */
public class Summary extends JPanel {
    JFrame frame;
    Baselining baseline;

    int startingWidth = 600;
    int startingHeight = 400;

    public Summary(JFrame frame, Baselining baseline){
        this.frame = frame;
        this.baseline = baseline;

        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(startingWidth, startingHeight);
        frame.setResizable(true);
        frame.setAlwaysOnTop(false);
        frame.setVisible(true);
        frame.setTitle("Summary");
        frame.setBackground(Color.white);
    }

    @Override
    public void paint (Graphics g) {
        g.setFont(new Font("Courier New", Font.BOLD, 22));
        g.drawString("Low",(int)(.1*getWidth()),(int)(.1*getHeight()));
        g.drawString("Medium", (int)(.25*getWidth()),(int) (.1 * getHeight()));
        g.drawString("High",(int)(.45*getWidth()),(int)(.1*getHeight()));
        g.drawString("Baseline",(int)(.6*getWidth()),(int)(.1*getHeight()));

        g.setFont(new Font("Courier New", Font.PLAIN, 20));

        g.drawString(baseline.getSum()+"", (int)(.62*this.getWidth()), (int)(.15*this.getHeight())+30);
    }
}
