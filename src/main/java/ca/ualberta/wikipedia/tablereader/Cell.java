package ca.ualberta.wikipedia.tablereader;

public class Cell {
	
	String type = null;
	String content = null;
	boolean hasException = false;
	boolean hasMisuseException = false;
	boolean cellHasRowspan =false;
	


	public Cell(String type, String content,boolean cellHasRowspan) {
		super();
		this.type = type;
		this.content = content;
		this.cellHasRowspan = cellHasRowspan;
	}
	
	public Cell(String type, String content,boolean cellHasRowspan,boolean hasException) {
		super();
		this.type = type;
		this.content = content;
		this.cellHasRowspan = cellHasRowspan;
		this.hasException= hasException;
	}
	
	public Cell(String type, String content) {
		super();
		this.type = type;
		this.content = content;
		
	}


	public boolean isHasMisuseException() {
		return hasMisuseException;
	}

	public void setHasMisuseException(boolean hasMisuseException) {
		this.hasMisuseException = hasMisuseException;
	}


	
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
	public boolean isCellHasRowspan() {
		return cellHasRowspan;
	}

	public void setCellHasRowspan(boolean cellHasRowspan) {
		this.cellHasRowspan = cellHasRowspan;
	}
	public boolean isHasException() {
		return hasException;
	}

	public void setHasException(boolean hasException) {
		this.hasException = hasException;
	}
	
}
