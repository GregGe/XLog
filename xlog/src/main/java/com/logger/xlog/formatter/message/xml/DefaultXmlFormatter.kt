package com.logger.xlog.formatter.message.xml

import com.logger.xlog.internal.Platform
import com.logger.xlog.internal.SystemCompat
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * Simply format the XML with a indent of {@value XML_INDENT}.
 * <br></br>TODO: Make indent size and enable/disable state configurable.
 */
class DefaultXmlFormatter : XmlFormatter {
    override fun format(data: String?): String {
        val formattedString: String
        if (data == null || data.trim { it <= ' ' }.isEmpty()) {
            Platform.get().warn("XML empty.")
            return ""
        }
        try {
            val xmlInput: Source = StreamSource(StringReader(data))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", XML_INDENT.toString()
            )
            transformer.transform(xmlInput, xmlOutput)
            formattedString = xmlOutput.writer.toString().replaceFirst(
                ">".toRegex(), ">" + SystemCompat.lineSeparator
            )
        } catch (e: Exception) {
            Platform.get().warn(e.message)
            return data
        }
        return formattedString
    }

    companion object {
        private const val XML_INDENT = 4
    }
}
