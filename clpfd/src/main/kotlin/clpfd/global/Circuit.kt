package clpfd.global


import clpCore.chocoModel
import clpCore.flip
import clpCore.setChocoModel
import clpCore.variablesMap
import clpfd.getIntAsVars
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.IntVar

object Circuit: UnaryPredicate.NonBacktrackable<ExecutionContext>("circuit") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val chocoModel = chocoModel
        val circuit = first.castToList().toList()
        val constraintVars = mutableListOf<IntVar>()
        for(term in circuit){
            when(term){
                is Var -> {
                    val chocoVar = chocoModel.variablesMap(listOf(term), context.substitution).keys.toList()[0] as IntVar
                    constraintVars.add(chocoVar)
                }
                is Integer -> constraintVars.add(chocoModel.intVar(term.value.toInt()))
                else -> throw IllegalStateException("argument is neither a variable nor an integer")
            }
        }
        chocoModel.circuit(constraintVars.toTypedArray()).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}