package client;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

/**
 * Created by kevok on 3/10/15.
 */
public class Panel extends JPanel{
    JFrame frame;
    Baselining baseline;
    JButton baseliningButton;
    JButton learningButton;
    JButton summaryButton;
    JSlider thresholdControl;
    JSpinner timeControl;

    int startingWidth = 800;
    int startingHeight = 600;
    
    public Panel(JFrame frame, Baselining baseline) {
        this.frame = frame;
        this.baseline = baseline;

        timeControl = new JSpinner(new SpinnerNumberModel(2,.5,10,.5));
        timeControl.setFont(new Font("Courier New", Font.PLAIN, 20));

        baseliningButton = new JButton("Start Baselining Phase");

        learningButton = new JButton("Start Learning Phase");

        /*Create Summary Button & Implementation for OnClick */
        summaryButton = new JButton("Summary");
        final Baselining b2 = baseline;
        summaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new Summary(new JFrame(), b2);
            }
        });

        /* Create Slider for adjusting Threshold */
        thresholdControl = new JSlider(JSlider.HORIZONTAL,0,100,50);
        thresholdControl.setMajorTickSpacing(10);
        thresholdControl.setMinorTickSpacing(1);
        thresholdControl.setPaintTicks(true);
        thresholdControl.setPaintLabels(true);

        /*Add all Buttons & Inputs to the Form */
        this.setLayout(null);
        this.add(timeControl);
        this.add(baseliningButton);
        this.add(learningButton);
        this.add(summaryButton);
        this.add(thresholdControl);
        
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(startingWidth, startingHeight);
        frame.setResizable(true);
        frame.setAlwaysOnTop(false);
        frame.setVisible(true);
        frame.setTitle("WristBand");
        frame.setBackground(Color.white);
        //green = new Color(5,128,0);

        frame.setMinimumSize(new Dimension(400,300));
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
        g.drawString("Data:", (int)(.1*this.getWidth()), (int)(.4*this.getHeight()));
        g.drawString("Sum:", (int)(.25*this.getWidth()), (int)(.4*this.getHeight()));
        //float sum = baseline.getSum();
        g.drawString(baseline.getSum()+"", (int)(.4*this.getWidth()), (int)(.4*this.getHeight())+30); //need to append "" to make the float a String
        g.drawString("Baseline:", (int)(.4*this.getWidth()), (int)(.4*this.getHeight()));
        //float avg = baseline.getBaseline();
        g.drawString(baseline.getBaseline()+"", (int)(.25*this.getWidth()), (int)(.4*this.getHeight())+30); //need to append "" to make the float a String
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
                ),(int)(.1*this.getWidth()),(int)(.4*this.getHeight()) + y);
            y += 20;
        }

        /* Reset all buttons and inputs if window was resized */
        timeControl.setBounds((int) (.2 * getWidth()), (int) (.02 * getHeight()), (int) (.6 * getWidth()), (int) (.07 * getHeight()));
        baseliningButton.setBounds((int)(.05*getWidth()),(int)(.1*getHeight()),(int)(.42*getWidth()),(int)(.1*getHeight()));
        learningButton.setBounds((int)(.5*getWidth()),(int)(.1*getHeight()),(int)(.42*getWidth()),(int)(.1*getHeight()));
        summaryButton.setBounds((int)(.65*getWidth()),(int)(.35*getHeight()),(int)(.25*getWidth()),(int)(.08*getHeight()));
        summaryButton.setBounds((int)(.65*getWidth()),(int)(.35*getHeight()),(int)(.25*getWidth()),(int)(.08*getHeight()));
        thresholdControl.setBounds((int)(.04*getWidth()),(int)(.24*getHeight()),(int)(.9*getWidth()),(int)(.08*getHeight()));
        revalidate();
    }

}
