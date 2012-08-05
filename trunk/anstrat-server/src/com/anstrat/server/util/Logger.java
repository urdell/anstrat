package com.anstrat.server.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatException;

public class Logger {
	
	private static final String LOG_DIRECTORY = "logs";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM 'kl' HH:mm");
	
	private File logFile;
	
	public Logger(){
		createLogFile();
	}
	
	private void createLogFile(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH;mm;ss");
		new File("logs").mkdir();
		logFile = new File(LOG_DIRECTORY, String.format("Server log %s.log", sdf.format(new Date())));
		
		try{
			if(!logFile.exists()){
				logFile.createNewFile();
			}
		}
		catch(IOException ioe){
			System.err.println("Couldn't create log file...");
			ioe.printStackTrace();
			logFile = null;
		}
	}
	
	public synchronized void log(String message, String level, Object... formatArgs){
		if(formatArgs.length > 0){
			try{
				message = String.format(message, formatArgs);
			}
			catch(IllegalFormatException e){
				
			}
			
		}
		
		message = String.format("[%s:%s][%s] %s", level, Thread.currentThread().getName(), dateFormat.format(new Date()), message);
		
		// Log to console
		System.out.print(message);
		
		// Log to file
		if(logFile != null){
			try{
				FileWriter fw = new FileWriter(logFile,true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.append(message);
				bw.close();
			}
			catch(FileNotFoundException fnfe){
				System.err.println("Log file not found.");
			}
			catch(IOException ioe){
				System.err.println("Error writing to log file.");
			}
		}
	}
	
	public void logln(String message, String level, Object... formatArgs){
		log(message+"\n", level, formatArgs);
	}
	
	public void info(String message, Object... args){
		logln(message, "INFO", args);
	}
	
	public void warning(String message, Object... args){
		logln(message, "WARN", args);
	}
	
	public void error(String message, Object... args){
		logln(message, "ERROR", args);
	}
}
