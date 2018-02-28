package util.parser;

import util.parser.ats.ATSFileParser;
import util.parser.ba.BAFileParser;

public class UtilParser {
	
	private UtilParser() {
		
	}
	
	public static SingleParser getSinleParser(ParserType type) {
		switch(type) {
		case BA:
			return new BAFileParser();
		default:
			return new ATSFileParser();
		}
	}
	
	public static DoubleParser getDoubleParser() {
		return new ATSFileParser();
	}

}
