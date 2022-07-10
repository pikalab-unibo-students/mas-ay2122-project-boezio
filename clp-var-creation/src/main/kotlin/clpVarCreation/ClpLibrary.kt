package clpVarCreation

import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library

object ClpLibrary : AliasedLibrary by Library.aliased(
    alias = "prolog.clp.int",
    primitives = listOf(In, Label, Labeling, Equals, NotEquals, GreaterThan, GreaterEquals, LessThan, LessEquals).associate { it.descriptionPair }
)