package util.parser;

import automata.IBuchi;

public interface DoubleParser extends Parser {
	
	IBuchi getFstBuchi();
	IBuchi getSndBuchi();

}
