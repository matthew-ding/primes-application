import java.io.*;
import java.util.*;

public class problem7 {
	static HashSet<String> derivedStrings = new HashSet<>(); //set of all derived strings
	static HashMap<String, HashSet<String>> nonTerminalDictionary = new HashMap<>(); //map of rules deriving 2 nonterminals
    static HashMap<String, HashSet<String>> terminalDictionary = new HashMap<>(); //map of rules deriving terminals
    
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
	    
	    System.out.println(dictionary);
	    
	    
	    Scanner cin = new Scanner(System.in);
	    System.out.println("Input number of output strings: ");
	    int n = cin.nextInt();
	    cin.close();
	    
	    //creating separate HashMaps for terminal and nonterminal derivations
	    for (String key: dictionary.keySet()) {
			for (String derivation: dictionary.get(key)) {
				if (derivation.length()==1) {
					if (!terminalDictionary.containsKey(key)) {
						terminalDictionary.put(key, new HashSet<>());
					}
					terminalDictionary.get(key).add(derivation);
				}
				else {
					if (!nonTerminalDictionary.containsKey(key)) {
						nonTerminalDictionary.put(key, new HashSet<>());
					}
					nonTerminalDictionary.get(key).add(derivation);
				}
			}
	    }
	    
	    if (dictionary.get("S0").contains("$")) {
	    	derivedStrings.add("$");
	    }
	    int size=1;
	    //generating at least n strings starting from smallest size
	    while (derivedStrings.size()<n) {
	    	GenerateStrings(size);
	    	size++;
	    }
	    
	    Derivation[] outputList = new Derivation[derivedStrings.size()];
	    int counter=0;
	    for (String a: derivedStrings) {
	    	outputList[counter] = new Derivation(a);
	    	counter++;
	    }
	    
	    //shortlex sort
	    Arrays.sort(outputList);
	    
	    PrintWriter out = new PrintWriter(new File("problem7.out")); 
	    
	    for (int i=0; i<n; i++) {
	    	out.print(outputList[i].derivation + " ");
	    	if (i%10==9) {
	    		out.println();
	    	}
	    }
	    if (n%10!=0) {
	    	out.println();
	    }
	    
	    out.close();
	} 
	
	//derives all strings of size n and adds to set of derived strings
	//n-1 nonterminal steps
	//n terminal steps
	public static void GenerateStrings(int n) {
		HashSet<String> currentDerived = new HashSet<>();
		HashSet<String> newCurrentDerived = new HashSet<>();
		currentDerived.add("S0");
		
		//generates all strings created from 1 nonterminal->nonterminal change
		for (int i=0; i<n-1; i++) {
			for (String a: currentDerived) {
				nonTerminalChange(a, newCurrentDerived);
			}
			//System.out.println(i + " " + newCurrentDerived);
			currentDerived.clear();
			for (String a:newCurrentDerived) {
				currentDerived.add(a);
			}
			newCurrentDerived.clear();
		}
		
		//generates all strings created from 1 nonterminal->terminal change
		for (int i=0; i<n; i++) {
			for (String a: currentDerived) {
				terminalChange(a, newCurrentDerived);
			}
			currentDerived.clear();
			for (String a:newCurrentDerived) {
				currentDerived.add(a);
			}
			newCurrentDerived.clear();
		}
		
		//updates derivedStrings set
		for (String a: currentDerived) {
			derivedStrings.add(a);
		}
	}
	
	//generates single nonterminal->non-terminal changes from an input
	public static void nonTerminalChange(String input, HashSet<String> newCurrentDerived) {
		if (input.equals("S0")) {
			for (String derivation: nonTerminalDictionary.get("S0")) {
				if (derivation.length()==2) {
					newCurrentDerived.add(derivation);
				}
			}
			return;
		}
		
		HashSet<Integer> nonTerminals = new HashSet<>();
		for (int i =0; i<input.length(); i++) {
			if (Character.isUpperCase(input.charAt(i))) {
				nonTerminals.add(i);
			}
		}
		
		for (int i: nonTerminals) {
			if (nonTerminalDictionary.containsKey(Character.toString(input.charAt(i)))) {
				for (String derivation: nonTerminalDictionary.get(Character.toString(input.charAt(i)))) {
					StringBuilder string = new StringBuilder(input);
					string.replace(i, i+1, derivation);
					newCurrentDerived.add(string.toString());
				}
			}
		}
	}
	
	//generates single nonterminal->terminal changes from an input
	public static void terminalChange(String input, HashSet<String> newCurrentDerived) {
		if (input.equals("S0")) {
			for (String derivation: terminalDictionary.get("S0")) {
				if (derivation.length()==1) {
					newCurrentDerived.add(derivation);
				}
			}
			return;
		}
		
		HashSet<Integer> nonTerminals = new HashSet<>();
		for (int i =0; i<input.length(); i++) {
			if (Character.isUpperCase(input.charAt(i))) {
				nonTerminals.add(i);
			}
		}
		
		for (int i:nonTerminals) {
			if (terminalDictionary.containsKey(Character.toString(input.charAt(i)))) {
				for (String derivation: terminalDictionary.get(Character.toString(input.charAt(i)))) {
					StringBuilder string = new StringBuilder(input);
					string.replace(i, i+1, derivation);
					newCurrentDerived.add(string.toString());
				}
			}
		}
	}
}
