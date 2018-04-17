import javax.swing.JOptionPane;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.Dialog.ModalExclusionType;

@SuppressWarnings("serial")
public class ConvertGUI extends JFrame {

	private JPanel contentPane;
	private JFileChooser chooser;
	private JButton selectInFileBtn;
	private JButton selectOutFileBtn;
	private JButton convertBtn;
	private JTextField input;
	private JTextField output;
	private JPanel panel;
	private JPanel panelUp;
	private JPanel panelDown;
	private LinearProblem lp;

	/**
	 * Create the frame.
	 */
	public ConvertGUI(){
		setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGap(0, 424, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGap(0, 251, Short.MAX_VALUE)
		);
		contentPane.setLayout(gl_contentPane);
		
		panel = new JPanel();
		panelUp = new JPanel();
		panelDown = new JPanel();
	    panelUp.setLayout(new GridLayout(2,2,10,0));
		selectInFileBtn = new JButton("Select");
		selectOutFileBtn = new JButton("Select");
		chooser = new JFileChooser();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		input = new JTextField("Input File Path...");
		output = new JTextField("Output Directory Path...");
		
		panelUp.add(input);
		panelUp.add(selectInFileBtn);
		panelUp.add(output);
		panelUp.add(selectOutFileBtn);
		panel.add(panelUp);
		panel.add(panelDown);
		panelDown.setLayout(new CardLayout(0, 0));
		convertBtn = new JButton("Convert");
		panelDown.add(convertBtn, "name_50649954272336");
		this.setContentPane(panel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		setTitle("Linear Problem Parser");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 364, 98);
		setResizable(false);
		
		convertBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e){	
				lp = new LinearProblem(readFile());
				Convert(lp);
			}
			
		});
		


		selectInFileBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
				chooser.setDialogTitle("Choose Input File");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//chooser.showOpenDialog(null);
				int returnVal = chooser.showOpenDialog(null);
		        if(returnVal == JFileChooser.APPROVE_OPTION) 
		        	input.setText(chooser.getSelectedFile().getAbsolutePath());
			}

		});
		
		selectOutFileBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
				chooser.setDialogTitle("Choose Output File");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//chooser.showOpenDialog(null);
				int returnVal = chooser.showOpenDialog(null);
		        if(returnVal == JFileChooser.APPROVE_OPTION) 
		        	output.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});
		
	}
	
	String readFile() {
		
		/* This method reads the problem from txt file and returns it to lowercase string */

		String lp="";
		File inFile = new File(input.getText());
        if (inFile.exists()){
            FileReader freader = null;
			try {
				freader = new FileReader(inFile);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Error in reading file! Check path & try again.", "Error!", JOptionPane.ERROR_MESSAGE);
			}
            BufferedReader reader = new BufferedReader(freader);
            String line="";
            try {
				line = reader.readLine();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error in reading file! Check path & try again.", "Error!", JOptionPane.ERROR_MESSAGE);
			}
            while (line != null){
            if (!line.trim().isEmpty()) {
                lp += line.trim();
                lp += "\n";
            }
                try {
					line = reader.readLine();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Error in reading file! Check path & try again.", "Error!", JOptionPane.ERROR_MESSAGE);
				}
            }
            try {
				reader.close();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error in reading file! Check path & try again.", "Error!", JOptionPane.ERROR_MESSAGE);
			}
        }

		return lp.toLowerCase().replaceAll(" ", "").replace("\t", "");
      
	}
	
	boolean inFileCheck(String inFile) {
		if (inFile == "") return false;
		return true;
	}
	
	int returnLineWithAllVariables(LinearProblem lp) {
		
		/* This method returns index of line, which includes all variables. */
		
		// Getting lines of lp in string array
		String[] lines = readFile().split("\n"); 
		
		// varCount = array for counting variables in each line
		Integer[] varCount = new Integer[lp.getM()+1];

				
				// Counting all variables & operators for each line
				for(int i=0; i<lp.getM()+1;i++) {
					for (int j=0;j<lines[i].length();j++) {
						if (Character.isLetter(lines[i].charAt(j))) {
							varCount[i]++;
						}
					}
					// We do not want to count other letters except the ones representing variables
					if (lines[i].contains("max") || lines[i].contains("min")) {
						// 3 = Length of string "max"/"min"
						varCount[i] -= 3; 
					}
					if (lines[i].contains("st")) {
						// 2 = Length of string "st"
						varCount[i] -= 2; 
					}
					
					if (lines[i].contains("s.t.")) {
						// 2 = Length of string "st" (. is NOT a letter, so Character.isLetter() function does not recognise '.' as letter
						varCount[i] -= 2; 
					}
					if (lines[i].contains("subject")) {
						// 7 = Length of string "subject"
						varCount[i] -= 7; 
					}
				}
				
				// Return index of max
				return Arrays.asList(varCount).indexOf(Collections.max(Arrays.asList(varCount)));
	}
	
	boolean typeOfProblemCheck(int minMax) {
		
		/* This method checks if max/min type entered correctly in txt file
		 * Returns false: If max/min type entered correctly.
		 * Returns true: If no errors found.
		 */
		
		// If type is wrong
		if (minMax == 0) {		
			JOptionPane.showMessageDialog(null, "Syntax error in function line!", "Error!", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		else return true;
	}
	
	boolean limitationCheck() {
		
		/* This method checks if st/s.t./subject entered correctly in txt file
		 * Returns false: If limitiation line needs modification.
		 * Returns true: If no errors found.
		 */
		
		// Getting lines of lp in string array
		String[] lines = readFile().split("\n");
				
		// Checking if second line beggins with st or s.t. or subject
		if (!lines[1].replaceAll("\\s+", "").substring(0, 2).equals("st") && !lines[1].replaceAll("\\s+", "").substring(0, 4).equals("s.t.") && !lines[1].replaceAll("\\s+", "").substring(0, 7).equals("subject")) {
			JOptionPane.showMessageDialog(null, "Syntax error! Define limitations!", "Error!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else return true;
	}
	
	boolean operatorCheck(LinearProblem lp) {
		
		/* This method checks if operators are put correctly in txt.
		 * Number of operators = Number of variables - 1 (for each line)
		 * Returns false: If math operators are put in a wrong way
		 * Returns true: If no errors found.
		 */
		
		// Getting lines of lp in string array
		String[] lines = readFile().split("\n");
		
		// varCount = array for counting variables in each line
		int[] varCount = new int[lp.getM()+1];

		// operatorCount = array for counting numeric operators
		int[] operatorCount = new int[lp.getM()+1];
		
		// Counting all variables & operators for each line
		for(int i=0; i<lp.getM()+1;i++) {
			for (int j=0;j<lines[i].length();j++) {
				if (Character.isLetter(lines[i].charAt(j))) {
					varCount[i]++;
				}
				if (lines[i].charAt(j) == '+' || lines[i].charAt(j) == '-') {
					operatorCount[i]++;
				}
			}
			// We do not want to count other letters except the ones representing variables
			if (lines[i].contains("max") || lines[i].contains("min")) {
				// 3 = Length of string "max"/"min"
				varCount[i] -= 3; 
			}
			if (lines[i].contains("st")) {
				// 2 = Length of string "st"
				varCount[i] -= 2; 
			}
			
			if (lines[i].contains("s.t.")) {
				// 2 = Length of string "st" (. is NOT a letter, so Character.isLetter() function does not recognise '.' as letter
				varCount[i] -= 2; 
			}
			if (lines[i].contains("subject")) {
				// 7 = Length of string "subject"
				varCount[i] -= 7; 
			}
	
		}
		// For each line
		for(int i=0;i<lp.getM();i++) {
			// number of variables >= number of operators
			if ((varCount[i] != operatorCount[i]+1) && (varCount[i] != operatorCount[i])) {
				return false;
			}
		}
		// If for loop doesn't break
		return true;
	}
	
	boolean mathSymbolCheck(LinearProblem lp) {
		
		/* This method checks if math symbols are put in correct way.
		 * Returns false: If math symbols are put in a wrong way
		 * Returns true: If no errors found.
		 */
		
		// Getting lines of lp in string array
		String[] lines = readFile().split("\n");
		// Deleting subject title at the beggining of sentence
		if(lines[1].contains("st")) {
			lines[1] = lines[1].replaceAll("st", "");
		}
		else if (lines[1].contains("s.t.")) {
			lines[1] = lines[1].replaceAll("s.t.", "");
		}
		else {
			lines[1] = lines[1].replaceAll("subject", "");
		}
		lines[1] = lines[1].trim();
		// If symbols '<' or '>' or '<=' or '>=' not found
		boolean found = false;
		// For each line except the first one
		for (int i=1;i<lp.getM()+1;i++) {
			found = false;
			// If line do not contain one of the symbols '<' '>' '<=' '>=', do nothing
			if (lines[i].contains("<") || lines[i].contains(">") || lines[i].contains("<=") || lines[i].contains(">=") || lines[i].contains("=") || lines[i].contains("==")) {
				// Start from the last variable found in line i
				for (int j=returnIndexOfLastVariable(lines[i])+1;j<lines[i].length();j++) {
					// If one of the symbols found
					if (found) {	

						// Check if there is something non-numeric after the math symbol
						if (!Character.isDigit(lines[i].charAt(j)) && lines[i].charAt(j) != '-' && lines[i].charAt(j) != '+'){
							System.out.print(lines[i].charAt(j));
							return false;
						}
						for (int y=j;y<lines[i].length();y++) {
						//	System.out.println(lines[i].charAt(y));
							if (!Character.isDigit(lines[i].charAt(y))){
								System.out.println("not char" +lines[i].charAt(y));
								return false;
							}
						}
						
						return true;
					}
					// If math symbol not found yet
					else {
						// If math symbol is found
						if (lines[i].charAt(j) == '<' || lines[i].charAt(j) == '>' || lines[i].charAt(j) == '=') {

							try {
								// If math symbol is '<=' or '>=', bypass the '='
								if (lines[i].charAt(j+1) == '=') {
									j++;
									found = true;
								}
								else {
									found = true;
								}
							} catch (Exception e1) {
								return false;
							}
						}
					}
				}
			}
			// If there is no math symbol at all, break
			else return false;
		}
		
		return true;
	}

int returnIndexOfLastVariable(String line) {
	
	/* This method returns the last variable (letter) of a string */
	
	// Keeping the index of last variable of each line
	int lastVar = 0;
	
	for (int j=0;j<line.length();j++) {
		if (Character.isLetter(line.charAt(j))) {
			lastVar = j;
		}
	}
	return lastVar;
}



boolean Check(LinearProblem lp) {
	
	/* This method makes all the checks needed in order to start conversion */
	
	// If input file failed to open
	if (!inFileCheck(readFile())) {
		JOptionPane.showMessageDialog(null, "Error in reading file! Check path & try again.", "Error!", JOptionPane.ERROR_MESSAGE);
		return false;
	}
	
	// If type of problem is correctly entered
	if (!typeOfProblemCheck(lp.getMinMax())) {
		JOptionPane.showMessageDialog(null, "Syntax Error! Determine the type!", "Error!", JOptionPane.ERROR_MESSAGE);
		return false;
	}
	
	// If limitation title is "st" or "s.t." or "subject"
	if (!limitationCheck()) {
		JOptionPane.showMessageDialog(null, "Syntax limitation error!", "Error!", JOptionPane.ERROR_MESSAGE);
		return false;
	}
	
	// If operators are correctly entered
	if (!operatorCheck(lp)) {
		JOptionPane.showMessageDialog(null, "Syntax Operator Error!", "Error!", JOptionPane.ERROR_MESSAGE);
		return false;
	}
	
	// If math symbols are correctly entered
	if (!mathSymbolCheck(lp)) {
		JOptionPane.showMessageDialog(null, "Syntax Math Symbol Error!", "Error!", JOptionPane.ERROR_MESSAGE);
		return false;
	}
	
	// If everything was ok, return true & start conversion
	return true;
}

void Convert(LinearProblem lp) {
	
	/* This method does the parsing & conversion */
	
	if(Check(lp)) {
		lp.writeFile(output.getText(), lp.getA(), lp.getC(), lp.getB(), lp.getMinMax(), lp.getEqin(), lp.getVariables(), lp.getM(), lp.getN());
	}
}

}