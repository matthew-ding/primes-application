import java.io.*;
import java.util.*;

public class problem7 {
	static int terminalNumber;
	static  ArrayList<Character> terminalList;
    
	public static void main(String[] args) throws FileNotFoundException{
		Scanner fin = new Scanner(new File("problem7.in"));
		fin.useDelimiter(":|\\n");
		Grammar grammar = new Grammar();
	
		//inputting grammar
		do  {
			String ruleleft = fin.next();
			String ruleright = fin.next();
			grammar.Add(ruleleft,ruleright);
		} while (fin.hasNextLine());
		
	    fin.close();
	    
	    //CNF Conversion
	    grammar.CNFConvert();
	    HashMap<String, HashSet<String>> dictionary = grammar.getGrammar();
	    
	    Scanner cin = new Scanner(System.in);
	    System.out.println("Input number of output strings: ");
	    int n = cin.nextInt();
	    cin.close();
	    
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
	    
	   
	    int counter=0;
	    int i=0;
	    terminalNumber=terminalList.size();
	    
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
	    		counter++;
	    		
	    		if (counter%10==0) {
		    		out.println();
		    	}
	    	}
	    	
	    	i++;
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
