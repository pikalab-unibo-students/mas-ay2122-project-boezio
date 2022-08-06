package clpVarCreation.globalConstraints

import clpVarCreation.*
import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.setChocoModel
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Integer as LogicInteger
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.Task

object CumulativeTwo : BinaryRelation.NonBacktrackable<ExecutionContext>("cumulative") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        ensuringArgumentIsList(1)
        val chocoModel = chocoModel
        val firstVars = first.variables.toSet()
        val secondVars = second.variables.toSet()
        val logicVars = firstVars.union(secondVars)
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        val tasks = mutableListOf<Task>()
        val heights = mutableListOf<IntVar>()
        val elementsList = first.castToList().toList()
        for (element in elementsList) {
            require(element.let { it is Struct && it.functor == "task" && it.arity == 5 }) {
                "$element is not a valid task"
            }
            val arguments = element.castToStruct().args
            val start = arguments[0]
            require(start.let { it is Var || it is LogicInteger }) {
                "$start is neither a variable nor an integer"
            }
            val chocoStart = getAsIntVar(start, varsMap)

            val duration = arguments[1]
            require(duration.let { it is Var || it is LogicInteger } ) {
                "$duration is neither an integer nor a variable"
            }
            val chocoDuration = getAsIntVar(duration, varsMap)

            val end = arguments[2]
            require(end.let { it is Var || it is LogicInteger }) {
                "$end is neither a variable nor an integer"
            }
            val chocoEnd = getAsIntVar(end, varsMap)

            val height = arguments[3]
            require(height.let { it is Var || it is LogicInteger }) {
                "$height is neither a variable nor an integer"
            }
            val chocoHeight = getAsIntVar(height, varsMap)

            tasks.add(Task(chocoStart, chocoDuration, chocoEnd))

            heights.add(chocoHeight)
        }
        val options = second.castToList().toList()
        require(options.let { it.size == 1 && it[0].let { it is Struct && it.arity == 1 && it.functor == "limit" }}) {
            "$options are invalid options"
        }
        val limitTerm = options[0].castToStruct().args[0]
        require(limitTerm.let { it is Var || it is LogicInteger }) {
            "$limitTerm is not declared neither as a variable nor an integer"
        }
        val chocoLimit = getAsIntVar(limitTerm, varsMap)
        val chocoTasks = tasks.toTypedArray()
        val chocoHeights = heights.toTypedArray()
        chocoModel.cumulative(chocoTasks, chocoHeights, chocoLimit).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}