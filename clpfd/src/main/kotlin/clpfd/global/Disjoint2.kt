package clpfd.global

import clpCore.*
import clpfd.getAsIntVar
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.IntVar
import it.unibo.tuprolog.core.Integer as LogicInteger

object Disjoint2 : UnaryPredicate.NonBacktrackable<ExecutionContext>("disjoint2") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val rectangles = first.castToList().toList()
        val xCoordinates = mutableListOf<IntVar>()
        val width = mutableListOf<IntVar>()
        val yCoordinates = mutableListOf<IntVar>()
        val height = mutableListOf<IntVar>()
        val chocoModel = chocoModel
        val logicVars = first.variables.toSet()
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        for (rectangle in rectangles) {
            if(!(rectangle.let { it is Struct && it.arity == 4 })) {
                throw DomainError.forArgument(context, signature, DomainError.Expected.PREDICATE_PROPERTY, rectangle)
            }
            val rectStructArguments = rectangle.castToStruct().args
            for (i in 0 until 4) {
                if(!(rectStructArguments[i].let { it is Var || it is LogicInteger })) {
                    throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, rectangle.castToStruct(), i)
                }
            }
            xCoordinates.add(getAsIntVar(rectStructArguments[0], varsMap, context.substitution))
            width.add(getAsIntVar(rectStructArguments[1], varsMap, context.substitution))
            yCoordinates.add(getAsIntVar(rectStructArguments[2], varsMap, context.substitution))
            height.add(getAsIntVar(rectStructArguments[3], varsMap, context.substitution))
        }
        val chocoX = xCoordinates.toTypedArray()
        val chocoY = yCoordinates.toTypedArray()
        val chocoWidth = width.toTypedArray()
        val chocoHeight = height.toTypedArray()
        chocoModel.diffN(chocoX, chocoY, chocoWidth, chocoHeight, true).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}