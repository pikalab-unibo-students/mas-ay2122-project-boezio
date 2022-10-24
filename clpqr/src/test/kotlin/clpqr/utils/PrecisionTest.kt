package clpqr.utils

import clpqr.Precision
import it.unibo.tuprolog.core.Real
import org.chocosolver.solver.Model
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class PrecisionTest {

    @Test
    fun testPrecisionAdmissibleValues(){
        assertThrows<NotImplementedError> {
            Precision.admissibleValues
        }
    }

    @Test
    fun testPrecisionDefaultValue(){
        assertEquals(Precision.defaultValue, Real.of(Model().precision))
    }
}