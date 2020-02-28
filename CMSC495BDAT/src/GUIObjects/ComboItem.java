package GUIObjects;

import java.io.Serializable;

public class ComboItem implements Serializable{
	private String value;
	private Integer key;
	
	public ComboItem(String str, int i) {
		value=str;
		key=i;
	}
	public String getValue() {
		return value;
	}
	public int getKey() {
		return key;
	}
	public String toString() {
		return value;
	}
}
