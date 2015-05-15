package client;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.*;
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
    private boolean inExtinction;
    private int suggestedThreshold;

    private static double maxTime = 20;
    private static double minTime = .5;
    private static int maxThreshold = 999;

    private JButton baseliningButton;
    private JButton learningButton;
    private JButton extinctionButton;

    private JButton summaryButton;
    private JSlider thresholdControl;
    private JSpinner timeControl;

    private JButton arduinoFail;
    private JButton arduinoTimeOut;

    private static int largeTextSize = 36;
    private static int mediumTextSize = 30;
    private static int smallTextSize = 22;
    private static int dataTextSize = 22;

    private JButton pauseButton;
    private JButton cancelButton;

    private int startingWidth = 800;
    private int startingHeight = 600;

    private void toggleAllVisible(){
        timeControl.setVisible(!timeControl.isVisible());
        baseliningButton.setVisible(!baseliningButton.isVisible());
        learningButton.setVisible(!learningButton.isVisible());
        extinctionButton.setVisible(!extinctionButton.isVisible());
        thresholdControl.setVisible(!thresholdControl.isVisible());
        summaryButton.setVisible(!summaryButton.isVisible());
        pauseButton.setVisible(!pauseButton.isVisible());
        cancelButton.setVisible(!cancelButton.isVisible());
    }

    private void changePause(JButton button){
        if(button.getText().equalsIgnoreCase("pause")){
            button.setText("Unpause");
        }
        else if(button.getText().equalsIgnoreCase("unpause")){
            button.setText("Pause");
        }
    }

    public Panel(JFrame frame, Baselining baseline) {
        this.frame = frame;
        this.baseline = baseline;
        networkThread = baseline.getWristbandInterface();
        inBaseline = false;
        inLearning = false;
        inExtinction = false;

        /* Create Spinner for control over the length of each phase */
        timeControl = new JSpinner(new SpinnerNumberModel(2,-Double.MAX_VALUE,Double.MAX_VALUE,.5));
        timeControl.setFont(new Font("Courier New", Font.PLAIN, smallTextSize));
        /* Create constraints on input of Time Controller */
        JComponent comp = timeControl.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        timeControl.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                constraintTimeInput();
            }
        });

        /* Create Baselining Phase Button & Implementation */
        baseliningButton = new JButton("Start Baselining Phase");
        baseliningButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                inBaseline = true;
                constraintTimeInput();
                getBaseline().setAllThresholds(maxThreshold); //set threshold very high to stop toy from activing during baseline
                toggleAllVisible();

                getBaseline().baselinePhase((Double) (timeControl.getValue()));

            }
        });

        /* Create Learning Phase Button & Implementation */
        learningButton = new JButton("Start Learning Phase");
        learningButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                inLearning = true;
                constraintTimeInput();
                toggleAllVisible();

                getBaseline().setAllThresholds(thresholdControl.getValue()); //send threshold before starting
                getBaseline().learningPhase((Double) timeControl.getValue());

            }
        });

        /* Create Extinction Phase Button & Implementation */
        extinctionButton = new JButton("Extinction Button");
        extinctionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                inExtinction = true;
                constraintTimeInput();
                toggleAllVisible();

                getBaseline().extinctionPhase((Double) timeControl.getValue());

            }
        });

        /* Create Pause Phase Button & Implementation */
        pauseButton = new JButton("Pause");
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                changePause(pauseButton);
                getBaseline().pause();
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
                    if(inBaseline){
                        getBaseline().baselineCancel();
                    }
                    if(inLearning){
                        getBaseline().learningCancel();
                    }
                    if(inExtinction){
                        getBaseline().extinctionCancel();
                    }
                    shutPhaseDown();
                }
            }
        });

        /*Create Summary Button & Implementation for OnClick */
        summaryButton = new JButton("Summary");
        summaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Summary.createSummary(new JFrame(), getBaseline()); //check before making a new Summary window
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

        /* Create method to resize fonts on window resize */
        frame.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent evt) {
                int buttonFontSize = (int)(getWidth()*.018) + (int)(getHeight()*.01);
                if(buttonFontSize < 12){
                    buttonFontSize = 12;
                }

                timeControl.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
                baseliningButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
                learningButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
                extinctionButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
                summaryButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
                arduinoFail.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
                arduinoTimeOut.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
                pauseButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
                cancelButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));

                smallTextSize = (int)(getWidth()*.018) + (int)(getHeight()*.012);
                mediumTextSize = (int)(getWidth()*.02) + (int)(getHeight()*.01);
                largeTextSize = (int)(getWidth()*.03) + (int)(getHeight()*.024);

                dataTextSize = (int)(getWidth()*.012) + (int)(getHeight()*.012);

                if(smallTextSize < 10){
                    smallTextSize = 10;
                }
                if(mediumTextSize < 16){
                    mediumTextSize = 16;
                }
                if(largeTextSize < 20){
                    largeTextSize = 20;
                }
                if(dataTextSize < 10){
                    dataTextSize = 10;
                }

            }
        });

        /*Add all Buttons & Inputs to the Form */
        this.setLayout(null);
        this.add(timeControl);
        this.add(baseliningButton);
        this.add(learningButton);
        this.add(extinctionButton);
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

    /**
     * Change all variables as needed for Phase shutdown
     */
    private void shutPhaseDown(){
        if(inBaseline){
            suggestedThreshold = (int)baseline.getBaseline();
            thresholdControl.setValue(suggestedThreshold);
            baseline.setAllThresholds(suggestedThreshold);
        }
        inBaseline = false;
        inLearning = false;
        inExtinction = false;
        toggleAllVisible();
    }

    /**
     * Ensures time input is between max and min limits.
     */
    private void constraintTimeInput(){
        if((Double)timeControl.getValue()>maxTime){
            timeControl.setValue(maxTime);
        }
        else if((Double)timeControl.getValue()<minTime){
            timeControl.setValue(minTime);
        }
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
            String loadDots = "";
            long seconds = System.currentTimeMillis()/1000;
            if(seconds%4 == 0){
                loadDots = ".";
            }
            else if(seconds%4 == 1){
                loadDots = "..";
            }
            else if(seconds%4 == 2){
                loadDots = "...";
            }
            g.setFont(new Font("Times New Roman", Font.PLAIN, largeTextSize));
            g.drawString("Loading" + loadDots, (int) (.25 * getWidth()), (int) (.4 * getHeight()));
            g.setFont(new Font("Times New Roman", Font.PLAIN, smallTextSize));
            g.drawString("Make sure the Aduino is turned on.", (int) (.35 * getWidth()), (int) (.5 * getHeight()));
        }
        else if(foundCom < 0){ //failed to find Ardiuno
            g.setFont(new Font("Times New Roman", Font.BOLD, largeTextSize));
            g.drawString("Failed to find Arduino!", (int) (.25 * getWidth()), (int) (.4 * getHeight()));
            g.setFont(new Font("Times New Roman", Font.PLAIN, smallTextSize));
            g.drawString("Please make sure the Arduino is connected and retry", (int) (.22 * getWidth()), (int) (.48 * getHeight()));
            arduinoFail.setVisible(true);
            arduinoFail.setBounds((int) (.25 * getWidth()), (int) (.55 * getHeight()), (int) (.35 * getWidth()), (int) (.13 * getHeight()));
        }
        else if(networkThread.getTimeOut()){
            g.setFont(new Font("Times New Roman", Font.BOLD, largeTextSize));
            g.drawString("The connection with the Arduino was lost.", (int) (.15 * getWidth()), (int) (.4 * getHeight()));
            g.setFont(new Font("Times New Roman", Font.PLAIN, smallTextSize));
            g.drawString("Please make sure the Arduino is connected and retry", (int) (.16 * getWidth()), (int) (.48 * getHeight()));
            arduinoTimeOut.setBounds((int) (.25 * getWidth()), (int) (.55 * getHeight()), (int) (.35 * getWidth()), (int) (.13 * getHeight()));
            arduinoTimeOut.setVisible(true);
        }
        else if(inBaseline){ //in baseline phase, collecting data
            displayPhaseScreen(g);
            g.setFont(new Font("Courier New", Font.PLAIN, mediumTextSize));
            g.drawString("Baseline Phase:", (int) (.045 * getWidth()), (int) (.26 * getHeight()));

            if(!baseline.getstartBaseline()){
                shutPhaseDown();
            }

            /*Display all incoming Data: */
            displayAllData(g);
        }
        else if(inLearning){ //in learning phase, collecting data
            displayPhaseScreen(g);
            g.setFont(new Font("Courier New", Font.PLAIN, mediumTextSize));
            g.drawString("Learning Phase: Threshold = " + thresholdControl.getValue(),(int) (.045 * getWidth()), (int) (.26 * getHeight()));
            g.drawString("In Phase: " + baseline.getPhaseNum() + " / " + baseline.getLearnPhases(),
                    (int)(.1*getWidth()), (int)(.33*getHeight()));

            if(!baseline.getStartLearning()){
                shutPhaseDown();
            }

            /*Display all incoming Data: */
            displayAllData(g);
        }
        else if(inExtinction){
            displayPhaseScreen(g);
            g.setFont(new Font("Courier New", Font.PLAIN, mediumTextSize));
            g.drawString("Extinction Phase:",(int) (.045 * getWidth()), (int) (.26 * getHeight()));

            if(!baseline.getStartExtinction()){
                shutPhaseDown();
            }

            /* Display all incoming data */
            displayAllData(g);
        }
        else{
            /* Reset all buttons and inputs if window was resized */
            timeControl.setBounds((int) (.52 * getWidth()), (int) (.02 * getHeight()), (int) (.3 * getWidth()), (int) (.07 * getHeight()));
            baseliningButton.setBounds((int) (.05 * getWidth()), (int) (.1 * getHeight()), (int) (.30 * getWidth()), (int) (.1 * getHeight()));
            learningButton.setBounds((int) (.35 * getWidth()), (int) (.1 * getHeight()), (int) (.30 * getWidth()), (int) (.1 * getHeight()));
            extinctionButton.setBounds((int) (.65 * getWidth()), (int) (.1 * getHeight()), (int) (.30 * getWidth()), (int) (.1 * getHeight()));
            summaryButton.setBounds((int) (.65 * getWidth()), (int) (.35 * getHeight()), (int) (.25 * getWidth()), (int) (.08 * getHeight()));
            summaryButton.setBounds((int) (.65 * getWidth()), (int) (.35 * getHeight()), (int) (.25 * getWidth()), (int) (.08 * getHeight()));
            thresholdControl.setBounds((int) (.04 * getWidth()), (int) (.24 * getHeight()), (int) (.9 * getWidth()), (int) (.08 * getHeight()));

            g.setFont(new Font("Times New Roman", Font.PLAIN, mediumTextSize));
            if(baseline.getbaselineData().size() > 0){
                g.drawString("Suggested Threshold: " + suggestedThreshold, (int) (.05 * getWidth()), (int) (.42 * getHeight()));
            }
            g.setFont(new Font("Times New Roman", Font.PLAIN, smallTextSize));
            g.drawString("Length of Phase (in minutes):",(int) (.18 * getWidth()), (int) (.07 * getHeight()));
            revalidate();
        }
    }

    /**
     * displayAllData is called from the paint function. It prints all incoming data to the screen.
     * @param g
     */
    private void displayAllData(Graphics g){
        LinkedList<DataPoint> values = new LinkedList<DataPoint>();
        if(baseline.getstartBaseline()){
            values = baseline.getbaselineData();
        }
        else if (baseline.getStartLearning()) {
            values = baseline.getLearningData();
        }
        else if(baseline.getStartExtinction()){
            values = baseline.getExtinctionData();
        }

        g.setFont(new Font("Courier New", Font.PLAIN, dataTextSize));
        g.drawString("Time: Data:", (int) (.03 * this.getWidth()), (int) (.4 * this.getHeight()));
        if(inBaseline){
            g.drawString("Sum:", (int) (.25 * this.getWidth()), (int) (.4 * this.getHeight()));
            //float sum = baseline.getSum();
            g.drawString(baseline.getSum() + "", (int) (.25 * this.getWidth()), (int) (.4 * this.getHeight()) + 30);
            g.drawString("Baseline:", (int) (.42 * this.getWidth()), (int) (.4 * this.getHeight()));
            //float avg = baseline.getBaseline();
            g.drawString(baseline.getBaseline() + "", (int) (.42 * this.getWidth()), (int) (.4 * this.getHeight()) + 30);
        }

        LinkedList<DataPoint> top20 = new LinkedList<DataPoint>();
        for (int i = values.size() - 1; i > Math.max(0, values.size() - 20); i--) {
            top20.add(values.get(i));
        }
        int y = 0;
        for (DataPoint d : top20) {
            g.drawString(d.getMovement(), (int) (.3 * this.getWidth()), (int) (.45 * this.getHeight()) + y);

            g.drawString(String.format(
                    "%7.2f: %5.2f",
                    d.getTime(),
                    Math.abs(d.getMagnitude())
            ), (int) (.005 * this.getWidth()), (int) (.45 * this.getHeight()) + y);
            y += 20;
        }
    }

    /**
     * Display all text and buttons for either Baseline or Learning Phase
     * @param g
     */
    private void displayPhaseScreen(Graphics g){
        pauseButton.setBounds((int) (.05 * getWidth()), (int) (.1 * getHeight()), (int) (.42 * getWidth()), (int) (.1 * getHeight()));
        cancelButton.setBounds((int) (.5 * getWidth()), (int) (.1 * getHeight()), (int) (.42 * getWidth()), (int) (.1 * getHeight()));

        g.setFont(new Font("Courier New", Font.PLAIN, largeTextSize));
        Double t = (Double)(timeControl.getValue());
        g.drawString(getTimeString(baseline.getTimerem()), (int) (.44 * this.getWidth()), (int) (.07 * this.getHeight()));
    }

}
