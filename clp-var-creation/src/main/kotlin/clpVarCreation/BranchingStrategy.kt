package clpVarCreation

import it.unibo.tuprolog.core.Atom

enum class BranchingStrategy {
    STEP, ENUM, BISECT;

    companion object {
        fun fromAtom(atom: Atom): BranchingStrategy? = when (atom.value) {
            "step" -> STEP
            "down" -> ENUM
            "bisect" -> BISECT
            else -> null
        }
    }
}