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

 * PROBLEM 12 MAIN CLASS
 * Overview:
 * Inputs a CFG in CNF form from problem12Grammar.in and 1000 strings from problem12.in
 * Assigns probabilities to the CFG associated with the 1000 strings and outputs to problem12.out
 * 
 * NOTE: This program works with any CNF grammar inputted in problem12Grammar.in, not just the one given in the problem
 * NOTE: Possible memory with very large inputs, please see Problem 11.1 Additional Analysis section of solution PDF
 */

public class problem12 {
	static ArrayList<Double>[][][] probabilityArray; //stores probabilities of rules
	static BackPointer[][][] backList; //list of backpointers
	static String[] keyArray; //array of nonterminals
	static ArrayList<DerivationList>[][][] derivForest; //stores all parse trees up to given node in CYK array
	static boolean[][][] checkedDerivForest; //tracks which nodes have been visited in derivForest

	public static void main(String[] args) throws FileNotFoundException {
		Scanner fin = new Scanner(new File("problem12Grammar.in"));
		fin.useDelimiter(":|\\n|\\s+");
		PGrammar grammar = new PGrammar();
		
		//inputting grammar
		do  {
			try {
				String ruleleft = fin.next().trim();
				String ruleright = fin.next().trim();
				grammar.Add(ruleleft,ruleright, 0);
			}
			catch (Exception e) {
				break;
			}
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
	    keyArray = problem11.createKeyArray(dictionary);
	    
	    //iteratively computing probabilities
	    for (int i=0; i<10; i++) {
		    Scanner fin2 = new Scanner(new File("problem12.in"));
			while (fin2.hasNext()) {
				String derivation = fin2.next();
				ProbabilityCYK(grammar, dictionary, derivation);
			}
			fin2.close();
			
			RecalculateProbability(grammar);
			
			System.out.println("Iteration " + (i+1) + ": Completed");
	    }
	    
	    System.out.println("Final calculation completed.");
	    
	    
	    //Output probabilities
	    PrintWriter out = new PrintWriter(new File("problem12.out"));
	    
	    for (String key: grammar.getGrammar().keySet()) {
			for (Derivation derivation:grammar.getGrammar().get(key)) {
				out.println(key + " " + derivation.getDerivation() + " " + derivation.getProbability());
			}
		}
		out.close();
	}
	
	//recalculates probabilities for each rule after each CYK iteration
	public static void RecalculateProbability(PGrammar Grammar) {
		HashMap<String, HashSet<Derivation>> dictionary = Grammar.getGrammar();
		for (String key: dictionary.keySet()) {
			int total=0; //total occurences of rules with specific key
			for (Derivation derivation: dictionary.get(key)) {
				total+=derivation.getCounter();
			}
			
			if (total!=0) {
				for (Derivation derivation: dictionary.get(key)) {
					int counter = derivation.getCounter();
					derivation.setProbability((double)counter/total);
					derivation.setCounter(0);
				}
			}
		}
	}
	
	//https://en.wikipedia.org/wiki/CYK_algorithm
	public static void ProbabilityCYK(PGrammar Grammar, HashMap<String, HashSet<Derivation>> grammar, String input) {
		if (input.equals("$")) {
			for (Derivation derivation: grammar.get("S")) {
				if (derivation.getDerivation().equals("$")) {
					Grammar.appendCounter("S", "$");
					break;
				}
			}
			return;	
		}
		
		//initializing arrays
		int n = input.length();
		int r = grammar.size();
		
		probabilityArray = new ArrayList[n+1][n+1][r+1];
		for (int i=0; i<=n; i++) {
			for (int j=0; j<=n; j++) {
				for (int k=0; k<=r; k++) {
					probabilityArray[i][j][k]=new ArrayList<Double>();
				}
			}
		}
		
		derivForest = new ArrayList[n+1][n+1][r+1];
		
		for (int i=0; i<=n; i++) {
			for (int j=0; j<=n; j++) {
				for (int k=0; k<=r; k++) {
					derivForest[i][j][k] = new ArrayList<DerivationList>();
				}
			}
		}
		
		checkedDerivForest = new boolean[n+1][n+1][r+1];
		
		backList = new BackPointer[n+1][n+1][r+1];
		for (int i=0; i<=n; i++) {
			for (int j=0; j<=n; j++) {
				for (int k=0; k<=r; k++) {
					backList[i][j][k]=new BackPointer();
				}
			}
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
				for (Derivation derived: grammar.get(keyArray[v])) {
					if (derived.getDerivation().equals(Character.toString(input.charAt(s-1)))) {
						probabilityArray[1][s][v].add(derived.getProbability());
					}
				}
			}	
		}
		
		//considering length 2->n substrings
		for (int l=2; l<=n; l++) {
			for (int s=1; s<=n-l+1; s++) {
				for (int p=1; p<=l-1; p++) {
					for (int a=1; a<=r; a++) {
						if (l==n && a!=1) {
							break;
						}
						for (Derivation derived:grammar.get(keyArray[a])) {
							if (derived.getDerivation().length()==2) {
								char firstChar = derived.getDerivation().charAt(0);
								char secondChar = derived.getDerivation().charAt(1);	
								//O(1) lookup of index for given string
								int b = keyMap.get(Character.toString(firstChar));
								int c = keyMap.get(Character.toString(secondChar));	
								
								double prob_splitting = derived.getProbability();
								if (probabilityArray[p][s][b].size()>0 && probabilityArray[l-p][s+p][c].size()>0) {
									probabilityArray[l][s][a].add(prob_splitting);
									backList[l][s][a].Add(p,b,c);
								}
							}
						}
					}
				}
			}
		}
		
		//key: probability, value: arraylist of derivations
		//sorts by descending probability
		TreeMap<Double, ArrayList<ArrayList<Derivation>>> probabilityDerivation = new TreeMap<>();
		
		//using backpointers to DFS trace through all parse trees
		generateLeftDerivation(grammar, n,1, 1, input);
		
		DerivationList parseTree = chooseParseTree(derivForest[n][1][1]);
		for (Derivation derivation: parseTree.getDerivationList()) {
			Grammar.appendCounter(derivation.getKey(), derivation.getDerivation());
		}
	}
	
	//Nearly identical method to Problem 11
	//This method stores derivation is ArrayList rather than string
	public static void generateLeftDerivation(HashMap<String, HashSet<Derivation>> grammar, int l, int s, int a, String input) {		
		if (l==1) {
			if (!checkedDerivForest[l][s][a]) {
				//reaching leaf of parse tree (nonterminal to terminal derivation)
				String terminal = Character.toString(input.charAt(s-1));
				ArrayList<Derivation> terminalDeriv = new ArrayList<>();
				terminalDeriv.add(new Derivation(keyArray[a], terminal));
				derivForest[l][s][a].add(new DerivationList(terminalDeriv, probabilityArray[l][s][a].get(0), 0));
				checkedDerivForest[l][s][a]=true;
			}
		}
		else {
			for (int i=0; i< backList[l][s][a].getPointer().size(); i++) {
				int p=backList[l][s][a].getPointer(i).get(0);
				int b=backList[l][s][a].getPointer(i).get(1);
				int c=backList[l][s][a].getPointer(i).get(2);
				
				//left terminal derivation
				generateLeftDerivation(grammar, p,s,b, input);
				
				//right terminal derivation
				generateLeftDerivation(grammar, l-p, s+p, c, input);
				
				//combining left and right node derivations, storing them into parent node
				if (!checkedDerivForest[l][s][a]) {
					derivForest[l][s][a].addAll(combineNodes(derivForest[p][s][b], derivForest[l-p][s+p][c], keyArray[a], b, c, probabilityArray[l][s][a].get(i)));
				}
			}
			checkedDerivForest[l][s][a]=true;
		}
	}
	
	//if there are multiple parse trees for a string, we choose one given their relative probabilities
	public static DerivationList chooseParseTree(ArrayList<DerivationList> parentNode) {
		double totalProbability=0;
		for (DerivationList parseTree: parentNode) {
			totalProbability+=parseTree.getProbability();
		}
		
		Random rand = new Random();
		double randomNumber = rand.nextDouble()*totalProbability; //generating random number to determine which rule to choose
		double counter=0;
		
		//random rule chosen calculation
		for (DerivationList parseTree: parentNode) {
			counter+=parseTree.getProbability();
			if (randomNumber<counter) {
				return parseTree;
			}
		}
		return parentNode.get(parentNode.size()-1);
	}
	
	//creates all possible derivations by merging to child nodes together
	public static ArrayList<DerivationList> combineNodes(ArrayList<DerivationList> childA, ArrayList<DerivationList> childB, String parent, int b, int c, double p) {
		ArrayList<DerivationList> parentList = new ArrayList<>(); //stores all final derivations
		//looping through all pairs of both child nodes
		for (DerivationList aList: childA) {
			for (DerivationList bList: childB) {
				double probability = aList.getProbability()*bList.getProbability()*p;
				ArrayList<Derivation> parseList = new ArrayList<>(); //stores one specific derivation which merges one derivation of A and B
				parseList.add(new Derivation(parent, keyArray[b] + keyArray[c]));
				
				//left half of derivation
				for (Derivation a: aList.getDerivationList()) {
					parseList.add(a);
				}
				//right half of derivaiton
				for (Derivation a: bList.getDerivationList()) {
					parseList.add(a);
				}
				
				parentList.add(new DerivationList(parseList, probability, 0));
			}
		}
		return parentList;
	}
}


