package GUIObjects;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class SQLParameters extends JPanel{
	//Public Elements for the Search Pull
	public String columnName;
	public String operator;
	public double value;
	//public String andOr;
	public boolean valid = true;
	
	//Create GUI Elements
	private JComboBox<String> columnSelection;
	private String[] discriminatorString= {">", ">=", "=", "<=", "<"};
	private JComboBox<String> discriminator = new JComboBox<String>(discriminatorString);
	private JTextField entryField=new JTextField();
	private JButton addButton;
	private JButton delButton;
	

	//Generate GUI
	public SQLParameters(String[] columns) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		columnSelection=new JComboBox<String>(columns);
		add(columnSelection);
		add(discriminator);
		add(entryField);
		addButton=new JButton("+");
		addButton.setForeground(Color.GREEN);
		delButton=new JButton("-");
		delButton.setForeground(Color.RED);
		JPanel addSubPanel=new JPanel();
		addSubPanel.setLayout(new BoxLayout(addSubPanel, BoxLayout.Y_AXIS));
		addSubPanel.add(addButton);
		addSubPanel.add(delButton);
		add(addSubPanel);
	}
	
	public void prepare() {
		
		//Set values from data selection
		valid=true;
		columnName=String.valueOf(columnSelection.getSelectedItem());
		operator=String.valueOf(discriminator.getSelectedItem());
		String tmpStr=entryField.getText();
		
		//Check for edge case of "" in value field
		if (entryField.getText().equals("")){
			entryField.setBackground(Color.WHITE);
			valid=false;
			return;
		}
		
		//Set value from user entry.  If invalid entry
		//BG color goes red and Valid=false
		try {
			value=Double.parseDouble(tmpStr);
			entryField.setBackground(Color.WHITE);
		}
		catch (NumberFormatException e){
			valid=false;
			entryField.setBackground(Color.RED);
		}
	}
}