package com.logger.xlog.message

import com.logger.xlog.LogConfiguration
import com.logger.xlog.LogItem
import com.logger.xlog.LogLevel
import com.logger.xlog.XLog
import com.logger.xlog.formatter.message.json.DefaultJsonFormatter
import com.logger.xlog.formatter.message.json.JsonFormatter
import com.logger.xlog.printer.AndroidPrinter
import com.logger.xlog.utils.AssertUtil
import com.logger.xlog.utils.XLogUtil
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DefaultJsonFormatterTest {

    var logContainer: MutableList<LogItem> = ArrayList()
    private val jsonString = "{\"query\":\"Pizza\",\"locations\":[94043,90210]}"

    private val formated = """
          {
              "query": "Pizza",
              "locations": [
                  94043,
                  90210
              ]
          }
        """.trimIndent()

    @Before
    fun setup() {
        XLogUtil.beforeTest()
        val mockJson: JSONObject = Mockito.mock(JSONObject::class.java)
        Mockito.`when`(mockJson.toString()).thenReturn(formated)
        val logConfiguration =
            LogConfiguration.Builder().logLevel(LogLevel.ALL).jsonFormatter(object : JsonFormatter {
                override fun format(data: String?): String? {
                    return mockJson.toString()
                }

            }).build()
        XLog.init(logConfiguration, object : AndroidPrinter() {
            override fun printChunk(logLevel: Int, tag: String?, msg: String) {
                logContainer.add(LogItem(logLevel, tag, msg))
            }
        })
    }

    @Test
    @Throws(Exception::class)
    fun testJsonFormater() {

        XLog.json(jsonString)
        println(logContainer.joinToString(", "))

        AssertUtil.assertHasLog(logContainer, formated)
    }
}