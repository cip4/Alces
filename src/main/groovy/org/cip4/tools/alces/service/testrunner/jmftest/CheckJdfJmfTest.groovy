package org.cip4.tools.alces.service.testrunner.jmftest

import groovy.xml.XmlSlurper
import org.cip4.jdflib.core.JDFElement
import org.cip4.jdflib.core.XMLDoc
import org.cip4.jdflib.validate.JDFValidator
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage
import org.cip4.tools.alces.service.testrunner.model.TestResult
import org.springframework.stereotype.Component

import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import java.nio.charset.StandardCharsets

import static org.cip4.tools.alces.service.testrunner.model.TestResult.*

@Component
class CheckJdfJmfTest implements JmfTest {

    private final static String RES_CHECK_JDF_XSL = "/org/cip4/tools/alces/service/testrunner/checkjdf.xsl";

    private final JDFValidator jdfValidator

    /**
     * Default constructor.
     */
    CheckJdfJmfTest() {

    }

    @Override
    Type getType() {
        return Type.JMF_BOTH_TEST
    }

    @Override
    String getDescription() {
        return "CheckJDF"
    }

    @Override
    TestResult runTest(AbstractJmfMessage message) {

        // create new jdf validator object
        JDFValidator jdfValidator = new JDFValidator()
        jdfValidator.bTiming = true
        jdfValidator.setPrint(false)
        jdfValidator.bQuiet = true
        jdfValidator.setIgnorePrivate(false)
        jdfValidator.level = JDFElement.EnumValidationLevel.Complete
        jdfValidator.bValidate = true
        jdfValidator.setJDFSchemaLocation("https://schema.cip4.org/jdfschema_1_7/JDF.xsd")

        // run check jdf
        XMLDoc xmlDoc = jdfValidator.processSingleStream(
                new ByteArrayInputStream(message.getBody().getBytes(StandardCharsets.UTF_8)),
                "foo.jmf",
                null
        )

        // analyze result
        def checkOutput = new XmlSlurper().parseText(xmlDoc.toXML())

        String schemaResult = checkOutput.TestFile.SchemaValidationOutput.@ValidationResult.toString()
        boolean checkJdfIsValid = checkOutput.TestFile.CheckJDFOutput.@IsValid.toBoolean()

        Result result = (schemaResult.equals("Valid") || schemaResult.equals("NotPerformed")) && checkJdfIsValid ? Result.PASSED : Result.FAILED

        // transform CheckJDF XML Output to xhtml
        Source xslSource = new StreamSource(CheckJdfJmfTest.class.getResourceAsStream(RES_CHECK_JDF_XSL))
        TransformerFactory factory = TransformerFactory.newInstance()
        Transformer transformer = factory.newTransformer(xslSource)

        Source xmlSource = new StreamSource(new StringReader(xmlDoc.toXML()))
        Writer stringWriter = new StringWriter()
        transformer.transform(xmlSource, new StreamResult(stringWriter))

        // create test result
        return new TestResult(this, message, result, stringWriter.toString())
    }
}
