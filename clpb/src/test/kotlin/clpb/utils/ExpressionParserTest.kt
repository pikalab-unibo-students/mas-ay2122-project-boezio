package clpb.utils

import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ExpressionParserTest {

    private val model = Model()
    private val parser = ExpressionParser(model, mapOf(), Dummy.context, Dummy.signature)

    @Test
    fun testExpressionVar(){

        val boolVar = Var.of("X")
        model.boolVar(boolVar.completeName)
        val varsMap = model.variablesMap(listOf(boolVar), Substitution.empty())
        val parser = ExpressionParser(model, varsMap.flip(), Dummy.context, Dummy.signature)
        val parsedExpr = boolVar.accept(parser)

        assertEquals(varsMap.flip()[boolVar] as ILogical, parsedExpr)
    }

    @Test
    fun testInvalidTerm(){

        val variable = Var.of("X")
        assertThrows<DomainError> {
            variable.accept(parser)
        }
    }

    @Test
    fun testInvalidInteger(){
        val integer = Integer.of(3)
        assertThrows<DomainError> {
            integer.accept(parser)
        }
    }

    @Test
    fun testInvalidOperator(){
        val struct = Struct.of("#>", Integer.of(1), Integer.of(2))
        assertThrows<TypeError> {
            struct.accept(parser)
        }
    }

    @Test
    fun testInvalidUnaryExpression(){
        val struct = Struct.of("+", Integer.of(1))
        assertThrows<TypeError> {
            struct.accept(parser)
        }
    }

}