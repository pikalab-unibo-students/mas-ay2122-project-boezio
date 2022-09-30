package clpb

import it.unibo.tuprolog.solve.library.Library

object ClpBLibrary: Library by Library.of(
    alias = "prolog.clp.boolean",
    primitives = listOf(
        Sat,
        Taut,
        Labeling,
        SatCount,
        WeightedMaximum,
        RandomLabeling
    ).associate { it.descriptionPair }
)