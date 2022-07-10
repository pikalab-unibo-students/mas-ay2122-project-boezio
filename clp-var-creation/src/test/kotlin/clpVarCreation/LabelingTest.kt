package clpVarCreation

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class LabelingTest {

    @Test
    fun testGreaterThan() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        // @giuseppeboezio i goal da dare in pasto ai solver vanno parsati come strutture.
        // era il fatto di parsarli come clausole che ti creava problemi.
        val goal = parser.parseStruct(
            "problem(X,Y),labeling([],[X,Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            // @giuseppeboezio è importante evitare di fare asserzioni facendo il confronto tra stringhe, se possibile.
            // meglio confrontare strutture dati. questo perchè a seconda di come lanci i test, i numeri dopo gli underscore
            // nei nomi delle variabili potrebbero cambiare.
            // usando lo scope del parser come ho fatto qui, puoi recuperare facilmente i riferimenti alle variabili
            // usate nel goal
            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(2),
                    varOf("Y") to intOf(1)
                ),
                solution.substitution
            )
        }

        // @giuseppeboezio noterai che il test non passa. La soluzione dà una sostituzione del tipo:
        // {X_1=10, Y_1=9, X_2=10, Y_2=9}
        // il che mi fa pensare che ci sono 2 problemi:
        // 1. il test è over-specifico: per come hai scritto il problema hai davvero garanzia che la prima soluzioe sia X=2 e Y=1?
        //    (nota che non è nemmeno detto che ci sia lo stesso risultato in tutte le run su tutte le macchine)
        //    cerca di scrivere un test che passi sempre se l'implementazione è corretta.
        //    tipo: in realtà, per come hai specificato il problema non ti frega che la X (o la Y) abbia un valore specifico,
        //     purchè il valore sia un intero e sia tra 1 e 10.
        // 2. la sostituzione dà 4 variabili al posto di 2, quindi secondo me da qualche parte a livello implementativo gestiamo male le variabili.
        //    qua tocca debuggare per capire dove.
    }
}