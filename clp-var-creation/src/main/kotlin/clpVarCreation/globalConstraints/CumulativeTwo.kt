package clpVarCreation.globalConstraints

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
            require(start is Var) {
                "$start is not a variable"
            }
            val chocoStart = varsMap[start.castToVar()] as IntVar
            val duration = arguments[1]
            require(duration is LogicInteger || duration is Var) {
                "$duration is neither an integer nor a variable"
            }
            val end = arguments[2]
            require(end is Var) {
                "$end is not a variable"
            }
            val chocoEnd = varsMap[end.castToVar()] as IntVar
            val height = arguments[3]
            require(height is Var) {
                "$height is not a variable"
            }
            val chocoHeight = varsMap[height.castToVar()] as IntVar
            if (duration is LogicInteger) {
                tasks.add(Task(chocoStart, duration.castToInteger().value.toInt(), chocoEnd))
            } else {
                tasks.add(Task(chocoStart, varsMap[duration.castToVar()] as IntVar, chocoEnd))
            }
            heights.add(chocoHeight)
        }
        val options = second.castToList().toList()
        require(options.let { it.size == 1 && it[0].let { it is Struct && it.arity == 1 && it.functor == "limit" }}) {
            "$options are invalid options"
        }
        val limitTerm = options[0].castToStruct().args[0]
        require(limitTerm is Var) {
            "$limitTerm is not declared as a variable"
        }
        val chocoLimit = varsMap[limitTerm.castToVar()] as IntVar
        val chocoTasks = tasks.toTypedArray()
        val chocoHeights = heights.toTypedArray()
        chocoModel.cumulative(chocoTasks, chocoHeights, chocoLimit).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}