package client;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

/**
 * Created by kevok on 3/10/15.
 */
public class Panel extends JPanel{
    private JFrame frame;
    private Baselining baseline;
    private NetworkThread networkThread;

    private boolean inBaseline;
    private boolean inLearning;

    private JButton baseliningButton;
    private JButton learningButton;
    private JButton summaryButton;
    private JSlider thresholdControl;
    private JSpinner timeControl;

    private JButton arduinoFail;
    private JButton arduinoTimeOut;

    private JButton pauseButton;
    private JButton cancelButton;

    private int startingWidth = 800;
    private int startingHeight = 600;

    private void toggleAllVisible(){
        timeControl.setVisible(!timeControl.isVisible());
        baseliningButton.setVisible(!baseliningButton.isVisible());
        learningButton.setVisible(!learningButton.isVisible());
        thresholdControl.setVisible(!thresholdControl.isVisible());
        summaryButton.setVisible(!summaryButton.isVisible());
        pauseButton.setVisible(!pauseButton.isVisible());
        cancelButton.setVisible(!cancelButton.isVisible());
    }

    public Panel(JFrame frame, Baselining baseline) {
        this.frame = frame;
        this.baseline = baseline;
        networkThread = baseline.getWristbandInterface();
        inBaseline = false;
        inLearning = false;

        /* Create Spinner for control over the length of each phase */
        timeControl = new JSpinner(new SpinnerNumberModel(2,.5,10,.5));
        timeControl.setFont(new Font("Courier New", Font.PLAIN, 20));

        /* Create Baselining Phase Button & Implementation */
        baseliningButton = new JButton("Start Baselining Phase");
        baseliningButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                inBaseline = true;
                getBaseline().setAllThresholds(999); //set threshold very high to stop toy from activing during baseline
                toggleAllVisible();

                getBaseline().baselinePhase((Double)(timeControl.getValue()));

                //TODO: collect data as Baselining Phase
            }
        });

        /* Create Learning Phase Button & Implementation */
        learningButton = new JButton("Start Learning Phase");
        learningButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                inLearning = true;
                toggleAllVisible();

                //TODO: collect data as Learning Phase
            }
        });

        /* Create Pause Phase Button & Implementation */
        pauseButton = new JButton("Pause");
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //TODO:Do stuff to stop incoming data.
            }
        });

        /* Create Cancel Phase Button & Implementation */
        cancelButton = new JButton("Cancel");
        cancelButton.setVisible(false);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int answer = JOptionPane.showConfirmDialog(
                        getFrame(),
                        "Cancel current Phase. Are you Sure?",
                        "Cancel Phase",
                        JOptionPane.YES_NO_OPTION);

                if(answer == 0){    //JOptionPane returns 0 when user selects Yes
                    shutPhaseDown();

                    //TODO: delete all recorded data
                }
            }
        });

        /*Create Summary Button & Implementation for OnClick */
        summaryButton = new JButton("Summary");
        summaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(!Summary.isWindowOpen()) {
                    new Summary(new JFrame(), getBaseline()); //check before making a new Summary window
                }
            }
        });

        /* Create Slider for adjusting Threshold */
        thresholdControl = new JSlider(JSlider.HORIZONTAL,0,100,100); //set default very high to match arduino's default
        thresholdControl.setMajorTickSpacing(10);
        thresholdControl.setMinorTickSpacing(1);
        thresholdControl.setPaintTicks(true);
        thresholdControl.setPaintLabels(true);

        /* Add Listner for Slider to send data to Baseline on Slider Change */
        ChangeListener SlideListener = new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) { //only true when threshold is recently changed
                    getBaseline().setAllThresholds(thresholdControl.getValue());
                }
            }
        };

        thresholdControl.addChangeListener(SlideListener);

        /* Create button for Reload if Arduino Fails */
        arduinoFail = new JButton("Retry");
        arduinoFail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                networkThread.restartFailedToConnect();
                arduinoFail.setVisible(false);
            }
        });

        /*Create Button for Loss of Connection to Arduino */
        arduinoTimeOut = new JButton("Retry");
        arduinoTimeOut.setVisible(false);
        arduinoTimeOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                networkThread.restartTimeout();
                arduinoTimeOut.setVisible(false);
            }
        });

        /*Add all Buttons & Inputs to the Form */
        this.setLayout(null);
        this.add(timeControl);
        this.add(baseliningButton);
        this.add(learningButton);
        this.add(summaryButton);
        this.add(thresholdControl);
        this.add(arduinoFail);
        this.add(arduinoTimeOut);
        this.add(pauseButton);
        this.add(cancelButton);

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

        /*Function which is called upon window closing */
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                networkThread.close();
            }
        });
    }

    private final JFrame getFrame(){
        return frame;
    }

    private final Baselining getBaseline(){
        return baseline;
    }
    private void setBaseline(Baselining b){
        baseline = b;
    }

    /**
     * getTimeString takes a double, which is the number of minutes remaining and converts it to a
     * String of type: "Minutes:"Seconds".
     * @param time
     * @return
     */
    private String getTimeString(double time){
        String output;
        int minutes = (int)time; //cut of the seconds and only take the minutes
        double seconds = (Math.abs(time-minutes)*100); //cut off the minutes and only take the seconds
        seconds = seconds*3;
        seconds = seconds/5;
        int outputSeconds = (int)seconds;

        if(outputSeconds>=10) {
            output = minutes + ":" + outputSeconds;
        }
        else{
            output = minutes + ":0" + outputSeconds;
        }
        return output;
    }

    private void shutPhaseDown(){
        inBaseline = false;
        inLearning = false;
        toggleAllVisible();
    }

    /**
     * paint(Graphics g) is called repeatedly whenever possible to 
     * repaint the window until the user exits the window. Repaint can be 
     * called to force an update immediately.
     */
    @Override
    public void paint (Graphics g) {
        super.paint(g);

        /*check for Ardiuno on startup */
        int foundCom = networkThread.getFoundCom();
        if(foundCom == 0 && !networkThread.getTimeOut()){ //still searching
            g.setFont(new Font("Times New Roman", Font.PLAIN, 40));
            g.drawString("Loading...", (int) (.25 * getWidth()), (int) (.4 * getHeight()));
        }
        else if(foundCom < 0){ //failed
            g.setFont(new Font("Times New Roman", Font.BOLD, 50));
            g.drawString("Failed to find Arduino!", (int) (.25 * getWidth()), (int) (.4 * getHeight()));
            arduinoFail.setVisible(true);
            arduinoFail.setBounds((int) (.25 * getWidth()), (int) (.55 * getHeight()), (int) (.35 * getWidth()), (int) (.13 * getHeight()));
        }
        else if(networkThread.getTimeOut()){
            arduinoTimeOut.setBounds((int) (.25 * getWidth()), (int) (.55 * getHeight()), (int) (.35 * getWidth()), (int) (.13 * getHeight()));
            arduinoTimeOut.setVisible(true);
        }
        else if(inLearning || inBaseline){ //in a phase, collecting data
            pauseButton.setBounds((int) (.05 * getWidth()), (int) (.1 * getHeight()), (int) (.42 * getWidth()), (int) (.1 * getHeight()));
            cancelButton.setBounds((int) (.5 * getWidth()), (int) (.1 * getHeight()), (int) (.42 * getWidth()), (int) (.1 * getHeight()));

            g.setFont(new Font("Courier New", Font.PLAIN, 26));
            Double t = (Double)(timeControl.getValue());
            g.drawString(getTimeString(baseline.getTimerem()), (int) (.42 * this.getWidth()), (int) (.06 * this.getHeight()));

            g.setFont(new Font("Courier New", Font.PLAIN, 30));
            if(inLearning){
                g.drawString("Learning Phase: Threshold = " + thresholdControl.getValue(),(int) (.045 * getWidth()), (int) (.26 * getHeight()));
            }

            if(!baseline.getstartBaseline()){
                shutPhaseDown();
            }

            /*Display all incoming Data: */
            displayAllData(g);
        }
        else{
            /* Reset all buttons and inputs if window was resized */
            timeControl.setBounds((int) (.2 * getWidth()), (int) (.02 * getHeight()), (int) (.6 * getWidth()), (int) (.07 * getHeight()));
            baseliningButton.setBounds((int) (.05 * getWidth()), (int) (.1 * getHeight()), (int) (.42 * getWidth()), (int) (.1 * getHeight()));
            learningButton.setBounds((int) (.5 * getWidth()), (int) (.1 * getHeight()), (int) (.42 * getWidth()), (int) (.1 * getHeight()));
            summaryButton.setBounds((int) (.65 * getWidth()), (int) (.35 * getHeight()), (int) (.25 * getWidth()), (int) (.08 * getHeight()));
            summaryButton.setBounds((int) (.65 * getWidth()), (int) (.35 * getHeight()), (int) (.25 * getWidth()), (int) (.08 * getHeight()));
            thresholdControl.setBounds((int) (.04 * getWidth()), (int) (.24 * getHeight()), (int) (.9 * getWidth()), (int) (.08 * getHeight()));
            revalidate();
        }
    }

    /**
     * displayAllData is called from the paint function. It prints all incoming data to the screen.
     * @param g
     */
    private void displayAllData(Graphics g){
        LinkedList<DataPoint> values = baseline.getbaselineData();
        g.setFont(new Font("Courier New", Font.PLAIN, 20));
        g.drawString("Data:", (int) (.06 * this.getWidth()), (int) (.4 * this.getHeight()));
        g.drawString("Sum:", (int) (.25 * this.getWidth()), (int) (.4 * this.getHeight()));
        //float sum = baseline.getSum();
        g.drawString(baseline.getSum() + "", (int) (.4 * this.getWidth()), (int) (.4 * this.getHeight()) + 30); //need to append "" to make the float a String
        g.drawString("Baseline:", (int) (.4 * this.getWidth()), (int) (.4 * this.getHeight()));
        //float avg = baseline.getBaseline();
        g.drawString(baseline.getBaseline() + "", (int) (.25 * this.getWidth()), (int) (.4 * this.getHeight()) + 30); //need to append "" to make the float a String
        LinkedList<DataPoint> top20 = new LinkedList<DataPoint>();
        for (int i = values.size() - 1; i > Math.max(0, values.size() - 20); i--) {
            top20.add(values.get(i));
        }
        int y = 0;
        for (DataPoint d : top20) {
            //this.setForeground(Color.BLUE);
            //g.setFont(g.getFont().setForeground(Color.BLUE));
            g.drawString(String.format(
                    "%7.2f: %5.2f",
                    d.getTime(),
                    Math.abs(d.getMagnitude())
            ), (int) (.01 * this.getWidth()), (int) (.45 * this.getHeight()) + y);
            y += 20;
        }
    }

}
