/*Gui
 * By Isaac Blach
 * Creates Core GUI and calls other classes to perform various functions
 */


package CMSC495BDAT;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import GUIObjects.ComboItem;
import GUIObjects.ComboItemDualString;
import GUIObjects.SearchOption;

public class GUI extends JFrame{
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem loadDB;
	//Create GUI objects
	private GridBagConstraints c = new GridBagConstraints();
	private Vector <SearchOption> searchOptions = new Vector<SearchOption>();
	//Combo Item Types Key
	//0 = 0 column or all column displays
	//1 = Single axis displays (eg histograms)
	//2 = Dual axis displays (eg Scatterplots)
	//3 = Triple axis displays
	private ComboItem[] types = {new ComboItem("Tabular", 0), new ComboItem("Histogram", 1), new ComboItem("Scatter Plot", 2)}; 
	private JComboBox<ComboItem> searchSelectorBox=new JComboBox<ComboItem>(types);
	private JPanel searchPanel=new JPanel();
	private JButton searchButton= new JButton("Search");
	private JComboBox<ComboItem> searchHistoryComboBox = new JComboBox<ComboItem>();
	
	private InputOutput io = new InputOutput();
	private ComboItemDualString[] columnNames;
	private SqlDatabase db = new SqlDatabase();
	private Vector<ComboItem> searchHistoryVector= new Vector<ComboItem>();
	
	public GUI() {
		searchOptions = new Vector<SearchOption>();
		startup();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	//Create main window.  Separated into it's own class to 
	private void createMainWindow() {
		setVisible(false);
		remove(searchPanel);
		//Reset Everything
		searchPanel=new JPanel();
	
		//Set Window Size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.height/2,screenSize.width/3);
		
		//Add menu
		menuBar=new JMenuBar();
		menu=new JMenu("File");
		loadDB=new JMenuItem("Load DB");
		
		
		//add Panels
		searchPanel.setLayout(new GridBagLayout());
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
		JMenuItem exportSearch = new JMenuItem("Export Data");
		exportSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(searchPanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					io.exportDB(db.getValuesDatabase(searchOptions.get(0).getSQLParameters()), fc.getSelectedFile());
				}
				else {
					return;
				}
				
			}
		});
		menu.add(exportSearch);
		
		//Create the Search Search Panel Layout
		
		c.anchor=GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.weightx=1;
		c.gridx=0;
		c.gridy=0;
		searchPanel.add(searchSelectorBox,c);
		searchSelectorBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (int i=0; i<searchOptions.size(); i++)
					searchOptions.get(i).setSearchType((ComboItem) searchSelectorBox.getSelectedItem());
			}
		});
		
		
		
		//Add Search Grid
		searchOptions.add(new SearchOption(columnNames, 0, "nowhere", (ComboItem)searchSelectorBox.getSelectedItem()));
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx=1;
		c.gridx=0;
		c.gridy=GridBagConstraints.RELATIVE;
		searchPanel.add(searchOptions.get(0),c);
		
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
		
		//Get current search history
		if (io.getSearchHistory()!=null) {
			searchHistoryVector=io.getSearchHistory();
			searchHistoryComboBox = new JComboBox<ComboItem>(searchHistoryVector);
		}
		else {
			searchHistoryVector=new Vector<ComboItem>();
		}
		//Search History Box
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx=GridBagConstraints.RELATIVE;
		c.weightx=1;
		c.gridx=0;
		c.ipady=20;
		c.gridwidth=1;
		searchPanel.add(searchHistoryComboBox,c);
		
		
		
		//Load Search Button
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx=GridBagConstraints.RELATIVE;
		c.weightx=1;
		c.gridx=0;
		c.ipady=20;
		c.gridwidth=1;
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadSearch((ComboItem)searchHistoryComboBox.getSelectedItem());		
			}
		});
		searchPanel.add(loadButton,c);
		revalidate();
		repaint();
		setVisible(true);

	}
	//pre GUI commands
	private void startup() {
		//check for previously used DB and load
		if (!(io.getCurrentDatabase()==null)) {
			db.selectDatabase(io.getCurrentDatabase());
			columnNames=io.loadColumnNames(io.getCurrentDatabase());
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
			columnNames=io.loadColumnNames(io.getCurrentDatabase());
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
			columnNames=io.loadColumnNames(io.getCurrentDatabase());
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

	
	//Execute Search
	private void executeSearch() {
		//Prep Search Type for Search Items
		JFrame displayFrame = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		displayFrame.setSize(screenSize.height/2,screenSize.width/2);
		//Save the search String
		searchOptions.get(0).prepare();
		saveSearch();
		//Generate Data and Send to Tabular View
		if (String.valueOf(searchSelectorBox.getSelectedItem()).equals("Tabular")) {
			TabularView chart = new TabularView(displayFrame, db.getValuesDatabase(searchOptions.get(0).getSQLParameters()), db.getColumnDatabase());
			return;
		}
		//
		if (String.valueOf(searchSelectorBox.getSelectedItem()).equals("Histogram")) {
			HistogramChartView chart = new HistogramChartView(displayFrame, db.getValuesDatabase(searchOptions.get(0).getColumn(), searchOptions.get(0).getSQLParameters()), searchOptions.get(0));
			return;
		}
		if (String.valueOf(searchSelectorBox.getSelectedItem()).equals("Scatter Plot")) {
			ScatterChartView chart = new ScatterChartView(displayFrame, db.getValuesDatabase(searchOptions.get(0).getColumn(), searchOptions.get(0).getSQLParameters()), searchOptions.get(0).getColumn(), db.getValuesDatabase(searchOptions.get(0).getColumn2(), searchOptions.get(0).getSQLParameters()), searchOptions.get(0).getColumn2());
			//ScatterChartView chart = new ScatterChartView(displayFrame, db.getValuesDatabase(searchOptions.get(0).getColumn(), searchOptions.get(0).getSQLParameters()), db.getValuesDatabase(searchOptions.get(0).getColumn2(), searchOptions.get(0).getSQLParameters()), searchOptions.get(0));
			return;
		}
		
	
		
	}
	//Save Search 
	private void saveSearch() {
		//Generate search ID
		Random rand = new Random();
		int id=rand.nextInt();
		id = (id<0) ? id*-1 : id;
		//Control the size of the search
		if (searchHistoryVector.size()>9){
			io.removeSearch(searchHistoryVector.get(0).getKey());
			searchHistoryComboBox.remove(0);
			searchHistoryVector.remove(0);
		}
		searchHistoryVector.add(new ComboItem(searchOptions.get(0).createSearchString(), id));
		searchHistoryComboBox.addItem(searchHistoryVector.get(searchHistoryVector.size()-1));
		io.saveSearch(searchOptions, id);
		io.updateSearchHistory(searchHistoryVector);
		
	}
	
	//Load Search from history
	private void loadSearch(ComboItem searchItem) {
		searchOptions=io.loadSearch(io.getCurrentDatabase(), searchItem.getKey());
		searchSelectorBox.setSelectedItem(searchOptions.get(0).getSearchType());
		createMainWindow();
	}
	
	
}
