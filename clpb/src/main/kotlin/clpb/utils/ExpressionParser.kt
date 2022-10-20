package clpb.utils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp
import org.chocosolver.solver.variables.BoolVar
import org.chocosolver.solver.variables.Variable

class ExpressionParser<T : Variable>(
    private val chocoModel: Model,
    private val variables: Map<Var, T>
) : DefaultTermVisitor<ILogical>() {
    override fun defaultValue(term: Term): ILogical =
        error("Unsupported sub-expression: $term")

    override fun visitVar(term: Var): ILogical =
        asExpression(variables[term] ?: error("No such a variable: $term"))

    override fun visitInteger(term: Integer): ILogical {
        return when(term.value.toInt()){
            1 -> chocoModel.boolVar(true)
            0 -> chocoModel.boolVar(false)
            else -> throw IllegalStateException()
        }
    }

    override fun visitStruct(term: Struct): ILogical {
        when (term.arity) {
            2 -> when (term.functor) {
                "+" -> return binaryExpression(term, LogOp::or)
                "*" -> return binaryExpression(term, LogOp::and)
                "#" -> return binaryExpression(term, LogOp::xor)
                "=:=" -> return binaryExpression(term, LogOp::ifOnlyIf)
                "=\\=" -> return binaryExpression(term, LogOp::xor)
                "=<" -> return binaryExpression(term, LogOp::implies)
                ">=" -> return binaryExpression(term, LogOp::xor)
                // Expr1 < Expr2 is equivalent to not Expr1 and Expr2
                "<" -> return LogOp.and(
                    unaryExpression(Struct.of(term.functor, LogicList.of(term[0])), UnaryOp.NOT),
                    term[1].accept(this)
                )
                // Expr1 > Expr2 is equivalent to Expr1 and not Expr2
                ">" -> return LogOp.and(
                    term[0].accept(this),
                    unaryExpression(Struct.of(term.functor, LogicList.of(term[1])), UnaryOp.NOT)
                )
            }
            1 -> when (term.functor) {
                "+" -> return unaryExpression(term, UnaryOp.OR)
                "*" -> return unaryExpression(term, UnaryOp.AND)
                "~" -> return unaryExpression(Struct.of(term.functor, LogicList.of(term[0])), UnaryOp.NOT)
            }
        }
        return super.visitStruct(term) // indirectly calls defaultValue(term)
    }

    private fun asExpression(variable: Variable): ILogical {
        return variable as BoolVar // this may fail!
    }

    private fun unaryExpression(struct: Struct, op: UnaryOp): ILogical {
        require(struct[0] is LogicList){
            "${struct[0]} is not a logic list"
        }
        val expressions = struct[0].castToList().toList()
        val first = expressions.map { it.accept(this) }.toTypedArray()
        return when(op){
            UnaryOp.AND -> LogOp.and(*first)
            UnaryOp.OR -> LogOp.or(*first)
            // there is not a not in LogOp
            UnaryOp.NOT -> LogOp.nand(*first)
        }
    }

    private fun binaryExpression(struct: Struct, operator: (ILogical, ILogical) -> ILogical): ILogical {
        val first = struct[0].accept(this)
        val second = struct[1].accept(this)
        return operator(first, second)
    }
}