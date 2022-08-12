package clpfd

import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.theory.parsing.ClausesParser

abstract class BaseTest {

    protected val termParser = TermParser.withDefaultOperators()
    protected val theoryParser = ClausesParser.withDefaultOperators()

}