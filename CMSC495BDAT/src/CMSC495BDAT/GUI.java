package CMSC495BDAT;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

import GUIObjects.SearchType;
import javafx.stage.FileChooser;

public class GUI extends JFrame implements ActionListener{
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem loadDB;
	
	private Vector <SearchType> searchTypes = new Vector<SearchType>();
	private String[] types = {"Tabular"};  //Types of displays
	private JComboBox<String> searchSelectorBox=new JComboBox<String>(types);
	private JPanel searchPanel=new JPanel();
	private JButton searchButton= new JButton("Search");
	private InputOutput io = new InputOutput();
	private String[] columnNames;
	private SqlDatabase db = new SqlDatabase();
	public GUI() {
		//startup();
		
		//Create Window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Set Window Size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.height/2,screenSize.width/3);
		
		//Add menu
		menuBar=new JMenuBar();
		menu=new JMenu("File");
		loadDB=new JMenuItem("Load DB");
		
		
		//add Panels
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
		
		searchPanel.setLayout(new GridLayout(6, 1));
		searchPanel.add(searchSelectorBox);
		//test data
		String[] testSrings = {"Apple", "Bunny", "Coyote"};
		for (int i=0; i<1; i++) {
			searchTypes.add(new SearchType(testSrings, 0, "Nowhere"));
		}
		for (int i=0; i<searchTypes.size(); i++) {
			searchPanel.add(searchTypes.get(i));
		}
		
		searchPanel.add(searchButton);
		//execute the Search
		searchButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) {
				executeSearch();
			}

			
		});
		setVisible(true);

	}
	//pre GUI commands
	private void startup() {
		//check for previously used DB and load
		if (io.getCurrentDatabase()==null) {
			db.selectDatabase(io.getCurrentDatabase());
		}
		else {
			dbSelectionMenu();
		}
	}
	//create menu to select or create DB
	private void dbSelectionMenu() {
		JFrame dbSelectFrame = new JFrame();
		JPanel dbSelectionPanel=new JPanel();
		dbSelectFrame.add(dbSelectionPanel);
		dbSelectionPanel.setLayout(new BoxLayout(dbSelectionPanel, BoxLayout.Y_AXIS));
		
		//Get current DB list and select current DB
		JComboBox dbList=new JComboBox(db.listDatabase());
		JButton accept=new JButton("Select DB");
		accept.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {
	    db.selectDatabase(String.valueOf(dbList.getSelectedItem()));
	    io.setCurrentDatabase(String.valueOf(dbList.getSelectedItem()));
	    columnNames=db.getColumnDatabase();
		}
		});
		
		
		JTextField nameBox=new JTextField("DB Name");
		
		
		//Perform File Selection and create a new DB
		JButton addDB = new JButton("New DB");
		addDB.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent arg0) {
			//check for invalid DB Names
			if (nameBox.getText()==null||nameBox.getText().equals("")||nameBox.getText().equals("DB Name")||nameBox.getText().matches("[A-Za-z][A-Za-z0-9]+"))
				return;
			
			//File Selection
			File csvFile;
			File DBName;
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
			columnNames=db.getColumnDatabase();
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
			searchTypes.get(0).prepare();
			
			//Convert Search arraylist to double array
			ArrayList <ArrayList <Double>> tmpVector = db.exportDatabase();
			double[][] dataList= new double[tmpVector.size()][tmpVector.get(0).size()];
			for (int i=0; i<tmpVector.size(); i++) {
				double[] tmp1DArray = new double[tmpVector.get(0).size()];
				for (int j=0; j<tmpVector.get(0).size(); j++) {
					tmp1DArray[j]=tmpVector.get(i).get(j).doubleValue();
				}
				dataList[i]= tmp1DArray;
			}
			//execute the search
			TabularView chart = new TabularView(displayFrame, dataList, db.getColumnDatabase());
		}
		//Prep Search Discriminators
		//Select Search Type
		//Execute Search
		//Send to Display Layer
		
	}
	
}
