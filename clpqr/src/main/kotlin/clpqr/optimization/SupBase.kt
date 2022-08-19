package clpqr.optimization

import clpqr.search.ProblemType

object SupBase: OptimumBase("sup") {
    override val problemType = ProblemType.MAXIMIZE
}
