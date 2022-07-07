package clpVarCreation

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

data class LabellingConfiguration(
    var branchingStrategy: BranchingStrategy = BranchingStrategy.STEP,
    var variableSelection: VariableSelectionStrategy = VariableSelectionStrategy.LEFTMOST,
    var problemType: ProblemType = ProblemType.SATISFY,
    var valueOrder: ValueOrder = ValueOrder.UP,
    var objective: Struct? = null // expression to be optimized if problemType is either MAXIMISE or MINIMISE,
) {
    companion object {
        // TODO no
        fun fromTerms(arguments: Iterable<Term>): LabellingConfiguration {
            val configuration = LabellingConfiguration()
            for (term in arguments) {
                when (term) {
                    is Atom -> {
                        VariableSelectionStrategy.fromAtom(term)?.let { configuration.variableSelection = it }
                        ValueOrder.fromAtom(term)?.let { configuration.valueOrder = it }
                        BranchingStrategy.fromAtom(term)?.let { configuration.branchingStrategy = it }
                    }
                    is Struct -> {
                        configuration.problemType = ProblemType.fromTerm(term)
                        if (configuration.problemType != ProblemType.SATISFY && term.arity == 1) {
                            configuration.objective = term.args[0].asStruct()
                        }
                    }
                }
            }
            return configuration
        }
    }
}