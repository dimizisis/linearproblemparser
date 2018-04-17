import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

import javax.swing.JOptionPane;

public class LinearProblem {
	
	private String lp;
	private int m;
	private int n;
	private int[][] A;
	private int[] c;
	private int[] b;
	private int minMax;
	private int[] eqin;
	private String function;
	private String[] variables;
	
	public LinearProblem(String lp) {
		this.lp = lp;
		this.function = this.returnFunction();
		this.m = this.returnM();
		this.n = this.returnN();
		this.A = this.returnA();
		this.c = this.returnC();
		this.b = this.returnB();
		this.minMax = this.returnProblemType();
		this.eqin = this.returnEqin();
		this.variables = this.returnVariables();
	}
	
	public String[] getVariables() {
		return variables;
	}

	public int getM() {
		return m;
	}

	public int getN() {
		return n;
	}

	public int[][] getA() {
		return A;
	}

	public int[] getC() {
		return c;
	}

	public int[] getB() {
		return b;
	}

	public int getMinMax() {
		return minMax;
	}

	public int[] getEqin() {
		return eqin;
	}

	public String getFunction() {
		return function;
	}

	/* Private Methods */ 
	
	private int returnM() {
		
		/* This method returns the number of problem's lines */
		
		// Getting lines of lp in string array
		String[] lines = lp.split("\n"); 
		
		// Number of text's lines
		int m = lines.length-1;
		
		return m;
	}
	
	private int returnN() {
		
		/* This method returns the number of variables used */
		
		// Getting lines of lp in string array
		String[] lines = lp.split("\n"); 
		
		// varCount = array for counting variables in each line
		Integer[] varCount = new Integer[this.getM()+1];
		
		// Initialize with zeros
		for(int i=0;i<varCount.length;i++)
			varCount[i] = 0;
		
		// Counting all variables & operators for each line
		for(int i=0; i<this.getM()+1;i++) {
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
		return Collections.max(Arrays.asList(varCount));
	}
	
	private int returnProblemType() {
		
		/* This method returns type of linear problem (-1 for min, 1 for max, 0 for error) */
		
		// First three letters is the type
		String type = this.getFunction().substring(0, 3);
		if (type.equals("max")) return 1;
		else if (type.equals("min")) return -1;
		else return 0; // 0 for error
 
	}
	
	private String returnFunction() {
		
		/* This method returns the function we want to minimize/maximaze */
		
		// Getting lines of lp in string array
		String[] lines = lp.split("\n"); 
		// The first line is the function
		String function = lines[0].replaceAll(" ","");
		return function;
	}

	private int[] returnB() {
	
	/* This method returns vector b (mx1 vector) */
	
	// The vector, which the function returns
	int b[] = new int[this.getM()];
	
	// Getting lines of lp in string array
	String[] lines = lp.split("\n");
			
	// If symbols '<' or '>' or '<=' or '>=' not found
	boolean found = false;
	// tempStr is a string, which will contain b[i] of line i in string format, i=0...m
	String tempStr = "";
	// For each line except the first one
	for (int i=1;i<=this.getM();i++) {
		tempStr = "";
		found = false;
		// If line do not contain one of the symbols '<' '>' '<=' '>=', do nothing
		if (lines[i].contains("<") || lines[i].contains(">") || lines[i].contains("<=") || lines[i].contains(">=") || lines[i].contains("=") || lines[i].contains("==")) {
			// Start from the last variable found in line i
			for (int j=returnIndexOfLastVariable(lines[i])+1;j<lines[i].length();j++) {	
				// If one of the symbols found
				if (found) {	
					// Check if there is something non-numeric after the math symbol
					if (!Character.isDigit(lines[i].charAt(j))){
						return null;
					}
					else {
						tempStr += lines[i].charAt(j);
					}
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
							return null;
						}
					}
				}
			}
		}
				// If there is no math symbol at all, break
				else return null;
				b[i-1] = Integer.parseInt(tempStr);
	}
	return b;
}

	private int[] returnC () {
		
		/* This method returns a vector c, which includes the coefficients of variables in function. */
	
	int[] c = new int[this.getN()];
	//First we find coefficients of variables in function (line 0)
	
	// coefficientStr is the string all digits of a coefficient are put
	// and after they are put, we convert to double and assign them in A array
	String coefficientStr = "";
	// Stack is needed in order to append the digits of a coefficient
	// in the correct order in coefficientStr string, 
	// when we search for them backwards (for(int y=j;y>=0;y--)...)
	Stack<String> stack = new Stack<String>();
	// For every line starting with the second one (not the function line)
	
	String temp="";
	
	boolean variableFound=false;
	
	int var=0;
	
	int type = getMinMax();
	
	// Function (String)
	String function = this.getFunction();
	
	// Deleting min/max word, in order to find variables
	if (type == 1)
		function = function.replaceAll("max", "");
	else 
		function = function.replaceAll("min", "");
	
				// For every character in line
				for(int j=0;j<function.length();j++) {
					// Initialize with ""
					coefficientStr = "";
					// If we are looking at the first character
					if (j==0) {
						// If the first character is letter, it is meant the coefficient is 1
						if (Character.isLetter(function.charAt(j)) || function.charAt(j) == '+') {
							// The following loop detects the index y of variable (x'y')
							variableFound=false;
							for (int p=j;p<function.length();p++) {
								// If character is variable
								if (Character.isLetter(function.charAt(p)) && !variableFound) {
									variableFound=true;
								}
								// If character is digit and is index of variable
								else if (Character.isDigit(function.charAt(p)) && variableFound) {;
									// Append
									temp += function.charAt(p);
								}
								// If it is not digit or letter and we've already found a variable
								else if (variableFound){
									var = Integer.parseInt(temp);
									temp = "";
									variableFound=false;
									break;
								}
							}
							// Coefficient in the right position of c
							c[var-1] = 1;

						}
						else if (function.charAt(j) == '-') {
							
							// The following loop detects the index y of variable (x'y')
							variableFound=false;
							for (int p=j;p<function.length();p++) {
								// If character is variable
								if (Character.isLetter(function.charAt(p)) && !variableFound) {
									variableFound=true;
								}
								// If character is digit and is index of variable
								else if (Character.isDigit(function.charAt(p)) && variableFound) {;
									// Append
									temp += function.charAt(p);
								}
								// If it is not digit or letter and we've already found a variable
								else if (variableFound){
									var = Integer.parseInt(temp);
									temp = "";
									variableFound=false;
									break;
								}
							}
							// Coefficient in the right position of c
							c[var-1] = 1;
							
						}
						else if (function.charAt(j) == '-') {
							
							// The following loop detects the index y of variable (x'y')
							variableFound=false;
							for (int p=j;p<function.length();p++) {
								// If character is variable
								if (Character.isLetter(function.charAt(p)) && !variableFound) {
									variableFound=true;
								}
								// If character is digit and is index of variable
								else if (Character.isDigit(function.charAt(p)) && variableFound) {;
									// Append
									temp += function.charAt(p);
								}
								// If it is not digit or letter and we've already found a variable
								else if (variableFound){
									var = Integer.parseInt(temp);
									temp = "";
									variableFound=false;
									break;
								}
							}	
							// Coefficient in the right position of c
							c[var-1] = -1;
						}
						// If the first character is digit, we search for the coefficient
						else if (Character.isDigit(function.charAt(j))){
							
							for(int y=j; y<function.length();y++) {
									if (!Character.isDigit(function.charAt(y)) && function.charAt(y) != '.') break;
									else {
										coefficientStr += function.charAt(y);
									}
							}
							
							// The following loop detects the index y of variable (x'y')
							variableFound=false;
							for (int p=j;p<function.length();p++) {
								// If character is variable
								if (Character.isLetter(function.charAt(p)) && !variableFound) {
									variableFound=true;
								}
								// If character is digit and is index of variable
								else if (Character.isDigit(function.charAt(p)) && variableFound) {;
									// Append
									temp += function.charAt(p);
								}
								// If it is not digit or letter and we've already found a variable
								else if (variableFound){
									var = Integer.parseInt(temp);
									temp = "";
									variableFound=false;
									break;
								}
							}
							// Coefficient in the right position of c
							if (!coefficientStr.contains("."))
								c[var-1] = Integer.parseInt(coefficientStr);
							else
								c[var-1] = (int) Double.parseDouble(coefficientStr);

						}
						// If the first character is digit, we search for the coefficient
						else if (Character.isDigit(function.charAt(j))){
							
							for(int y=j; y<function.length();y++) {
									if (!Character.isDigit(function.charAt(y))) break;
									else {
										coefficientStr += function.charAt(y);
									}
							}
							// The following loop detects the index y of variable (x'y')
							variableFound=false;
							for (int p=j;p<function.length();p++) {
								// If character is variable
								if (Character.isLetter(function.charAt(p)) && !variableFound) {
									variableFound=true;
								}
								// If character is digit and is index of variable
								else if (Character.isDigit(function.charAt(p)) && variableFound) {;
									// Append
									temp += function.charAt(p);
								}
								// If it is not digit or letter and we've already found a variable
								else if (variableFound){
									var = Integer.parseInt(temp);
									temp = "";
									variableFound=false;
									break;
								}
							}
							// Coefficient in the right position of A
							c[var-1] = Integer.parseInt(coefficientStr);
						}
					}
					// If we are not looking at the first character, we have to go back until
					// we find something is not digit. The digits we found
					// in the opposite order, is the coefficient we are looking for.
					else if (Character.isLetter(function.charAt(j)) && j>0){
						if (function.charAt(j-1) == '-') {
							coefficientStr = "-1";
						}
						else if (function.charAt(j-1) == '+') {
							coefficientStr = "1";
						}
						if (Character.isDigit(function.charAt(j-1))) {
							for (int y=j-1;y>=0;y--) {
								if (Character.isDigit(function.charAt(y))) {
									stack.push(String.valueOf(function.charAt(y)));
								}
								
								// Operator found, stop
								else if (function.charAt(y) == '+') {
									break;
								}
								
								// Operator found, stop
								else if (function.charAt(y) == '-') {
									stack.push(String.valueOf(function.charAt(y)));
									break;
								}
							}
						}
						// We use stack to append the digits in the opposite order
						while(!stack.isEmpty())  {
							coefficientStr += stack.pop();
						}
						// The following loop detects the index y of variable (x'y')
						variableFound=false;
						for (int p=j;p<function.length();p++) {
							// If character is variable
							if (Character.isLetter(function.charAt(p)) && !variableFound) {
								variableFound=true;
							}
							// If character is digit and is index of variable
							else if (Character.isDigit(function.charAt(p)) && variableFound) {;
								// Append
								temp += function.charAt(p);
							}
							// If it is not digit or letter and we've already found a variable
							else if (variableFound){
								var = Integer.parseInt(temp);
								temp = "";
								variableFound=false;
								break;
							}
							if (p == function.length()-1) {
								var = Integer.parseInt(temp);
								variableFound=false;
							}
						}
						// Coefficient in the right position of c
						c[var-1] = Integer.parseInt(coefficientStr);
					}	
				}
	return c;
}

	private int[][] returnA() {
	
	/* This method returns a (m-1)xn array, which includes the coefficients
	 * for limitations' variables. 
	 */
	
		String[] lines = lp.split("\n");
		
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
	
		int[][] A = new int[this.getM()][this.getN()]; // Automatically initialized with zeros
		
		// coefficientStr is the string all digits of a coefficient are put
		// and after they are put, we convert to double and assign them in A array
		String coefficientStr = "";
		// Stack is needed in order to append the digits of a coefficient
		// in the correct order in coefficientStr string, 
		// when we search for them backwards (for(int y=j;y>=0;y--)...)
		Stack<String> stack = new Stack<String>();
		
		boolean variableFound=false;
		
		String temp="";
		
		int var = 0;

		// For every line starting with the second one (not the function line)		
	for(int i=1;i<=this.getM();i++) {
			// For every character in line
			for(int j=0;j<lines[i].length();j++) {
				// Initialize with ""
				coefficientStr = "";
				// If we are looking at the first character
				if (j==0) {
					// If the first character is letter, it is meant the coefficient is 1
					if (Character.isLetter(lines[i].charAt(j)) || lines[i].charAt(j) == '+') {
						
						// The following loop detects the index y of variable (x'y')
						variableFound=false;
						for (int p=j;p<lines[i].length();p++) {
							// If character is variable
							if (Character.isLetter(lines[i].charAt(p)) && !variableFound) {
								variableFound=true;
							}
							// If character is digit and is index of variable
							else if (Character.isDigit(lines[i].charAt(p)) && variableFound) {;
								// Append
								temp += lines[i].charAt(p);
							}
							// If it is not digit or letter and we've already found a variable
							else if (variableFound){
								var = Integer.parseInt(temp);
								temp = "";
								variableFound=false;
								break;
							}
						}
						// Coefficient in the right position of A
						A[i-1][var-1] = 1;

					}
					else if (lines[i].charAt(j) == '-') {
						
						// The following loop detects the index y of variable (x'y')
						variableFound=false;
						for (int p=j;p<lines[i].length();p++) {
							// If character is variable
							if (Character.isLetter(lines[i].charAt(p)) && !variableFound) {
								variableFound=true;
							}
							// If character is digit and is index of variable
							else if (Character.isDigit(lines[i].charAt(p)) && variableFound) {;
								// Append
								temp += lines[i].charAt(p);
							}
							// If it is not digit or letter and we've already found a variable
							else if (variableFound){
								var = Integer.parseInt(temp);
								temp = "";
								variableFound=false;
								break;
							}
						}
						// Coefficient in the right position of A
						A[i-1][var-1] = 1;
						
					}
					else if (lines[i].charAt(j) == '-') {
						
						// The following loop detects the index y of variable (x'y')
						variableFound=false;
						for (int p=j;p<lines[i].length();p++) {
							// If character is variable
							if (Character.isLetter(lines[i].charAt(p)) && !variableFound) {
								variableFound=true;
							}
							// If character is digit and is index of variable
							else if (Character.isDigit(lines[i].charAt(p)) && variableFound) {;
								// Append
								temp += lines[i].charAt(p);
							}
							// If it is not digit or letter and we've already found a variable
							else if (variableFound){
								var = Integer.parseInt(temp);
								temp = "";
								variableFound=false;
								break;
							}
						}	
						// Coefficient in the right position of A
						A[i-1][var-1] = -1;
					}
					// If the first character is digit, we search for the coefficient
					else if (Character.isDigit(lines[i].charAt(j))){
						
						for(int y=j; y<lines[i].length();y++) {
								if (!Character.isDigit(lines[i].charAt(y))) break;
								else {
									coefficientStr += lines[i].charAt(y);
								}
						}
						
						// The following loop detects the index y of variable (x'y')
						variableFound=false;
						for (int p=j;p<lines[i].length();p++) {
							// If character is variable
							if (Character.isLetter(lines[i].charAt(p)) && !variableFound) {
								variableFound=true;
							}
							// If character is digit and is index of variable
							else if (Character.isDigit(lines[i].charAt(p)) && variableFound) {;
								// Append
								temp += lines[i].charAt(p);
							}
							// If it is not digit or letter and we've already found a variable
							else if (variableFound){
								var = Integer.parseInt(temp);
								temp = "";
								variableFound=false;
								break;
							}
						}
						// Coefficient in the right position of A
						A[i-1][var-1] = Integer.parseInt(coefficientStr);

					}
					// If the first character is digit, we search for the coefficient
					else if (Character.isDigit(lines[i].charAt(j))){
						
						for(int y=j; y<lines[i].length();y++) {
								if (!Character.isDigit(lines[i].charAt(y))) break;
								else {
									coefficientStr += lines[i].charAt(y);
								}
						}
						// The following loop detects the index y of variable (x'y')
						variableFound=false;
						for (int p=j;p<lines[i].length();p++) {
							// If character is variable
							if (Character.isLetter(lines[i].charAt(p)) && !variableFound) {
								variableFound=true;
							}
							// If character is digit and is index of variable
							else if (Character.isDigit(lines[i].charAt(p)) && variableFound) {;
								// Append
								temp += lines[i].charAt(p);
							}
							// If it is not digit or letter and we've already found a variable
							else if (variableFound){
								var = Integer.parseInt(temp);
								temp = "";
								variableFound=false;
								break;
							}
						}
						// Coefficient in the right position of A
						A[i-1][var-1] = Integer.parseInt(coefficientStr);
					}
				}
				// If we are not looking at the first character, we have to go back until
				// we find something is not digit. The digits we found
				// in the opposite order, is the coefficient we are looking for.
				else if (Character.isLetter(lines[i].charAt(j)) && j>0){
					if (lines[i].charAt(j-1) == '-') {
						coefficientStr = "-1";
					}
					else if (lines[i].charAt(j-1) == '+') {
						coefficientStr = "1";
					}
					if (Character.isDigit(lines[i].charAt(j-1))) {
						for (int y=j-1;y>=0;y--) {
							if (Character.isDigit(lines[i].charAt(y))) {
								stack.push(String.valueOf(lines[i].charAt(y)));
							}
							
							// Operator found, stop
							else if (lines[i].charAt(y) == '+') {
								break;
							}
							
							// Operator found, stop
							else if (lines[i].charAt(y) == '-') {
								stack.push(String.valueOf(lines[i].charAt(y)));
								break;
							}
						}
					}
					// We use stack to append the digits in the opposite order
					while(!stack.isEmpty())  {
						coefficientStr += stack.pop();
					}
					// The following loop detects the index y of variable (x'y')
					variableFound=false;
					for (int p=j;p<lines[i].length();p++) {
						// If character is variable
						if (Character.isLetter(lines[i].charAt(p)) && !variableFound) {
							variableFound=true;
						}
						// If character is digit and is index of variable
						else if (Character.isDigit(lines[i].charAt(p)) && variableFound) {;
							// Append
							temp += lines[i].charAt(p);
						}
						// If it is not digit or letter and we've already found a variable
						else if (variableFound){
							var = Integer.parseInt(temp);
							temp = "";
							variableFound=false;
							break;
						}
					}
					// Coefficient in the right position of A
					A[i-1][var-1] = Integer.parseInt(coefficientStr);
				}	
			}
}
	return A;
}

	private int[] returnEqin() {
	
	/* This method returns a m-1 array, which includes math symbols coded
	 * (-1 for <, 0 for =, 1 for >) 
	 */
	
	int eqin[] = new int[getM()];
	// Getting lines of lp in string array
		String[] lines = lp.split("\n");
				
		// If symbols '<' or '>' or '<=' or '>=' not found
		boolean found = false;
		// For each line except the first one
		for (int i=1;i<=getM();i++) {
			found = false;
			// If line do not contain one of the symbols '<' '>' '<=' '>=', do nothing
			if (lines[i].contains("<") || lines[i].contains(">") || lines[i].contains("=")) {
				// Start from the last variable found in line i
				for (int j=returnIndexOfLastVariable(lines[i])+1;j<lines[i].length();j++) {	
					if (found) break;
					// If math symbol not found yet
					else {
						// If math symbol is found
						if (lines[i].charAt(j) == '<') {
							eqin[i-1] = -1;
							found = true;
						}
						else if(lines[i].charAt(j) == '>') {
							eqin[i-1] = 1;
							found = true;
						}
						else if(lines[i].charAt(j) == '=') {
							eqin[i-1] = 0;
							found = true;
						}
					}
				}
			}
		}
		return eqin;
}
	
	private int returnIndexOfLastVariable(String line) {
		
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
	
	private String[] returnVariables() {
		
		/* This function returns all variables in String[] */
		
		String[] lines = lp.split("\n");
		
		String[] variables = new String[this.getN()];
		
		int k=0;
		
		// variableFound turns true every time the character we get in for loop is letter
		boolean variableFound=false;
		
		// We want the type of problem, in order to delete min/max word from the beggining of function
		int type = getMinMax();
		
		// Index line which has all variables
		int indexOfline = this.returnLineWithAllVariables();
		
		// The line (String)
		String line = lines[indexOfline];
		
		// Deleting min/max word, in order to find variables
		if(indexOfline==0) {
			if (type == 1)
				line = this.returnFunction().replaceAll("max", "");
			else 
				line = this.returnFunction().replaceAll("min", "");
		}
		else if (indexOfline==1) {
			if (line.contains("st")) 
				line = line.replaceAll("st", "");
			else if (line.contains("s.t."))
				line = line.replaceAll("s.t.", "");
			else if (line.contains("subject"))
				line.replaceAll("subject", "");
		}
		
		String temp="";

		// For every character in first line
				for (int j=0;j<line.length();j++) {
					// If character is variable
					if (Character.isLetter(line.charAt(j)) && !variableFound) {
						variableFound=true;
						// Get the letter of variable (x, y etc.)
						temp = String.valueOf(line.charAt(j));
					}
					// If character is digit and is index of variable
					else if (Character.isDigit(line.charAt(j)) && variableFound) {;
						// Append
						temp += line.charAt(j);
					}
					// If it is not digit or letter and we've already found variable
					else if (variableFound){
						variables[k] = temp;
						k++;
						temp = "";
						variableFound=false;
					}
				}
				
				// For the last variable (does not enter the last else if)
				try {
				variables[k] = temp;		
				}catch(Exception e) {
					// Do nothing
				}
				
				return variables;
			}
	
	private int returnLineWithAllVariables() {
		
		// Getting lines of lp in string array
		String[] lines = lp.split("\n"); 
		
		// varCount = array for counting variables in each line
		Integer[] varCount = new Integer[getM()+1];

		// Initialize with zeros
		for(int i=0;i<varCount.length;i++)
			varCount[i] = 0;
				
				// Counting all variables & operators for each line
				for(int i=0; i<getM()+1;i++) {
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
	
	public void writeFile(String output, int[][] A, int[] c, int[] b, int minMax, int[] eqin, String[] variables, int m, int n) {
		
		/* This method writes to a file named "converted_lp.txt" the problem converted */
		
		File outFile = new File(output+"\\converted_lp.txt");
		try {
	    FileWriter writer = new FileWriter(outFile, true);
	    
		writer.write("("+minMax+")");
		
		writer.write(System.lineSeparator());
		writer.write(System.lineSeparator());
		
		writer.write("c\t[\t");
	    for (int i=0; i<n;i++)
	    	writer.write(c[i]+"\t");
	    writer.write("]");
	    
	    writer.write(System.lineSeparator());
	    writer.write(System.lineSeparator());
	    
	    writer.write("[\t");
	    for (int i=0;i<n;i++)
	    	writer.write(variables[i]+"\t");
	    writer.write("]");
	    
		writer.write(System.lineSeparator());
		writer.write(System.lineSeparator());
		
		writer.write("s.t.\tA\t[\t");
		
		for(int i = 0; i<m; i++){
		    for(int j = 0; j<n; j++)
		    		writer.write(A[i][j]+"\t");
		}
		writer.write("]");
		
		writer.write(System.lineSeparator());
		writer.write(System.lineSeparator());

	    writer.write("[\t");
	    for(int i = 0; i<m; i++)
	    	// Eqin will be in 4th (0,1,2,3,4) line of output text file
	    	writer.write(eqin[i]+"\t");
	    writer.write("]");
	    
	    writer.write(System.lineSeparator());
	    writer.write(System.lineSeparator());
	    
	    writer.write("b\t[\t");
	    for(int i = 0; i<m; i++)
	    	writer.write(b[i]+"\t");
	    writer.write("]");
	    
		writer.flush();
	    writer.close();
	    
	    int dialogResult = JOptionPane.showConfirmDialog(null, "Conversion complete! Open converted_lp.txt?", "Success!", JOptionPane.YES_NO_OPTION);
		
	    if(dialogResult == JOptionPane.YES_OPTION)
	    	Desktop.getDesktop().edit(outFile);    

	    
		} catch (IOException e6) {
			JOptionPane.showMessageDialog(null, "Error in tracing output folder! Check path & try again.", "Error!", JOptionPane.ERROR_MESSAGE);
		}
	}

}
