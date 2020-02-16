/*
 * SearchType
 * By Isaac Blach
 * Displays and stores data about the desired graph.  Contains Vector of SQLParameters
 */

package GUIObjects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class SearchType extends JPanel{
	private JButton colorChooser1;
	private JCheckBox additionalOption;
	private JButton colorChooser2;
	private JButton addButton;
	private JButton delButton;
	private Vector <SQLParameters> discriminators;
	public SearchType(String[] colums, int layerNumber , String option) {
		setLayout(new GridLayout(2,2));
		add(createSearchBar(0 , option));
		discriminators=new Vector<SQLParameters>();
		for (int i=0; i<3; i++) {
			discriminators.add(new SQLParameters(colums));
			add(discriminators.get(i));
		}
		
	//Text EntryBox
	//CheckBox
	//+- vertical Split
	}
	private JPanel createSearchBar(int layerNumber , String option) {
		JPanel searchTypePanel=new JPanel();
		BoxLayout boxLayout=new BoxLayout(searchTypePanel, BoxLayout.X_AXIS);
		searchTypePanel.setLayout(boxLayout);
		colorChooser1 = new JButton(" ");
		colorChooser1.setBackground(Color.RED);
		colorChooser1.setPreferredSize(getPreferredSize());;
		searchTypePanel.add(colorChooser1);
		additionalOption = new JCheckBox("option");
		searchTypePanel.add(additionalOption);
		colorChooser2 = new JButton(" ");
		colorChooser2.setBackground(Color.BLUE);
		searchTypePanel.add (colorChooser2);
		addButton=new JButton("+");
		addButton.setForeground(Color.GREEN);
		delButton=new JButton("-");
		delButton.setForeground(Color.RED);
		JPanel addSubPanel=new JPanel();
		addSubPanel.setLayout(new BoxLayout(addSubPanel, BoxLayout.Y_AXIS));
		addSubPanel.add(addButton);
		addSubPanel.add(delButton);
		searchTypePanel.add(addSubPanel);	//DropDownMenu
		return searchTypePanel;
	}
	
	//Set Values for Search
	public void prepare() {
		for (int i=0; i<discriminators.size(); i++) {
			discriminators.get(i).prepare();
		}
	}
	public Vector <SQLParameters> getSQLParameters() {
		return discriminators;
	}
}
