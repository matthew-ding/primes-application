package problem10_11_12;
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
 */
//PROBLEM 10, 11, 12 HELPER CLASS
//stores a probabilistic free grammar and associated methods

public class PGrammar {
	//key: nonterminal of the leftside of a rule
	//value: stores a Derivation object, which contains the generated string and probability
	HashMap<String, HashSet<Derivation>> grammar = new HashMap<>();
	
	//adding rule to grammar
	public void Add(String key, String value, double probability) {
		if (!grammar.containsKey(key)) {
			grammar.put(key, new HashSet<Derivation>());
		}
		grammar.get(key).add(new Derivation(value, probability));
	}
	
	//accessing HashMap
	public HashMap<String, HashSet<Derivation>> getGrammar() {
		return grammar;
	}
	
	//Setting probability of specific rule
	public void setProbability(String key, String value, double probability) {
		for (Derivation derivation: grammar.get(key)) {
			if (derivation.getDerivation().equals(value)) {
				derivation.setProbability(probability);
				break;
			}
		}	
	}
	
	//appends counter of specific rule by amount
	public void appendCounter(String key, String value) {
		for (Derivation derivation: grammar.get(key)) {
			if (derivation.getDerivation().equals(value)) {
				derivation.appendCounter();
				break;
			}
		}
	}
	
	public double getProbability(String key, String value) {
		for (Derivation derivation: grammar.get(key)) {
			if (derivation.getDerivation().equals(value)) {
				return derivation.getProbability();
			}
		}
		return -1;
	}
}
