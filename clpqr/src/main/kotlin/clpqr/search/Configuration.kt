package clpqr.search

import it.unibo.tuprolog.core.Term

data class Configuration(
    var problemType: ProblemType = ProblemType.SATISFY,
    var objective: Term? = null // expression to be optimized if problemType is either MAXIMISE or MINIMISE
)