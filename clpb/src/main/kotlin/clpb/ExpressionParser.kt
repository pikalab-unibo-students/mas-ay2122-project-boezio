package clpb

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.LogOp
import org.chocosolver.solver.variables.Variable

class ExpressionParser<T : Variable>(
    private val chocoModel: Model,
    private val variables: Map<Var, T>
) : DefaultTermVisitor<LogOp>() {
    override fun defaultValue(term: Term): LogOp =
        error("Unsupported sub-expression: $term")

    override fun visitVar(term: Var): LogOp =
        asExpression(variables[term] ?: error("No such a variable: $term"))

    override fun visitInteger(term: Integer): LogOp {
        val value = term.value.toInt()
        return if(value == 1){
            chocoModel.boolVar(true) as LogOp
        } else if (value == 0){
            chocoModel.boolVar(false) as LogOp
        } else{
            throw IllegalStateException()
        }
    }

    override fun visitStruct(term: Struct): LogOp {
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
                    unaryExpression(LogicList.of(term[0]), UnaryOp.NOT),
                    term[1].accept(this)
                )
                // Expr1 > Expr2 is equivalent to Expr1 and not Expr2
                ">" -> return LogOp.and(
                    term[0].accept(this),
                    unaryExpression(LogicList.of(term[1]), UnaryOp.NOT)
                )
            }
            1 -> when (term.functor) {
                "+" -> return unaryExpression(term, UnaryOp.OR)
                "*" -> return unaryExpression(term, UnaryOp.AND)
                "~" -> return unaryExpression(LogicList.of(term), UnaryOp.NOT)
            }
        }
        return super.visitStruct(term) // indirectly calls defaultValue(term)
    }

    private fun asExpression(variable: Variable): LogOp {
        return variable as LogOp // this may fail!
    }

    private fun unaryExpression(struct: Struct, op: UnaryOp): LogOp {
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

    private fun binaryExpression(struct: Struct, operator: (LogOp, LogOp) -> LogOp): LogOp {
        val first = struct[0].accept(this)
        val second = struct[1].accept(this)
        return operator(first, second)
    }
}