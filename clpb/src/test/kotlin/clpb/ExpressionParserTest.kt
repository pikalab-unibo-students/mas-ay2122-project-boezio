package clpb

import clpCore.flip
import clpCore.variablesMap
import clpb.utils.ExpressionParser
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Var
import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.variables.BoolVar
import org.chocosolver.solver.variables.Variable
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExpressionParserTest {

    private val model = Model()

    @Test
    fun testExpressionVar(){

        val boolVar = Var.of("X")
        model.boolVar(boolVar.completeName)
        val varsMap = model.variablesMap(listOf(boolVar))
        val parser = ExpressionParser(model, varsMap.flip())
        val parsedExpr = boolVar.accept(parser)

        assertEquals(varsMap.flip()[boolVar] as ILogical, parsedExpr)
    }

}