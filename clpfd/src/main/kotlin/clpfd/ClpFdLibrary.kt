package clpfd

import clpfd.domain.In
import clpfd.domain.Ins
import clpfd.globalConstraints.*
import clpfd.relationalConstraints.*
import clpfd.search.Label
import clpfd.search.Labeling
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
        ScalarProduct,
        TuplesIn,
        Element,
        Disjoint2,
        Circuit,
        GlobalCardinalityTwo,
        GlobalCardinalityThree,
        CumulativeTwo,
        Serialized,
        LexChain,
        Sum
    ).associate { it.descriptionPair },
    theory = Theory.of(
        listOf(
            In,
            Label,
            Chain.Base,
            Chain.Recursive,
            CumulativeOne,
            LexChainN.Base,
            LexChainN.Recursive,
            TuplesInN.Base,
            TuplesInN.Recursive
        ).map { it.implementation }
    )
)