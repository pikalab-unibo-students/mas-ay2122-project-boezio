package clpqr.optimization

import clpqr.search.ProblemType

object InfBase: OptimumBase("inf") {
    override val problemType = ProblemType.MINIMIZE
}
