package domain;
public class Algorithm{
	
	private String name;
	private String folderName;
	private String algorithm;
	private String description;

	public void setName(String name){
		this.name = name;
	}

	public void setFolderName(String folderName){
		this.folderName = folderName;
	}

	public void setAlgorithm(String algorithm){
		this.algorithm = algorithm;
	}

	public void setDescription(String description){
		this.description = description;
	}
	
	public String toString(){
		return name;
	}

	public String getName() {
		return name;
	}

	public String getFolderName() {
		return folderName;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String getDescription() {
		return description;
	}
}
