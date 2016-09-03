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
 * Main GUI frame built using Swing.
 * TODO: only display words that have been attempted!!!
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
	private TableModelAdapter _tableModelAdapter;

	private SpellingStatsModel _data;

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
		_txtArea.setEditable(false);/*
		 DefaultCaret caret = (DefaultCaret) _txtArea.getCaret();
		 caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);*/
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


	/**
	 * When "Enter" key or _enterButton is pressed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String currentText = _enterTxtField.getText();

		_txtArea.append(currentText+"\n");

		if(_data.testMode()){
			String message = _data.testLogic(currentText);
			_txtArea.append(message);
			if(!_data.testMode()){//exist spelling mode
				_newButton.setEnabled(true);
				_reviewButton.setEnabled(true);
				_showStatsButton.setEnabled(true);
				_clearStatsButton.setEnabled(true);
			}
		}
		_txtArea.setCaretPosition(_txtArea.getDocument().getLength());
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
				_txtArea.append("===============START=================\n");
				_txtArea.append("Starting new spelling quizz...\n");
				_data.startNewTest();
				String message = _data.testLogic("");
				_txtArea.append(message);
				_newButton.setEnabled(false);
				_reviewButton.setEnabled(false);
				_showStatsButton.setEnabled(false);
				_clearStatsButton.setEnabled(false);
			}
		});

		_reviewButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				_txtArea.append("===============START=================\n");
				_txtArea.append("Now reviewing mistakes...\n");
				_data.startReviewTest();
				String message = _data.testLogic("");
				_txtArea.append(message);
				if(_data.testMode()){//exist spelling mode
					_newButton.setEnabled(false);
					_reviewButton.setEnabled(false);
					_showStatsButton.setEnabled(false);
					_clearStatsButton.setEnabled(false);
				}
			}			
		});

		/**
		 * Spelling Stats Model is sorted alphabetically before opening the JTable
		 * view of the statistics.
		 */
		_showStatsButton.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				_data.sortAlphabetically();
				displayStatsTable();
			}
		});

		/**
		 * clears the failed list and resets all Mastered, Faulted, and Failed to 0
		 */
		_clearStatsButton.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				clearHistory();
			}
		});

		/**
		 * Show hint, which should be cleared when JTextField is put into focus.
		 */
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
	 * Check if user wants to delete history before deleting. Display a message once successfully cleared.
	 */
	public void clearHistory(){
		int dialogResult = JOptionPane.showConfirmDialog (this, "Are you sure you want to clear your history?","Warning",JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){
			_data.resetStats();
			JOptionPane.showMessageDialog(this, "Statistics successfullly cleared.");
		}
	}

	/**
	 * Creates a new pop-up frame to display a table view of current user's spelling statistics.
	 * Table adapter is remade when "Show Stats" button is pressed to update table.
	 */
	public void displayStatsTable(){
		_tableModelAdapter = new TableModelAdapter(_data);
		if(_statsFrame != null){ // close old stats window
			_statsFrame.dispose();
		}
		_statsFrame = new JFrame("Spelling Statistics");
		_wordStatsTable = new JTable(_tableModelAdapter);
		JScrollPane scrollPaneForTable = new JScrollPane(_wordStatsTable);
		scrollPaneForTable.setPreferredSize(new Dimension(300,150));
		_statsFrame.getContentPane().add(scrollPaneForTable, BorderLayout.CENTER);
		_statsFrame.setSize(600, 300);
		_statsFrame.setMinimumSize(new Dimension(300,200));
		_statsFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		_statsFrame.setVisible(true);
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
	}

	/**
	 * Saves the SpellingStatsModel object instance to a hidden .ser file
	 * Called when main GUI frame is closed.
	 */
	private void writeData(){
		if(!_hiddenFile.exists()){ //create hidden .ser file if it does not exist
			try {
				_hiddenFile.createNewFile();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
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
			JOptionPane.showMessageDialog(this, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	



}
