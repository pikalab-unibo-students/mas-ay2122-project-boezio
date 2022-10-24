package clpb.utils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse

class BoolExprEvaluatorTest {

    private val evaluator = BoolExprEvaluator(Dummy.context, Dummy.signature)

    @Test
    fun testNotUnaryOperator(){
        val struct = Struct.of("~", Integer.of(1))
        assertFalse(struct.accept(evaluator))
    }

    @Test
    fun testInvalidTerm(){

        val variable = Var.of("X")
        assertThrows<TypeError> {
            variable.accept(evaluator)
        }
    }

    @Test
    fun testInvalidInteger(){
        val integer = Integer.of(3)
        assertThrows<DomainError> {
            integer.accept(evaluator)
        }
    }

    @Test
    fun testInvalidOperator(){
        val struct = Struct.of("#>", Integer.of(1), Integer.of(2))
        assertThrows<TypeError> {
            struct.accept(evaluator)
        }
    }

    @Test
    fun testInvalidUnaryExpression(){
        val struct = Struct.of("+", Integer.of(1))
        assertThrows<TypeError> {
            struct.accept(evaluator)
        }
    }
}