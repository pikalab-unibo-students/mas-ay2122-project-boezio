package clpfd

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.flags.invoke
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test

class CPModelTest {

    private val theoryParser = ClausesParser.withDefaultOperators()
    private val termParser = TermParser.withDefaultOperators()

    @Test
    fun testCPModel(){

        val theory = Theory.of(
            Rule.of(
                termParser.parseStruct("problem(P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P133, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155, P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P233, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255, P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P333, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355, P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P433, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455)"),
                termParser.parseStruct("ins([P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P133, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155, P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P233, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255, P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P333, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355, P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P433, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455],'..'(0,2))"),
                termParser.parseStruct("global_cardinality([P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155], ['-'(1,7),'-'(2,7),'-'(0,11)])"),
                termParser.parseStruct("global_cardinality([P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255], ['-'(1,6),'-'(2,8),'-'(0,11)])"),
                termParser.parseStruct("global_cardinality([P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355], ['-'(1,5),'-'(2,7),'-'(0,13)])"),
                termParser.parseStruct("global_cardinality([P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455], ['-'(1,7),'-'(2,3),'-'(0,15)])"),
                termParser.parseStruct("all_distinct_except_0([P111,P211,P311,P411])"),
                termParser.parseStruct("all_distinct_except_0([P112,P212,P312,P412])"),
                termParser.parseStruct("all_distinct_except_0([P113,P213,P313,P413])"),
                termParser.parseStruct("all_distinct_except_0([P114,P214,P314,P414])"),
                termParser.parseStruct("all_distinct_except_0([P115,P215,P315,P415])"),
                termParser.parseStruct("all_distinct_except_0([P121,P221,P321,P421])"),
                termParser.parseStruct("all_distinct_except_0([P122,P222,P322,P422])"),
                termParser.parseStruct("all_distinct_except_0([P123,P223,P323,P423])"),
                termParser.parseStruct("all_distinct_except_0([P124,P224,P324,P424])"),
                termParser.parseStruct("all_distinct_except_0([P125,P225,P325,P425])"),
                termParser.parseStruct("all_distinct_except_0([P131,P231,P331,P431])"),
                termParser.parseStruct("all_distinct_except_0([P132,P232,P332,P432])"),
                termParser.parseStruct("all_distinct_except_0([P133,P233,P333,P433])"),
                termParser.parseStruct("all_distinct_except_0([P134,P234,P334,P434])"),
                termParser.parseStruct("all_distinct_except_0([P135,P235,P335,P435])"),
                termParser.parseStruct("all_distinct_except_0([P141,P241,P341,P441])"),
                termParser.parseStruct("all_distinct_except_0([P142,P242,P342,P442])"),
                termParser.parseStruct("all_distinct_except_0([P143,P243,P343,P443])"),
                termParser.parseStruct("all_distinct_except_0([P144,P244,P344,P444])"),
                termParser.parseStruct("all_distinct_except_0([P145,P245,P345,P445])"),
                termParser.parseStruct("all_distinct_except_0([P151,P251,P351,P451])"),
                termParser.parseStruct("all_distinct_except_0([P152,P252,P352,P452])"),
                termParser.parseStruct("all_distinct_except_0([P153,P253,P353,P453])"),
                termParser.parseStruct("all_distinct_except_0([P154,P254,P354,P454])"),
                termParser.parseStruct("all_distinct_except_0([P155,P255,P355,P455])"),
                termParser.parseStruct("#\\/(#\\/(#\\/(#\\/(tuples_in([[P111, P121, P131, P141, P151]],[[0,0,0,0,0]]),tuples_in([[P112, P122, P132, P142, P152]],[[0,0,0,0,0]])),tuples_in([[P113, P123, P133, P143, P153]],[[0,0,0,0,0]])),tuples_in([[P114, P124, P134, P144, P154]],[[0,0,0,0,0]])),tuples_in([[P115, P125, P135, P145, P155]],[[0,0,0,0,0]]))"),
                termParser.parseStruct("#\\/(#\\/(#\\/(#\\/(tuples_in([[P211, P221, P231, P241, P251]],[[0,0,0,0,0]]),tuples_in([[P212, P222, P232, P242, P252]],[[0,0,0,0,0]])),tuples_in([[P213, P223, P233, P243, P253]],[[0,0,0,0,0]])),tuples_in([[P214, P224, P234, P244, P254]],[[0,0,0,0,0]])),tuples_in([[P215, P225, P235, P245, P255]],[[0,0,0,0,0]]))"),
                termParser.parseStruct("#\\/(#\\/(#\\/(#\\/(tuples_in([[P311, P321, P331, P341, P351]],[[0,0,0,0,0]]),tuples_in([[P312, P322, P332, P342, P352]],[[0,0,0,0,0]])),tuples_in([[P313, P323, P333, P343, P353]],[[0,0,0,0,0]])),tuples_in([[P314, P324, P334, P344, P354]],[[0,0,0,0,0]])),tuples_in([[P315, P325, P335, P345, P355]],[[0,0,0,0,0]]))"),
                termParser.parseStruct("#\\/(#\\/(#\\/(#\\/(tuples_in([[P411, P421, P431, P441, P451]],[[0,0,0,0,0]]),tuples_in([[P412, P422, P432, P442, P452]],[[0,0,0,0,0]])),tuples_in([[P413, P423, P433, P443, P453]],[[0,0,0,0,0]])),tuples_in([[P414, P424, P434, P444, P454]],[[0,0,0,0,0]])),tuples_in([[P415, P425, P435, P445, P455]],[[0,0,0,0,0]]))")
            )
        )

        val goal = termParser.parseStruct("""
            problem(P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P133, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155, P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P233, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255, P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P333, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355, P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P433, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455),
            label([P111, P112, P113, P114, P115, P121, P122, P123, P124, P125, P131, P132, P133, P134, P135, P141, P142, P143, P144, P145, P151, P152, P153, P154, P155, P211, P212, P213, P214, P215, P221, P222, P223, P224, P225, P231, P232, P233, P234, P235, P241, P242, P243, P244, P245, P251, P252, P253, P254, P255, P311, P312, P313, P314, P315, P321, P322, P323, P324, P325, P331, P332, P333, P334, P335, P341, P342, P343, P344, P345, P351, P352, P353, P354, P355, P411, P412, P413, P414, P415, P421, P422, P423, P424, P425, P431, P432, P433, P434, P435, P441, P442, P443, P444, P445, P451, P452, P453, P454, P455])
        """.trimIndent())

        val solver = ClassicSolverFactory.solverOf(
            staticKb = theory,
            libraries =  ClpFdLibrary.toRuntime(),
            flags = FlagStore.EMPTY + TrackVariables { ON }
        )

        val solution = solver.solveOnce(goal)
    }

    @Test
    fun testNestedOrGoal(){

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #\\/(#\\/('#='(X,1),'#='(X,2)),'#='(X,3)) , label([X])"
        )

        val solver = ClassicSolverFactory.solverOf(
            libraries =  ClpFdLibrary.toRuntime(),
            flags = FlagStore.EMPTY + TrackVariables { ON }
        )

        val solution = solver.solveOnce(goal)

    }

    @Test
    fun testNestedOrTheoryAndGoal(){

        val theory = theoryParser.parseTheory("""
            problem(X) :-
                in(X, '..'(1,10)),
                #\/(#\/('#='(X,1),'#='(X,2)),'#='(X,3)).
        """.trimIndent())

        val goal = termParser.parseStruct("problem(X),label([X])")

        val solver = ClassicSolverFactory.solverOf(
            staticKb = theory,
            libraries =  ClpFdLibrary.toRuntime(),
            flags = FlagStore.EMPTY + TrackVariables { ON }
        )

        val solution = solver.solveOnce(goal)
    }

    @Test
    fun testTuplesInWithOr(){

        val theory = theoryParser.parseTheory("""
            problem(X,Y) :-
                ins([X,Y], '..'(1,10)),
                #\/(tuples_in([[X,Y]],[[1,2]]),tuples_in([[X,Y]],[[3,3]])).
        """.trimIndent())

        val goal = termParser.parseStruct("problem(X,Y),label([X,Y])")

        val solver = ClassicSolverFactory.solverOf(
            staticKb = theory,
            libraries =  ClpFdLibrary.toRuntime(),
            flags = FlagStore.EMPTY + TrackVariables { ON }
        )

        val solution = solver.solveOnce(goal)

    }

}