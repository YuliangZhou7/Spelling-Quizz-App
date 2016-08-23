import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;


public class SpellingGUI extends JFrame implements ActionListener{

	private JButton _newButton;
	private JButton _reviewButton;
	private JButton _clearStatsButton;
	private JButton _showStatsButton;
	private JButton _enterButton;
	
	private JTextArea _txtArea;
	private JTextField _enterTxt;
	private JScrollPane _scroll;

	public SpellingGUI(){
		super("Spelling Quizz");
		setUp();
		addListeners();
	}

	private void addListeners() {
		_newButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		_reviewButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}			
		});
		
		_showStatsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		_clearStatsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		_enterTxt.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                _enterTxt.setText("");
            }
        });

		
		_enterTxt.addActionListener(this);
		
		_enterButton.addActionListener(this);
	}

	public void setUp(){
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

		JPanel display = new JPanel(new BorderLayout());
		display.setBorder(new EmptyBorder(10,10,10,10));
		_txtArea = new JTextArea("Welcome to the super awesome spelling quizz!\n",20,5);
		_scroll = new JScrollPane(_txtArea);
	    _scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_txtArea.setEditable(false);
		_enterTxt = new JTextField("Enter word here");
		
		JPanel bottom = new JPanel(new BorderLayout());
		_enterButton = new JButton("Enter");
		bottom.add(_enterTxt,BorderLayout.CENTER);
		bottom.add(_enterButton, BorderLayout.EAST);
		bottom.setBorder(new EmptyBorder(10,10,10,10));

		display.add(_txtArea,BorderLayout.CENTER);
		display.add(_scroll,BorderLayout.EAST);
		
		getContentPane().add(optionsPanel,BorderLayout.NORTH);
		getContentPane().add(display, BorderLayout.CENTER);
		getContentPane().add(bottom,BorderLayout.SOUTH);
		
		setSize(400,300);
		setMinimumSize(new Dimension(320,250));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

	
	//When "Enter" or _enterButton is pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		_txtArea.append(_enterTxt.getText()+"\n");
		_enterTxt.setText("");
	}



}
