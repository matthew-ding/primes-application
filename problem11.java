import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class problem11 {

	static ArrayList<Double>[][][] array;
	static BackPointer[][][] backList;
	static String[] keyArray;
	
	public static void main(String[] args) throws FileNotFoundException {
		//TODO: CHANGE FILE
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
	    
	    //TODO: make repeated code (i.e. input) into a function
	    
	    //input of number of strings to output
	    Scanner cin = new Scanner(System.in);
	    System.out.println("Input string: ");
	    String n = cin.next();
	    cin.close();
	    HashMap<String, HashSet<Derivation>> dictionary= grammar.getGrammar();
	
	    //System.out.println(n);
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
		
		//Arrays.fill(array, new ArrayList<Double>());
		backList = new BackPointer[n+1][n+1][r+1];
		for (int i=0; i<=n; i++) {
			for (int j=0; j<=n; j++) {
				for (int k=0; k<=r; k++) {
					backList[i][j][k]=new BackPointer();
				}
			}
		}

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
		
		//using backpointers to DFS trace through all parse trees
		if (array[n][1][1].size()==0) {
			System.out.println("Not in the language");
			return;
		}
		else {
			//key: probability, value: arraylist of derivations
			//sorts by descending probability
			TreeMap<Double, ArrayList<String>> probabilityDerivation = new TreeMap<>();
			generateLeftDerivation(probabilityDerivation, grammar, n,1,1, "S", 1, input, "S");
			
			//output
			for (double probability: probabilityDerivation.descendingKeySet()) {
				for (String derived: probabilityDerivation.get(probability)) {
					System.out.println("Derivation: " + derived);
					System.out.println("Probability: " + probability);
					System.out.println();
				}
			}
		}
	}
	
	//does a single rule derivation (moves one level down parse tree)
	public static DerivationList generateLeftDerivation(TreeMap<Double, ArrayList<String>> probabilityDerivation, HashMap<String, HashSet<Derivation>> grammar, 
			int l, int s, int a, String derivation, double probability, String input, String derivationList) {		
		
		if (l==1) {
			//reaching leaf of parse tree (nonterminal to terminal derivation)
			String terminal = Character.toString(input.charAt(s-1));
			derivation=derivation.replaceFirst(keyArray[a], terminal);
			derivationList+= " - > " + derivation;
			probability*=array[l][s][a].get(0);
			
			//last function call of parse tree
			if (s==input.length()) {
				if (!probabilityDerivation.containsKey(probability)) {
					probabilityDerivation.put(probability, new ArrayList<String>());
				}
				probabilityDerivation.get(probability).add(derivationList);
				//reset parse tree
				return new DerivationList("S", "S", 1);
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
				derivationList+= " - > " + derivation;
				
				//left terminal derivation
				DerivationList derivationObj = generateLeftDerivation(probabilityDerivation, grammar, p,s,b, derivation, probability, input, derivationList);
				derivationList = derivationObj.getDerivationList();
				derivation = derivationObj.getDerivation();
				probability = derivationObj.getProbability();
				
				//right terminal derivation
				derivationObj = generateLeftDerivation(probabilityDerivation, grammar, l-p, s+p, c, derivation, probability, input, derivationList);
				derivationList = derivationObj.getDerivationList();
				derivation = derivationObj.getDerivation();
				probability = derivationObj.getProbability();
			}
		}
		return new DerivationList(derivationList, derivation, probability);
	}
}
