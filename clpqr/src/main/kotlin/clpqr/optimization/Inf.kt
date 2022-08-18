package clpqr.optimization

import clpqr.search.ProblemType

object Inf: Optimum("inf") {
    override val problemType = ProblemType.MINIMIZE
}