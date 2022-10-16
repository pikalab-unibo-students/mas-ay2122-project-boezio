package clpqr

import org.junit.jupiter.api.Test

class DumpTest: BaseTest() {

    @Test
    fun testDumpAtoms(){

        val goal = termParser.parseStruct(
            "{ X + Y = 10.0 }, dump([X,Y],[a,b],Cons)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val codedAnswer = termParser.parseStruct(
            "[a + b = 10.0]"
        )

        termParser.scope.with {
            solution.assertSolutionAssigns(
                0.0,
                varOf("Cons") to codedAnswer
            )
        }
    }

    @Test
    fun testDumpMixed(){

        val goal = termParser.parseStruct(
            "{ X + Y = 10.0 }, dump([X,Y],[a,f(a)],Cons)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val codedAnswer = termParser.parseStruct(
            "[a + f(a) = 10.0]"
        )

        termParser.scope.with {
            solution.assertSolutionAssigns(
                0.0,
                varOf("Cons") to codedAnswer
            )
        }
    }

    @Test
    fun testDumpNotReplaceVariable(){

        val goal = termParser.parseStruct(
            "{ X + Y = 10.0 }, dump([X],[a],Cons)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val codedAnswer = termParser.parseStruct(
            "[a + Y = 10.0]"
        )

        termParser.scope.with {
            solution.assertSolutionAssigns(
                0.0,
                varOf("Cons") to codedAnswer
            )
        }
    }

    @Test
    fun testDumpIntegerValue(){

        val goal = termParser.parseStruct(
            "{ X + Y = 10 }, dump([X],[a],Cons)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val codedAnswer = termParser.parseStruct(
            "[a + Y = 10]"
        )

        termParser.scope.with {
            solution.assertSolutionAssigns(
                0.0,
                varOf("Cons") to codedAnswer
            )
        }
    }
}