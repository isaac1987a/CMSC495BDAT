package GUIObjects;

import java.io.Serializable;

public class ComboItemDualString implements Serializable{
	private String str1;
	private String str2;
	public ComboItemDualString(String str1, String str2) {
		this.str1=str1;
		this.str2=str2;
	}
	//Column Name
	public String getColumnName() {
		return str1;
	}
	//Min Max String
	public String getMinMax() {
		return str2;
	}
	public String toString() {
		return str1+str2;
	}
}
