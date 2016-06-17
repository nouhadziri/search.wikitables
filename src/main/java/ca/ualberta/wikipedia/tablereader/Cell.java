package ca.ualberta.wikipedia.tablereader;

public class Cell {
	String type = null;
	String content = null;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Cell(String type, String content) {
		super();
		this.type = type;
		this.content = content;
	}
}
