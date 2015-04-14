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

        Panel panel = new Panel(new JFrame(), baseline);
        
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
