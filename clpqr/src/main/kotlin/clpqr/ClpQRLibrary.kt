package clpqr

import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library

object ClpQRLibrary: AliasedLibrary by Library.aliased(
    alias = "prolog.clp.qr",
    primitives = listOf(
        Constraint,
        Satisfy,
        Entailed
    ).associate { it.descriptionPair })
