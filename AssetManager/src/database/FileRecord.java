package database;

import java.io.File;
import java.io.Serializable;

public class FileRecord implements Serializable {
	private static final long serialVersionUID = 5077002891853342345L;
	protected String path;
    protected String hash;
    
    public FileRecord(String path, String hash) {
		super();
		this.path = path;
		this.hash = hash;
	}
	public String getPath() {
		return path;
	}
	public String getHash() {
		return hash;
	}
	
	public String getDirectory() {
		File f = new File(path);
		return f.getParent();
	}
	
}
