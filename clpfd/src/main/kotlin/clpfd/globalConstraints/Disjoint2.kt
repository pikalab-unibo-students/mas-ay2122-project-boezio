package clpfd.globalConstraints

import clpCore.*
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
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
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        for (rectangle in rectangles) {
            require(rectangle.let { it is Struct && it.arity == 4 }) {
                "$rectangle has an invalid structure"
            }
            val rectStructArguments = rectangle.castToStruct().args
            for (i in 0 until 4) {
                require(rectStructArguments[i].let { it is Var || it is LogicInteger }) {
                    "${rectStructArguments[i]} is neither a variable nor an integer"
                }
            }
            xCoordinates.add(getAsIntVar(rectStructArguments[0], varsMap))
            width.add(getAsIntVar(rectStructArguments[1], varsMap))
            yCoordinates.add(getAsIntVar(rectStructArguments[2], varsMap))
            height.add(getAsIntVar(rectStructArguments[3], varsMap))
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