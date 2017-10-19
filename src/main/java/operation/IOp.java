package operation;

public interface IOp {
    
    default String startMessage() {
        return "Start operation " + getOperantionName();
    }

    default String exitMessage() {
        return "Finished operation " + getOperantionName();
    }
    
    String getOperantionName();

}
