import java.io.*;
import java.util.*;

public class problem12 {
	//TODO: nonterminals generating both nonterminals and terminal probabilities are off
	static boolean[][][] array; //dynamic programming array
	static BackPointer[][][] backList; //list of backpointers
	static String[] keyArray; //array of nonterminals
	
	//TODO: add explanation of algorithm? In PDF?
	public static void main(String[] args) throws FileNotFoundException {
		//TODO: change file back
		Scanner fin = new Scanner(new File("problem12Grammar.in"));
		fin.useDelimiter(":|\\n| ");
		PGrammar grammar = new PGrammar();
		
		//inputting grammar
		do  {
			String ruleleft = fin.next();
			//System.out.println(ruleleft);
			String ruleright = fin.next();
			//System.out.println(ruleright);
			grammar.Add(ruleleft,ruleright, 0);
		} while (fin.hasNextLine());
	    fin.close();
	    
	    //obtaining Probabilistic CFG
	    HashMap<String, HashSet<Derivation>> dictionary = grammar.getGrammar();
	    
	    //setting all rules for within each nonterminal to be equally likely
	    for (String key: dictionary.keySet()) {
	    	double probability=1.0/dictionary.get(key).size();
	    	for (Derivation derivation: dictionary.get(key)) {
	    		grammar.setProbability(key, derivation.getDerivation(), probability);
	    	}
	    }
	    
	    //creating order list of nonterminals for CYK
	    createKeyArray(dictionary);
	    
	    for (int i=0; i<30; i++) {
	    	//TODO: change file back
		    Scanner fin2 = new Scanner(new File("problem10.out"));
			while (fin2.hasNext()) {
				String derivation = fin2.next();
				ProbabilityCYK(grammar, grammar.getGrammar(), derivation);
			}
			fin2.close();
			
			RecalculateProbability(grammar);
			
			/*
			for (String key: grammar.getGrammar().keySet()) {
				for (Derivation derivation:grammar.getGrammar().get(key)) {
					System.out.println(key + " " + derivation.getDerivation() + " " + derivation.getProbability());
				}
			}
			System.out.println();
			*/
	    }
	    
	    for (String key: grammar.getGrammar().keySet()) {
			for (Derivation derivation:grammar.getGrammar().get(key)) {
				System.out.println(key + " " + derivation.getDerivation() + " " + derivation.getProbability());
			}
		}
		System.out.println();
	}
	
	//recalculates probabilities for each rule after each CYK iteration
	public static void RecalculateProbability(PGrammar Grammar) {
		HashMap<String, HashSet<Derivation>> dictionary = Grammar.getGrammar();
		for (String key: dictionary.keySet()) {
			int total=0; //total occurences of rules with specific key
			for (Derivation derivation: dictionary.get(key)) {
				total+=derivation.getCounter();
			}
			for (Derivation derivation: dictionary.get(key)) {
				int counter = derivation.getCounter();
				derivation.setProbability((double)counter/total);
				derivation.setCounter(0);
			}
		}
	}
	
	//https://en.wikipedia.org/wiki/CYK_algorithm
	public static void ProbabilityCYK(PGrammar Grammar, HashMap<String, HashSet<Derivation>> grammar, String input) {
		if (input.equals("$")) {
			for (Derivation derivation: grammar.get("S")) {
				if (derivation.getDerivation().equals("$")) {
					derivation.appendCounter();
					break;
				}
			}
			return;	
		}
		
		int n = input.length();
		int r = grammar.size();
		
		//standard CYK dynamic programming array
		array = new boolean[n+1][n+1][r+1]; 
		
		//array of backpointers for parse trees
		backList = new BackPointer[n+1][n+1][r+1];
		
		//System.out.println("NEW backList " + input);
		for (int i=0; i<=n; i++) {
			for (int j=0; j<=n; j++) {
				for (int k=0; k<=r; k++) {
					backList[i][j][k]=new BackPointer();
				}
			}
		}

		//considering length 1 substrings
		for (int s=1; s<=n; s++) {
			for (int v=1; v<=r; v++) {
				for (Derivation derived: grammar.get(keyArray[v])) {
					if (derived.derivation.equals(Character.toString(input.charAt(s-1)))) {
						array[1][s][v]=true;
					}
				}
			}	
		}
		
		//considering length 2->n substrings
		for (int l=2; l<=n; l++) {
			for (int s=1; s<=n-l+1; s++) {
				for (int p=1; p<=l-1; p++) {
					for (int a=1; a<=r; a++) {
						
						//we only care about start symbol deriving entire string
						if (l==n && a!=1) {
							break;
						}
						
						//System.out.println("INPUT: " + input + " a: " + a + " L: " + l + " string: " + keyArray[a]);
						for (Derivation derived:grammar.get(keyArray[a])) {
							if (derived.derivation.length()==2) {
								char firstChar = derived.derivation.charAt(0);
								char secondChar = derived.derivation.charAt(1);
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
								
								//if this substring can be derived
								if (array[p][s][b] && array[l-p][s+p][c]) {
									array[l][s][a]=true;
									backList[l][s][a].Add(p,b,c);
									/*
									System.out.println("ADDING TO BACKLIST " + l+ " " + s + " " + a);
									System.out.println(input);
									System.out.println();
									*/
								}
							}
						}
					}
				}
			}
		}
		
		//using backpointers to DFS trace through all parse trees
		generateLeftDerivation(Grammar, n ,1,1, "S", input);
	}
	
	
	//does a single rule derivation (moves one level down parse tree)
	public static String generateLeftDerivation(PGrammar grammar, 
			int l, int s, int a, String derivation, String input) {		
		
		if (l==1) {
			/*
			System.out.println("TERMINAL " + l+ " " + s + " " + a);
			System.out.println(input);
			System.out.println();
			*/
			
			//reaching leaf of parse tree (nonterminal to terminal derivation)
			String terminal = Character.toString(input.charAt(s-1));
			derivation=derivation.replaceFirst(keyArray[a], terminal);
			
			grammar.appendCounter(keyArray[a], terminal);
			
			//last function call of parse tree, reset parse tree
			if (s==input.length()) {
				return "S";
			}
		}
		else {
			int i; //specific backpointer used
			if (backList[l][s][a].getPointer().size()>1) {
				i=chooseRule(backList[l][s][a].getPointer(), a, grammar);
			}
			else {
				i=0;
			}
			/*
			System.out.println(l+ " " + s + " " + a);
			System.out.println(input);
			System.out.println();
			*/
			int p=backList[l][s][a].getPointer(i).get(0);
			int b=backList[l][s][a].getPointer(i).get(1);
			int c=backList[l][s][a].getPointer(i).get(2);
			String key = keyArray[a];
			String derived = keyArray[b] + keyArray[c];
	
			//updating derivation
			derivation=derivation.replaceFirst(key, derived);
			grammar.appendCounter(key, derived);
			
			//left terminal derivation
			derivation = generateLeftDerivation(grammar, p,s,b, derivation, input);
			
			//right terminal derivation
			derivation = generateLeftDerivation(grammar, l-p, s+p, c, derivation, input);
		}
		
		return derivation;
	}
	
	//out of a list of possible nonterminal rules, chooses one randomly
	//TODO: does order stay the same, can you just return i?
	public static int chooseRule(ArrayList<ArrayList<Integer>> pointerList, int a, PGrammar Grammar) {
		ArrayList<String> ruleList = new ArrayList<>(); //list of nonterminals to possiblity derive
		ArrayList<Double> probabilities = new ArrayList<>(); //list of probabilities for each rule
		double totalProbability=0; //sum of all probabilities of all rules being chosen from
		
		//getting all rules and probabilities into parallel ArrayLists
		for (ArrayList<Integer> rule: pointerList) {
			int b = rule.get(1);
			int c = rule.get(2);
			String nonTerminal = keyArray[b]+keyArray[c];
			
			//if (!ruleList.contains(nonTerminal)) {
				ruleList.add(nonTerminal);
				double probability = Grammar.getProbability(keyArray[a], nonTerminal); //probability of specified rule
				probabilities.add(probability);
				totalProbability+=probability;
			//}
		}
		
		Random rand = new Random();
		double randomNumber = rand.nextDouble()*totalProbability; //generating random number to determine which rule to choose
		double counter=0;
		
		//random rule chosen calculation
		for (int i=0; i<ruleList.size();i++) {
			counter+=probabilities.get(i);
			if (randomNumber<counter) {
				/*
				System.out.println(ruleList.toString());
				System.out.println(probabilities.toString());
				System.out.println(i);
				System.out.println();
				*/
				return i;
			}
		}
		return -1;
	}
	
	//creating order list of nonterminals for CYK
	public static void createKeyArray(HashMap<String, HashSet<Derivation>> grammar) {
		int r = grammar.size();
		
		String[] tempKeyArray = grammar.keySet().toArray(new String[r]);
		
		//setting S0 nonterminal to the first index
		for (int i=0; i<r; i++) {
			if (tempKeyArray[i].equals("S")) {
				tempKeyArray[i]=tempKeyArray[0];
				tempKeyArray[0]="S";
				break;
			}
		}
		
		//making array 1-based indexing
		keyArray = new String[r+1]; //array of nonterminals
		for (int i=1; i<=r; i++) {
			keyArray[i]=tempKeyArray[i-1];
		}
	}
}



