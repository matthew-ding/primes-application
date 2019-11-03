import java.io.*;
import java.util.*;

public class problem10 {
	static boolean derivationComplete = false;
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner fin = new Scanner(new File("problem10.in"));
		fin.useDelimiter(":|\\n| ");
		PGrammar grammar = new PGrammar();
		
		//inputting grammar
		do  {
			String ruleleft = fin.next();
			String ruleright = fin.next();
			double probability = Double.valueOf(fin.next());
			grammar.Add(ruleleft,ruleright, probability);
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
	    
	    while (counter<n) {
		    String derivedString="S";
		    derivationComplete=false;
		    while (!derivationComplete) {
		    	derivedString = leftDerive(derivedString, dictionary);
		    }
		    System.out.print(derivedString+" ");
		    out.print(derivedString+" ");
		    counter++;
		    if (counter%10==0) {
		    	out.println();
		    	System.out.println();
		    }
	    }
	    if (!(n%10==0)) {
	    	out.println();
	    }
	    out.close();
	}
	
	//derives from single leftmost nonterminal with given probabilities
	//TODO: breaking with empty strings?
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
