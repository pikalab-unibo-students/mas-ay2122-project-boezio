package clpqr.optimization

import clpqr.search.ProblemType

object Minimize: Optimize("minimize") {
    override val problemType = ProblemType.MINIMIZE
}
