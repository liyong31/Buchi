package operation.explore;

import java.util.LinkedList;

import automata.DOA;
import automata.StateDA;
import util.ISet;
import util.UtilISet;

public class UtilExplore {
    
    public static void explore(DOA da) {
        LinkedList<StateDA> walkList = new LinkedList<>();
        walkList.add(da.getState(da.getInitialState()));

        ISet visited = UtilISet.newISet();

        while (!walkList.isEmpty()) {
            StateDA s = walkList.remove();
            if (visited.get(s.getId()))
                continue;
            visited.set(s.getId());

            for (int letter = 0; letter < da.getAlphabetSize(); letter++) {
                int succ = s.getSuccessor(letter);
                if (!visited.get(succ)) {
                    walkList.addFirst(da.getState(succ));
                }
            }
        }
    }

}
