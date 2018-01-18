package main;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import database.FileDB;
import processing.InputProcessor;

public class InputInterface {
	private static final String flAddDirDB = "-d";
	private static final String flAddFileDB = "-f";
	private static final String flExistsFileDB = "-e";
	private static final String flExistsDirDB = "-E";
	private static final String flStatusDB = "-s";
	private static final String flPrintDB = "-p";
	private static final String flRemoveFileDB = "-r";
	private static final String flRemoveDirDB = "-R";
	private static final String flCleanupDirDB = "-c";
	private static final String flRebuildPathsDB = "-b";
		
	private FileDB db;
	private InputProcessor inputProcessor;
	private ArrayList<MenuPoint> menu;
	
	public InputInterface() {
		this.inputProcessor = new InputProcessor();
		this.menu = getMenuItems();
	}
	
	private void prepareDB(String filePathDB) {
		File dbF = new File(filePathDB);
    	if (dbF.exists() && !dbF.isDirectory()) {
    		db = FileDB.load(filePathDB);
    	}
    	else if (!dbF.isDirectory()) {
    		db = new FileDB(filePathDB);
    		db.save();
    	}
    	else {
    		System.out.println("Incorrect DB file path.");
    	}
	}
	
	private void processCommand(String switchFlag, String inputPath) {
		switch (switchFlag) {
		case flAddDirDB: 
			db.addDirectory2DB(inputPath);
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		case flAddFileDB: 
			db.addFile2DB(inputPath);
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		case flExistsFileDB: 
			System.out.println("Not implemented yet.");
			break;
		case flExistsDirDB: 
			System.out.println("Not implemented yet.");
			break;
		case flStatusDB: 
			System.out.println(db.getDBstatus());
			break;
		case flPrintDB: 
			System.out.println("Not implemented yet.");
			break;
		case flRemoveFileDB:
			db.removeFileDB(inputPath);
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		case flRemoveDirDB:
			db.removeDirDB(inputPath);
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		case flCleanupDirDB:
			db.cleanupDirDB(inputPath);
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		case flRebuildPathsDB:
			db.rebuildPathsDB();
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		default: 
			System.out.println("Incorrect command on input.");
		}
	}
	
	public void run(String[] args) {
		String filePathDB = null;
		String switchFlag = null;
		String inputPath = null;
		if (args.length > 0) {
			filePathDB = args[0];
		}
		if (args.length > 1) {
			switchFlag = args[1];
		}
		if (args.length > 2) {
			inputPath = args[2];
		}
		if (filePathDB == null) {
			System.out.println("Please define DB file location.");
		}
		prepareDB(filePathDB);
		if (switchFlag != null) {
			processCommand(switchFlag, inputPath);
		}
		
		/*
		MenuPoint mp;
		if (args.length > 0) {
			mp = getMenuItemBySwitch(args[0]);
			System.out.println(runFunction(mp, args));
		}
		else {
			printMenu(menu);
			//TODO get user input
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		*/
	}

	private String runFunction(MenuPoint mp, String[] args) {
		String result = null;
		if (mp == null) {
			System.out.println("Illegral option switch!");
			return result;
		}	
		try {
		  Class<?> c = inputProcessor.getClass();
		  Method method = null;
		  switch (mp.getArgumentCount()) {
		    case 0: method = c.getDeclaredMethod(mp.getReferenceMethod()); 
		    		result = (String) method.invoke(inputProcessor); break;
		    case 1: method = c.getDeclaredMethod(mp.getReferenceMethod(), String.class); 
		    		result = (String) method.invoke(inputProcessor, args[1]); break;
		    case 2: method = c.getDeclaredMethod(mp.getReferenceMethod(), String.class, String.class); 
		    		result = (String) method.invoke(inputProcessor, args[1], args[2]); break;
		    case 3: method = c.getDeclaredMethod(mp.getReferenceMethod(), String.class, String.class, String.class); 
		    		result = (String) method.invoke(inputProcessor, args[1], args[2], args[3]); break; 
		  }
		} catch (Exception e) { 
			System.out.println("Something went wrong.");
			e.printStackTrace();
		}
		return result;
	}
	
	private ArrayList<MenuPoint> getMenuItems() {
		ArrayList<MenuPoint> menu = new ArrayList<MenuPoint>();
		menu.add(new MenuPoint("Check file", "-c", 1, "fromFile", "checkFile"));
		menu.add(new MenuPoint("Check files in directory", "-cd", 1, "fromDir", "checkDir"));
		menu.add(new MenuPoint("Add file", "-a", 2, "fromFile, toDir", "addFile"));
		menu.add(new MenuPoint("Add files from directory", "-ad", 2, "fromDir, toDir", "addDir"));
		menu.add(new MenuPoint("Collect web tmblrs", "-ct", 2, "addr, toDir", "collectTumblr"));
		return menu;
	}
	
	private void printMenu(ArrayList<MenuPoint> menu) {
		System.out.println("Menu:");
		for (MenuPoint m: menu) {
			System.out.println("  " + m.getId() + ". " + m.getName() + 
							   "  [" + m.getSwitchPattern() + "]  [" + m.getDesciption() + "]");
		}
	}
	
	private MenuPoint getMenuItemBySwitch(String switchOpt) {
		for (MenuPoint p: menu) {
			if (p.getSwitchPattern().equals(switchOpt)) {
				return p;
			}
		}
		return null;
	}
	
}
