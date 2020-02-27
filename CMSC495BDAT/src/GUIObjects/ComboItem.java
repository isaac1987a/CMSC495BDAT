package GUIObjects;

public class ComboItem {
	private String value;
	private int key;
	
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
