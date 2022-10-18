package clpfd.global

import clpCore.*
import clpfd.getAsIntVar
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.constraints.extension.Tuples
import it.unibo.tuprolog.core.List as LogicList
import org.chocosolver.solver.variables.IntVar
import it.unibo.tuprolog.core.Integer as LogicInteger

object TuplesIn : BinaryRelation.NonBacktrackable<ExecutionContext>("tuples") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listVar = first.castToList().toList()
        val innerList = listVar[0].castToList().toList()
        val logicVars = innerList.filterIsInstance<Var>().distinct()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        val tupleList = mutableListOf<IntVar>()
        for(elem in innerList){
            if(!elem.let { it is Var || it is Integer })
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
            tupleList.add(getAsIntVar(elem, varsMap, context.substitution))
        }
        val relation = Tuples(true)
        ensuringArgumentIsList(1)
        val tuples = second.castToList().toList()
        val numElemList = innerList.size
        for (tuple in tuples) {
            if(tuple !is LogicList)
                throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, tuple)
            val tupleElems = tuple.castToList().toList()
            if(tupleElems.size != numElemList)
                throw DomainError.forArgument(context, signature, DomainError.Expected.ATOM_PROPERTY, Atom.of(numElemList.toString()))
            for(elem in tupleElems){
                if(elem is Var)
                    throw ExistenceError.of(
                        context,
                        ExistenceError.ObjectType.RESOURCE,
                        elem,
                        "Variable are not supported as element"
                    )
                else if(elem !is LogicInteger)
                    throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
            }
            val integerElems = tupleElems.filterIsInstance<LogicInteger>()
            val elemRelation = integerElems.map { it.value.toInt() }.toIntArray()
            relation.add(elemRelation)
        }
        chocoModel.table(tupleList.toTypedArray(), relation, "CT+").post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}