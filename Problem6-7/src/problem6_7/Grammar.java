package problem6_7;
import java.util.*;

/* PROBLEM 6 AND 7 HELPER CLASS
 * Overview:
 * This class stores a Context Free Grammar within a HashMap and associated conversion methods
 * 
 * Citation for CNF Conversion Algorithm:
 * https://sarielhp.org/teach/07/b_spring_08/Lectures/lect_15.pdf
 * Method names in this class are associated with a specific section in the paper
*/

public class Grammar {
	//keys for the left-side nonterminals
	//value is a set of all right-side strings that the key can generate
	private HashMap<String, HashSet<String>> grammar = new HashMap<>();
	boolean derivesNull = false; //true if empty string is in language
	
	//tracks which variables have been outputed by newNonTerminal method
	boolean[] variablesUsed = new boolean[90];
	
	//1.1 Outline of conversion algorithm
	public void CNFConvert() {
		//begin by deleting all rules that map a nonterminal to itself, as these are useless rules
		HashMap<String, HashSet<String>> toDelete = new HashMap<>();
		for (String key: grammar.keySet()) {
			for (String derivation: grammar.get(key)) {
				if (key.equals(derivation)) {
					if (!toDelete.containsKey(key)) {
						toDelete.put(key, new HashSet<String>());
					}
					toDelete.get(key).add(derivation);
				}
			}
		}
		
		for (String key: toDelete.keySet()) {
			for (String derivation: toDelete.get(key)) {
				Delete(key,derivation);
			}
		}
		
		//if the grammar has deleted symbols, we remove all rules that contain these symbols
		while (true) {
			if (!removeEmptyNonterminal()) {
				break;
			}
		}
		
		Add("S0", "S"); //add new start nonterminal
		removeNullable(); //removing nullable pairs
		
		//if the grammar has deleted symbols, we remove all rules that contain these symbols
		while (true) {
			if (!removeEmptyNonterminal()) {
				break;
			}
		}

		removeAllUnit(); //removing unit pairs
		RestructureA(); //replacing derivations 2+ characters and at least 1 terminal
		RestructureB(); //replacing derivations with >2 nonterminals
		
		if (derivesNull) {
			Add("S0", "$");
		}
	}
	
	public HashMap<String, HashSet<String>> getGrammar() {
		return grammar;
	}

	//1.4: Final restructuring Part 1: fixing 2+ symbols on right hand side w/ at least one terminal
	public void RestructureA() {
		//stores new rules that need to be added
		HashMap<String, String> toAdd = new HashMap<String, String>();
		
		HashSet<Character> checkedTerminals = new HashSet<Character>();
		
		//if we find a derivation that is >=size 2 and has terminals
		//we replace the terminal with an associated nonterminal that derives it
		for (String key: grammar.keySet()) {
			for (String derivation: grammar.get(key)) {
				if (derivation.length()>=2) {
					for (int i=0; i<derivation.length();i++) {
						char currentChar = derivation.charAt(i);
						if (Character.isLowerCase(currentChar)) {
							if (!checkedTerminals.contains(currentChar)) {
								Replace(Character.toString(currentChar), toAdd);
								checkedTerminals.add(currentChar);
							}
						}
					}
				}
			}
		}
		
		//adds all new rules that were previously stored in toAdd
		for (String newKey:toAdd.keySet()) {
			for (String key: grammar.keySet()) {
				HashSet<String> newDerivations = new HashSet<String>();
				
				//list of strings with terminals that need to be replaced
				for (String derivation: grammar.get(key)) {
					if (derivation.length()>=2) {
						String replacement = derivation.replace(toAdd.get(newKey), newKey);
						if (!replacement.equals(derivation)) {
							newDerivations.add(replacement);
							newDerivations.remove(derivation);
						}
						else {
							newDerivations.add(derivation);
						}	
					}
					else {
						newDerivations.add(derivation);
					}
				}
				grammar.put(key, newDerivations);
			}
		}
	
		for (String newKey: toAdd.keySet()) {
			Add(newKey, toAdd.get(newKey));
		}
	}
	
	//1.4 Part 2: fixing having two or more variables on righthand side
	public void RestructureB() {
		//map of rules to be added at the end of the method
		HashMap<String, HashSet<String>> toAdd = new HashMap<>();
		//map of rules to be deleted at the end of the method
		HashMap<String, HashSet<String>> toDelete = new HashMap<>();
		//Both maps are <key, derivation> pairs
		//can add/delete multiple derivations from 1 key, so we have a set for the value
		
		//Stores all derivations that have been gone over
		//avoids adding unecessary variables identical derivations of different keys
		HashSet<String> checked = new HashSet<>();
		
		for (String key: grammar.keySet()) {
			for (String derivation: grammar.get(key)) {
				if (derivation.length()>=3) {
					if (!checked.contains(derivation)) {
						//contains keys that also derive the current derivation
						//avoids unnecessary duplicate derivations
						HashSet<String> keys = obtainKeys(derivation);
						
						for (String deleteKey: keys) {
							if (!toDelete.containsKey(deleteKey)) {
								toDelete.put(deleteKey, new HashSet<String>());
							}
							toDelete.get(deleteKey).add(derivation);
						}
						
						checked.add(derivation);
						
						//substituting string of 3+ nonterminals with replacement sequence
						//i.e.: ABC becomes AD, where D->BC
						String currentNewVariable = newNonTerminal();
						for (int i=0; i<derivation.length()-1; i++) {
							//beginning of derivation
							if (i==0) {
								String newDerivation = Character.toString(derivation.charAt(i))+currentNewVariable;
								for (String deleteKey: keys) {
									if (!toAdd.containsKey(deleteKey)) {
										toAdd.put(deleteKey, new HashSet<String>());
									}
									toAdd.get(deleteKey).add(newDerivation);
								}
							}
							//end of derivation
							else if (i==derivation.length()-2) {
								String newDerivation = Character.toString(derivation.charAt(i)) + Character.toString(derivation.charAt(i+1));
								if (!toAdd.containsKey(currentNewVariable)) {
									toAdd.put(currentNewVariable, new HashSet<String>());
								} 
								toAdd.get(currentNewVariable).add(newDerivation);
							}
							//middle of derivation
							else {
								String n = currentNewVariable;
								currentNewVariable = newNonTerminal();
								String newDerivation = Character.toString(derivation.charAt(i))+currentNewVariable;
								if (!toAdd.containsKey(n)) {
									toAdd.put(n, new HashSet<String>());
								}
								toAdd.get(n).add(newDerivation);
							}
						}
					}
				}
			}
		}
		
		//updating grammar
		for (String rule:toAdd.keySet()) {
			for (String derivation: toAdd.get(rule)) {
				Add(rule, derivation);
			}
		}
		for (String rule:toDelete.keySet()) {
			for (String derivation: toDelete.get(rule)) {
				Delete(rule, derivation);
			}
		}
	}
	
	//returns set of all keys that derive the inputted string
	public HashSet<String> obtainKeys(String n) {
		HashSet<String> output = new HashSet<>();
		for (String key: grammar.keySet()) {
			for (String derivation: grammar.get(key)) {
				if (derivation.equals(n)) {
					output.add(key);
				}
			}
		}
		return output;
	}
	
	//replaces a specific terminal with a newly generated non-terminal
	//adds new rule: non-terminal -> terminal, into a list of rules to be added
	public void Replace(String terminal, HashMap<String, String> toAdd) {
		String newKey;
		//TODO: double check this if
		if (!grammar.containsKey(terminal.toUpperCase()) && !toAdd.containsKey(terminal.toUpperCase())) {
			newKey = terminal.toUpperCase();
			toAdd.put(newKey, terminal);
		}
		else {
			newKey = newNonTerminal();
			toAdd.put(newKey, terminal);
		}
	}
	
	//generating a new non-terminal that doesn't currently exist in the grammar
	public String newNonTerminal() {
		int ascii = 65;
		
		while (grammar.containsKey(Character.toString((char)ascii)) || variablesUsed[ascii]) {
			ascii++;
		}
		
		variablesUsed[ascii]=true;
		return Character.toString((char)ascii);
	}
	
	//1.3: Removing unit pairs
	public void removeAllUnit() {
		//if a nonterminal's derivations has changed, it is added to toCheck, because
		//it may contain new unit pairs itself
		PriorityQueue<String> toCheck = new PriorityQueue<>();
		
		for (String key : grammar.keySet()) {
			toCheck.add(key);
		}
		
		//if we find a unit pair A->B, we add everything that B derives to A's derivations
		while (!toCheck.isEmpty()) {
			HashSet<String> toDelete = new HashSet<>();
			String currentKey = toCheck.poll();
			boolean removed = false; //true if any derivation has been removed
			for (String derivation : grammar.get(currentKey)) {
				if (derivation.length() == 1 && Character.isUpperCase(derivation.charAt(0))) {
					toDelete.add(derivation);
					removed = true;
				}
			}
			for (String derivation: toDelete) {
				removeUnitPair(currentKey, derivation);
			}
			if (removed) {
				toCheck.add(currentKey);
			}
		}
	}

	//removes unit pair and replaces the derived value with all derivations it has
	public void removeUnitPair(String key, String value) {
		for (String derivation : grammar.get(value)) {
			Add(key, derivation);
		}
		Delete(key, value);
	}

	//1.2: Removing nullable variables
	public void removeNullable() {
		LinkedList<String> nullablelist = new LinkedList<>();
		PriorityQueue<String> toCheck = new PriorityQueue<>();
		toCheck.add("$");

		//BFS search for nullable variables
		while (toCheck.size() != 0) {
			String currentCheck = toCheck.poll();
			for (String key : grammar.keySet()) {
				for (String derivation : grammar.get(key)) {
					if (derivation.equals(currentCheck)) {
						
						//if S0 derives empty string then it is in the language
						if (key.equals("S0")) {
							derivesNull=true;
						}
						nullablelist.add(key);
						toCheck.add(key);
						break;
					}
				}
			}
		}
		
		for (String key : grammar.keySet()) {
			HashSet<String> toAdd = new HashSet<>();
			for (String derivation : grammar.get(key)) {
				LinkedList<Integer> currentNullableSet = findNullable(derivation, nullablelist);
				int size = currentNullableSet.size();

				//iterating through all subsets of nullable variables in each derivation
				//adds a new derivation for each subset removed
				for (int i = 1; i < Math.pow(2, size); i++) {
					String excludedString = binaryConvert(size, i);
					HashSet<Integer> currentExcluded = new HashSet<>();
					for (int j = 0; j < size; j++) {
						if (excludedString.charAt(j) == ('1')) {
							currentExcluded.add(currentNullableSet.get(j));
						}
					}
					
					String newDerivation = buildNewString(derivation, currentExcluded);
					if (!newDerivation.equals(key) && !newDerivation.equals("")) {
						toAdd.add(newDerivation);
					}
				}
			}
			for (String i : toAdd) {
				Add(key, i);
			}
		}
		
		HashSet<String> deleteKeys = new HashSet<>(); //set of keys that only derive empty string
		for (String key : grammar.keySet()) {
			//if the empty string is the only derivation of a key
			if (grammar.get(key).contains("$") && grammar.get(key).size()==1) {
				deleteKeys.add(key);
			}
			else {
				Delete(key, "$");
			}
		}
		
		for (String key: deleteKeys) {
			grammar.remove(key);
		}
	}
	
	//removes terminals that are empty (derive nothing) after removing nullables
	//we remove derivations that have an empty nonterminal
	public boolean removeEmptyNonterminal() {
		boolean removed=false; //returns true if this function changes the grammar in any way
		HashMap<String, String> toRemove = new HashMap<>();
		for (String key: grammar.keySet()) {
			for (String derivation: grammar.get(key)) {
				for (int i=0; i<derivation.length();i++) {
					if (!grammar.containsKey(Character.toString(derivation.charAt(i))) && Character.isUpperCase(derivation.charAt(i))) {
						toRemove.put(key, derivation);
						removed=true;
					}
				}
			}
		}
		for (String key: toRemove.keySet()) {
			Delete(key, toRemove.get(key));
		}
		return removed;
		
	}

	// finds indices of all nullable variables in a given string
	public LinkedList<Integer> findNullable(String s, LinkedList<String> nullablelist) {
		LinkedList<Integer> indexlist = new LinkedList<Integer>();
		for (int i = 0; i < s.length(); i++) {
			for (String j : nullablelist) {
				if (String.valueOf(s.charAt(i)).equals(j)) {
					indexlist.add(i);
					break;
				}
			}
		}
		return indexlist;
	}

	//constructs a string by removing specific characters
	public String buildNewString(String s, HashSet<Integer> excluded) {
		String newString = "";

		for (int i = 0; i < s.length(); i++) {
			if (!excluded.contains(i)) {
				newString += s.charAt(i);
			}
		}

		return newString;
	}

	//adding rule to grammar
	public void Add(String key, String value) {
		if (!grammar.containsKey(key)) {
			grammar.put(key, new HashSet<String>());
		}
		grammar.get(key).add(value);
	}

	//deleting rule from grammar
	public void Delete(String key, String value) {
		grammar.get(key).remove(value);
		if (grammar.get(key).isEmpty()) {
			grammar.remove(key);
		}
	}

	//converts a number of binary, with leading zeroes given the inputted size
	//this is used to construct all subsequences of a string to delete nullables
	public String binaryConvert(int size, int number) {
		String output = "";
		while (number > 0) {
			output = number % 2 + output;
			number /= 2;
		}

		while (output.length() < size) {
			output = 0 + output;
		}

		return output;
	}
}
