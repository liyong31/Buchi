package operation.complement.rank;

public interface LevelRanking {
    
    void addLevelRank(int state, int rank);
    
    int getLevelRank(int state);
   
}
