package clpVarCreation.globalConstraints

import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.setChocoModel
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.IntVar

object Disjoint2 : UnaryPredicate.NonBacktrackable<ExecutionContext>("disjoint2") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val rectangles = first.castToList().toList()
        val xCoordinates = mutableListOf<IntVar>()
        val width = mutableListOf<IntVar>()
        val yCoordinates = mutableListOf<IntVar>()
        val height = mutableListOf<IntVar>()
        val chocoModel = chocoModel
        val logicVars = first.variables.toList()
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        for (rectangle in rectangles) {
            require(rectangle.let { it is Struct && it.arity == 4 }) {
                "$rectangle has an invalid structure"
            }
            val rectStructArguments = rectangle.castToStruct().args
            for (i in 0 until 4) {
                require(rectStructArguments[i] is Var) {
                    "$rectStructArguments[i] is not a variable"
                }
            }
            xCoordinates.add(varsMap[rectStructArguments[0].castToVar()] as IntVar)
            width.add(varsMap[rectStructArguments[1].castToVar()] as IntVar)
            yCoordinates.add(varsMap[rectStructArguments[2].castToVar()] as IntVar)
            height.add(varsMap[rectStructArguments[3].castToVar()] as IntVar)
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