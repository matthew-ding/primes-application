package problem6_7;
import java.util.*;
import java.io.*;

/* Development Framework:
Eclipse IDE for Java Developers
Version: 2019-09 R (4.13.0)
Build id: 20190917-1200

Platform:
macOS 10.14.5

Language:
Java Version 8 Update 221 (build 1.8.0_221-b11)

PROBLEM 6 MAIN CLASS
 * Overview:
 * Program begins by converting inputted grammar to CNF Form
 * Proceeds to use CYK algorithm
 * Outputs "Yes" or "No" to console depending on whether inputted
 * string is in the language
 */

public class problem6 {

	public static void main(String[] args) throws FileNotFoundException{
		Scanner fin = new Scanner(new File("problem6.in"));
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
	    
	    System.out.println(grammar.getGrammar());
	    
	    //String input
	    Scanner cin = new Scanner(System.in);
	    System.out.println("Input String: ");
	    String input = cin.next();
	    cin.close();
	    
	    //CYK Algorithm
	    if (CYK(grammar.getGrammar(), input)) {
	    	System.out.println("Yes");
	    }
	    else {
	    	System.out.println("No");
	    }
	}
	
	//https://en.wikipedia.org/wiki/CYK_algorithm
	public static boolean CYK(HashMap<String, HashSet<String>> grammar, String input) {
		if (input.equals("$")) {
			if (grammar.get("S0").contains("$")) {
				return true;
			}
			else {
				return false;
			}
		}
		
		int n = input.length();
		int r = grammar.size();
		boolean[][][] array = new boolean[n+1][n+1][r+1];
		
		String[] tempKeyArray = grammar.keySet().toArray(new String[r]);
		
		//setting S0 nonterminal to the first index
		for (int i=0; i<r; i++) {
			if (tempKeyArray[i].equals("S0")) {
				tempKeyArray[i]=tempKeyArray[0];
				tempKeyArray[0]="S0";
				break;
			}
		}
		
		//making array 1-based indexing
		String[] keyArray = new String[r+1]; //array of nonterminals
		for (int i=1; i<=r; i++) {
			keyArray[i]=tempKeyArray[i-1];
		}
		
		//maps nonterminal to its integer index in the array
		//created to get O(1) lookup for index number from nonTerminal
		HashMap<String, Integer> keyMap = new HashMap<>();
		for (int i=1; i<=r; i++) {
			keyMap.put(keyArray[i],i);
		}
		
		//considering length 1 substrings
		for (int s=1; s<=n; s++) {
			for (int v=1; v<=r; v++) {
				if (grammar.get(keyArray[v]).contains(Character.toString(input.charAt(s-1)))) {
					array[1][s][v]=true;
				}
			}	
		}
		
		//considering length 2->n substrings
		for (int l=2; l<=n; l++) {
			for (int s=1; s<=n-l+1; s++) {
				for (int p=1; p<=l-1; p++) {
					for (int a=1; a<=r; a++) {
						for (String derivation:grammar.get(keyArray[a])) {
							if (derivation.length()==2) {
								char firstChar = derivation.charAt(0);
								char secondChar = derivation.charAt(1);
								//O(1) lookup of index for given string
								int b = keyMap.get(Character.toString(firstChar));
								int c = keyMap.get(Character.toString(secondChar));
								
								if (array[p][s][b] && array[l-p][s+p][c]) {
									array[l][s][a]=true;
								}
							}
						}
					}
				}
			}
		}
		
		//check if string is in our language
		if (array[n][1][1]) {
			return true;
		}
		else {
			return false;
		}
	}
}
