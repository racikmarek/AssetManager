package main;

public class MenuPoint {
	private static int id_global = 0;
	private int id;
	private String name;
	private String switchPattern;
	private int argumentCount;
	private String description;
	private String referenceMethod;
	
	public MenuPoint(String name, String switchPattern, int argumentCount, String description, String referenceMethod) {
		super();
		this.id = id_global++;
		this.name = name;
		this.switchPattern = switchPattern;
		this.argumentCount = argumentCount;
		this.description = description;
		this.referenceMethod = referenceMethod;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSwitchPattern() {
		return switchPattern;
	}

	public int getArgumentCount() {
		return argumentCount;
	}
	
	public String getDesciption() {
		return description;
	}
	
	public String getReferenceMethod() {
		return referenceMethod;
	}
	
}
