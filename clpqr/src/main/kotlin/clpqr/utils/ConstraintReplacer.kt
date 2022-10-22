package clpqr.utils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.TypeError

class ConstraintReplacer(
    private val varsMap: Map<Var, Term>,
    private val context: ExecutionContext,
    private val signature: Signature
): DefaultTermVisitor<Term>() {

    override fun defaultValue(term: Term) =
        throw TypeError.forArgument(context, signature, TypeError.Expected.TYPE_REFERENCE, term)

    override fun visitVar(term: Var): Term{

        return if(term in varsMap.keys){
            varsMap[term]!!
        }else{
            term
        }
    }

    override fun visitInteger(term: Integer) = term

    override fun visitReal(term: Real) = term

    override fun visitStruct(term: Struct) = Struct.of(term.functor, term.args.map { it.accept(this) })

}
