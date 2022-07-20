package clpVarCreation.globalConstraints

import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.IntVar

object Circuit: UnaryPredicate.NonBacktrackable<ExecutionContext>("circuit") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val circuit = first.castToList().toList()
        val circuitVars = circuit.filterIsInstance<Var>().distinct()
        require(circuit.size == circuitVars.size) {
            "$circuit does not contain only variables"
        }
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(circuitVars).flip()
        val chocoCircuit = circuitVars.map { varsMap[it] as IntVar}.toTypedArray()
        return replySuccess {
            chocoModel.circuit(chocoCircuit).post()
        }
    }
}