package problem10_11_12;
import java.io.*;
import java.util.*;

/*
Development Framework:
Eclipse IDE for Java Developers
Version: 2019-09 R (4.13.0)
Build id: 20190917-1200

Platform:
macOS 10.14.5

Language:
Java Version 8 Update 221 (build 1.8.0_221-b11)

 * PROBLEM 10 MAIN CLASS
 * Overview:
 * Inputs a PCFG in CNF form and an integer n
 * Outputs n strings randomly generated from the PCFG to problem10.out
 * 
 * Strings our randomly generated through a leftmost derivation:
 * We find the leftmost nonterminal at any instance and use a rule to convert it,
 * with the relative probabilities of all its rules -> repeat until derivation complete
 */

public class problem10 {
	static boolean derivationComplete = false;
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner fin = new Scanner(new File("problem10.in"));
		fin.useDelimiter(":|\\n|\\s+");
		PGrammar grammar = new PGrammar();
		
		//inputting grammar
		do  {
			try {
				String ruleleft = fin.next().trim();
				String ruleright = fin.next().trim();
				double probability = Double.valueOf(fin.next().trim());
				grammar.Add(ruleleft,ruleright, probability);
			}
			catch (Exception e) {
				break;
			}
		} while (fin.hasNextLine());
	    fin.close();
	    
	    HashMap<String, HashSet<Derivation>> dictionary= grammar.getGrammar();
	    
	    //input of number of strings to output
	    Scanner cin = new Scanner(System.in);
	    System.out.println("Input number of output strings: ");
	    int n = cin.nextInt();
	    cin.close();
	    
	    int counter=0; //number of strings outputted
	    PrintWriter out = new PrintWriter(new File("problem10.out"));
	    
	    //outputs strings until n have been outputed
	    while (counter<n) {
		    String derivedString="S";
		    derivationComplete=false;
		    while (!derivationComplete) {
		    	derivedString = leftDerive(derivedString, dictionary);
		    }
		    out.print(derivedString+" ");
		    counter++;
		    if (counter%10==0) {
		    	out.println();
		    }
	    }
	    if (!(n%10==0)) {
	    	out.println();
	    }
	    out.close();
	}
	
	//derives from single leftmost nonterminal with given probabilities
	public static String leftDerive(String input, HashMap<String, HashSet<Derivation>> grammar) {
		Random rand = new Random();
		double probability = rand.nextDouble();
		int location=-1; //position of leftmost nonterminal
		String nonTerminal=""; //leftmost nonterminal
		
		//finding leftmost nonterminal to derive
		for (int i=0; i<input.length(); i++) {
			if (Character.isUpperCase(input.charAt(i))) {
				location=i;
				nonTerminal=Character.toString(input.charAt(i));
				break;
			}
		}
		
		//if we cannot find a nonterminal, our derivation is complete
		if (location==-1) {
			derivationComplete=true;
			return input;
		}
		
		String replacement="";
		double probabilityCounter=0; //cumulative number determining which derivation occurs
		
		//probability calculation for determining derivation
		for (Derivation derivation: grammar.get(nonTerminal)) {
			probabilityCounter+=derivation.getProbability();
			if (probabilityCounter>probability) {
				replacement=derivation.getDerivation();
				break;
			}
		}
		
		if (replacement.equals("$")) {
			replacement="";
		}
		
		StringBuilder output = new StringBuilder(input);
		output.replace(location, location+1, replacement);
		return output.toString();
	}
}
