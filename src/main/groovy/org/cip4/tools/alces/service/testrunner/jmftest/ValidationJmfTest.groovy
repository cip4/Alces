package org.cip4.tools.alces.service.testrunner.jmftest

import groovy.xml.XmlSlurper
import org.cip4.jdflib.core.JDFElement
import org.cip4.jdflib.core.JDFParser
import org.cip4.jdflib.core.XMLDoc
import org.cip4.jdflib.validate.JDFValidator
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage
import org.cip4.tools.alces.service.testrunner.model.TestResult
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets

import static org.cip4.tools.alces.service.testrunner.model.TestResult.*

@Component
class ValidationJmfTest implements JmfTest {

    private final JDFValidator jdfValidator

    /**
     * Default constructor.
     */
    ValidationJmfTest() {

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

        // create test result
        return new TestResult(this, message, result, xmlDoc.toXML())
    }
}
