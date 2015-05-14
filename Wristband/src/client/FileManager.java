package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;



/**
 *Reads and writes files
 *@author iancamp
 */
public final class FileManager{

	private static final String SAVE_DIRECTORY = "./";

	/**
	 *Save the contents of the {@code Baselining}
	 *@param b: The {@code Baselining} object holding the data {@code LinkedList}
	 *@param id: The id of the baby which will be put at the top of the file
	 */
	public static void saveToCSV(Baselining b, String id){
		DataPoint dp; //the current data point in the list
		FileWriter fw;
		BufferedWriter bw;
		String fileName = generateFileName(id); //create a unique filename 
		File file = new File(SAVE_DIRECTORY + fileName); //create a file reference
		DateFormat timeFmt = new SimpleDateFormat("HH:mm");
		DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
		Date timeDate = new Date();
		String time = timeFmt.format(timeDate);
		String date = dateFmt.format(timeDate);
		
		try{
			if(!file.exists())
				file.createNewFile(); //create the file if it does not exist (it shouldn't)

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			//put name,date,time at the top of the file
			bw.write("ID,date,startTime,threshold,phase,currentTime,magnitude,movement\n");
			
			
			//loop through list of data and write to csv file:
			
			//baseline loop
			ListIterator<DataPoint> li = b.getbaselineData().listIterator();
			while(li.hasNext()){
				dp = li.next();
				bw.write(
						id + "," +
						date + "," +
						time + "," +
						b.getThreshold() + "," +
						dp.getPhase() + "," +
						dp.getTime() + "," +
						dp.getMagnitude() + "," +
						dp.getMovement() + "\n"
						);	
			}
			
			//learning phase loop
			li = b.getLearningData().listIterator();
			while(li.hasNext()){
				dp = li.next();
				bw.write(
						id + "," +
						date + "," +
						time + "," +
						b.getThreshold() + "," +
						dp.getPhase() + "," +
						dp.getTime() + "," +
						dp.getMagnitude() + "," +
						dp.getMovement() + "\n"
						);	
			}

			//extinction phase loop
			li = b.getExtinctionData().listIterator();
			while(li.hasNext()){
				dp = li.next();
				bw.write(
						id + "," +
						date + "," +
						time + "," +
						b.getThreshold() + "," +
						dp.getPhase() + "," +
						dp.getTime() + "," +
						dp.getMagnitude() + "," +
						dp.getMovement() + "\n"
						);	
			}

			bw.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 *Generates a filename for a new csv file
	 *Format: Baby id_yyyy-mm-dd_hh-mm.csv
	 *@param id The id of the baby which will be a part of the file name
	 *@return fileName A filename matching the above format
	 */
	public static String generateFileName(String id){
		DateFormat dateFmt = new SimpleDateFormat("_yyyy-MM-dd_HH-mm");
		Date date = new Date();

		return id + dateFmt.format(date) + ".csv";
	}

}