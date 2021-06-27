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

 * PROBLEM 11 MAIN CLASS
 * Overview:
 * Inputs a PCFG in CNF form and a string
 * Outputs all possible derivations of the string from highest to lowest probability to the console
 * 
 * We first use the CYK algorithm to build the parse forest for the string
 * We then build this forest bottom up by storing all partial derivations of a node at that node, along with probabilities
 * i.e. the leaf nodes store a nonterminal -> terminal rule
 * i.e. the root node stores all derivations of the string starting from the start symbol
 * 
 * NOTE: Possible memory with very large inputs, please see Problem 11.1 section of solution PDF
 */

public class problem11 {

	static ArrayList<Double>[][][] probabilityArray; //list of probabilties for each backpointer for each node
	static BackPointer[][][] backList; //list of backpointers
	static String[] keyArray; //array of nonterminals
	static ArrayList<DerivationList>[][][] derivForest; //stores all parse trees up to given node in CYK array
	static boolean[][][] checkedDerivForest; //tracks which nodes have been visited in derivForest
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner fin = new Scanner(new File("problem11.in"));
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
	    
	    //input of number of strings to output
	    Scanner cin = new Scanner(System.in);
	    System.out.println("Input string: ");
	    String n = cin.next();
	    cin.close();
	    
	    //Probabilistic CFG
	    HashMap<String, HashSet<Derivation>> dictionary= grammar.getGrammar();
	    
	    //creating list of nonterminals
	    keyArray = createKeyArray(dictionary);
	
	    //Generating all parse trees
	    ProbabilityCYK(dictionary, n);
	}
	
	//https://en.wikipedia.org/wiki/CYK_algorithm
	public static void ProbabilityCYK(HashMap<String, HashSet<Derivation>> grammar, String input) {
		if (input.equals("$")) {
			boolean derivesEmpty=false;
			for (Derivation derivation: grammar.get("S")) {
				if (derivation.getDerivation().equals("$")) {
					derivesEmpty=true;
					System.out.println("Derivation: S -> $");
					System.out.println("Probability: " + derivation.getProbability());
					System.out.println();
				}
			}
			
			if (!derivesEmpty) {
				System.out.println("Not in the language");
			}
			
			return;	
		}
		
		//initializing variables and arrays
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
					derivForest[i][j][k]=new ArrayList<DerivationList>();
				}
			}
		}
		
		backList = new BackPointer[n+1][n+1][r+1];
		for (int i=0; i<=n; i++) {
			for (int j=0; j<=n; j++) {
				for (int k=0; k<=r; k++) {
					backList[i][j][k]=new BackPointer();
				}
			}
		}
		checkedDerivForest = new boolean[n+1][n+1][r+1];
		
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
						probabilityArray[1][s][v].add(derived.probability);
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
								
								double prob_splitting = derived.probability;
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
		
		//using backpointers to DFS trace through all parse trees
		if (probabilityArray[n][1][1].size()==0) {
			System.out.println("Not in the language");
			return;
		}
		else {
			//key: probability, value: arraylist of derivations
			//sorts by descending probability
			TreeMap<Double, HashSet<String>> parseTreeMap = new TreeMap<>();

			generateLeftDerivation(grammar, n,1,1, input); //beginning parsing of tree
			
			//adding all parse trees to map
			for (DerivationList parseTree: derivForest[n][1][1]) {
				if (!parseTreeMap.containsKey(parseTree.getProbability())) {
					parseTreeMap.put(parseTree.getProbability(), new HashSet<String>());
				}
				parseTreeMap.get(parseTree.getProbability()).add(parseTree.printArrayList());
			}
			
			//output
			for (double probability: parseTreeMap.descendingKeySet()) {
				for (String derived: parseTreeMap.get(probability)) {
					System.out.println("Derivation: " + derived);
					System.out.println("Probability: " + probability);
					System.out.println();
				}
			}
		}
	}
	
	//does a single rule derivation (moves one level down parse tree)
	//DFS search down the tree, builds derivations bottom up, where final derivations will be stored at the root
	public static void generateLeftDerivation(HashMap<String, HashSet<Derivation>> grammar, int l, int s, int a, String input) {		
		if (l==1) {
			if (!checkedDerivForest[l][s][a]) {
				//reaching leaf of parse tree (nonterminal to terminal derivation)
				String terminal = Character.toString(input.charAt(s-1));
				ArrayList<String> terminalDeriv = new ArrayList<>(); //stores a single nonterminal->terminal rule
				terminalDeriv.add(keyArray[a]);
				terminalDeriv.add(terminal);
				derivForest[l][s][a].add(new DerivationList(terminalDeriv, probabilityArray[l][s][a].get(0)));
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
				if (!checkedDerivForest[l][s][a]) {//checking if this node hasn't already been visited
					derivForest[l][s][a].addAll(combineNodes(derivForest[p][s][b], derivForest[l-p][s+p][c], keyArray[a], probabilityArray[l][s][a].get(i)));
				}
			}
			checkedDerivForest[l][s][a]=true; //marking node as visited
		}
	}
	
	//creating order list of nonterminals for CYK
	public static String[] createKeyArray(HashMap<String, HashSet<Derivation>> grammar) {
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
		String[] outputKeyArray = new String[r+1]; //array of nonterminals
		for (int i=1; i<=r; i++) {
			outputKeyArray[i]=tempKeyArray[i-1];
		}
		
		return outputKeyArray;
	}
	
	//creates all possible derivations by merging to child nodes together
	public static ArrayList<DerivationList> combineNodes(ArrayList<DerivationList> childA, ArrayList<DerivationList> childB, String parent, double p) {
		ArrayList<DerivationList> parentList = new ArrayList<>(); //stores all final derivations
		
		//looping through all pairs of both child nodes
		for (DerivationList aList: childA) {
			for (DerivationList bList: childB) {
				double probability = aList.getProbability()*bList.getProbability()*p;
				ArrayList<String> parseList = new ArrayList<>(); //stores one specific derivation which merges one derivation of A and B
				parseList.add(parent);
				
				//left half of derivation
				for (String a: aList.getArrayList()) {
					parseList.add(a + bList.getArrayList().get(0));
				}
				//right half of derivation
				for (int i=1; i<bList.getArrayList().size(); i++) {
					parseList.add(aList.getArrayList().get(aList.getArrayList().size()-1) + bList.getArrayList().get(i));
				}
				
				//adding complete derivation to list
				parentList.add(new DerivationList(parseList, probability));
			}
		}
		return parentList;
	}
}
