package clpqr

import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.chocosolver.solver.Model

abstract class BaseTest {

    protected val termParser = TermParser.withDefaultOperators()
    protected val theoryParser = ClausesParser.withDefaultOperators()
    protected val precision = Model().precision

}