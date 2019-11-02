import java.util.*;
import java.io.*;


public class problem6 {

	public static void main(String[] args) throws FileNotFoundException{
		Scanner fin = new Scanner(new File("problem6.in"));
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
	    
	    /*
	    //int result = 0;
	    //PrintWriter out = new PrintWriter(new File("problem6.out"));
	    //out.println(result);
	    //out.close();
	    */
	}
	
	//https://en.wikipedia.org/wiki/CYK_algorithm
	public static boolean CYK(HashMap<String, HashSet<String>> grammar, String input) {
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
								int b=0,c=0;
								//Binary search method:
								//String[] newKeyArray = Arrays.copyOfRange(keyArray, 2, r+1);
								//int b=2+Arrays.binarySearch(newKeyArray, Character.toString(firstChar));
								//int c=2+Arrays.binarySearch(newKeyArray, Character.toString(secondChar));
									
								for (int i=2; i<=r; i++) {
									if (keyArray[i].equals(Character.toString(firstChar))) {
										b=i;
									}
									if (keyArray[i].equals(Character.toString(secondChar))) {
										c=i;
									}
								}
								
								if (array[p][s][b] && array[l-p][s+p][c]) {
									array[l][s][a]=true;
								}
							}
						}
					}
				}
			}
		}
		
		if (array[n][1][1]) {
			return true;
		}
		else {
			return false;
		}
	}
}
