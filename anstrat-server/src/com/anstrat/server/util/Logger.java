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
	
	private enum Level {INFO, WARN, ERROR};
	
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
	
	private synchronized void logln(String message, Level level, Object... formatArgs){
		if(formatArgs.length > 0){
			// Don't crash the application if there's an error in the log message format
			try{
				message = String.format(message, formatArgs);
			}
			catch(IllegalFormatException e){
				level = Level.ERROR;
				message = "Error in log message String.format() args: '" + e.getMessage() + "'.";
			}
		}
		
		// Get the source file and line number
		StackTraceElement element = Thread.currentThread().getStackTrace()[3];
		
		// Remove the package names
		String[] split = element.getClassName().split("\\.");
		String simpleClassName = split[split.length - 1];
		
		message = String.format("[%s][%s][%s:%s:%d] %s\n", 
				level,
				dateFormat.format(new Date()),
				Thread.currentThread().getName(), 
				simpleClassName, 
				element.getLineNumber(), 
				message);
		
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

	public void info(String message, Object... args){
		logln(message, Level.INFO, args);
	}
	
	public void warning(String message, Object... args){
		logln(message, Level.WARN, args);
	}
	
	public void error(String message, Object... args){
		logln(message, Level.ERROR, args);
	}
	
	public void exception(Throwable t, String message, Object... args){
		logln(String.format("%s : %s", message, t.getMessage()), Level.ERROR, args);
	}
}
