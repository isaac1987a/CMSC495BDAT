/*Gui
 * By Isaac Blach
 * Creates Core GUI and calls other classes to perform various functions
 */


package CMSC495BDAT;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;

import GUIObjects.SearchOption;
import javafx.stage.FileChooser;

public class GUI extends JFrame implements ActionListener{
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem loadDB;
	//Create GUI objects
	private GridBagLayout gridLayout=new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	private Vector <SearchOption> SearchOptions = new Vector<SearchOption>();
	private String[] types = {"Tabular", "Histogram", "Scatter Plot"};  //Types of displays
	private JComboBox<String> searchSelectorBox=new JComboBox<String>(types);
	private JPanel searchPanel=new JPanel();
	private JButton searchButton= new JButton("Search");
	private JComboBox<String> loadSearchBox = new JComboBox();
	
	private InputOutput io = new InputOutput();
	private String[] columnNames= {""};
	private SqlDatabase db = new SqlDatabase();
	
	public GUI() {
		SearchOptions = new Vector<SearchOption>();
		startup();
		
		//Create Window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	//Create main window.  Separated into it's own class to 
	private void createMainWindow() {
		setVisible(false);
		//Reset Everything
		searchPanel.removeAll();
		searchPanel=new JPanel();
		
		//Set Window Size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.height/2,screenSize.width/3);
		
		//Add menu
		menuBar=new JMenuBar();
		menu=new JMenu("File");
		loadDB=new JMenuItem("Load DB");
		
		
		//add Panels
		searchPanel.setLayout(gridLayout);
		add(searchPanel);
		
		//add MenuBar
		this.setJMenuBar(menuBar);
		menuBar.add(menu);
		menu.add(loadDB);
		loadDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dbSelectionMenu();
			}
		});
		
		//Create the Search Search Panel Layout
		
		c.anchor=GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.weightx=1;
		c.gridx=0;
		c.gridy=0;
		searchPanel.add(searchSelectorBox,c);
		
		//Add Search Grid
		SearchOptions.add(new SearchOption(columnNames, 0, "nowhere", searchSelectorBox.getSelectedItem().toString()));
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx=1;
		c.gridx=0;
		c.gridy=GridBagConstraints.RELATIVE;
		searchPanel.add(SearchOptions.get(0),c);
		
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx=1;
		c.gridx=0;
		c.ipady=20;
		c.anchor=GridBagConstraints.PAGE_END;
		c.gridy=GridBagConstraints.RELATIVE;
		c.gridwidth=1;
		searchPanel.add(searchButton,c);
		//execute the Search
		searchButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) {
				executeSearch();
			}
			
		});
		//Save Search Test Button
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx=GridBagConstraints.RELATIVE;
		c.weightx=1;
		c.gridx=0;
		c.ipady=20;
		c.gridwidth=1;
		searchPanel.add(loadSearchBox,c);
		
		
		
		//Load Search Test Button
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx=GridBagConstraints.RELATIVE;
		c.weightx=1;
		c.gridx=0;
		c.ipady=20;
		c.gridwidth=1;
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//if (arg0.getSource().equals(loadButton))
					//loadSearch();		
			}
		});
		searchPanel.add(loadButton,c);
		
		
		
		setVisible(true);

	}
	//pre GUI commands
	private void startup() {
		//check for previously used DB and load
		if (!(io.getCurrentDatabase()==null||(!io.getCurrentDatabase().matches("[A-Za-z][A-Za-z0-9]+")))) {
			db.selectDatabase(io.getCurrentDatabase());
			columnNames=db.getColumnDatabase();
			createMainWindow();
		}
		else {
			dbSelectionMenu();
		}
	}
	//create menu to select or create DB
	private void dbSelectionMenu() {
		JFrame dbSelectFrame = new JFrame("Select DB");
		dbSelectFrame.setSize(200, 300);
		JPanel dbSelectionPanel=new JPanel();
		dbSelectFrame.add(dbSelectionPanel);
		dbSelectionPanel.setLayout(new BoxLayout(dbSelectionPanel, BoxLayout.Y_AXIS));
		

		//Get current DB list and select current DB
		JComboBox<String> dbList=new JComboBox<String>(db.listDatabase());
		JButton accept=new JButton("Select DB");
		accept.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {
			db.selectDatabase(String.valueOf(dbList.getSelectedItem()));
			io.setCurrentDatabase(String.valueOf(dbList.getSelectedItem()));
			columnNames=db.getColumnDatabase();
			createMainWindow();
			
			//Clean up this window
			dbSelectFrame.setVisible(false);
			dbSelectFrame.dispose();
		}
		});
		
		
		JTextField nameBox=new JTextField("DB Name");
		
		
		//Perform File Selection and create a new DB
		JButton addDB = new JButton("New DB");
		addDB.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {
			//check for invalid DB Names
			if (nameBox.getText()==null||nameBox.getText().equals("")||nameBox.getText().equals("DB Name")||!nameBox.getText().matches("[A-Za-z][A-Za-z0-9]+")) {
				nameBox.setBackground(Color.RED);
				return;
			}
			//File Selection
			File csvFile;
			final JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(dbSelectFrame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
		     	 csvFile = fc.getSelectedFile();
			}
			else {
				return;
			}
			
			//Parse File and set DB Names
			io.parseFile(csvFile, nameBox.getText());
			db.selectDatabase(io.getCurrentDatabase());
			columnNames=db.getColumnDatabase();
			createMainWindow();
			
			//Clean up this window
			dbSelectFrame.setVisible(false);
			dbSelectFrame.dispose();
		}
		});
		
		//add Objects to GUI
		dbSelectionPanel.add(dbList);
		dbSelectionPanel.add(accept);
		dbSelectionPanel.add(addDB);
		dbSelectionPanel.add(nameBox);
		dbSelectFrame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//Execute Search
	private void executeSearch() {
		//Prep Search Type for Search Items
		JFrame displayFrame = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		displayFrame.setSize(screenSize.height/2,screenSize.width/2);
		
		//Generate Data and Send to Tabular View
		if (String.valueOf(searchSelectorBox.getSelectedItem()).equals("Tabular")) {
			SearchOptions.get(0).prepare();
			
			//execute the search
			TabularView chart = new TabularView(displayFrame, db.getValuesDatabase(SearchOptions.get(0).getSQLParameters()), db.getColumnDatabase());
			//saveSearch();
			return;
		}
		if (String.valueOf(searchSelectorBox.getSelectedItem()).equals("Histogram")) {
			SearchOptions.get(0).prepare();
			double [] values = {1,2,3,4,5,6,7,8,9};
			ScatterChartView chart = new ScatterChartView(displayFrame, db.getValuesDatabase(SearchOptions.get(0).getColumn(), SearchOptions.get(0).getSQLParameters()), SearchOptions.get(0).getColumn());
			//saveSearch();
			return;
		}
		if (String.valueOf(searchSelectorBox.getSelectedItem()).equals("Scatter Plot")) {
			
			//saveSearch();
			return;
		}
		
	
		
	}
	/*public void saveSearch() {
		Random rand = new Random();
		int id=rand.nextInt();
		io.saveSearch(io.getCurrentDatabase(), SearchOptions.getSearchName()+","+id, SearchOptions,  id);
	}
	public void loadSearch(String searchName) {
		String[] splitStr = searchName.split(",");
		SearchOptions=io.loadSearch(Integer.parseInt(splitStr[splitStr.length-1]));
		searchSelectorBox.setSelectedItem(SearchOptions.get(0).searchType);
		buildGUI();
	}*/
}
