package problem6_7;
import java.io.*;
import java.util.*;

/* Development Framework:
Eclipse IDE for Java Developers
Version: 2019-09 R (4.13.0)
Build id: 20190917-1200

Platform:
macOS 10.14.5

Language:
Java Version 8 Update 221 (build 1.8.0_221-b11)

PROBLEM 7 MAIN CLASS
 * 
 * Overview:
 * Takes in a integer n<=1000 and prints the first n integers in an inputted language in shortlex order
 * 
 * We do this by sequentially generated all strings under our alphabet (defined by all possible terminals)
 * We then check if the generated string is in the language with CYK
 * 
This program will output one of two different messages if less than n strings are written to the output file:
1: "Program ended (Pumping Lemma)"
 	This is outputted if it is proven by the pumping lemma that no more strings other than the <n strings outputted exist in the language
2: "Program ended (5000 strings)"
	This is outputted if we check 5000 consecutive strings (shortlex order), and none of them are in the language
	Note that there may be more strings in the language that aren't outputted, but I have decided to termiante the program at this point
	
3: If no console message is shown, we have successfully found all n strings
 */

public class problem7 {
	static int terminalNumber;
	static  ArrayList<Character> terminalList;
    
	public static void main(String[] args) throws FileNotFoundException{
		Scanner fin = new Scanner(new File("problem7.in"));
		fin.useDelimiter(":|\\n|\\s+");
		Grammar grammar = new Grammar();
	
		//inputting grammar
		do  {
			try {
				String ruleleft = fin.next().trim();
				String ruleright = fin.next().trim();
				grammar.Add(ruleleft,ruleright);
			}
			catch (Exception e) {
				break;
			}
		} while (fin.hasNextLine());
		
	    fin.close();
	    
	    //CNF Conversion
	    grammar.CNFConvert();
	    HashMap<String, HashSet<String>> dictionary = grammar.getGrammar();
	    
	    Scanner cin = new Scanner(System.in);
	    System.out.println("Input number of output strings: ");
	    int n = cin.nextInt();
	    cin.close();
	    
	    //list storing all terminals in alphabetic order
	    terminalList = new ArrayList<>();
	    for (String key: dictionary.keySet()) {
	    	for (String derivation:dictionary.get(key)) {
	    		if (derivation.length()==1 && !terminalList.contains(derivation.charAt(0))) {
	    			if (!(derivation.charAt(0)=='$')) {
	    				terminalList.add(derivation.charAt(0));
	    			}
	    		}
	    	}
	    }
	    Collections.sort(terminalList);
	    
	    
	    PrintWriter out = new PrintWriter(new File("problem7.out"));
	    
	    int counter=0; //stores number of strings that have been outputted
	    int i=0; //a counter that represents what string we are testing
	    int lastDerived=0; //represents the i value of the last string the the language (used for 5000 string termination)
	    terminalNumber=terminalList.size(); //number of terminals in the grammar
	    int nonTerminalNumber = grammar.getGrammar().keySet().size(); //number of nonterminals
	    int p = (int) (Math.pow(2.0, nonTerminalNumber-1) +1); //pumping length
	    boolean pump = false; //true if a string exists between sizes p and 2p, i.e. if the language is infinite
	    
	    //check if empty string is in language
	    if (problem6.CYK(dictionary, "$")) {
	    	out.print("$ ");
	    	counter++;
	    }
	    
	    while (counter<n) {
	    	//checks if string is in language with CYK
	    	if (problem6.CYK(dictionary, str(i))) {
	    		//generates all strings in shortlex order given our alphabet of terminals
	    		out.print(str(i) + " ");
	    		if (str(i).length()>=p && str(i).length()<=2*p) {
	    			pump=true;
	    		}
	    		counter++;
	    		lastDerived=i;
	    		
	    		if (counter%10==0) {
		    		out.println();
		    	}
	    	}
	    	i++;
	    	
	    	//terminating after 5000 consecutive strings not in language
	    	if (i-lastDerived>5000) {
	    		System.out.println("Program ended (5000 strings)");
	    		break;
	    	}
	    	//terminate after pumping lemma proves there are no more strings to produce
	    	else if (str(i).length()>2*p && !pump) {
	    		System.out.println("Program ended (Pumping Lemma)");
	    		break;
	    	}
	    }
	    
	    if (counter%10!=0) {
	    	out.println();
	    }
	    
	    out.close();
	} 
	
	//https://stackoverflow.com/questions/8710719/generating-an-alphabetic-sequence-in-java
	//creates i+1th string in shortlex order, skipping the empty string
	static String str(int i) {
	    return i < 0 ? "" : str((i / terminalNumber) - 1) + terminalList.get(i % terminalNumber);
	}
}
