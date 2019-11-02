//https://sarielhp.org/teach/07/b_spring_08/Lectures/lect_15.pdf
//CNF Convert Algorithm
//Methods are associated with a given section in the paper

import java.util.*;

public class Grammar {
	HashMap<String, HashSet<String>> grammar = new HashMap<>();
	
	//tracks which variables have been outputed by newNonTerminal method
	boolean[] variablesUsed = new boolean[90];
	
	//1.1 Outline of conversion algorithm
	public void CNFConvert() {
		Add("S0", "S");
		System.out.println("A");
		System.out.println(grammar);
		System.out.println();
		System.out.println();
		removeNullable();
		System.out.println("B");
		System.out.println(grammar);
		System.out.println();
		System.out.println();
		removeAllUnit();
		System.out.println("C");
		System.out.println(grammar);
		System.out.println();
		System.out.println();
		RestructureA();
		System.out.println("D");
		System.out.println(grammar);
		System.out.println();
		System.out.println();
		RestructureB();
		System.out.println("E");
		System.out.println(grammar);
		System.out.println();
		System.out.println();
		System.out.println(grammar);
	}

	//1.4: Final restructuring Part 1: fixing 2+ symbols on right hand side w/ at least one terminal
	public void RestructureA() {
		//stores new rules that need to be added
		HashMap<String, String> toAdd = new HashMap<String, String>();
		HashSet<Character> checkedTerminals = new HashSet<Character>();
		
		for (String key: grammar.keySet()) {
			HashMap<String, String> toReplace = new HashMap<>();
			for (String derivation: grammar.get(key)) {
				if (derivation.length()>=2) {
					for (int i=0; i<derivation.length();i++) {
						char currentChar = derivation.charAt(i);
						if (Character.isLowerCase(currentChar)) {
							if (!checkedTerminals.contains(currentChar)) {
								Replace(Character.toString(currentChar), toAdd, toReplace);
								checkedTerminals.add(currentChar);
							}
						}
					}
				}
			}
			
			for (String derivation: toReplace.keySet()) {
				if (!derivation.equals(toReplace.get(derivation))) {
					grammar.get(key).add(toReplace.get(derivation));
					grammar.get(key).remove(derivation);
				}
			}
		}
		
		//adds all new rules that were previously stored
		for (String leftRule: toAdd.keySet()) {
			Add(leftRule, toAdd.get(leftRule));
		}
	}
	
	//1.4 Part 2: fixing having two or more variables on righthand side
	public void RestructureB() {
		HashMap<String, String> toAdd = new HashMap<>();
		HashMap<String, String> toDelete = new HashMap<>();
		
		//Stores all derivations that have been gone over
		//avoids adding unecessary variables identical derivations of different keys
		//HashSet<String> checked = new HashSet<>();
		
		for (String key: grammar.keySet()) {
			for (String derivation: grammar.get(key)) {
				if (derivation.length()>=3) {
					if (!toDelete.containsValue(derivation)) {
						//contains keys that also derive the current derivation
						HashSet<String> keys = obtainKeys(derivation);
						
						for (String deleteKey: keys) {
							toDelete.put(deleteKey, derivation);
						}
						
						String currentNewVariable = newNonTerminal();
						for (int i=0; i<derivation.length()-1; i++) {
							if (i==0) {
								String newDerivation = Character.toString(derivation.charAt(i))+currentNewVariable;
								for (String deleteKey: keys) {
									toAdd.put(deleteKey, newDerivation);
								}
							}
							else if (i==derivation.length()-2) {
								String newDerivation = Character.toString(derivation.charAt(i)) + Character.toString(derivation.charAt(i+1));
								toAdd.put(currentNewVariable, newDerivation);
							}
							else {
								String n = currentNewVariable;
								currentNewVariable = newNonTerminal();
								String newDerivation = Character.toString(derivation.charAt(i))+currentNewVariable;
								toAdd.put(n, newDerivation);
							}
						}
					}
				}
			}
		}
		//updating grammar
		for (String rule:toAdd.keySet()) {
			Add(rule, toAdd.get(rule));
		}
		for (String rule:toDelete.keySet()) {
			Delete(rule, toDelete.get(rule));
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
	public void Replace(String terminal, HashMap<String, String> toAdd, HashMap<String, String> toReplace) {
		String newKey;
		if (!grammar.containsKey(terminal.toUpperCase())) {
			newKey = terminal.toUpperCase();
			toAdd.put(newKey, terminal);
		}
		else {
			newKey = newNonTerminal();
			toAdd.put(newKey , terminal);
		}
		
		for (String key: grammar.keySet()) {
			//list of strings with terminals that need to be replaced
			for (String derivation: grammar.get(key)) {
				if (derivation.length()>=2) {
					toReplace.put(derivation,derivation.replace(terminal, newKey));
				}
			}
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
		PriorityQueue<String> toCheck = new PriorityQueue<>();
		for (String key : grammar.keySet()) {
			toCheck.add(key);
		}

		while (!toCheck.isEmpty()) {
			HashSet<String> toDelete = new HashSet<>();
			String currentKey = toCheck.poll();
			boolean removed = false;
			for (String derivation : grammar.get(currentKey)) {
				if (derivation.length() == 1 && Character.isUpperCase(derivation.charAt(0))) {
					toDelete.add(derivation);
					//removeUnitPair(currentKey, derivation);
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

		// BFS search for nullable variables
		while (toCheck.size() != 0) {
			String currentCheck = toCheck.poll();
			for (String key : grammar.keySet()) {
				for (String derivation : grammar.get(key)) {
					if (derivation.equals(currentCheck)) {
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

				// iterating through all subsets of nullable variables in each derivation
				// adds a new derivation for each one
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

		for (String key : grammar.keySet()) {
			Delete(key, "$");
		}

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

	public String buildNewString(String s, HashSet<Integer> excluded) {
		String newString = "";

		for (int i = 0; i < s.length(); i++) {
			if (!excluded.contains(i)) {
				newString += s.charAt(i);
			}
		}

		return newString;
	}

	public void Add(String key, String value) {
		if (!grammar.containsKey(key)) {
			grammar.put(key, new HashSet<String>());
		}
		grammar.get(key).add(value);
	}

	public void Delete(String key, String value) {
		grammar.get(key).remove(value);
		if (grammar.get(key).isEmpty()) {
			grammar.remove(key);
		}
	}

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
