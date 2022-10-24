package clpfd.utils

import clpfd.ExpressionParser
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.chocosolver.solver.Model
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ExpressionParserTest {

    private val LOWER_BOUND = 1
    private val UPPER_BOUND = 10
    private val varNames = listOf("X", "Y", "Z")

    private val model = Model().let { model1 ->
        varNames.map { model1.intVar(it, LOWER_BOUND, UPPER_BOUND) }
        model1
    }
    private val chocoVars = model.vars
    private val tuPrologVars = varNames.map { Var.of(it) }

    private val mapVars = (tuPrologVars zip chocoVars).toMap()
    private val expressionParser = ExpressionParser(
        model, mapVars, Dummy.context.substitution, Dummy.context, Dummy.signature
    )

    @Test
    fun testVisitVar() {

        // variable X is used
        val variable = tuPrologVars[0]
        val arithmeticExpr = variable.accept(expressionParser)
        // check whether the expression is a variable
        assertTrue(arithmeticExpr.isExpressionLeaf)

    }

    @Test
    fun testVisitInteger() {

        val integer = Integer.of(2)
        val arithmeticExpr = integer.accept(expressionParser)
        // if the expression is an integer it has no children
        assertEquals(arithmeticExpr.noChild, 0)

    }

    @Test
    fun testVisitStructAdd() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val struct = Struct.of("+", varX, varY)
        val arithmeticExpr = struct.accept(expressionParser)
        // the expression has the two variables as children
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitStructSub() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val struct = Struct.of("-", varX, varY)
        val arithmeticExpr = struct.accept(expressionParser)
        // the expression has the two variables as children
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitStructMul() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val struct = Struct.of("*", varX, varY)
        val arithmeticExpr = struct.accept(expressionParser)
        // the expression has the two variables as children
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitStructPow() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val struct = Struct.of("^", varX, varY)
        val arithmeticExpr = struct.accept(expressionParser)
        // the expression has the two variables as children
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitStructDiv() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val struct = Struct.of("div", varX, varY)
        val arithmeticExpr = struct.accept(expressionParser)
        // the expression has the two variables as children
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitStructMod() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val struct = Struct.of("mod", varX, varY)
        val arithmeticExpr = struct.accept(expressionParser)
        // the expression has the two variables as children
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitStructMin() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val struct = Struct.of("min", varX, varY)
        val arithmeticExpr = struct.accept(expressionParser)
        // the expression has the two variables as children
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitStructMax() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val struct = Struct.of("max", varX, varY)
        val arithmeticExpr = struct.accept(expressionParser)
        // the expression has the two variables as children
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitStructAbs() {

        val variable = tuPrologVars[0]
        val struct = Struct.of("abs", variable)
        val arithmeticExpr = struct.accept(expressionParser)
        // The only child of the expression is the argument of abs
        assertEquals(arithmeticExpr.noChild, 1)

    }

    @Test
    fun testVisitStructNegation() {

        val variable = tuPrologVars[0]
        val struct = Struct.of("-", variable)
        val arithmeticExpr = struct.accept(expressionParser)
        // The only child of the expression is the argument of abs
        assertEquals(arithmeticExpr.noChild, 1)

    }

    @Test
    fun testVisitStruct() {

        val varX = tuPrologVars[0]
        val varY = tuPrologVars[1]
        val varZ = tuPrologVars[2]

        // +(*(X,Y),div(Z,2))
        val firstArgument = Struct.of("*", varX, varY)
        val secondArgument = Struct.of("div", varZ, Integer.of(2))
        val struct = Struct.of("+", firstArgument , secondArgument)

        val arithmeticExpr = struct.accept(expressionParser)
        assertEquals(arithmeticExpr.noChild, 2)

    }

    @Test
    fun testVisitReal() {

        val realNum = Real.of("3.0")
        assertThrows<TypeError> { realNum.accept(expressionParser) }

    }

    @Test
    fun testNotExistingVariable() {

        val newVar = Var.of("NewVar")
        assertThrows<DomainError> { newVar.accept(expressionParser) }

    }

    @Test
    fun testDivNotSupported() {

        val struct = Struct.of("//", Integer.of(1), Integer.of(2))
        assertThrows<ExistenceError> { struct.accept(expressionParser) }

    }

    @Test
    fun testRemNotSupported() {

        val struct = Struct.of("rem", Integer.of(1), Integer.of(2))
        assertThrows<ExistenceError> { struct.accept(expressionParser) }

    }

    @Test
    fun testNotExistingOperator() {

        val struct = Struct.of("invalid_op", Integer.of(1), Integer.of(2))
        assertThrows<TypeError> { struct.accept(expressionParser) }

    }

}