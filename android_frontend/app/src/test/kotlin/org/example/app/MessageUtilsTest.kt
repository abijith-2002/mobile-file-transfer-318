package org.example.app

import org.junit.Test
import org.junit.Assert.assertEquals

class MessageUtilsTest {
    @Test
    fun testGetMessage() {
        // Expect the same spacing as produced by MessageUtils.message()
        assertEquals("Hello     World!", MessageUtils.message())
    }
}
