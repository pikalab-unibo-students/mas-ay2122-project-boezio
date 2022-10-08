package clpfd.global

import clpCore.*
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Integer as LogicInteger
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object GlobalCardinalityTwo : BinaryRelation.NonBacktrackable<ExecutionContext>("global_cardinality") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listTerms = first.castToList().toList()
        for (elem in listTerms) {
            require(elem is Var) {
                "$elem is not a variable"
            }
        }
        val firstVars = listTerms.filterIsInstance<Var>().toSet()
        ensuringArgumentIsList(1)
        val pairs = second.castToList().toList()
        val chocoModel = chocoModel
        val occVars = second.variables.toSet()
        val logicVars = firstVars.union(occVars)
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        val values = mutableListOf<Int>()
        val occurrences = mutableListOf<IntVar>()
        for (pair in pairs) {
            require(pair.let { it is Struct && it.arity == 2 && it.functor == "-" }) {
                "$pair is not a pair"
            }
            val arguments = pair.castToStruct().args
            val value = arguments[0]
            require(value is LogicInteger) {
                "$value is not an integer"
            }
            values.add(value.castToInteger().value.toInt())
            val occ = arguments[1]
            require(occ is Var) {
                "$occ is not a variable"
            }
            val originalOcc = occ.castToVar().getOuterVariable(context.substitution)
            occurrences.add(varsMap[originalOcc] as IntVar)
        }
        val chocoVars = firstVars.map {
            val originalVar = it.getOuterVariable(context.substitution)
            varsMap[originalVar] as IntVar
        }.toTypedArray()
        val chocoValues = values.toIntArray()
        val chocoOccurrences = occurrences.toTypedArray()
        chocoModel.globalCardinality(chocoVars, chocoValues, chocoOccurrences, true).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}