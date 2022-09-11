package clpqr

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import kotlin.math.abs
import kotlin.math.exp
import kotlin.test.assertIs
import kotlin.test.fail

fun assertSubstitutionsMatch(expected: Substitution, actual: Substitution) = when {
    expected is Substitution.Fail && actual is Substitution.Fail -> {
        // ok
    }
    expected is Substitution.Unifier && actual is Substitution.Unifier -> {
        for ((variable, value) in expected) {
            if (variable !in actual || actual[variable] != value) {
                fail("Expected assignment <$variable=$value> is missing in actual substitution <$actual>")
            }
        }
    }
    else -> fail("Expected: <$expected>, actual <$actual>")
}

fun Solution.assertSolutionAssigns(assignment1: Pair<Var, Term>, vararg otherAssignments: Pair<Var, Term>) {
    assertIs<Solution.Yes>(this)
    assertSubstitutionsMatch(
        expected = Substitution.of(assignment1, *otherAssignments),
        actual = substitution
    )
}

fun assertSubstitutionsDouble(expected: Substitution, actual: Substitution, precision: Double) = when {
    expected is Substitution.Fail && actual is Substitution.Fail -> {
        // ok
    }
    expected is Substitution.Unifier && actual is Substitution.Unifier -> {
        for ((variable, value) in expected) {
            val actualValue = actual[variable]!!.castToReal().value.toDouble()
            val expectedValue = value.castToReal().value.toDouble()
            if (variable !in actual || abs(actualValue - expectedValue) > precision) {
                fail("Expected assignment <$variable=$value> is missing in actual substitution <$actual>")
            }
        }
    }
    else -> fail("Expected: <$expected>, actual <$actual>")
}

fun Solution.assertSolutionAssignsDouble(
    precision: Double,
    assignment1: Pair<Var, Term>,
    vararg otherAssignments: Pair<Var, Term>
) {
    assertIs<Solution.Yes>(this)
    assertSubstitutionsDouble(
        expected = Substitution.of(assignment1, *otherAssignments),
        actual = substitution,
        precision
    )
}
