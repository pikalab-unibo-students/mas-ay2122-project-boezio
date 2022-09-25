package clpqr

import clpqr.mip.BBInfFive
import clpqr.mip.BBInfFour
import clpqr.mip.BBInfThree
import clpqr.optimization.*
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.theory.Theory

object ClpQRLibrary: Library by Library.of(
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
        BBInfThree,
        Dump
    ).associate { it.descriptionPair },
    theory = Theory.of(
        listOf(
            BBInfFour
        ).map { it.implementation }
    )
)
