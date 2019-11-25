package problem10_11_12;
import java.util.*;

public class PGrammar {
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
