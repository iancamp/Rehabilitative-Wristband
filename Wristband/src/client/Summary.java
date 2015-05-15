package client;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

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
				String os = System.getProperty("os.name").toLowerCase(); //the operating system
				String path = "";
				String id = (String) JOptionPane.showInputDialog(
						getFrame(),
						"Input the Baby's ID\n"
								+ "To be saved in the export file.",
								"Baby's ID Input",
								JOptionPane.PLAIN_MESSAGE,
								null,
								null,
						"ID");
				if(id != null){ //if an id was entered and cancel was not selected

					//show save dialog box
					JFileChooser j = new JFileChooser();
					j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					Integer opt = j.showSaveDialog(getFrame());


					if(opt == j.APPROVE_OPTION){ //if the save dialog "OK" button was pressed
						try {
							path = j.getSelectedFile().getCanonicalPath(); //get the file path (does not include trailing "/"
						} catch (IOException e) {
							e.printStackTrace();
						}

						//check for OS so the correct / format can be appended to the path
						if(os.contains("mac os x") || os.contains("linux"))
							path += "/";
						else if(os.contains("windows"))
							path += "\\";
						//System.out.println(path);

						FileManager.saveToCSV(getBaseline(), id,path);
					}

				}
			}
		});

		deleteDataButton = new JButton("Delete All Data");
		deleteDataButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
		deleteDataButton.setVisible(true);
		deleteDataButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				getBaseline().baselineCancel();
				getBaseline().learningCancel();
				getBaseline().extinctionCancel();
				repaint();
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
		g.setFont(new Font("Times New Roman", Font.BOLD, 22));
		g.drawString("Low", (int) (.24 * getWidth()), (int) (.3 * getHeight()));
		g.drawString("Medium", (int) (.39 * getWidth()),(int) (.3 * getHeight()));
		g.drawString("High", (int) (.59 * getWidth()), (int) (.3 * getHeight()));
		g.drawString("Baseline", (int) (.8 * getWidth()), (int) (.3 * getHeight()));

		g.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		g.drawString("Baseline Phase", (int) (.02 * getWidth()), (int) (.45 * getHeight()));
		g.drawString("Learning Phase", (int) (.02 * getWidth()),(int) (.6 * getHeight()));
		g.drawString("Extinction Phase", (int)(.02*getWidth()),(int) (.75 * getHeight()));

		g.drawString(baseline.getLowPercentages()[0]+"", (int) (.25 * getWidth()), (int) (.45 * getHeight()));
		g.drawString(baseline.getMediumPercentages()[0] + "", (int) (.4 * getWidth()), (int) (.45 * getHeight()));
		g.drawString(baseline.getHighPercentages()[0]+"", (int) (.6 * getWidth()), (int) (.45 * getHeight()));

		g.drawString(baseline.getLowPercentages()[1]+"", (int) (.25 * getWidth()), (int) (.6 * getHeight()));
		g.drawString(baseline.getMediumPercentages()[1]+"", (int) (.4 * getWidth()), (int) (.6 * getHeight()));
		g.drawString(baseline.getHighPercentages()[1]+"", (int) (.6 * getWidth()), (int) (.6 * getHeight()));

		g.drawString(baseline.getLowPercentages()[2]+"", (int) (.25 * getWidth()), (int) (.75 * getHeight()));
		g.drawString(baseline.getMediumPercentages()[2]+"", (int) (.4 * getWidth()), (int) (.75 * getHeight()));
		g.drawString(baseline.getHighPercentages()[2]+"", (int) (.6 * getWidth()), (int) (.75 * getHeight()));

		saveButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
		saveButton.setBounds((int) (this.getWidth() * .12), (int) (this.getHeight() * .05), (int) (this.getWidth() * .30), (int) (this.getHeight() * .15));

		deleteDataButton.setFont(new Font("Times New Roman", Font.PLAIN, buttonFontSize));
		deleteDataButton.setBounds((int) (this.getWidth() * .52), (int) (this.getHeight() * .05), (int) (this.getWidth() * .30), (int) (this.getHeight() * .15));

		g.setFont(new Font("Courier New", Font.PLAIN, 20));

		g.drawString(baseline.getBaseline()+"", (int)(.81*this.getWidth()), (int)(.35*this.getHeight())+30);

		revalidate();
	}
}
