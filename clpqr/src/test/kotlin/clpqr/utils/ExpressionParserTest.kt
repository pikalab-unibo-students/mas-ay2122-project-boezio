package clpqr.utils

import clpqr.Dummy
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.chocosolver.solver.Model
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ExpressionParserTest {

    private val parser = ExpressionParser(
        Model(), mapOf(), Substitution.empty(), Dummy.context, Dummy.signature
    )

    @Test
    fun testInvalidOperator(){

        val expression = Struct.of("arctan", Real.of(1.0))
        assertThrows<TypeError> {
            expression.accept(parser)
        }

    }

    @Test
    fun testInvalidVariable(){

        val expression = Var.of("X")
        assertThrows<DomainError> {
            expression.accept(parser)
        }

    }

    @Test
    fun testPowerNotSupported(){

        val expression = Struct.of("^", Real.of(2.0), Real.of(3.0))
        assertThrows<ExistenceError> {
            expression.accept(parser)
        }

    }

    @Test
    fun testTanNotSupported(){

        val expression = Struct.of("tan", Real.of(2.0))
        assertThrows<ExistenceError> {
            expression.accept(parser)
        }

    }

    @Test
    fun testExpNotSupported(){

        val expression = Struct.of("exp", Real.of(2.0))
        assertThrows<ExistenceError> {
            expression.accept(parser)
        }

    }

    @Test
    fun testPowNotSupported(){

        val expression = Struct.of("pow", Real.of(2.0))
        assertThrows<ExistenceError> {
            expression.accept(parser)
        }

    }

}