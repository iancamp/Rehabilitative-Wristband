package client;

import java.awt.LayoutManager;
import java.awt.*;
import javax.swing.*;

/**
 * Created by kevok on 3/10/15.
 */
public class Main {
    public static void main(String[] args) {
        Baselining baseline = new Baselining();
        
        JButton learningButton = new JButton("Start Baselining Phase");
        learningButton.setBounds(50,10,300,50);
        JButton trainingButton = new JButton("Start Learning Phase");
        trainingButton.setBounds(400,10,300,50);
        JButton summaryButton = new JButton("Summary");
        summaryButton.setBounds(550,170,200,50);
        
        /* Create Slider for adjusting Threshold */
        JSlider thresholdControl = new JSlider(JSlider.HORIZONTAL,0,100,50);
        thresholdControl.setBounds(10, 70, 700, 100);
        thresholdControl.setMajorTickSpacing(10);
        thresholdControl.setMinorTickSpacing(1);
        thresholdControl.setPaintTicks(true);
        thresholdControl.setPaintLabels(true);
        
        
        Panel panel = new Panel(new JFrame(), baseline);
        
        panel.setLayout(null);
        panel.add(learningButton);
        panel.add(trainingButton);
        panel.add(thresholdControl);
        panel.add(summaryButton);
        
        try {while (true) {
            baseline.updateData();
            panel.repaint();
            synchronized (baseline) {baseline.wait(100);}
        }} catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }
}
