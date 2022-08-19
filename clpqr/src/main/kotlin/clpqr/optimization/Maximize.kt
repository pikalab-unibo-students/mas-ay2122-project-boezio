package clpqr.optimization

import clpqr.search.ProblemType

object Maximize: Optimize("maximize") {
    override val problemType = ProblemType.MAXIMIZE
}
