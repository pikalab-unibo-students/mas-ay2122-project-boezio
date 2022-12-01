package clpfd.reification

import clpCore.chocoModel
import clpCore.flip
import clpCore.getOuterVariable
import clpCore.variablesMap
import clpfd.ExpressionParser
import clpfd.getAsIntVar
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsList
import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.extension.Tuples
import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression
import org.chocosolver.solver.variables.BoolVar
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.Variable

class ReificationParser(
    private val chocoModel: Model,
    private val substitution: Substitution.Unifier,
    private val context: ExecutionContext,
    private val signature: Signature
) : DefaultTermVisitor<ILogical>() {
    override fun defaultValue(term: Term): ILogical =
        throw TypeError.forArgument(context, signature, TypeError.Expected.TYPE_REFERENCE, term)

    override fun visitStruct(term: Struct): ILogical {
        when (term.arity) {
            2 -> when (term.functor) {
                // relational constraints
                "#=" -> return applyRelConstraint(term[0], term[1],ArExpression::eq).boolVar()
                "#\\=" -> return applyRelConstraint(term[0], term[1],ArExpression::ne).boolVar()
                "#>" -> return applyRelConstraint(term[0], term[1],ArExpression::gt).boolVar()
                "#<" -> return applyRelConstraint(term[0], term[1],ArExpression::lt).boolVar()
                "#>=" -> return applyRelConstraint(term[0], term[1],ArExpression::ge).boolVar()
                "#=<" -> return applyRelConstraint(term[0], term[1],ArExpression::le).boolVar()
                // reification constraints
                "#\\/" -> return LogOp.or(term[0].accept(this), term[1].accept(this))
                "#/\\" -> return LogOp.and(term[0].accept(this), term[1].accept(this))
                "#\\" -> return LogOp.xor(term[0].accept(this), term[1].accept(this))
                "#<==>" -> return LogOp.ifOnlyIf(term[0].accept(this), term[1].accept(this))
                "#==>" -> return LogOp.implies(term[0].accept(this), term[1].accept(this))
                "#<==" -> return LogOp.implies(term[1].accept(this), term[0].accept(this))
                // BREAKING SWI PROLOG API
                "tuples_in" -> return reifyTuplesIn(term[0],term[1])
            }
            1 -> when (term.functor) {
                "#\\" -> return LogOp.nand(term[0].accept(this))
            }
        }
        return super.visitStruct(term) // indirectly calls defaultValue(term)
    }

    private fun applyRelConstraint(
        first: Term,
        second: Term,
        operation: (ArExpression, ArExpression) -> ReExpression
    ): ReExpression {
        val logicalVars = (first.variables + second.variables).toSet()
        val varMap = chocoModel.variablesMap(logicalVars, substitution).flip()
        val parser = ExpressionParser(chocoModel, varMap, substitution, context, signature)
        val firstExpression = first.accept(parser)
        val secondExpression = second.accept(parser)
        return operation(firstExpression, secondExpression)
    }

    fun getAsIntVar(term: Term, map: Map<Var, Variable>, substitution: Substitution.Unifier): IntVar{
        return if(term is Var) {
            val originalVar = term.castToVar().getOuterVariable(substitution)
            map[originalVar] as IntVar
        }else
            chocoModel.intVar(term.castToInteger().value.toInt())
    }
    private fun reifyTuplesIn(term1: Term, term2: Term) : BoolVar{
        val listVar = term1.castToList().toList()
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
        val tuples = term2.castToList().toList()
        val numElemList = innerList.size
        for (tuple in tuples) {
            if(tuple !is List)
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
                else if(elem !is Integer)
                    throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
            }
            val integerElems = tupleElems.filterIsInstance<Integer>()
            val elemRelation = integerElems.map { it.value.toInt() }.toIntArray()
            relation.add(elemRelation)
        }
        return chocoModel.table(tupleList.toTypedArray(), relation, "CT+").reify()
    }
}