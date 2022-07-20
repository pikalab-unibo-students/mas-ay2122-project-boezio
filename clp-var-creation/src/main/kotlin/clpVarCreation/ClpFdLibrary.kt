package clpVarCreation

import clpVarCreation.domain.In
import clpVarCreation.domain.Ins
import clpVarCreation.globalConstraints.*
import clpVarCreation.relationalConstraints.*
import clpVarCreation.search.Label
import clpVarCreation.search.Labeling
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.theory.Theory

object ClpFdLibrary : AliasedLibrary by Library.aliased(
    alias = "prolog.clp.int",
    primitives = listOf(
        Ins,
        Labeling,
        Equals,
        NotEquals,
        GreaterThan,
        GreaterEquals,
        LessThan,
        LessEquals,
        AllDistinct,
        Sum,
        ScalarProduct,
        LexChain,
        TuplesIn,
        Element,
        Disjoint2,
        Circuit
    ).associate { it.descriptionPair },
    theory = Theory.of(
        listOf(
            In,
            Label
        ).map { it.implementation }
    )
)