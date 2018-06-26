package automata;

/**
 * Deterministic Omega Automata
 * */
public interface IDA extends IA {
    
    int getInitialState();
    int getSuccessor(int state, int letter);
}
