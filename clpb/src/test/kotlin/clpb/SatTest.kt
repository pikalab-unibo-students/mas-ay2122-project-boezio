package clpb

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class SatTest: BaseTest() {

    @Test
    fun testSatAndTrue(){

        val goal = termParser.parseStruct(
            "sat(X * Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)

    }

    @Test
    fun testSatAndFalse(){

        val goal = termParser.parseStruct(
            "sat(*(X,~(X)))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatTrueValue(){

        val goal = termParser.parseStruct(
            "sat(X + 1)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatFalseValue(){

        val goal = termParser.parseStruct(
            "sat(X + 0)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatFalseValueFalseOutcome(){

        val goal = termParser.parseStruct(
            "sat(X * 0)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatLessThanTrue(){

        val goal = termParser.parseStruct(
            "sat(X < Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatLessThanFalse(){

        val goal = termParser.parseStruct(
            "sat(1 < Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatGreaterThanTrue(){

        val goal = termParser.parseStruct(
            "sat(X > Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatGreaterThanFalse(){

        val goal = termParser.parseStruct(
            "sat(0 > Y)"
        )

        val solver = getSolver()


        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatXor(){

        val goal = termParser.parseStruct(
            "sat(#(X,Y))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatEq(){

        val goal = termParser.parseStruct(
            "sat(X =:= Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatNotEq(){

        val goal = termParser.parseStruct(
            "sat(X =\\= Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatLessEq(){

        val goal = termParser.parseStruct(
            "sat(X =< Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatGreaterEq(){

        val goal = termParser.parseStruct(
            "sat(X >= Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatNot(){

        val goal = termParser.parseStruct(
            "sat(~(X))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatMultipleAnd(){

        val goal = termParser.parseStruct(
            "sat(*([X,Y,Z]))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatMultipleAndFalseOutcome(){

        val goal = termParser.parseStruct(
            "sat(*([0,Y,Z]))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatMultipleOr(){

        val goal = termParser.parseStruct(
            "sat(+([X,Y,Z]))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatNestedExpression(){

        val goal = termParser.parseStruct(
            "sat(+([*([X,~(W),1]),Y,>(Z,0)]))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatSingleVariable(){

        val goal = termParser.parseStruct(
            "sat(X)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatNoVariables(){

        val goal = termParser.parseStruct(
            "sat(1 + 0)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatNoVariablesNestedTrue(){

        val goal = termParser.parseStruct(
            "sat(+([1,*(1,1)]))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatNoVariablesNestedFalse(){

        val goal = termParser.parseStruct(
            "sat(*([1,*(1,0)]))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatDoubleSatFalse(){

        val goal = termParser.parseStruct(
            "sat(X*1),sat(X =\\= 1)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatDoubleSatTrue(){

        val goal = termParser.parseStruct(
            "sat(X =< Y), sat(Y =< Z)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

}