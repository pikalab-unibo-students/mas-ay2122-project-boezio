package clpqr.optimization

import clpqr.search.ProblemType

object Sup: Optimum("sup") {
    override val problemType = ProblemType.MAXIMIZE
}
