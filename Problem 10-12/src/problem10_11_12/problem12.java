package problem10_11_12;
import java.io.*;
import java.util.*;

public class problem12 {
	static ArrayList<Double>[][][] array; //dynamic programming array
	static BackPointer[][][] backList; //list of backpointers
	static String[] keyArray; //array of nonterminals
	
	//TODO: add explanation of algorithm? In PDF?
	public static void main(String[] args) throws FileNotFoundException {
		Scanner fin = new Scanner(new File("problem12Grammar.in"));
		fin.useDelimiter(":|\\n| ");
		PGrammar grammar = new PGrammar();
		
		//inputting grammar
		do  {
			String ruleleft = fin.next();
			String ruleright = fin.next();
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
	    
	    //iteratively computing probabilities
	    for (int i=0; i<10; i++) {
		    Scanner fin2 = new Scanner(new File("problem12.in"));
			while (fin2.hasNext()) {
				String derivation = fin2.next();
				ProbabilityCYK(grammar, dictionary, derivation);
			}
			fin2.close();
			
			RecalculateProbability(grammar);
	    }
	    
	    //Output probabilities
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
					Grammar.appendCounter("S", "$");
					break;
				}
			}
			return;	
		}
		
		int n = input.length();
		int r = grammar.size();
		array = new ArrayList[n+1][n+1][r+1];
		for (int i=0; i<=n; i++) {
			for (int j=0; j<=n; j++) {
				for (int k=0; k<=r; k++) {
					array[i][j][k]=new ArrayList<Double>();
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
		
		//considering length 1 substrings
		for (int s=1; s<=n; s++) {
			for (int v=1; v<=r; v++) {
				for (Derivation derived: grammar.get(keyArray[v])) {
					if (derived.derivation.equals(Character.toString(input.charAt(s-1)))) {
						array[1][s][v].add(derived.probability);
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
								
								double prob_splitting = derived.probability; //*array[p][s][b]*array[l-p][s+p][c];
								if (array[p][s][b].size()>0 && array[l-p][s+p][c].size()>0) {
									array[l][s][a].add(prob_splitting);
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
		generateLeftDerivation(probabilityDerivation, grammar, n,1,1, "S", 1, input, new ArrayList<Derivation>());
		
		ArrayList<Derivation> parseTree = chooseParseTree(probabilityDerivation);
		for (Derivation derivation: parseTree) {
			Grammar.appendCounter(derivation.getKey(), derivation.getDerivation());
		}
	}
	
	//does a single rule derivation (moves one level down parse tree)
	public static DerivationList generateLeftDerivation(TreeMap<Double, ArrayList<ArrayList<Derivation>>> probabilityDerivation, HashMap<String, HashSet<Derivation>> grammar, 
			int l, int s, int a, String derivation, double probability, String input, ArrayList<Derivation> derivationList) {		
		
		if (l==1) {
			//reaching leaf of parse tree (nonterminal to terminal derivation)
			String terminal = Character.toString(input.charAt(s-1));
			derivation=derivation.replaceFirst(keyArray[a], terminal);
			derivationList.add(new Derivation(keyArray[a],terminal));
			probability*=array[l][s][a].get(0);
			
			//last function call of parse tree
			if (s==input.length()) {
				if (!probabilityDerivation.containsKey(probability)) {
					probabilityDerivation.put(probability, new ArrayList<ArrayList<Derivation>>());
				}
				probabilityDerivation.get(probability).add(derivationList);
				//reset parse tree
				return new DerivationList(new ArrayList<Derivation>(), "S", 1);
			}
		}
		else {
			for (int i=0; i< backList[l][s][a].getPointer().size(); i++) {
				//probability calculation
				probability*=array[l][s][a].get(i);
				
				int p=backList[l][s][a].getPointer(i).get(0);
				int b=backList[l][s][a].getPointer(i).get(1);
				int c=backList[l][s][a].getPointer(i).get(2);
				
				//updating derivation
				derivation=derivation.replaceFirst(keyArray[a], keyArray[b] + keyArray[c]);
				derivationList.add(new Derivation(keyArray[a], keyArray[b]+keyArray[c]));
				
				//left terminal derivation
				DerivationList derivationObj = generateLeftDerivation(probabilityDerivation, grammar, p,s,b, derivation, probability, input, derivationList);
				derivationList = derivationObj.getArrayList();
				derivation = derivationObj.getDerivation();
				probability = derivationObj.getProbability();
				
				//right terminal derivation
				derivationObj = generateLeftDerivation(probabilityDerivation, grammar, l-p, s+p, c, derivation, probability, input, derivationList);
				derivationList = derivationObj.getArrayList();
				derivation = derivationObj.getDerivation();
				probability = derivationObj.getProbability();
			}
		}
		return new DerivationList(derivationList, derivation, probability);
	}
	
	public static ArrayList<Derivation> chooseParseTree(TreeMap<Double, ArrayList<ArrayList<Derivation>>> probabilityDerivation) {
		double totalProbability=0;
		ArrayList<ArrayList<Derivation>> parseTreeList = new ArrayList<ArrayList<Derivation>>();//list of all possible parse trees
		ArrayList<Double> probabilityList = new ArrayList<>();
		for (double probability: probabilityDerivation.keySet()) {
			for (ArrayList<Derivation> parseTree: probabilityDerivation.get(probability)) {
				parseTreeList.add(parseTree);
				probabilityList.add(probability);
				totalProbability+=probability;
			}
		}
		
		Random rand = new Random();
		double randomNumber = rand.nextDouble()*totalProbability; //generating random number to determine which rule to choose
		double counter=0;
		
		//random rule chosen calculation
		for (int i=0; i<parseTreeList.size();i++) {
			counter+=probabilityList.get(i);
			if (randomNumber<counter) {
				return parseTreeList.get(i);
			}
		}
		return null;
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



