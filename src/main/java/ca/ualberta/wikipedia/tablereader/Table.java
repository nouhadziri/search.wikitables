package ca.ualberta.wikipedia.tablereader;



public class Table {

	public boolean hasRowspan;
	public boolean hasColspan;
	public boolean hasMixRowspanAndColspan;
	public boolean hasNestedTable;
	public boolean hasException;
	public boolean hasCaption;
	
	public boolean hasNoHeader;
	public boolean hasMisuseException;
	
	public Table(boolean hasRowspan, boolean hasColspan, boolean hasMixRowspanAndColspan, boolean hasNestedTable,
			boolean hasException,boolean hasNoHeader,boolean hasCaption,boolean hasMisuseException) {
		super();
		this.hasRowspan = hasRowspan;
		this.hasColspan = hasColspan;
		this.hasMixRowspanAndColspan = hasMixRowspanAndColspan;
		this.hasNestedTable = hasNestedTable;
		this.hasException = hasException;
		this.hasNoHeader = hasNoHeader;
		this.hasCaption = hasCaption;
		this.hasMisuseException=hasMisuseException;
 	}

	

	public boolean isHasNoHeader() {
		return hasNoHeader;
	}



	public void setHasNoHeader(boolean hasNoHeader) {
		this.hasNoHeader = hasNoHeader;
	}



	public boolean isHasRowspan() {
		return hasRowspan;
	}

	public void setHasRowspan(boolean hasRowspan) {
		this.hasRowspan = hasRowspan;
	}

	public boolean isHasColspan() {
		return hasColspan;
	}

	public void setHasColspan(boolean hasColspan) {
		this.hasColspan = hasColspan;
	}

	public boolean isHasMixRowspanAndColspan() {
		return hasMixRowspanAndColspan;
	}

	public void setHasMixRowspanAndColspan(boolean hasMixRowspanAndColspan) {
		this.hasMixRowspanAndColspan = hasMixRowspanAndColspan;
	}

	public boolean isHasNestedTable() {
		return hasNestedTable;
	}

	public void setHasNestedTable(boolean hasNestedTable) {
		this.hasNestedTable = hasNestedTable;
	}

	public boolean isHasException() {
		return hasException;
	}

	public void setHasException(boolean hasException) {
		this.hasException = hasException;
	}

}
