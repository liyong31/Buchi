package util.parser;

import java.util.List;

public interface Parser {
	
	void parse(String file);
	
	List<String> getAlphabet();

}
