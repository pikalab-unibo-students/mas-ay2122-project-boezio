package clpqr.utils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor

class ConstraintReplacer(private val varsMap: Map<Var, Term>): DefaultTermVisitor<Term>() {

    override fun defaultValue(term: Term) = throw IllegalStateException("Cannot handle $term as constraint")

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
