/*
 * SearchType
 * By Isaac Blach
 * Displays and stores data about the desired graph.  Contains Vector of SQLParameters
 */

package GUIObjects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Random;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import CMSC495BDAT.GUI;

public class SearchOption extends JPanel implements ActionListener{
	private ComboItemDualString[] columns;
	GridBagLayout gridLayout = new GridBagLayout();
	private JComboBox <ComboItemDualString>columnsBox;
	private JComboBox <ComboItemDualString>columnsBox2;
	private JButton colorChooser1;
	private JCheckBox additionalOption;
	private JButton colorChooser2;
	private Vector <SQLParameters> parametersVector;
	private Vector <JPanel> addSubPanelVector;
	private ComboItem searchType;


	public SearchOption(ComboItemDualString[] columns, int layerNumber , String option, ComboItem searchType) {
		addSubPanelVector = new Vector<JPanel>();
		columnsBox=new JComboBox<ComboItemDualString>(columns);
		columnsBox2=new JComboBox<ComboItemDualString>(columns);
		this.searchType=searchType;
		this.columns=columns;
		parametersVector=new Vector<SQLParameters>();

		setLayout(gridLayout);
		
		createSearchBar(0 , option);
		
		
		
	//Text EntryBox
	//CheckBox
	//+- vertical Split
	}
	//Creates the horisontal bar with search data on it
	private void createSearchBar(int layerNumber , String option) {
		//JPanel searchTypePanel=new JPanel();
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill=(GridBagConstraints.HORIZONTAL);
		//BoxLayout boxLayout=new BoxLayout(searchTypePanel, BoxLayout.X_AXIS);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=.7;
		c.gridx=0;
		c.gridy=0;
		add(columnsBox,c);
		//add the second option for 
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=.7;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=0;
		add(columnsBox2,c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=0.3;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=0;
		c.insets = new Insets(0,10,0,10);
		colorChooser1 = new JButton(" ");
		colorChooser1.setBackground(Color.RED);
		add(colorChooser1,c );
		//Calls the color chooser and sets the background color of a button
		colorChooser1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				colorChooser1.setBackground(JColorChooser.showDialog(SearchOption.this, "Choose Background Color",colorChooser1.getBackground()));
				
			}
			
		});
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=0.4;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=0;
		c.insets = new Insets(0,0,0,0);
		additionalOption = new JCheckBox("option");
		add(additionalOption,c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=0.3;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=0;
		c.insets = new Insets(0,10,0,10);
		colorChooser2 = new JButton(" ");
		colorChooser2.setBackground(Color.BLUE);
		add (colorChooser2,c);
		//Calls the color chooser and sets the background color of a button
		colorChooser1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				colorChooser2.setBackground(JColorChooser.showDialog(SearchOption.this, "Choose Background Color",colorChooser2.getBackground()));
				
			}
			
		});
		
		addParameter();
	
		
	}
	
	//Set Values for Search
	public void prepare() {
		for (int i=0; i<parametersVector.size(); i++) {
			parametersVector.get(i).prepare();
		}
	}
	public Vector <SQLParameters> getSQLParameters() {
		return parametersVector;
	}
	//return column1
	public String getColumn() {
		ComboItemDualString tmpItem = (ComboItemDualString)columnsBox.getSelectedItem();
		return tmpItem.getColumnName();
	}
	//return column 2
	public String getColumn2() {
		ComboItemDualString tmpItem = (ComboItemDualString)columnsBox2.getSelectedItem();
		return tmpItem.getColumnName();
	}
	
	//Create a panel for the add and delete buttons
	private JPanel createAddSubPanel(int i) {
		
		//Create the add button
		JButton addButton=new JButton("+");
		addButton.setActionCommand(Integer.toString(i));
		addButton.setForeground(Color.GREEN);
		addButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {
				addParameter();
				revalidate();
				repaint();
			}
		});
		//Create the Delete Button
		JButton delButton=new JButton("-");
		delButton.setActionCommand(Integer.toString(i));
		delButton.setForeground(Color.RED);
		delButton.addActionListener(this);
		
		//Create panel and add buttons
		JPanel addSubPanel=new JPanel();
		addSubPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Add the addbutton
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty=.5;
		c.gridx=0;
		c.gridy=0;
		addSubPanel.add(addButton);
		
		//add the delete button
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty=.5;
		c.gridx=0;
		c.gridy=GridBagConstraints.RELATIVE;
		addSubPanel.add(delButton);
		
		//add this panel to a vector for deletion as needed
		addSubPanelVector.add(addSubPanel);
		return addSubPanel;
	}
	
	//add a parametersVector line and a set of add/delete buttons
	private void addParameter() {
		//Create Random Key Value
		Random rand = new Random();
		int i=rand.nextInt();
		
		GridBagConstraints c = new GridBagConstraints();
		//add the discriminator Panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=.9;
		c.gridx=0;
		c.gridy=GridBagConstraints.RELATIVE;		
		parametersVector.add(new SQLParameters(columns, i));
		add(parametersVector.get(parametersVector.size()-1),c);
		//add the addition/removal panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=.1;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=parametersVector.size();
		add(createAddSubPanel(i), c);
	}
	
	//Action Performed for Delete Button
	@Override
	public void actionPerformed(ActionEvent e) {
		if (parametersVector.size()>1) {
			//iterate through the parametersVector to find the correct one then remove and delete it and its associated object
			for (int i=0; i<parametersVector.size(); i++) {
				if (Integer.parseInt(e.getActionCommand())==parametersVector.get(i).objectNumber){
					this.remove(parametersVector.get(i));
					this.remove(addSubPanelVector.get(i));
					parametersVector.remove(i);
					addSubPanelVector.remove(i);
				}
			
			revalidate();
			repaint();
			}	
		}
	}
	
	
	//String for displaying in the search history
	public String createSearchString() {
		String str=searchType+" ";
		if (searchType.getValue().equals("Histogram")||searchType.getValue().equals("Scatter Plot")) {
			str+=columnsBox.getSelectedItem().toString()+" ";
		}
		if (searchType.getValue().equals("Scatter Plot")) {
			str+=columnsBox2.getSelectedItem().toString()+" ";
		}
		for (int i=0; i<parametersVector.size(); i++) {
			str+=parametersVector.get(i).createSearchString() + " ";
		}
		
		return str;
	}
	//set search type for archiving and set the visible buttons for each search type
	public void setSearchType(ComboItem item) {
		searchType=item;
		//Tabular View
		if (searchType.getKey()==0) {
			additionalOption.setVisible(false);
			colorChooser1.setVisible(false);
			colorChooser2.setVisible(false);
			columnsBox.setVisible(false);
			columnsBox2.setVisible(false);
			revalidate();
			repaint();
		}
			//Single Variable Charts
		if (searchType.getKey()==1) {
			colorChooser1.setVisible(true);
			columnsBox.setVisible(true);
			
			additionalOption.setVisible(false);
			colorChooser2.setVisible(false);
			columnsBox2.setVisible(false);
			revalidate();
			repaint();
		}
		//dual variable charts
		if (searchType.getKey()==2) {
			additionalOption.setVisible(false);
			colorChooser1.setVisible(true);
			colorChooser2.setVisible(true);
			columnsBox.setVisible(true);
			columnsBox2.setVisible(true);
			revalidate();
			repaint();
		}
	}
	public Color getColor1() {
		return colorChooser1.getBackground();
	}
	public Color getColor2() {
		return colorChooser2.getBackground();
	}
	public ComboItem getSearchType() {
		return searchType;
	}
	public ComboItemDualString getColumns() {
		return columns;
	}
}
