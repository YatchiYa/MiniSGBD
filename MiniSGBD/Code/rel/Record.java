package rel;

import java.util.ArrayList;

public class Record {
	public ArrayList<String> values;

	
	

	public Record(ArrayList<String> values) {
		this.values = values;
	}

	public Record() {
		super();
	}

	public ArrayList<String> getListValues() {
		return values;
	}

	public void setListValues(ArrayList<String> listValues) {
		this.values = values;
	}
	
	
}
