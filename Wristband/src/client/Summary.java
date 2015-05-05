package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Henry on 4/15/2015.
 */
public class Summary extends JPanel {
    private JFrame frame;
    private Baselining baseline;

    private JButton saveButton;

    private int startingWidth = 600;
    private int startingHeight = 400;

    private static boolean windowOpen = false;

    public JFrame getFrame(){
        return frame;
    }

    public static boolean isWindowOpen() { return windowOpen; }

    public Summary(JFrame frame, Baselining baseline){
        this.frame = frame;
        this.baseline = baseline;
        windowOpen = true;

        saveButton = new JButton("Save Data");
        saveButton.setVisible(true);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String name = (String)JOptionPane.showInputDialog(
                        getFrame(),
                        "Input the Baby's name\n"
                                + "To be saved in the export file.",
                        "Baby's Name Input",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "Name");
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                windowOpen = false;
            }
        });

        //this.add(saveButton);

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
        g.drawString("Low",(int)(.1*getWidth()),(int)(.3*getHeight()));
        g.drawString("Medium", (int)(.25*getWidth()),(int) (.3 * getHeight()));
        g.drawString("High", (int)(.45*getWidth()),(int) (.3 * getHeight()));
        g.drawString("Baseline",(int)(.6*getWidth()),(int)(.3*getHeight()));

        saveButton.setBounds((int)(this.getWidth()*.1),(int)(this.getHeight()*.05),(int)(this.getWidth()*.25),(int)(this.getHeight()*.1));

        g.setFont(new Font("Courier New", Font.PLAIN, 20));

        g.drawString(baseline.getSum()+"", (int)(.62*this.getWidth()), (int)(.35*this.getHeight())+30);

        revalidate();
    }
}
