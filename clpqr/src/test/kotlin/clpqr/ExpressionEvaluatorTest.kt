package clpqr

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import org.chocosolver.solver.Model
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExpressionEvaluatorTest {

    @Test
    fun testSumOfNumbers(){

        val expression = Struct.of("+",Integer.of(10), Integer.of(5))
        val parser = ExpressionEvaluator(mapOf())
        val value = expression.accept(parser)

        assertEquals(value, 15.0)
    }

    @Test
    fun testSumVariableAndDouble(){

        val model = Model()
        val prologVar = Var.of("x")
        val chocoVar = model.realVar("x", 8.5)
        val varsMap = mapOf(prologVar to chocoVar)
        val expression = Struct.of("+", prologVar, Real.of(1.5))
        val parser = ExpressionEvaluator(varsMap)
        val value = expression.accept(parser)

        assertEquals(value, 10.0)
    }

    @Test
    fun testNestedOperators(){

        val model = Model()
        val prologVar = Var.of("x")
        val chocoVar = model.realVar("x", 8.0)
        val varsMap = mapOf(prologVar to chocoVar)
        val expression = Struct.of(
            "*",
            Struct.of("abs", Integer.of(-3)),
            Struct.of("pow", Real.of(2.0))
        )
        val parser = ExpressionEvaluator(varsMap)
        val value = expression.accept(parser)

        assertEquals(value, 12.0)
    }
}