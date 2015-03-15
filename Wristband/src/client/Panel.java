package client;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.util.LinkedList;

/**
 * Created by kevok on 3/10/15.
 */
public class Panel extends JPanel{
    JFrame frame;
    Baselining baseline;
    Color green;
    
    public Panel(JFrame frame, Baselining baseline) {
        this.frame = frame;
        this.baseline = baseline;
        
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(true);
        frame.setAlwaysOnTop(false);
        frame.setVisible(true);
        frame.setTitle("Test Window");
        frame.setBackground(Color.white);
        green = new Color(5,128,0);
    }
    /**
     * paint(Graphics g) is called repeatedly whenever possible to 
     * repaint the window until the user exits the window. Repaint can be 
     * called to force an update immediately.
    */
    @Override
    public void paint (Graphics g) {
        super.paint(g);
        LinkedList<DataPoint> values = baseline.getSessionData();
        g.setFont(new Font("Courier New", Font.PLAIN, 20));
        g.drawString("Data:", 42, 40);
        g.drawString("Sum:", 220, 40);
        //float sum = baseline.getSum();
        g.drawString(baseline.getSum()+"", 220, 60); //need to append "" to make the float a String
        g.drawString("Baseline:", 360, 40);
        //float avg = baseline.getBaseline();
        g.drawString(baseline.getBaseline()+"", 360, 60); //need to append "" to make the float a String
        LinkedList<DataPoint> top20 = new LinkedList<DataPoint>();
        for (int i = values.size()-1; i > Math.max(0, values.size() - 20); i--) {
            top20.add(values.get(i));
        }
        int y = 0;
        for(DataPoint d : top20){
            //this.setForeground(Color.BLUE);
            //g.setFont(g.getFont().setForeground(Color.BLUE));
            g.drawString(String.format(
                    "%7.2f: %5.2f",
                    d.getTime(),
                    Math.abs(d.getMagnitude())
                ),6,60+y);
            y+=20;
        }
    }

}
