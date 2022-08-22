package clpqr

import clpqr.optimization.*
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library

object ClpQRLibrary: AliasedLibrary by Library.aliased(
    alias = "prolog.clp.qr",
    primitives = listOf(
        Constraint,
        Satisfy,
        Entailed,
        Inf,
        Sup,
        Inf,
        Sup,
        Minimize,
        Maximize
    ).associate { it.descriptionPair })
