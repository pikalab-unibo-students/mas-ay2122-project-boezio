package clpVarCreation

import clpVarCreation.domain.In
import clpVarCreation.domain.Ins
import clpVarCreation.globalConstraints.*
import clpVarCreation.relationalConstraints.*
import clpVarCreation.search.Label
import clpVarCreation.search.Labeling
import it.unibo.tuprolog.solve.classic.stdlib.rule.Call
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import it.unibo.tuprolog.solve.stdlib.primitive.Univ
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
//        Univ
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
//            Call
        ).map { it.implementation }
    )
)