package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Henry on 4/15/2015.
 */
public class Summary extends JPanel {
    private JFrame frame;
    private Baselining baseline;

    private JButton saveButton;
    private JButton deleteDataButton;
    private int buttonFontSize = 22;

    private int startingWidth = 600;
    private int startingHeight = 400;

    private static boolean windowOpen = false;

    public JFrame getFrame(){
        return frame;
    }
    public Baselining getBaseline() { return baseline; }

    public static boolean isWindowOpen() { return windowOpen; }

    public Summary(JFrame frame, Baselining baseline){
        this.frame = frame;
        this.baseline = baseline;

        windowOpen = true;

        saveButton = new JButton("Save Data");
        saveButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
        saveButton.setVisible(true);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String id = (String) JOptionPane.showInputDialog(
                        getFrame(),
                        "Input the Baby's ID\n"
                                + "To be saved in the export file.",
                        "Baby's ID Input",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "ID");
                FileManager.saveToCSV(getBaseline(), id);
            }
        });

        deleteDataButton = new JButton("Delete All Data");
        deleteDataButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
        deleteDataButton.setVisible(true);
        deleteDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //TODO: delete errything
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                windowOpen = false;
            }
        });

        /* Create method to resize fonts on window resize */
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                buttonFontSize = (int) (getWidth() * .022) + (int) (getHeight() * .01);
                if (buttonFontSize < 12) {
                    buttonFontSize = 12;
                }
            }
        });

        this.setLayout(null);
        this.add(saveButton);
        this.add(deleteDataButton);

        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(startingWidth, startingHeight);
        frame.setResizable(true);
        frame.setAlwaysOnTop(false);
        frame.setVisible(true);
        frame.setTitle("Summary");
        frame.setBackground(Color.white);

        frame.setMinimumSize(new Dimension(400, 300));
    }

    /**
     * Factory to create a Summary, only if one does not already exist.
     * @param frame
     * @param baseline
     * @return
     */
    public static Summary createSummary(JFrame frame, Baselining baseline){
        if(!windowOpen){
            return new Summary(frame,baseline);
        }
        else{
            return null;
        }
    }

    @Override
    public void paint (Graphics g) {
    	super.paint(g);
        g.setFont(new Font("Courier New", Font.BOLD, 22));
        g.drawString("Low", (int) (.1 * getWidth()), (int) (.3 * getHeight()));
        g.drawString("Medium", (int) (.25 * getWidth()),(int) (.3 * getHeight()));
        g.drawString("High", (int)(.45*getWidth()),(int) (.3 * getHeight()));
        g.drawString("Baseline",(int)(.6*getWidth()),(int)(.3*getHeight()));

        saveButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
        saveButton.setBounds((int) (this.getWidth() * .12), (int) (this.getHeight() * .05), (int) (this.getWidth() * .30), (int) (this.getHeight() * .15));

        deleteDataButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
        deleteDataButton.setBounds((int) (this.getWidth() * .52), (int) (this.getHeight() * .05), (int) (this.getWidth() * .30), (int) (this.getHeight() * .15));

        g.setFont(new Font("Courier New", Font.PLAIN, 20));

        g.drawString(baseline.getBaseline()+"", (int)(.62*this.getWidth()), (int)(.35*this.getHeight())+30);

        revalidate();
    }
}
