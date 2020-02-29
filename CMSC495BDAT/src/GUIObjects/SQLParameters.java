/*
 * SQLParameters
 * By Isaac Blach
 * Used to display search parameters to send to the SQL DB.
 */

package GUIObjects;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
	public boolean valid = true;
	
	//Create GUI Elements
	private JComboBox<ComboItemDualString> columnSelection;
	private String[] discriminatorString= {">", ">=", "=", "<=", "<"};
	private JComboBox<String> discriminator = new JComboBox<String>(discriminatorString);
	
	private JTextField entryField=new JTextField();
	public int objectNumber;
	

	//Generate GUI
	public SQLParameters(ComboItemDualString[] columns, int i) {
		objectNumber = i;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		columnSelection=new JComboBox<ComboItemDualString>(columns);
		
		c.weightx=.5;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=0;
		add(columnSelection,c);
		
		c.weightx=.5;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=0;
		add(discriminator,c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=2.0;
		c.weighty=1.0;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=0;
		entryField.setColumns(15);
		add(entryField,c);
				
		
		/*c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=.5;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=0;
		addButton=new JButton("+");
		addButton.setForeground(Color.GREEN);
		delButton=new JButton("-");
		delButton.setForeground(Color.RED);
		JPanel addSubPanel=new JPanel();
		addSubPanel.setLayout(new BoxLayout(addSubPanel, BoxLayout.Y_AXIS));
		addSubPanel.add(addButton);
		addSubPanel.add(delButton);
		add(addSubPanel,c);*/
	}
	
	public void prepare() {
		
		//Set values from data selection
		valid=true;
		ComboItemDualString selectedItem = (ComboItemDualString)columnSelection.getSelectedItem();
		columnName=selectedItem.getColumnName();
		operator=String.valueOf(discriminator.getSelectedItem());
		String tmpStr=entryField.getText();
		//Check for edge case of "" in value field
		if (entryField.getText().equals("")||entryField.getText()==null){
			entryField.setBackground(Color.WHITE);
			valid=false;
			return;
		}
		
		//Set value from user entry.  If invalid entry
		//BG color goes red and Valid=false
		if ((entryField.getText().matches("[0-9]+")||entryField.getText().matches("[0-9]+[.][0-9]*"))) {
			try {
				value=Double.parseDouble(tmpStr);
				entryField.setBackground(Color.WHITE);
			}
			catch (NumberFormatException e){
				valid=false;
				entryField.setBackground(Color.RED);
			}
		}
		else {
			entryField.setBackground(Color.RED);
			valid=false;
		}
	}

	public String createSearchString() {
		if (valid)
			return columnSelection.getSelectedItem().toString() + " "+ discriminator.getSelectedItem().toString()+ " "+ entryField.getText();
		return "";
	}
}