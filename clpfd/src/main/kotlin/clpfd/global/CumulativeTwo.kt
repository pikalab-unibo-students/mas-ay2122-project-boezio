package clpfd.global

import clpCore.*
import clpfd.getAsIntVar

import it.unibo.tuprolog.core.Integer as LogicInteger
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
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
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        val tasks = mutableListOf<Task>()
        val heights = mutableListOf<IntVar>()
        val elementsList = first.castToList().toList()
        for (element in elementsList) {
            if(!(element.let { it is Struct && it.functor == "task" && it.arity == 5 })) {
                throw DomainError.forArgument(context, signature, DomainError.Expected.PREDICATE_PROPERTY, element)
            }
            val arguments = element.castToStruct().args
            for (i in 0 until 4) {
                if(!(arguments[i].let { it is Var || it is LogicInteger })) {
                    throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, element, i)
                }
            }
            val chocoStart = getAsIntVar(arguments[0], varsMap, context.substitution)
            val chocoDuration = getAsIntVar(arguments[1], varsMap, context.substitution)
            val chocoEnd = getAsIntVar(arguments[2], varsMap, context.substitution)
            val chocoHeight = getAsIntVar(arguments[3], varsMap, context.substitution)
            tasks.add(Task(chocoStart, chocoDuration, chocoEnd))
            heights.add(chocoHeight)
        }
        val options = second.castToList().toList()
        if(!(options.let { terms -> terms.size == 1 && terms[0].let { it is Struct && it.arity == 1 && it.functor == "limit" }})) {
            throw DomainError.forArgument(context, signature, DomainError.Expected.PREDICATE_PROPERTY, second)
        }
        val limitTerm = options[0].castToStruct().args[0]
        if(!(limitTerm.let { it is Var || it is LogicInteger })) {
            throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, options[0].castToStruct())
        }
        val chocoLimit = getAsIntVar(limitTerm, varsMap, context.substitution)
        val chocoTasks = tasks.toTypedArray()
        val chocoHeights = heights.toTypedArray()
        chocoModel.cumulative(chocoTasks, chocoHeights, chocoLimit).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}