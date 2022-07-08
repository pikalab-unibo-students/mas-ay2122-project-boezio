package clpVarCreation

import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.core.Integer
import org.chocosolver.solver.Model
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ExpressionParserTest {

    private val LOWER_BOUND = 1
    private val UPPER_BOUND = 10
    private val varNames = listOf("X", "Y", "Z")

    private val model = Model().let {
        val chocoModel = it
        varNames.map { chocoModel.intVar(it, LOWER_BOUND, UPPER_BOUND) }
        chocoModel
    }
    private val chocoVars = model.vars

    private val termParser = TermParser.withDefaultOperators
    private val tuPrologVars = varNames.map { termParser.parseVar(it) }

    private val mapVars = (tuPrologVars zip chocoVars).toMap()
    private val expressionParser = ExpressionParser(model, mapVars)


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

        val NUM = 10
        val integer = Integer.of(NUM)
        val arithmeticExpr = integer.accept(expressionParser)
        // if the expression is an integer it has no children
        assertEquals(arithmeticExpr.noChild, 0)

    }
}