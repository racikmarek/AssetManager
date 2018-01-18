package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.bind.DatatypeConverter;

public class FileDB extends FileRecord implements Serializable {
	private static final long serialVersionUID = -2417985372526925516L;
	private static String eol = "\n";
    private HashMap<String, FileRecord> records;
    private HashSet<String> appliedFiles; //processed
    private HashSet<String> ommitedFiles; //not processed due to correct logic
    private HashSet<String> skippedFiles; //not processed due to error
    
    public FileDB(String path) {
    	super(path, "");
    	this.path = path;
    	this.records = new HashMap<>();
    	this.appliedFiles = new HashSet<>();
    	this.ommitedFiles = new HashSet<>();
    	this.skippedFiles = new HashSet<>();
    }
    
    public static FileDB load(String filePath) {
    	FileDB loadedDB = null;
    	try {
	    	FileInputStream fis = new FileInputStream(filePath);
	    	ObjectInputStream ois = new ObjectInputStream(fis);
	    	loadedDB = (FileDB) ois.readObject();
	    	ois.close();
	    	fis.close();
	    	System.out.println("DB file loaded.");
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Failed to load DB file.");
			e.printStackTrace();
		}
    	return loadedDB;
    }
	
    public void save() {
    	try {
    		String filePath = getPath();
			FileOutputStream fos = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
			fos.close();
			System.out.println("DB file saved.");
		} catch (IOException e) {
			System.out.println("Failed to save DB file.");
			e.printStackTrace();
		}
    }
    
    public boolean existsFileDB(String filePath) {
    	clearResult();
    	return existsFileDBloc(filePath);
    }
    
    public boolean existsFileDBloc(String filePath) {
    	File f = new File(filePath);
    	if (f.exists() && !f.isDirectory()) {
    		String hash = calculateHash(filePath);
    		this.appliedFiles.add(records.get(hash).getPath());
    		return records.containsKey(hash);
    	}
    	else {
    		this.ommitedFiles.add(records.get(hash).getPath());
    	}
    	return false;
    }
    
    public void existsFilesDirDB(String path) {
    	clearResult();
    	existsFilesDirDBloc(path);
    }
    
    public void existsFilesDirDBloc(String path) {
    	File inF = new File(path);
    	if (inF.exists() && inF.isDirectory()) {
    		File[] files = inF.listFiles();
    		for (File f: files) {
    			if (f.isDirectory()) {
    				existsFilesDirDBloc(f.getAbsolutePath());
    			}
    			else {
    				existsFileDBloc(f.getAbsolutePath());
    			}
    		}
    	}
    	else {
    		this.skippedFiles.add(path);
    	}
    }
    
    public void removeDirDB(String path) {
    	clearResult();
    	removeDirDBloc(path);
    }
    
    public void removeDirDBloc(String path) {
    	File inF = new File(path);
    	if (inF.exists() && inF.isDirectory()) {
    		File[] files = inF.listFiles();
    		for (File f: files) {
    			if (f.isDirectory()) {
    				removeDirDBloc(f.getAbsolutePath());
    			}
    			else {
    				removeFileDBloc(f.getAbsolutePath());
    			}
    		}
    	}
    	else {
    		this.skippedFiles.add(path);
    	}
    }    
    
    public void removeFileDB(String filePath) {
    	clearResult();
    	removeFileDBloc(filePath);
    }
    
    public void removeFileDBloc(String filePath) {
    	File f = new File(filePath);
    	if (f.exists() && !f.isDirectory()) {
    		String hash = calculateHash(filePath);
    		if (records.containsKey(hash)) {
    			records.remove(hash);
    			this.appliedFiles.add(filePath);
    		}
    		else {
    			this.ommitedFiles.add(filePath);
    		}
    	}
    	else {
    		this.skippedFiles.add(filePath);
    	}
    }
    
    public void addFile2DB(String filePath) {
    	clearResult();
    	addFile2DBloc(filePath);
    }
    
    public void addFile2DBloc(String filePath) {
    	File f = new File(filePath);
    	if (f.exists() && !f.isDirectory()) {
    		String hash = calculateHash(filePath);
    		if (!records.containsKey(hash)) {
    			FileRecord rec = new FileRecord(filePath, hash);
        		records.put(hash, rec);
        		this.appliedFiles.add(filePath);
    		}
    		else {
    			this.ommitedFiles.add(filePath);
    		}
    	}
    	else {
    		this.skippedFiles.add(filePath);
    	}
    }
    
    public void addDirectory2DB(String path) {
    	clearResult();
    	addDirectory2DBloc(path);
    }
    
    public void addDirectory2DBloc(String path) {
    	File inF = new File(path);
    	if (inF.exists() && inF.isDirectory()) {
    		File[] files = inF.listFiles();
    		for (File f: files) {
    			if (f.isDirectory()) {
    				addDirectory2DBloc(f.getAbsolutePath());
    			}
    			else {
    				addFile2DBloc(f.getAbsolutePath());
    			}
    		}
    	}
    	else {
    		this.skippedFiles.add(path);
    	}
    }
    
    public void cleanupDirDB(String path) {
    	clearResult();
    	FileDB db = new FileDB(path);
    	db.addDirectory2DB(path);
    	HashSet<String> toRemove = new HashSet<>();
    	for (String hash: records.keySet()) {
    		if (!db.records.containsKey(hash)) {
    			this.appliedFiles.add(records.get(hash).getPath());
    			toRemove.add(hash);
    		}
    		else {
    			this.ommitedFiles.add(records.get(hash).getPath());
    		}
    	}
    	for (String hash: toRemove) {
    		records.remove(hash);
    	}
    }
    
    private boolean isPrefix(String s, HashSet<String> dirs) {
    	for (String a: dirs) {
    		if (a.startsWith(s)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public HashSet<String> getMostShallowDirectories(HashSet<String> dirs) {
    	HashSet<String> newDirs = new HashSet<>();
    	for (String dir: dirs) {
    		if (!isPrefix(dir, newDirs)) {
    			newDirs.add(dir);
    		}
    	}
    	if (dirs.size() == newDirs.size()) {
    		return newDirs;
    	}
    	else {
    		return getMostShallowDirectories(newDirs);
    	}
    }
    
    private String searchFile(String directory, String hash) {
    	//TODO
    	try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public String searchFile(HashSet<String> directories, String hash) {
    	for (String dir: directories) {
    		String newPath = searchFile(dir, hash);
    		if (newPath != null) {
    			return newPath;
    		}
    	}
    	return null;
    }
    
    private HashSet<String> getAllDirs() {
    	HashSet<String> result = new HashSet<>();
    	for (FileRecord rec: records.values()) {
    		result.add(rec.getDirectory());
    	}
    	return result;
    }
    
	public void rebuildPathsDB() {
		HashSet<String> msd = getMostShallowDirectories(getAllDirs());
    	for (String hash: records.keySet()) {
    		FileRecord rec = records.get(hash);
    		File f = new File(rec.getPath());
    		if (f.exists() && !f.isDirectory()) {
    			String fileHash = calculateHash(rec.getPath());
    			if (fileHash.equals(hash)) {  //found match, no change
    				this.ommitedFiles.add(records.get(hash).getPath());
    			}
    			else { //found new file covering the old one
        			String newDir = searchFile(msd, hash);
        			if (newDir != null) {
        				this.appliedFiles.add(records.get(hash).getPath() + " -> " + newDir);
        				rec.path = newDir;
        			}
        			else {
        				this.skippedFiles.add(records.get(hash).getPath());
        			}
    			}
    		}
    		else { //file no more exists in the location, search for it
    			String newDir = searchFile(msd, hash);
    			if (newDir != null) {
    				this.appliedFiles.add(records.get(hash).getPath() + " -> " + newDir);
    				rec.path = newDir;
    			}
    			else {
    				this.skippedFiles.add(records.get(hash).getPath());
    			}
    		}
    	}
	}
    
    public HashSet<String> getFileHashesDB(String path) {
    	return (HashSet<String>) records.keySet();
    }
    
    private static String calculateHash(String filePath) {
    	Path path = Paths.get(filePath);    	
		try {
			byte[] input = Files.readAllBytes(path);
	    	byte[] output = MessageDigest.getInstance("MD5").digest(input);
	    	return DatatypeConverter.printHexBinary(output);
		} catch (IOException | NoSuchAlgorithmException e) {
			System.out.println("Failed to make hash: " + eol + path );
			return "";
		}
    }
    
    public void clearResult() {
    	this.appliedFiles.clear();
    	this.ommitedFiles.clear();
    	this.skippedFiles.clear();
    }
    
    public String getResult() {
    	String result = "";
    	result += "---------------------------------------------------" + eol;
    	result += "Action applied for these files: (" + this.appliedFiles.size() + ")" + eol;
    	for (String filePath: this.appliedFiles) {
    		result += filePath + eol;
    	}
    	result += "---------------------------------------------------" + eol;
    	result += "Ommited files: (" + this.ommitedFiles.size() + ")" + eol;
    	for (String filePath: this.ommitedFiles) {
    		result += filePath + eol;
    	}
    	result += "---------------------------------------------------" + eol;
    	result += "Skipped files: (" + this.skippedFiles.size() + ")" + eol;
    	for (String filePath: this.skippedFiles) {
    		result += filePath + eol;
    	}
    	result += "---------------------------------------------------" + eol;
    	return result;
    }
    
    public String getDBstatus() {
    	String out = "Status of File DB: " + eol;
    	out += path + eol;
    	out += "-----------------------" + eol;
    	out += "File count: " + records.size();
    	return out;
    }
    
    @Override
    public String toString() {
    	String result = "";
    	for (String hash: records.keySet()) {
    		String path = records.get(hash).getPath();
    		result += hash + " -> ";
    		result += path + eol;
    	}
    	return result;
    }
    
}
