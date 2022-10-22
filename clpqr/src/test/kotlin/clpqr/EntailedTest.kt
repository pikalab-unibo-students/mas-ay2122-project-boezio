package clpqr

import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertTrue

class EntailedTest: BaseTest() {

    @Test
    fun testEntailedTrue(){

        val goal = termParser.parseStruct(
            "{ X > 10.0 }, entailed(X > 0.0), satisfy([X,Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)

    }


    // Strange behaviour, constraint check returns true
    @Test @Ignore
    fun testEntailedFalse(){

        val goal = termParser.parseStruct(
            "{ X > Y, Y > Z }, entailed(Z > X), satisfy([X,Y,Z])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testEntailedSimpleFalse(){

        val goal = termParser.parseStruct(
            "{ X > 10.0 }, entailed(X < 5.0), satisfy([X,Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test @Ignore
    fun testEntailedEquals(){

        val goal = termParser.parseStruct(
            "{ X = 10.0 }, entailed(X = 10.0), satisfy([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testEntailedEqualsFirstVersion(){

        val goal = termParser.parseStruct(
            "{ X = 10.0 }, entailed(X = 10.0)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testEntailedEqualsSecondVersion(){

        val goal = termParser.parseStruct(
            "{ X =:= 10.0 }, entailed(X =:= 10.0)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testEntailedLessEqualsFirstVersion(){

        val goal = termParser.parseStruct(
            "{ '<='(X,Y), '<='(Y,10.0) }, entailed('<='(Y,10.0)), satisfy([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testEntailedLessEqualsSecondVersion(){

        val goal = termParser.parseStruct(
            "{ '=<'(X,Y), '=<'(Y,10.0) }, entailed('=<'(Y,10.0)), satisfy([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testEntailedGreaterEquals(){

        val goal = termParser.parseStruct(
            "{ '>='(X,Y), '>='(Y,10.0) }, entailed('>='(Y,10.0))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testInvalidArgument(){

        val goal = termParser.parseStruct(
            "{ X > 10.0 }, entailed(a), satisfy([X,Y])"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.COMPOUND)

    }

    @Test
    fun testConstraintIsRealNumber(){

        val goal = termParser.parseStruct(
            "{ X > 10.0 }, entailed(3.0), satisfy([X,Y])"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.TYPE_REFERENCE)

    }

    @Test
    fun testInvalidConstraintArity(){

        val goal = termParser.parseStruct(
            "{ X > 10.0 }, entailed('>'(X,Y,Z)), satisfy([X,Y])"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.PREDICATE_PROPERTY)

    }

    @Test
    fun testInvalidConstraintOperator(){

        val goal = termParser.parseStruct(
            "{ X > 10.0 }, entailed('invalid'(X,Y)), satisfy([X,Y])"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.PREDICATE_PROPERTY)

    }

    @Test @Ignore
    fun testUndefinedEvaluation(){

        val goal = termParser.parseStruct(
            "{ X > 10.0, Y > 5.0 }, entailed(X > Y), satisfy([X,Y])"
        )

        // entail returns true, strange result

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.ATOM_PROPERTY)

    }


}