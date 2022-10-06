package clpfd

import clpfd.domain.In
import clpfd.domain.Ins
import clpfd.global.*
import clpfd.reflection.*
import clpfd.reification.*
import clpfd.relational.*
import clpfd.search.Label
import clpfd.search.Labeling
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.theory.Theory

object ClpFdLibrary: Library by Library.of(
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
        Sum,
        Not,
        Or,
        And,
        Xor,
        Iff,
        Implies,
        InverseImplies,
        FdVar,
        FdInf,
        FdSup,
        FdSize,
        FdDom,
        FdDegree
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
            TuplesInN.Recursive,
            ZCompare.Greater,
            ZCompare.Less,
            ZCompare.Equals
        ).map { it.implementation }
    )
)