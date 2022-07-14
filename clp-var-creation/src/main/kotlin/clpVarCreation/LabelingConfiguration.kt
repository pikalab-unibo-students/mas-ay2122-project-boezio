package clpVarCreation

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

data class LabelingConfiguration(
    var branchingStrategy: BranchingStrategy = BranchingStrategy.STEP,
    var variableSelection: VariableSelectionStrategy = VariableSelectionStrategy.LEFTMOST,
    var problemType: ProblemType = ProblemType.SATISFY,
    var valueOrder: ValueOrder = ValueOrder.UP,
    var objective: Term? = null // expression to be optimized if problemType is either MAXIMISE or MINIMISE,
) {
    companion object {
        fun fromTerms(arguments: Iterable<Term>): LabelingConfiguration {
            val configuration = LabelingConfiguration()
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
                            configuration.objective = term.args[0]
                        }
                    }
                }
            }
            return configuration
        }
    }
}