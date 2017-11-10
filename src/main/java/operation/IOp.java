package operation;

public interface IOp {
    
    default String startMessage() {
        return "Start operation " + getName();
    }

    default String exitMessage() {
        return "Finished operation " + getName();
    }
    
    String getName();

}
