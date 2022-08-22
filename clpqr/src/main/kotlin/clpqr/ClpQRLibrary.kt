package clpqr

import clpqr.optimization.*
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.theory.Theory

object ClpQRLibrary: AliasedLibrary by Library.aliased(
    alias = "prolog.clp.qr",
    primitives = listOf(
        Constraint,
        Satisfy,
        Entailed,
        Inf,
        Sup,
        Minimize,
        Maximize,
        BBInfFive,
        BBInfThree
    ).associate { it.descriptionPair },
    theory = Theory.of(
        listOf(
            BBInfFour
        ).map { it.implementation }
    )
)
