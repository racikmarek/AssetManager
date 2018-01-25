package main;

import java.io.File;
import database.FileDB;

public class InputInterface {
	private static final String flAddDirDB = "-A";
	private static final String flAddFileDB = "-a";
	private static final String flExistsFileDB = "-e";
	private static final String flExistsDirDB = "-E";
	private static final String flStatusDB = "-s";
	private static final String flPrintDB = "-p";
	private static final String flRemoveFileDB = "-r";
	private static final String flRemoveDirDB = "-R";
	private static final String flRefreshDirDB = "-c";
	private static final String flRebuildPathsDB = "-b";
	private static final String flFindDupsDB = "-d";
	private static final String flHelpDB = "-h";
		
	private FileDB db;
	
	public InputInterface() {
	}
	
	private void prepareDB(String filePathDB) {
		File dbF = new File(filePathDB);
    	if (dbF.exists() && !dbF.isDirectory()) {
    		db = FileDB.load(dbF.getAbsolutePath());
    	}
    	else if (!dbF.isDirectory()) {
    		db = new FileDB(dbF.getAbsolutePath());
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
			db.existsFileDB(inputPath);
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		case flExistsDirDB: 
			db.existsFilesDirDB(inputPath);
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		case flStatusDB: 
			System.out.println(db.getDBstatus());
			break;
		case flPrintDB: 
			System.out.println(db);
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
		case flRefreshDirDB:
			db.refreshDirDB();
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		case flRebuildPathsDB:
			//db.rebuildPathsDB();
			db.save();
			System.out.println(db.getResult());
			System.out.println(db.getDBstatus());
			break;
		case flFindDupsDB:
			if (inputPath != null) {
				db.findDupsDB(inputPath);
				System.out.println(db.getResult());
				System.out.println(db.getDBstatus());
			}
			else {
				//db.findDupsDB();
				System.out.println(db.getResult());
				System.out.println(db.getDBstatus());
			}
			break;
		case flHelpDB:
			System.out.println("Help: not implemented yet");
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
			return;
		}
		prepareDB(filePathDB);
		if (switchFlag != null) {
			processCommand(switchFlag, inputPath);
		}
	}
	
}
