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
import java.util.Random;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import CMSC495BDAT.GUI;

public class SearchOption extends JPanel implements ActionListener{
	private String[] columns;
	GridBagLayout gridLayout = new GridBagLayout();
	private JComboBox <String>columnsBox;
	private JComboBox <String>columnsBox2;
	private JButton colorChooser1;
	private JCheckBox additionalOption;
	private JButton colorChooser2;
	private Vector <SQLParameters> parametersVector;
	private Vector <JPanel> addSubPanelVector;
	public String searchType;

	protected static final Insets insets1 = new Insets(0,10,4,10);
	public SearchOption(String[] columns, int layerNumber , String option, String searchType) {
		addSubPanelVector = new Vector<JPanel>();
		columnsBox=new JComboBox<String>(columns);
		columnsBox2=new JComboBox<String>(columns);
		this.searchType=searchType;
		this.columns=columns;
		parametersVector=new Vector<SQLParameters>();

		setLayout(gridLayout);
		
		createSearchBar(0 , option, searchType);
		
		
		
	//Text EntryBox
	//CheckBox
	//+- vertical Split
	}
	private void createSearchBar(int layerNumber , String option, String searchType) {
		//JPanel searchTypePanel=new JPanel();
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill=(GridBagConstraints.HORIZONTAL);
		//BoxLayout boxLayout=new BoxLayout(searchTypePanel, BoxLayout.X_AXIS);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=.9;
		c.gridx=0;
		c.gridy=0;
		add(columnsBox,c);
		//add the second option for 
		//if (searchType.equals("Scatter Plot")){
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx=1;
			c.gridx=GridBagConstraints.RELATIVE;
			c.gridy=0;
			add(columnsBox2,c);
		//}
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=0.3;
		c.gridx=GridBagConstraints.RELATIVE;
		c.gridy=0;
		c.insets = new Insets(0,10,0,10);
		colorChooser1 = new JButton(" ");
		colorChooser1.setBackground(Color.RED);
		add(colorChooser1,c );
		
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
		
		addDisc();
	
		
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
	public String getColumn() {
		return columnsBox.getSelectedItem().toString();
	}
	public String getColumn2() {
		return columnsBox2.getSelectedItem().toString();
	}
	
	//Create a panel for the add and delete buttons
	private JPanel createAddSubPanel(int i) {
		
		//Create the add button
		JButton addButton=new JButton("+");
		addButton.setActionCommand(Integer.toString(i));
		addButton.setForeground(Color.GREEN);
		addButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {
				addDisc();
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
	private void addDisc() {
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
			//iterate through the parametersVector to find the correct one then remve and delete it and its associated object
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
	/*public String saveSearch() {
		String str="";
		str+=columnsBox.getSelectedItem().toString() + "," + columnsBox.getSelectedItem().toString()+ ","
		return "";
	}*/
	public String createSearchString() {
		String str=searchType+" ";
		if (searchType.equals("Histogram")||searchType.equals("Scatter Plot")) {
			str+=columnsBox.getSelectedItem().toString()+" ";
		}
		if (searchType.equals("Scatter Plot")) {
			str+=columnsBox2.getSelectedItem().toString()+" ";
		}
		for (int i=0; i<parametersVector.size(); i++) {
			str+=parametersVector.get(i).createSearchString() + " ";
		}
		
		return str;
	}
}
