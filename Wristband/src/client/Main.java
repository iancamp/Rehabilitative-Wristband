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
        
        /* Create Buttons for starting/stopping learning/training sessions */
        JButton learningButton = new JButton("Start Learning Phase");
        learningButton.setBounds(50,10,300,50);
        JButton trainingButton = new JButton("Start Training Phase");
        trainingButton.setBounds(400,10,300,50);
        
        /* Summary Link Button */
        JButton summaryButton = new JButton("Summary");
        summaryButton.setBounds(550,170,200,50);
        
        /* TO BE DELETED AFTER WE SELECT A SINGLE GUI TO USE */
        JButton learningButton2 = new JButton("Start Timed Learning Phase");
        learningButton2.setBounds(50,10,300,50);
        JButton trainingButton2 = new JButton("Start Timed Training Phase");
        trainingButton2.setBounds(400,10,300,50);
        JButton summaryButton2 = new JButton("Summary");
        summaryButton2.setBounds(550,170,200,50);
        
        /* Create Integer Text Box for adjusting set Session Time */
        SpinnerModel model = new SpinnerNumberModel(2, .5, 5, .5);     
        JSpinner sessionTimeSpinner = new JSpinner(model);
        sessionTimeSpinner.setBounds(710, 10, 60, 50);
        
        /* Create Slider for adjusting Threshold */
        JSlider thresholdControl = new JSlider(JSlider.HORIZONTAL,0,100,50);
        thresholdControl.setBounds(10, 70, 700, 100);
        thresholdControl.setMajorTickSpacing(10);
        thresholdControl.setMinorTickSpacing(1);
        thresholdControl.setPaintTicks(true);
        thresholdControl.setPaintLabels(true);
        
        /* Create Integer Text Box for adjusting Threshold */
        model = new SpinnerNumberModel(50, 1, 100, 1);     
        JSpinner thresholdSpinner = new JSpinner(model);
        thresholdSpinner.setBounds(10, 70, 700, 50);
        
        
        Panel panel = new Panel(new JFrame(), baseline);
        Panel panel2 = new Panel(new JFrame(), baseline);
        
        panel.setLayout(null);
        panel.add(learningButton);
        panel.add(trainingButton);
        panel.add(thresholdControl);
        panel.add(summaryButton);
        
        
        panel2.setLayout(null);
        panel2.add(learningButton2);
        panel2.add(trainingButton2);
        panel2.add(thresholdSpinner);
        panel2.add(sessionTimeSpinner);
        panel2.add(summaryButton2);
        
        try {while (true) {
            baseline.updateData();
            panel.repaint();
            panel2.repaint();
            synchronized (baseline) {baseline.wait(100);}
        }} catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }
}
