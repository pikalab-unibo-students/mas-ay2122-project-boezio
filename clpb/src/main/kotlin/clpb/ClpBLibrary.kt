package clpb

import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.theory.Theory

object ClpBLibrary: Library by Library.of(
    alias = "prolog.clp.boolean",
    primitives = listOf(
        Sat,
        Labeling
    ).associate { it.descriptionPair }
)