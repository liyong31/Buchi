# Buchi

Buchi is a simple library for Büchi word automata. It currently implements following complementation algorithms:

1. Ramsey-based algorithms described by Sistla, Vardi and Wolper [1].

2. Rank-based algorithms described by Kupferman and Vardi [2] and Schewe [3].

3. Slice-based algorithms described by Vardi and Wilke [4] and later improved by Tsai et al. [5].

4. Tuple-based algorithm described by Allred and Ultes-Nitsche [6] which is consistent with the one in [5].

5. NCSB complementation algorithms described by Blahoudek et al. [7] and later improved by Chen et al. [8].


# References
[1] A. Prasad Sistla, Moshe Y. Vardi, and Pierre Wolper. "The complementation problem for Büchi automata with applications to temporal logic". Theoretical Computer Science, Vol 49, No 2,3 (1987), pp 217-237.

[2] Orna Kupferman and  Moshe Y. Vardi. "Weak alternating automata are not that weak". ACM Transactions on Computational Logic, Vol 2, No 3, (2001) pp 408-429. 

[3] Sven Schewe. "Büchi Complementation Made Tight". In STACS 2009, Vol 3, pp 661-672.

[4] Moshe Y. Vardi and Thomas Wilke. "Automata: From Logics to Algorithms". In Logic and Automata: History and Perspective.

[5] Ming-Hsien Tsai, Seth Fogarty, Moshe Y. Vardi, and Yih-Kuen Tsay. "State of Büchi Complementation". In Logical Method in Computer Science, Vol. 10(4:13)2014, pp. 1–27.

[6] Joel Allred and Ulrich Ultes-Nitsche. "Complementing Buchi Automata with a Subset-tuple Construction".

[7] František Blahoudek, Matthias Heizmann, Sven Schewe, Jan Strejček and Ming-Hsien Tsai. "Complementing Semi-deterministic Büchi Automata". In TACAS 2016, pp 770-787.

[8] Yu-Fang Chen, Matthias Heizmann, Ondřej Lengál, Yong Li, Ming-Hsien Tsai, Andrea Turrini and Lijun Zhang. "Advanced Automata-based Algorithms for Program Termination Checking". In PLDI, pp 135-150. 
