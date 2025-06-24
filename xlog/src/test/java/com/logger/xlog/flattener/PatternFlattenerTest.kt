package com.logger.xlog.flattener

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PatternFlattenerTest {
    @Test
    fun testParsePattern() {
        var parameters: List<String?> = PatternFlattener.parsePattern(
            "{d yyyy-MM-dd hh:mm:ss.SSS} {l}/{t}: {m}"
        )
        assertNotNull(parameters)
        assertEquals(4, parameters.size)
        assertEquals("d yyyy-MM-dd hh:mm:ss.SSS", parameters[0])
        assertEquals("l", parameters[1])
        assertEquals("t", parameters[2])
        assertEquals("m", parameters[3])

        parameters = PatternFlattener.parsePattern(
            "Abc { d yyyy  } {l }/{ t}: { m } def"
        )
        assertNotNull(parameters)
        assertEquals(4, parameters.size)
        assertEquals(" d yyyy  ", parameters[0])
        assertEquals("l ", parameters[1])
        assertEquals(" t", parameters[2])
        assertEquals(" m ", parameters[3])

        parameters = PatternFlattener.parsePattern(
            "No valid parameter {f }"
        )
        assertNotNull(parameters)
        assertEquals(1, parameters.size)
        assertEquals("f ", parameters[0])

        parameters = PatternFlattener.parsePattern(
            "No parameter"
        )
        assertNotNull(parameters)
        assertEquals(0, parameters.size)
    }

    @Test
    fun testParseDateParameter() {
        // Test date format.
        var parameterFiller: PatternFlattener.ParameterFiller? =
            PatternFlattener.parseDateParameter(
                "{d yyyy}", "d yyyy"
            )
        assertNoNullAndClass(parameterFiller, PatternFlattener.DateFiller::class.java)
        assertEquals("yyyy", (parameterFiller as PatternFlattener.DateFiller).dateFormat)

        // Test default date format.
        parameterFiller = PatternFlattener.parseDateParameter("{d}", "d")
        assertNoNullAndClass(parameterFiller, PatternFlattener.DateFiller::class.java)
        assertEquals(
            PatternFlattener.DEFAULT_DATE_FORMAT,
            (parameterFiller as PatternFlattener.DateFiller).dateFormat
        )

        // Test invalid format.
        parameterFiller = PatternFlattener.parseDateParameter("{D}", "D")
        assertNull(parameterFiller)
    }

    @Test
    fun testParseLevelParameter() {
        // Test short level format.
        var parameterFiller: PatternFlattener.ParameterFiller? =
            PatternFlattener.parseLevelParameter("{l}", "l")
        assertNoNullAndClass(parameterFiller, PatternFlattener.LevelFiller::class.java)
        assertFalse((parameterFiller as PatternFlattener.LevelFiller).useLongName)

        // Test long level format.
        parameterFiller = PatternFlattener.parseLevelParameter("{L}", "L")
        assertNoNullAndClass(parameterFiller, PatternFlattener.LevelFiller::class.java)
        assertTrue((parameterFiller as PatternFlattener.LevelFiller).useLongName)

        // Test invalid format.
        parameterFiller = PatternFlattener.parseDateParameter("{ll}", "ll")
        assertNull(parameterFiller)
    }

    @Test
    fun testParseTagParameter() {
        var parameterFiller: PatternFlattener.ParameterFiller? = PatternFlattener.parseTagParameter(
            "{t}", "t"
        )
        assertNoNullAndClass(parameterFiller, PatternFlattener.TagFiller::class.java)

        // Test invalid format.
        parameterFiller = PatternFlattener.parseDateParameter("{T}", "T")
        assertNull(parameterFiller)
    }

    @Test
    fun testParseMessageParameter() {
        var parameterFiller: PatternFlattener.ParameterFiller? =
            PatternFlattener.parseMessageParameter(
                "{m}", "m"
            )
        assertNoNullAndClass(parameterFiller, PatternFlattener.MessageFiller::class.java)

        // Test invalid format.
        parameterFiller = PatternFlattener.parseDateParameter("{M}", "M")
        assertNull(parameterFiller)
    }

    private fun assertNoNullAndClass(
        parameterFiller: PatternFlattener.ParameterFiller?,
        clazz: Class<*>
    ) {
        assertNotNull("Parameter filler not created", parameterFiller)
        assertTrue(
            "Parameter filler class not expected: " + parameterFiller!!.javaClass,
            parameterFiller.javaClass == clazz
        )
    }
}