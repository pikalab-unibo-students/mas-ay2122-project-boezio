package clpqr

import clpqr.optimization.Inf
import clpqr.optimization.Sup
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library

object ClpQRLibrary: AliasedLibrary by Library.aliased(
    alias = "prolog.clp.qr",
    primitives = listOf(
        Constraint,
        Satisfy,
        Entailed,
        Inf,
        Sup
    ).associate { it.descriptionPair })
