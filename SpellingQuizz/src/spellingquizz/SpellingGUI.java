package spellingquizz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;


/**
 * 
 * @author Yuliang Zhou yzho746
 *
 */
public class SpellingGUI extends JFrame implements ActionListener{

	private JButton _newButton;
	private JButton _reviewButton;
	private JButton _clearStatsButton;
	private JButton _showStatsButton;
	private JButton _enterButton;

	private JTextArea _txtArea;
	private JTextField _enterTxtField;
	private JScrollPane _scroll;
	private JTable _wordStatsTable;

	private JFrame _statsFrame;

	private SpellingStatsModel _data;
	private TableModelAdapter _tableModelAdapter;

	private File _hiddenFile = new File(".spellingData.ser");
	private File _wordListFile = new File("wordlist");

	public SpellingGUI(){
		super("Spelling Quizz");
		setUpGUI();
		openData();
		setUpEventListeners();
	}

	/**
	 * Set up layout of main GUI frame
	 */
	private void setUpGUI(){
		//TOP: options panel for buttons
		JPanel optionsPanel = new JPanel(new GridLayout(2,2,10,10));
		optionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		_newButton = new JButton("New Quizz!");
		_reviewButton = new JButton("Review Mistakes!");
		_clearStatsButton = new JButton("Clear Stats!");
		_showStatsButton = new JButton("Show Stats!");
		optionsPanel.add(_newButton);
		optionsPanel.add(_reviewButton);
		optionsPanel.add(_clearStatsButton);
		optionsPanel.add(_showStatsButton);
		//MIDDLE: main text area
		JPanel display = new JPanel(new BorderLayout());
		display.setBorder(new EmptyBorder(10,10,10,10));
		_txtArea = new JTextArea("Welcome to the super awesome spelling quizz!\n",20,5);
		_txtArea.setEditable(false);
		_scroll = new JScrollPane(_txtArea);
		_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		display.add(_scroll,BorderLayout.CENTER);
		//BOTTOM: text field for entering answers
		JPanel bottom = new JPanel(new BorderLayout());
		_enterTxtField = new JTextField("Enter word here...");
		_enterButton = new JButton("Enter");
		bottom.add(_enterTxtField,BorderLayout.CENTER);
		bottom.add(_enterButton, BorderLayout.EAST);
		bottom.setBorder(new EmptyBorder(10,10,10,10));

		//Add TOP MIDDLE and BOTTOM section to frame
		getContentPane().add(optionsPanel,BorderLayout.NORTH);
		getContentPane().add(display, BorderLayout.CENTER);
		getContentPane().add(bottom,BorderLayout.SOUTH);

		setSize(400,300);
		setMinimumSize(new Dimension(350,250));
		//setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.out.println("Closed");
				writeData();
				e.getWindow().dispose();
				if(_statsFrame != null){
					_statsFrame.dispose();
				}
			}
		});

	}


	public static void main(String[] agrs){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SpellingGUI frame = new SpellingGUI();
				frame.setVisible(true);
			}
		});
	}


	//When "Enter" key or _enterButton is pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		String currentText = _enterTxtField.getText();

		
		//reset TextField
		_enterTxtField.setText("");
	}

	/**
	 * Set up listeners to all buttons and JTextField
	 */
	private void setUpEventListeners() {
		_newButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newTest();
			}
		});

		_reviewButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				festivalRead("Please spell dynamite.");
			}			
		});

		_showStatsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				_data.sortAlphabetically();
				displayStatsTable();
			}
		});

		_clearStatsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				_data.resetStats();
				showMessage("Statistics successfullly cleared.");
			}
		});

		_enterTxtField.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(_enterTxtField.getText().equals("Enter word here...")){
					_enterTxtField.setText("");
				}

			}
		});

		_enterTxtField.addActionListener(this);

		_enterButton.addActionListener(this);
	}


	/**
	 * 
	 */
	public void newTest(){
		String[] testWords = _data.getThreeRandomWords();
		for(int i=0;i<3;i++){
			festivalRead(testWords[i]);
			//wait for input
			
		}
		
	}

	/**
	 * Uses festival to read a message to the user.
	 */
	public void festivalRead(String message){
		String cmd = "echo " + message + " | festival --tts";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c",cmd);
		try {
			Process process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new pop-up frame to display a table view of current user's spelling statistics.
	 */
	public void displayStatsTable(){
		_tableModelAdapter = new TableModelAdapter(_data);
		if(_statsFrame != null){
			_statsFrame.dispose();
		}
		_statsFrame = new JFrame("Spelling Statistics");
		_wordStatsTable = new JTable(_tableModelAdapter);
		JScrollPane scrollPaneForTable = new JScrollPane(_wordStatsTable);
		scrollPaneForTable.setPreferredSize(new Dimension(300,150));
		_statsFrame.getContentPane().add(scrollPaneForTable, BorderLayout.CENTER);
		_statsFrame.setSize(400, 300);
		_statsFrame.setMinimumSize(new Dimension(300,200));
		_statsFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		_statsFrame.setVisible(true);
	}

	/**
	 * Reads each line of "wordlist" file and adds any new words to current word list object in SpellingStatsModel
	 */
	public void updateWordList(){
		try {
			FileReader fr = new FileReader(_wordListFile);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine())!=null){
				_data.addNewWord(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			showMessage(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Upon creating the GUI frame open the saved data if it exists. 
	 * Otherwise create a new object.
	 * Only called in constructor of main GUI frame
	 */
	private void openData() {
		if(_hiddenFile.exists()){
			try {
				FileInputStream streamIn = new FileInputStream(".spellingData.ser");
				ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
				_data = (SpellingStatsModel) objectinputstream.readObject();
				streamIn.close();
				objectinputstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else{//if serialized data does not exist then create a new object
			_data = new SpellingStatsModel();
			System.out.println("new object created");
		}
		updateWordList();
		System.out.println("testCounter READ: " + _data.testCounter);
	}

	/**
	 * Saves the SpellingStatsModel object instance to a hidden .ser file
	 * Called when main GUI frame is closed.
	 */
	private void writeData(){
		System.out.println("testCounter WRITE: " + _data.testCounter);
		if(!_hiddenFile.exists()){ //create hidden .ser file if it does not exist
			try {
				_hiddenFile.createNewFile();
			} catch (IOException e) {
				showMessage(e.getMessage());
			}
		}
		//write SpellingStatsModel object instance's data to the hidden file
		try {
			FileOutputStream fout = new FileOutputStream(".spellingData.ser", false);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(_data);
			fout.close();
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Display a pop-up message
	 * @param message
	 */
	public void showMessage(String message){
		JOptionPane.showMessageDialog(this, message);
	}







}
