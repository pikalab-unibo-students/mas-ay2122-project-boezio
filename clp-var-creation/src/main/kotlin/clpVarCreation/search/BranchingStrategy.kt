package clpVarCreation.search

import it.unibo.tuprolog.core.Atom

enum class BranchingStrategy {
    STEP, ENUM, BISECT;

    companion object {
        fun fromAtom(atom: Atom): BranchingStrategy? = when (atom.value) {
            "step" -> STEP
            "enum" -> ENUM
            "bisect" -> BISECT
            else -> null
        }
    }
}