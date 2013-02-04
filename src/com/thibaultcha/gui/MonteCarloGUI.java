package com.thibaultcha.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.thibaultcha.montecarlo.MonteCarlo;
import com.thibaultcha.montecarlo.singlethread.MonteCarloSingleThread;


public class MonteCarloGUI extends JFrame implements ActionListener, Runnable, KeyListener
{	
	private static final long serialVersionUID = -8703843689450789725L;
	private MonteCarlo monteCarlo; // Model
	private Thread t;
	private JButton startButton;
	private JPanel westPanel;
	private JComboBox select;
	private JTextArea resultArea;
	private JTextField callPutFlagField, sField, xField, tField, rField, bField, vField, nStepsField, nSimulationsField;
	
	/**
	 * Constructor, take a model in parameter
	 * @param monteCarlo
	 * @throws IOException
	 */
	public MonteCarloGUI(MonteCarlo monteCarlo) throws IOException
	{
		super();
		// Model
		this.monteCarlo = monteCarlo;
		// View
		String title;
		if (monteCarlo.getClass().equals(MonteCarloSingleThread.class))
			title = "Monte Carlo Single Thread";
		else
			title = "Monte Carlo Multi Thread";
		this.setTitle(title);
		this.setBoxes((String) select.getSelectedItem());
		this.setSize(600, 400);
		this.setResizable(false);
		// Center window
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
	    this.setLocation(x, y);
	    
	    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	@Override
	protected void frameInit()
	{
		super.frameInit();
		JPanel p = new JPanel();
	    p.setLayout(new BorderLayout());

	    // NORTH
		JPanel northPanel = new JPanel();
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		northPanel.add(startButton);
		try {
			select = new JComboBox(this.readFromFile("values.txt").toArray());
			select.addActionListener(this);
			northPanel.add(select);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// CENTER
		JPanel centerPanel = new JPanel(new BorderLayout());
		resultArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(resultArea);
		centerPanel.add(scrollPane);
		resultArea.setEditable(false);
		resultArea.setBackground(centerPanel.getBackground());
		scrollPane.setBorder(BorderFactory.createLineBorder(centerPanel.getBackground()));
		
		// WEST
		GridLayout experimentLayout = new GridLayout(9,2);
		westPanel = new JPanel(experimentLayout);
		westPanel.setBorder(BorderFactory.createLineBorder(westPanel.getBackground()));
		JLabel callLabel = new JLabel("callPutFlagField"); westPanel.add(callLabel);
		callPutFlagField = new JTextField(); westPanel.add(callPutFlagField); callPutFlagField.addKeyListener(this);
		JLabel sLabel = new JLabel("S"); westPanel.add(sLabel);
		sField = new JTextField(); westPanel.add(sField); sField.addKeyListener(this);
		JLabel xLabel = new JLabel("X"); westPanel.add(xLabel);
		xField = new JTextField(); westPanel.add(xField); xField.addKeyListener(this);
		JLabel tLabel = new JLabel("T"); westPanel.add(tLabel);
		tField = new JTextField(); westPanel.add(tField); tField.addKeyListener(this);
		JLabel rLabel = new JLabel("R"); westPanel.add(rLabel);
		rField = new JTextField(); westPanel.add(rField); rField.addKeyListener(this);
		JLabel bLabel = new JLabel("B"); westPanel.add(bLabel);
		bField = new JTextField(); westPanel.add(bField); bField.addKeyListener(this);
		JLabel vLabel = new JLabel("V"); westPanel.add(vLabel);
		vField = new JTextField(); westPanel.add(vField); vField.addKeyListener(this);
		JLabel nStepsLabel = new JLabel("Steps"); westPanel.add(nStepsLabel);
		nStepsField = new JTextField(); westPanel.add(nStepsField); nStepsField.addKeyListener(this);
		JLabel nSimulationsLabel = new JLabel("Simulations"); westPanel.add(nSimulationsLabel);
		nSimulationsField = new JTextField(); westPanel.add(nSimulationsField); nSimulationsField.addKeyListener(this);
		
		// SOUTH
		JPanel southPanel = new JPanel();
		JTextField creditsField = new JTextField();
		creditsField.setEnabled(false);
		creditsField.setBorder(BorderFactory.createLineBorder(southPanel.getBackground()));
		creditsField.setBackground(southPanel.getBackground());
		creditsField.setText("Thibault Charbonnier - 2013");
		southPanel.add(creditsField);
		
		// BUILD VIEW
		p.add(northPanel, BorderLayout.NORTH);
		p.add(westPanel, BorderLayout.WEST);
		p.add(centerPanel, BorderLayout.CENTER);
		p.add(southPanel, BorderLayout.SOUTH);
		getContentPane().add(p);
	}
	
	/**
	 * Listener, used as a controller
	 */
	public void actionPerformed(ActionEvent event) {	
		if (event.getActionCommand().equals("comboBoxChanged")) {
			this.setBoxes((String) select.getSelectedItem());
		} else if (event.getActionCommand().equals("Start")) {			
			t = new Thread(this);
			t.start();
		}
	}
	
	/**
	 * Run the Monte Carlo algorithm in a parallel thread  
	 */
	@Override
	public void run()
	{
		startButton.setEnabled(false);
		try {
			long start = System.nanoTime();
			double premium = monteCarlo.runMonteCarlo(callPutFlagField.getText(),
					Double.parseDouble(sField.getText()),
					Double.parseDouble(xField.getText()),
					Double.parseDouble(tField.getText()),
					Double.parseDouble(rField.getText()),
					Double.parseDouble(bField.getText()),
					Double.parseDouble(vField.getText()),
					Integer.parseInt(nStepsField.getText()),
					Integer.parseInt(nSimulationsField.getText()));
			long end = System.nanoTime();
			resultArea.append("Price = " + premium + "\n");   
			resultArea.append("calculated in " + (end - start)/1.0e9 + " seconds\n\n");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		startButton.setEnabled(true);
	}
	
	/**
	 * Read from values.txt to have parameters to run the Monte Carlo
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public ArrayList<String> readFromFile(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
	    ArrayList<String> retour = new ArrayList<String>();
	    String line = null;
	    while((line = reader.readLine()) != null) 
	    	retour.add(line);
	    
	    reader.close();
	    return retour;    
	}

	/**
	 * Set JTextFields from values in JComboBox (the select built by the file values.txt)
	 * @param params
	 */
	public void setBoxes(String params) {
		final String[] paramsArray = params.split(",");
		int i=0;
		for (Component c : this.westPanel.getComponents()) {
		    if (c instanceof JTextField) {
		        ((JTextField) c).setText(paramsArray[i]);
		    	i++;
		    }
		}
	}
	
	/**
	 * Override from KeyListener Implementation, used to check each field to verify user's inputs
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int i=0;
		for (Component c : this.westPanel.getComponents()) {
		    if (c instanceof JTextField) {
		    	JTextField field = (JTextField) c;
		    	if (field.equals(callPutFlagField)) { // callPutFlag must be 'p' or 'c'
		    		if (!field.getText().equals("c") && !field.getText().equals("p")) {
		    			field.setBackground(Color.RED);
		    			startButton.setEnabled(false);
		    		} else {
		    			field.setBackground(Color.WHITE);
		    			startButton.setEnabled(true);
		    		}
		    	} else if (i > 0 && i < 7) { // S, X  T, R, B, V  must be double
		    		if (Pattern.matches(fpRegex, field.getText())) {	
		    			field.setBackground(Color.WHITE);
		    			startButton.setEnabled(true);
		    		} else {
		    			field.setBackground(Color.RED);
		    			startButton.setEnabled(false);
		  	        }
		    	} else { // Steps, Simulation must be Integer
		    		if (isInteger(field.getText())) {
		    			field.setBackground(Color.WHITE);
		    			startButton.setEnabled(true);
		    		} else {
		    			field.setBackground(Color.RED);
		    			startButton.setEnabled(false);
		    		}
		    	}
		    	i++;
		    }
		}
	}
	
	/**
	 * Check if a string can be parsed as an Integer for GUI JTextField
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	/**
	 * From JavaDoc to test if pattern can be parsed as a double for GUI JTextField
	 * http://docs.oracle.com/javase/6/docs/api/java/lang/Double.html#valueOf%28java.lang.String%29
	 */
	final String Digits     = "(\\p{Digit}+)";
	  final String HexDigits  = "(\\p{XDigit}+)";
	        // an exponent is 'e' or 'E' followed by an optionally 
	        // signed decimal integer.
	        final String Exp        = "[eE][+-]?"+Digits;
	        final String fpRegex    =
	            ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
	             "[+-]?(" + // Optional sign character
	             "NaN|" +           // "NaN" string
	             "Infinity|" +      // "Infinity" string

	             // A decimal floating-point string representing a finite positive
	             // number without a leading sign has at most five basic pieces:
	             // Digits . Digits ExponentPart FloatTypeSuffix
	             // 
	             // Since this method allows integer-only strings as input
	             // in addition to strings of floating-point literals, the
	             // two sub-patterns below are simplifications of the grammar
	             // productions from the Java Language Specification, 2nd 
	             // edition, section 3.10.2.

	             // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
	             "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

	             // . Digits ExponentPart_opt FloatTypeSuffix_opt
	             "(\\.("+Digits+")("+Exp+")?)|"+

	       // Hexadecimal strings
	       "((" +
	        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
	        "(0[xX]" + HexDigits + "(\\.)?)|" +

	        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
	        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

	        ")[pP][+-]?" + Digits + "))" +
	             "[fFdD]?))" +
	             "[\\x00-\\x20]*");// Optional trailing "whitespace"

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
