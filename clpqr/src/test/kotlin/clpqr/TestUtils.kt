package clpqr

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import kotlin.math.abs
import kotlin.math.exp
import kotlin.test.assertIs
import kotlin.test.fail

fun assertSubstitutionsMatch(expected: Substitution, actual: Substitution, precision: Double) = when {
    expected is Substitution.Fail && actual is Substitution.Fail -> {
        // ok
    }
    expected is Substitution.Unifier && actual is Substitution.Unifier -> {
        for ((variable, value) in expected) {
            // no exact match for real variables
            if (value is Real) {
                val expectedValue = value.value.toDouble()
                val actualValue = actual[variable]!!.castToReal().value.toDouble()
                if(abs(expectedValue - actualValue) > precision)
                    fail("Expected assignment <$variable=$value> is missing in actual substitution <$actual>")
            }else if (variable !in actual || actual[variable] != value) {
                fail("Expected assignment <$variable=$value> is missing in actual substitution <$actual>")
            }
        }
    }
    else -> fail("Expected: <$expected>, actual <$actual>")
}

fun Solution.assertSolutionAssigns(
    precision: Double,
    assignment1: Pair<Var, Term>,
    vararg otherAssignments: Pair<Var, Term>
) {
    assertIs<Solution.Yes>(this)
    assertSubstitutionsMatch(
        expected = Substitution.of(assignment1, *otherAssignments),
        actual = substitution,
        precision = precision
    )
}