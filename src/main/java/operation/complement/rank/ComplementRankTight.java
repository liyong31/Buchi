package operation.complement.rank;

import automata.IBuchi;

public class ComplementRankTight extends ComplementRank<StateRankTight> {

    public ComplementRankTight(IBuchi operand) {
        super(operand);
    }

    @Override
    public String getName() {
        return "ComplementRankTight";
    }

    @Override
    protected StateRankTight makeRankState(int id, LevelRanking lvlRank) {
        return new StateRankTight(this, id, lvlRank);
    }
}
