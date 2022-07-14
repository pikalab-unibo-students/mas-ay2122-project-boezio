package clpVarCreation

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
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