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
	
	public HashMap<String, HashSet<Derivation>> getGrammar() {
		return grammar;
	}
}
