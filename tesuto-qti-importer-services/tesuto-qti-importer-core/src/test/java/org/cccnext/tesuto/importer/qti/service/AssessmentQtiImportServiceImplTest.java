/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.cccnext.tesuto.importer.qti.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.AssessmentPartSubmissionMode;
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentChildExpressionDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentParentExpressionDto;
import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemResponseMappingDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseProcessingDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleChoiceDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.ToolsItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.enums.ItemBankStatusType;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.DeliveryTypeMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.PrerequisiteMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.SectionMetadataDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSelectionDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.exception.TesutoException;
import org.cccnext.tesuto.importer.service.upload.PackageResults;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import com.amazonaws.AmazonServiceException;

import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;
import uk.ac.ed.ph.jqtiplus.provision.BadResourceException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

/**
 * * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackages = {"org.cccnext.tesuto.content.repository.mongo"})
@ContextConfiguration(locations = { "classpath:/commonImportContext.xml", "classpath:/qtiTestSamples.xml" })

public class AssessmentQtiImportServiceImplTest {
    @Resource(name = "assessmentQtiImportService")
    AssessmentQtiImportService assessmentQtiImportService;
    @Resource(name = "metadataParseTestFileService")
    ParseTestFileService metadataParseTestFileService;
    @Resource(name = "nestedParseTestFileService")
    ParseTestFileService nestedParseTestFileService;
    @Resource(name = "linearParseTestFileService")
    ParseTestFileService linearParseTestFileService;
    @Resource(name = "choiceParseTestFileService")
    ParseTestFileService choiceParseTestFileService;
    @Resource(name = "choiceWithManifestParseTestFileService")
    ParseTestFileService choiceWithManifestParseTestFileService;
    @Resource(name = "lsiParseTestFileService")
    ParseTestFileService lsiParseTestFileService;
    @Resource(name = "multiItemsParseTestFileService")
    ParseTestFileService mulitiItemsParseTestFileService;
    @Resource(name = "mappingResponseParseTestFileService")
    ParseTestFileService mappingResponseTestFileService;
    @Resource(name = "branchRuleParseTestFileService")
    ParseTestFileService branchRuleParseTestFileService;
    @Resource(name = "unsupportedBranchRuleParseTestFileService")
    ParseTestFileService unsupportedBranchRuleParseTestFileService;
    @Resource(name = "unsupportedNestedBranchRuleParseTestFileService")
    ParseTestFileService unsupportedNestedBranchRuleParseTestFileService;
    @Resource(name = "malformedMetadataContainsOnlyRootNodeParseTestFileService")
    ParseTestFileService malformedMetadataContainsOnlyRootNodeParseTestFileService;
    @Resource(name = "malformedMetadataParseTestFileService")
    ParseTestFileService malformedMetadataParseTestFileService;
    @Resource(name = "invalidSchemaTestFileService")
    ParseTestFileService invalidSchemaTestFileService;
    @Resource(name = "updatedMetadata_1_29_2016_ParseTestFileService")
    ParseTestFileService updatedMetadata_1_29_2016_ParseTestFileService;
    @Resource(name = "updatedMetadata_3_17_2016_ParseTestFileService")
    ParseTestFileService updatedMetadata_3_17_2016_ParseTestFileService;
    @Resource(name = "updatedMetadata_8_10_2016_ParseTestFileService")
    ParseTestFileService updatedMetadata_8_10_2016_ParseTestFileService;
    @Resource(name = "updatedMetadata_10_01_2016_ParseTestFileService")
    ParseTestFileService updatedMetadata_10_01_2016_ParseTestFileService;
    @Resource(name = "branchRuleCycleTargetParseTestFileService")
    ParseTestFileService branchRuleCycleTargetParseTestFileService;
    @Resource(name = "allOutcomesPossible")
    ParseTestFileService allOutcomesPossible;
    @Resource(name = "selectionTestFileService")
    ParseTestFileService selectionTestFileService;
    @Resource(name = "schemaValidationErrors")
    ParseTestFileService schemaValidationErrorsService;
    @Resource(name = "xmlParsingValidationErrors")
    ParseTestFileService xmlParsingErrorsService;
    @Resource(name = "missingResource")
    ParseTestFileService missingResourceService;
    @Resource(name = "invalidAssessmentTestXml")

    ParseTestFileService invalidAssessmentTestXmlService;
    @Resource(name = "imsmanifestNotInRoot")
    ParseTestFileService imsmanifestNotInRootService;
    @Resource(name = "mathSampleFull")
    ParseTestFileService mathSampleFullService;
    @Resource(name = "ariaTagsTest")
    ParseTestFileService ariaTagsTest;

    // Overall Performance / Performance by Category metadata tests
    @Resource(name = "perfValid")
    ParseTestFileService perfValid;
    @Resource(name = "perf4CategoryRanges")
    ParseTestFileService perf4CategoryRanges;
    @Resource(name = "perf6Ranges")
    ParseTestFileService perf6Ranges;
    @Resource(name = "perfGapInRanges")
    ParseTestFileService perfGapInRanges;
    @Resource(name = "perfMinGreaterThanMax")
    ParseTestFileService perfMinGreaterThanMax;
    @Resource(name = "perfNotSequential")
    ParseTestFileService perfNotSequential;
    @Resource(name = "perfPosition2")
    ParseTestFileService perfPosition2;

    private String rootDir = "src/test/resources/qti-test-samples/";

    @Test
    public void packageWithValidPerformanceMetadataHasNoErrorsOrWarnings() throws Exception {
        perfValid.parseTestFile();
        PackageResults packageResults = perfValid.getPackageResults();

        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationWarnings()));
        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationErrors()));
    }

    @Test
    public void packageWith4CategoryRangesFailsValidation() throws Exception {
        perf4CategoryRanges.parseTestFile();
        PackageResults packageResults = perf4CategoryRanges.getPackageResults();

        ValidationMessage tooManyRangesError = new ValidationMessage();
        tooManyRangesError.setMessage("Performance by category metadata should contain exactly 3 performance ranges per category.");
        tooManyRangesError.setNode("performanceRange");
        tooManyRangesError.setFileType(ValidationMessage.FileType.ASSESSMENT_METADATA);

        assertTrue(packageResults.getValidationErrors().contains(tooManyRangesError));
    }

    @Test
    public void packageWith6RangesFailsValidation() throws Exception {
        perf6Ranges.parseTestFile();
        PackageResults packageResults = perf6Ranges.getPackageResults();

        ValidationMessage tooManyRangesError = new ValidationMessage();
        tooManyRangesError.setMessage("Overall performance metadata should contain exactly 5 performance ranges.");
        tooManyRangesError.setNode("overallPerformance");
        tooManyRangesError.setFileType(ValidationMessage.FileType.ASSESSMENT_METADATA);

        assertTrue(packageResults.getValidationErrors().contains(tooManyRangesError));
    }

    @Test
    public void packageWithGapInRangesFailsValidation() throws Exception {
        perfGapInRanges.parseTestFile();
        PackageResults packageResults = perfGapInRanges.getPackageResults();

        ValidationMessage minMaxGapError = new ValidationMessage();
        minMaxGapError.setMessage("Performance range min should equal previous performance position's performance range max.");
        minMaxGapError.setNode("performanceRange");
        minMaxGapError.setFileType(ValidationMessage.FileType.ASSESSMENT_METADATA);

        assertTrue(packageResults.getValidationErrors().contains(minMaxGapError));
    }

    @Test
    public void packageWithMinGreaterThanMaxFailsValidation() throws Exception {
        perfMinGreaterThanMax.parseTestFile();
        PackageResults packageResults = perfMinGreaterThanMax.getPackageResults();

        ValidationMessage minGreaterThanMaxError = new ValidationMessage();
        minGreaterThanMaxError.setMessage("Performance range max should be greater than min.");
        minGreaterThanMaxError.setNode("performanceRange");
        minGreaterThanMaxError.setFileType(ValidationMessage.FileType.ASSESSMENT_METADATA);

        assertTrue(packageResults.getValidationErrors().contains(minGreaterThanMaxError));
    }

    @Test
    public void packageWithNotSequentialRangesFailsValidation() throws Exception {
        perfNotSequential.parseTestFile();
        PackageResults packageResults = perfNotSequential.getPackageResults();

        ValidationMessage notSequentialError = new ValidationMessage();
        notSequentialError.setMessage("Performance range positions are not sequential.");
        notSequentialError.setNode("performanceRange");
        notSequentialError.setFileType(ValidationMessage.FileType.ASSESSMENT_METADATA);

        assertTrue(packageResults.getValidationErrors().contains(notSequentialError));
    }

    @Test
    public void packagageWithStartingPositionOtherThan1FailsValidation() throws Exception {
        perfPosition2.parseTestFile();
        PackageResults packageREsults = perfPosition2.getPackageResults();

        ValidationMessage notStartingAtPosition1Error = new ValidationMessage();
        notStartingAtPosition1Error.setMessage("Performance range position should begin with position 1.");
        notStartingAtPosition1Error.setNode("performanceRange");
        notStartingAtPosition1Error.setFileType(ValidationMessage.FileType.ASSESSMENT_METADATA);

        assertTrue(packageREsults.getValidationErrors().contains(notStartingAtPosition1Error));
    }

    @Test
    public void packageWithAllAriaTagsHasNoErrorsOrWarnings() throws Exception {
        ariaTagsTest.parseTestFile();
        PackageResults packageResults = ariaTagsTest.getPackageResults();

        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationWarnings()));
        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationErrors()));
    }

    @Test
    public void packageWithAllAriaTagsParsesAriaTagsProperly() throws Exception {
        ariaTagsTest.parseTestFile();
        PackageResults packageResults = ariaTagsTest.getPackageResults();

        assertTrue(packageResults.getAssessmentItemDtos().size() == 1);
        AssessmentItemDto assessmentItemDto = packageResults.getAssessmentItemDtos().iterator().next();
        assertTrue(assessmentItemDto.getInteractions().size() == 1);
        AssessmentInteractionDto assessmentInteractionDto = assessmentItemDto.getInteractions().iterator().next();

        assertEquals(assessmentInteractionDto.getAriaControls(), "RESPONSE");
        assertEquals(assessmentInteractionDto.getAriaDescribedBy(), "RESPONSE");
        assertEquals(assessmentInteractionDto.getAriaFlowsTo(), "RESPONSE");
        assertEquals(assessmentInteractionDto.getAriaLabel(), "This is my choice interaction");
        assertEquals(assessmentInteractionDto.getAriaLabelledBy(), "RESPONSE");
        assertEquals(assessmentInteractionDto.getAriaLevel(), "42");
        assertEquals(assessmentInteractionDto.getAriaLive(), "off");
        assertEquals(assessmentInteractionDto.getAriaOrientation(), "horizontal");
        assertEquals(assessmentInteractionDto.getAriaOwns(), "RESPONSE");

        assertEquals(((AssessmentChoiceInteractionDto) assessmentInteractionDto).getChoices().size(), 3);
        for (AssessmentSimpleChoiceDto choice : ((AssessmentChoiceInteractionDto) assessmentInteractionDto).getChoices()) {
            assertTrue(choice.getAriaLabel().matches("^Choice [123] (Red|Blue|Green)$"));
        }
    }

    @Test
    public void packageWithImageHasNoErrorsOrWarnings() throws Exception {
        mathSampleFullService.parseTestFile();
        PackageResults packageResults = mathSampleFullService.getPackageResults();

        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationWarnings()));
        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationErrors()));
    }

    @Test
    public void manifestNotAtRootWillResultInValidationErrors() throws Exception{
        imsmanifestNotInRootService.parseTestFile();
        PackageResults packageResults = imsmanifestNotInRootService.getPackageResults();
        assertNotNull(packageResults.getValidationErrors());

        //Imsmanifest file fails to parse as expected xml
        ValidationMessage maninfestNotFoundError = new ValidationMessage();
        maninfestNotFoundError.setExpectedFile("this-content-package:/imsmanifest.xml");
        maninfestNotFoundError.setMessage("IMS Manifest file was not found. Ensure imsmanifest.xml is at root and compress package again.");

        assertTrue(packageResults.getValidationErrors().contains(maninfestNotFoundError));
    }

    @Test
    public void invalidAssessmentTestXmlWillResultInExpectedValidationErrors() throws Exception {
        invalidAssessmentTestXmlService.parseTestFile();
        PackageResults packageResults = invalidAssessmentTestXmlService.getPackageResults();
        assertNotNull(packageResults.getValidationErrors());

        ValidationMessage invalidAssessmentXml = new ValidationMessage();
        invalidAssessmentXml.setExpectedFile("/tests/a001/a001.xml");
        invalidAssessmentXml.setMessage("The element type \"branchRule\" must be terminated by the matching end-tag \"</branchRule>\".");
        invalidAssessmentXml.setLine(67);
        invalidAssessmentXml.setColumn(7);

        assertTrue(packageResults.getValidationErrors().contains(invalidAssessmentXml));
    }

    @Test
    public void schemaValidationErrorsWillResultInExpectedValidationErrors() throws Exception {
        schemaValidationErrorsService.parseTestFile();
        PackageResults packageResults = schemaValidationErrorsService.getPackageResults();
        assertNotNull(packageResults.getValidationErrors());

        List<ValidationMessage> errors = createExpectedErrors();
        assertEquals(errors, packageResults.getValidationErrors());
    }

    @Test
    public void twoPackagesWithMissingResourcesWillNotHaveCollisions() throws Exception {

        ValidationMessage missingResourceFromSchemaValidationErrors = new ValidationMessage();
        missingResourceFromSchemaValidationErrors.setExpectedFile("/tests/a001/resources/assessmentstylesheet.css (No such file or directory)");
        missingResourceFromSchemaValidationErrors.setMessage("File not found.");

        ValidationMessage missingResourceFromMissingResourcePackage = new ValidationMessage();
        missingResourceFromMissingResourcePackage.setExpectedFile("/tests/a001/resources/assessmentstyle_DNE.css (No such file or directory)");
        missingResourceFromMissingResourcePackage.setMessage("File not found.");

        schemaValidationErrorsService.parseTestFile();
        missingResourceService.parseTestFile();

        PackageResults schemaValidationErrorsPackageResults = schemaValidationErrorsService.getPackageResults();
        PackageResults missingResourcesPackageResults = missingResourceService.getPackageResults();

        List<ValidationMessage> schemaErrors = schemaValidationErrorsPackageResults.getValidationErrors();
        List<ValidationMessage> missingResourcesErrors = missingResourcesPackageResults.getValidationErrors();
        List<ValidationMessage> schemaWarnings = schemaValidationErrorsPackageResults.getValidationWarnings();
        List<ValidationMessage> missingResourcesWarnings = missingResourcesPackageResults.getValidationWarnings();

        //Ensure no error collisions.
        for(ValidationMessage schemaError: schemaErrors){
            assertFalse(missingResourcesErrors.contains(schemaError));
        }

        //Ensure no warning collisions.
        for(ValidationMessage schemaWarning: schemaWarnings){
            assertFalse(missingResourcesWarnings.contains(schemaWarning));
        }

        //Ensure that they both do have missing resource errors.
        assertTrue(missingResourcesErrors.contains(missingResourceFromMissingResourcePackage));
        assertTrue(schemaErrors.contains(missingResourceFromSchemaValidationErrors));
    }




    @Test
    public void xmlParsingValidationErrorsWillResultInExpectedValidationErrors() throws Exception {
        xmlParsingErrorsService.parseTestFile();
        PackageResults packageResults = xmlParsingErrorsService.getPackageResults();
        assertNotNull(packageResults.getValidationErrors());

        List<ValidationMessage> errors = createExpectedXmlParsingErrors();
        assertEquals(errors, packageResults.getValidationErrors());
    }

    public List<ValidationMessage> createExpectedXmlParsingErrors(){
        List<ValidationMessage> errors = new ArrayList<>();

        //Imsmanifest file fails to parse as expected xml
        ValidationMessage manifestInvalidXmlError = new ValidationMessage();
        manifestInvalidXmlError.setExpectedFile("/imsmanifest.xml");
        manifestInvalidXmlError.setMessage("The end-tag for element type \"resource\" must end with a '>' delimiter.");
        manifestInvalidXmlError.setLine(72);
        manifestInvalidXmlError.setColumn(15);
        errors.add(manifestInvalidXmlError);


        //Imsmanifest file does not match schema.  Broken node manifest is not found.
        ValidationMessage manifestNodeMissingError = new ValidationMessage();
        manifestNodeMissingError.setExpectedFile("/imsmanifest.xml");
        manifestNodeMissingError.setMessage("cvc-elt.1.a: Cannot find the declaration of element 'manifest'.");
        manifestNodeMissingError.setLine(4);
        manifestNodeMissingError.setColumn(35);
        errors.add(manifestNodeMissingError);

        //Imsmanifest file fails to parse as expected xml
        ValidationMessage secondResourceNodeXmlError = new ValidationMessage();
        secondResourceNodeXmlError.setExpectedFile("this-content-package:/imsmanifest.xml");
        secondResourceNodeXmlError.setMessage("The end-tag for element type \"resource\" must end with a '>' delimiter.");
        secondResourceNodeXmlError.setLine(72);
        secondResourceNodeXmlError.setColumn(15);
        errors.add(secondResourceNodeXmlError);

        //AssessmentItem file fails to parse as expected xml
        ValidationMessage assessmentItemInvalidXmlError = new ValidationMessage();
        assessmentItemInvalidXmlError.setExpectedFile("/items/i012/i012.xml");
        assessmentItemInvalidXmlError.setMessage("The element type \"itemBody\" must be terminated by the matching end-tag \"</itemBody>\".");
        assessmentItemInvalidXmlError.setLine(17);
        assessmentItemInvalidXmlError.setColumn(3);
        errors.add(assessmentItemInvalidXmlError);
        return errors;
    }

    public List<ValidationMessage> createExpectedErrors(){
        List<ValidationMessage> errors = new ArrayList<>();

        //AssessmentTest does not match schema. Required attribute missing.
        ValidationMessage assessmentTestSchemaValidationError = new ValidationMessage();
        assessmentTestSchemaValidationError.setExpectedFile("/tests/a001/a001.xml");
        assessmentTestSchemaValidationError.setNode("equal");
        assessmentTestSchemaValidationError.setMessage("Required attribute 'toleranceMode' has not been assigned a value");
        assessmentTestSchemaValidationError.setLine(9);
        assessmentTestSchemaValidationError.setColumn(18);
        errors.add(assessmentTestSchemaValidationError);

        //AssessmentTest does not match schema. Branch Rule target does not exist.
        ValidationMessage branchRuleTargetDNEError = new ValidationMessage();
        branchRuleTargetDNEError.setExpectedFile("/tests/a001/a001.xml");
        branchRuleTargetDNEError.setNode("branchRule");
        branchRuleTargetDNEError.setMessage("branchRule target a001_testlet12_DNE was not found within the testPart with identifier a001_tp1");
        branchRuleTargetDNEError.setLine(26);
        branchRuleTargetDNEError.setColumn(47);

        errors.add(branchRuleTargetDNEError);

        //AssessmentTest does not match schema. Duplicate identifier.
        ValidationMessage duplicateItemRefError = new ValidationMessage();
        duplicateItemRefError.setExpectedFile("/tests/a001/a001.xml");
        duplicateItemRefError.setNode("assessmentItemRef");
        duplicateItemRefError.setMessage("Duplicate identifier item-9");
        duplicateItemRefError.setLine(59);
        duplicateItemRefError.setColumn(92);

        errors.add(duplicateItemRefError);

        //AssessmentTest does not match schema. Duplicate identifier part of the above error
        ValidationMessage duplicateItemRefError2 = new ValidationMessage();
        duplicateItemRefError2.setExpectedFile("/tests/a001/a001.xml");
        duplicateItemRefError2.setNode("assessmentItemRef");
        duplicateItemRefError2.setMessage("Duplicate identifier item-9");
        duplicateItemRefError2.setLine(69);
        duplicateItemRefError2.setColumn(80);
        errors.add(duplicateItemRefError2);

        //AssessmentItem does not match schema. Node is invalid.
        ValidationMessage assessmentItemSchemaValidationError = new ValidationMessage();
        assessmentItemSchemaValidationError.setExpectedFile("/items/i003/i003.xml");
        assessmentItemSchemaValidationError.setNode("correctResponse");
        assessmentItemSchemaValidationError.setMessage("Invalid values count. Expected: 1. Found: 2");
        assessmentItemSchemaValidationError.setLine(7);
        assessmentItemSchemaValidationError.setColumn(26);
        errors.add(assessmentItemSchemaValidationError);

        ValidationMessage missingResource = new ValidationMessage();
        missingResource.setExpectedFile("/tests/a001/resources/assessmentstylesheet.css (No such file or directory)");
        missingResource.setMessage("File not found.");
        errors.add(missingResource);

        //IMSManifest does not match schema. 2.2 Schema does not mach node.
        //        <metadata>
        //          <schema>QTIv2.1 Package</schema>
        //          <schemaversion>1.0.0</schemaversion>
        //        </metadata>
        ValidationMessage imsSchemaValidationError = new ValidationMessage();
        imsSchemaValidationError.setExpectedFile("/imsmanifest.xml");
        imsSchemaValidationError.setMessage("cvc-enumeration-valid: Value 'QTIv2.1 Package' is not facet-valid with respect to enumeration '[QTIv2.2 Package, QTIv2.2 Item Bank Package, QTIv2.2 Object Bank Package]'. It must be a value from the enumeration.");
        imsSchemaValidationError.setLine(6);
        imsSchemaValidationError.setColumn(41);
        errors.add(imsSchemaValidationError);

        return errors;
    }

    @Test
    public void testGreaterThanExpressionImportsAsExpected() throws Exception {
        AssessmentChildExpressionDto childExpressionDto = getChildExpressionForBranchRule(0, "a001_testlet7");
        assertEquals(ExpressionType.GT, childExpressionDto.getExpressionType());
        assertEquals("a001_testlet3.SCORE", childExpressionDto.getVariable());
        assertEquals(0.0, childExpressionDto.getBaseValue(), 0.0);
    }

    @Test
    public void testGreaterThanOrEqualExpressionImportsAsExpected() throws Exception {
        AssessmentChildExpressionDto childExpressionDto = getChildExpressionForBranchRule(1, "a001_testlet8");
        assertEquals(ExpressionType.GTE, childExpressionDto.getExpressionType());
        assertEquals("a001_testlet4.SCORE", childExpressionDto.getVariable());
        assertEquals(1f, childExpressionDto.getBaseValue(), 0.0);
    }

    @Test
    public void testEqualExpressionImportsAsExpected() throws Exception {
        AssessmentChildExpressionDto childExpressionDto = getChildExpressionForBranchRule(2, "a001_testlet9");
        assertEquals(ExpressionType.EQUAL, childExpressionDto.getExpressionType());
        assertEquals("a001_testlet5.SCORE", childExpressionDto.getVariable());
        assertEquals(2, childExpressionDto.getBaseValue(), 0.0);
    }

    @Test
    public void testLessThanOrEqualExpressionImportsAsExpected() throws Exception {
        AssessmentChildExpressionDto childExpressionDto = getChildExpressionForBranchRule(3, "a001_testlet10");
        assertEquals(ExpressionType.LTE, childExpressionDto.getExpressionType());
        assertEquals("a001_testlet6.SCORE", childExpressionDto.getVariable());
        assertEquals(3, childExpressionDto.getBaseValue(), 0.0);
    }

    @Test
    public void testLessThanExpressionImportsAsExpected() throws Exception {
        AssessmentChildExpressionDto childExpressionDto = getChildExpressionForBranchRule(4, "a001_testlet11");
        assertEquals(ExpressionType.LT, childExpressionDto.getExpressionType());
        assertEquals("a001_testlet7.SCORE", childExpressionDto.getVariable());
        assertEquals(4.004, childExpressionDto.getBaseValue(), 0.00001);
    }

    @Test
    public void branchRuleCycleWillResultInValidationErrors() throws Exception {
        branchRuleCycleTargetParseTestFileService.parseTestFile();
        PackageResults packageResults = branchRuleCycleTargetParseTestFileService.getPackageResults();
        assertNotNull(packageResults.getValidationErrors());

        //branchRule cycle
        ValidationMessage branchCycle1 = new ValidationMessage();
        branchCycle1.setExpectedFile("/tests/a001/a001.xml");
        branchCycle1.setNode("branchRule");
        branchCycle1.setMessage("branchRule target a001_testlet1 must come after a001_testlet2");
        branchCycle1.setColumn(42);
        branchCycle1.setLine(25);

        //branchRule cycle
        ValidationMessage branchCycle2 = new ValidationMessage();
        branchCycle2.setExpectedFile("/tests/a001/a001.xml");
        branchCycle2.setNode("branchRule");
        branchCycle2.setMessage("branchRule target a001_testlet2 must come after a001_testlet3");
        branchCycle2.setColumn(42);
        branchCycle2.setLine(41);

        assertTrue(packageResults.getValidationErrors().contains(branchCycle1));
        assertTrue(packageResults.getValidationErrors().contains(branchCycle2));
    }

    public void testUnSupportedExpressionWillResultInValidationErrors() throws XmlResourceNotFoundException, AmazonServiceException,
            BadResourceException, NoSuchAlgorithmException, IOException, TesutoException, URISyntaxException,
            ParserConfigurationException, SAXException, TransformerException {
        unsupportedBranchRuleParseTestFileService.parseTestFile();
        PackageResults packageResults = unsupportedBranchRuleParseTestFileService.getPackageResults();

        assertNotNull(packageResults.getValidationErrors());

        ValidationMessage branchruleNotSupportedError = new ValidationMessage();
        branchruleNotSupportedError.setMessage("Parent Expression type is not currently supported: divide");

        assertTrue(packageResults.getValidationErrors().contains(branchruleNotSupportedError));
    }

    public void testUnSupportedNestedExpressionWillResultInValidationErrors() throws XmlResourceNotFoundException,
            AmazonServiceException, BadResourceException, NoSuchAlgorithmException, IOException, TesutoException,
            URISyntaxException, ParserConfigurationException, SAXException, TransformerException {
        unsupportedNestedBranchRuleParseTestFileService.parseTestFile();

        PackageResults packageResults = unsupportedNestedBranchRuleParseTestFileService.getPackageResults();

        assertNotNull(packageResults.getValidationErrors());

        ValidationMessage branchruleNotSupportedError = new ValidationMessage();
        branchruleNotSupportedError.setMessage("Parent Expression type is not currently supported: and");

        assertTrue(packageResults.getValidationErrors().contains(branchruleNotSupportedError));
    }

	@Test
	public void allBranchRuleOutcomesArePossible() throws XmlResourceNotFoundException, AmazonServiceException, BadResourceException, NoSuchAlgorithmException, IOException, TesutoException, URISyntaxException, ParserConfigurationException, SAXException, TransformerException {
		allOutcomesPossible.parseTestFile();  //No exception thrown
        PackageResults packageResults = allOutcomesPossible.getPackageResults();

        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationErrors()));
        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationWarnings()));
	}

    private AssessmentChildExpressionDto getChildExpressionForBranchRule(int branchruleIndex, String target)
            throws XmlResourceNotFoundException, AmazonServiceException, BadResourceException, NoSuchAlgorithmException,
            IOException, TesutoException, URISyntaxException, ParserConfigurationException, SAXException,
            TransformerException {
        branchRuleParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = branchRuleParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentPartDto assessmentPartDto = assessmentDto.getAssessmentParts().get(0);

        List<AssessmentSectionDto> assessmentSections = assessmentPartDto.getAssessmentSections();
        AssessmentSectionDto assessmentSectionDto = assessmentSections.get(2);
        assertEquals("a001_testlet3", assessmentSectionDto.getId()); // ensure
                                                                     // testing
                                                                     // the
                                                                     // correct
                                                                     // section

        AssessmentBranchRuleDto assessmentBranchRuleDto = assessmentSectionDto.getBranchRules().get(branchruleIndex);
        assertNotNull(assessmentBranchRuleDto);
        assertEquals(target, assessmentBranchRuleDto.getTarget());

        AssessmentParentExpressionDto assessmentParentExpressionDto = assessmentBranchRuleDto.getAssessmentParentExpression();
        assertNull(assessmentParentExpressionDto.getExpressionType());
        assertNull(assessmentParentExpressionDto.getAssessmentParentExpressionDtoList());

        return assessmentParentExpressionDto.getAssessmentChildExpressionDtoList().get(0);
    }

    @Test
    public void testParentExpressionAndChildExpressionAreCreatedForABranchRule() throws Exception {
        branchRuleParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = branchRuleParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentPartDto assessmentPartDto = assessmentDto.getAssessmentParts().get(0);

        List<AssessmentSectionDto> assessmentSections = assessmentPartDto.getAssessmentSections();
        AssessmentSectionDto assessmentSectionDto = assessmentSections.get(0);
        assertEquals("a001_testlet1", assessmentSectionDto.getId()); // ensure
                                                                     // testing
                                                                     // the
                                                                     // correct
                                                                     // section

        AssessmentBranchRuleDto branchRuleDto = assessmentSectionDto.getBranchRules().get(0); // only
                                                                                              // one
                                                                                              // branch
                                                                                              // rule
        assertNotNull(branchRuleDto);

        AssessmentParentExpressionDto parentExpressionDto = branchRuleDto.getAssessmentParentExpression();
        assertNotNull(parentExpressionDto);
        assertEquals(ExpressionType.AND, parentExpressionDto.getExpressionType());
        assertTrue(parentExpressionDto.getAssessmentParentExpressionDtoList().isEmpty()); // There
                                                                                          // shouldn't
                                                                                          // be
                                                                                          // a
                                                                                          // parent
                                                                                          // list

        List<AssessmentChildExpressionDto> childExpressionDtos = parentExpressionDto
                .getAssessmentChildExpressionDtoList();
        assertEquals(2, childExpressionDtos.size());

        assertEquals(ExpressionType.GTE, childExpressionDtos.get(0).getExpressionType());
        assertEquals("a001_testlet1.SCORE", childExpressionDtos.get(0).getVariable());
        assertEquals(1.5, childExpressionDtos.get(0).getBaseValue(), 0.0);

        assertEquals(ExpressionType.LTE, childExpressionDtos.get(1).getExpressionType());
        assertEquals("a001_testlet1.SCORE", childExpressionDtos.get(1).getVariable());
        assertEquals(5.0, childExpressionDtos.get(1).getBaseValue(), 0.0);
    }

    @Test
    public void testChildExpressionCanBeNestedThreeLevelsDeep() throws Exception {
        branchRuleParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = branchRuleParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentPartDto assessmentPartDto = assessmentDto.getAssessmentParts().get(0);

        List<AssessmentSectionDto> assessmentSections = assessmentPartDto.getAssessmentSections();
        AssessmentSectionDto assessmentSectionDto = assessmentSections.get(1);
        assertEquals("a001_testlet2", assessmentSectionDto.getId()); // ensure
                                                                     // testing
                                                                     // the
                                                                     // correct
                                                                     // section

        AssessmentBranchRuleDto branchRuleDto = assessmentSectionDto.getBranchRules().get(0); // only
                                                                                              // one
                                                                                              // branch
                                                                                              // rule
        assertNotNull(branchRuleDto);

        // First level of parent expression
        AssessmentParentExpressionDto parentExpressionDto = branchRuleDto.getAssessmentParentExpression();
        assertNotNull(parentExpressionDto);
        assertEquals(ExpressionType.OR, parentExpressionDto.getExpressionType());
        assertTrue(parentExpressionDto.getAssessmentChildExpressionDtoList().isEmpty());

        // Two Parent expressions at this level only the first one will be
        // tested.
        assertEquals(2, parentExpressionDto.getAssessmentParentExpressionDtoList().size());

        // Second level of parent expression
        AssessmentParentExpressionDto secondLevelANDparentExpressionDto = parentExpressionDto
                .getAssessmentParentExpressionDtoList().get(0);
        assertNotNull(secondLevelANDparentExpressionDto);
        assertEquals(ExpressionType.AND, secondLevelANDparentExpressionDto.getExpressionType());

        AssessmentChildExpressionDto childExpressionDto = secondLevelANDparentExpressionDto
                .getAssessmentChildExpressionDtoList().get(0);
        assertEquals(ExpressionType.GTE, childExpressionDto.getExpressionType());
        assertEquals("a001_testlet2.SCORE", childExpressionDto.getVariable());
        assertEquals(1, childExpressionDto.getBaseValue(), 0.0);

        // One OR Parent expression at this level
        assertEquals(1, secondLevelANDparentExpressionDto.getAssessmentParentExpressionDtoList().size());

        // Third level of parent expression
        AssessmentParentExpressionDto thirdLevelORparentExpressionDto = secondLevelANDparentExpressionDto
                .getAssessmentParentExpressionDtoList().get(0);
        assertNotNull(thirdLevelORparentExpressionDto);
        assertEquals(ExpressionType.OR, thirdLevelORparentExpressionDto.getExpressionType());
        assertTrue(thirdLevelORparentExpressionDto.getAssessmentParentExpressionDtoList().isEmpty()); // No
                                                                                                      // further
                                                                                                      // nesting

        // TWO children LTE and GT child expressions
        AssessmentChildExpressionDto childExpressionDto1 = thirdLevelORparentExpressionDto
                .getAssessmentChildExpressionDtoList().get(0);
        assertEquals(ExpressionType.LTE, childExpressionDto1.getExpressionType());
        assertEquals("a001_testlet2.SCORE", childExpressionDto1.getVariable());
        assertEquals(2, childExpressionDto1.getBaseValue(), 0.0);

        AssessmentChildExpressionDto childExpressionDto2 = thirdLevelORparentExpressionDto
                .getAssessmentChildExpressionDtoList().get(1);
        assertEquals(ExpressionType.GT, childExpressionDto2.getExpressionType());
        assertEquals("a001_testlet2.SCORE", childExpressionDto2.getVariable());
        assertEquals(0, childExpressionDto2.getBaseValue(), 0.0);
    }

    @Test
    public void testNestedSectionImport() throws Exception {
        nestedParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = nestedParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentPartDto assessmentPartDto = assessmentDto.getAssessmentParts().get(0);
        List<AssessmentSectionDto> assessmentSectionLevel1 = assessmentPartDto.getAssessmentSections();
        assertEquals("a001_master_section", assessmentSectionLevel1.get(0).getId());
        assertEquals("test_outer_section", assessmentSectionLevel1.get(0).getTitle());
        assertEquals(false, assessmentSectionLevel1.get(0).getVisible());

        List<AssessmentSectionDto> assessmentSectionLevel2 = assessmentSectionLevel1.get(0).getAssessmentSections();
        assertEquals("a001_testlet1", assessmentSectionLevel2.get(0).getId());
        assertEquals("Entry Testlet 1", assessmentSectionLevel2.get(0).getTitle());
        assertEquals(false, assessmentSectionLevel2.get(0).getVisible());
        assertEquals("<p xmlns=\"http://www.imsglobal.org/xsd/imsqti_v2p1\">Test Content</p>",
                assessmentSectionLevel2.get(0).getRubricBlocks().get(0).getContent());
        assertEquals("candidate".toUpperCase(),
                assessmentSectionLevel2.get(0).getRubricBlocks().get(0).getViews().get(0));

        List<AssessmentSectionDto> assessmentSectionLevel3 = assessmentSectionLevel2.get(0).getAssessmentSections();
        assertEquals("a001_bundle1", assessmentSectionLevel3.get(0).getId());
        assertEquals("Item Bundle 1", assessmentSectionLevel3.get(0).getTitle());
        assertEquals(true, assessmentSectionLevel3.get(0).getVisible());

        List<AssessmentSectionDto> assessmentSectionLevel4 = assessmentSectionLevel3.get(0).getAssessmentSections();
        assertEquals(new ArrayList(), assessmentSectionLevel4);

    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testMalformedAssessmentMetadataImportWhenRootNodeExistsShouldReturnNullAssessmentMetadataDto()
            throws Exception {
        // First check assessment. There is only one.
        malformedMetadataContainsOnlyRootNodeParseTestFileService.parseTestFile();
        PackageResults packageResults  = malformedMetadataContainsOnlyRootNodeParseTestFileService.getPackageResults();
        AssessmentDto assessmentDto = packageResults.getAssessmentDtos().get(0);

        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNull(assessmentMetadataDto);

        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationErrors()));

        ValidationMessage assessmentMetadataWarning = new ValidationMessage();
        assessmentMetadataWarning.setMessage("There is not a matching assessment for the metadata with the identifier: null");
        assessmentMetadataWarning.setNode("identifier");
        assessmentMetadataWarning.setFileType(ValidationMessage.FileType.ASSESSMENT_METADATA);

        assertTrue(packageResults.getValidationWarnings().contains(assessmentMetadataWarning));

    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testMalformedItemMetadataImportWhenRootNodeExistsShouldReturnNullItemMetadataDto() throws Exception {
        malformedMetadataContainsOnlyRootNodeParseTestFileService.parseTestFile();
        PackageResults packageResults = malformedMetadataContainsOnlyRootNodeParseTestFileService
               .getPackageResults();
        AssessmentItemDto assessmentItemDto = packageResults.getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNull(itemMetadataDto);

        assertTrue(CollectionUtils.isEmpty(packageResults.getValidationErrors()));

        ValidationMessage assessmentMetadataWarning = new ValidationMessage();
        assessmentMetadataWarning.setMessage("There is not a matching item for the itemMetadata with the identifier: i001");
        assessmentMetadataWarning.setNode("identifier");
        assessmentMetadataWarning.setFileType(ValidationMessage.FileType.ITEM_METADATA);

        assertTrue(packageResults.getValidationWarnings().contains(assessmentMetadataWarning));
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    @Ignore //passes with ide but not in maven
    public void testMalformedAssessmentMetadataWithMalformedPreRequisteNodeShouldReturnNullPrerequisite()
            throws Exception {
        // First check assessment. There is only one.
        malformedMetadataParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = malformedMetadataParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        PrerequisiteMetadataDto prerequisiteMetadataDto = assessmentMetadataDto.getPreRequisite();
        assertNull(prerequisiteMetadataDto);

    }

    @Test
    public void testSchemaValidationInvalidWillResultInExpectedValidationErrors() throws XmlResourceNotFoundException,
            AmazonServiceException, BadResourceException, NoSuchAlgorithmException, IOException, TesutoException,
            URISyntaxException, ParserConfigurationException, SAXException, TransformerException {
        invalidSchemaTestFileService.parseTestFile();
        PackageResults packageResults =  invalidSchemaTestFileService.getPackageResults();
        assertNotNull(packageResults.getValidationErrors());

        ValidationMessage invalidSchema = new ValidationMessage();
        invalidSchema.setExpectedFile("/ilsimath16-001-014-007-00/qti.xml");
        invalidSchema.setMessage("cvc-complex-type.2.4.a: Invalid content was found starting with element 'inlineChoiceInteraction'. One of '{\"http://www.w3.org/1998/Math/MathML\":malignmark, \"http://www.w3.org/1998/Math/MathML\":mglyph}' is expected.");
        invalidSchema.setLine(58);
        invalidSchema.setColumn(81);

        assertTrue(packageResults.getValidationErrors().contains(invalidSchema));
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    @Ignore //passes with ide not maven
    public void testMalformedAssessmentMetadataWithMalformedSectionIdAndParameterWillReturnNullForIndividualSections()
            throws Exception {
        // First check assessment. There is only one.
        malformedMetadataParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = malformedMetadataParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        List<SectionMetadataDto> section = assessmentMetadataDto.getSection();
        assertNotNull(section);
        assertEquals(0, section.size());

    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testMalformedItemMetadataWithMalformedCommonCoreWillReturnExpectedArray() throws Exception {
        // First check assessment. There is only one.
        malformedMetadataParseTestFileService.parseTestFile();
        AssessmentItemDto assessmentItemDto = malformedMetadataParseTestFileService.getPackageResults().getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);
        List<String> commonCores = itemMetadataDto.getCommonCoreRef();
        assertNotNull(commonCores);

        assertEquals(1, commonCores.size());
        assertEquals("", commonCores.get(0).trim());

    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    @Ignore //passes with ide but not maven
    public void testMalformedItemMetadataWithMalformedCompetenciesWillReturnExpectedCompetenciesItemMetadataDto()
            throws Exception {
        // First check assessment. There is only one.
        malformedMetadataParseTestFileService.parseTestFile();
        AssessmentItemDto assessmentItemDto = malformedMetadataParseTestFileService.getPackageResults().getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);

        CompetenciesItemMetadataDto competenciesItemMetadataDto = itemMetadataDto.getCompetencies();
        assertNotNull(competenciesItemMetadataDto); // Empty Array of
                                                    // Compentencies

        List<CompetencyRefItemMetadataDto> competencyRefItemMetadataDtoList = competenciesItemMetadataDto
                .getCompetencyRef();
        assertNotNull(competencyRefItemMetadataDtoList); // Array with nothing
                                                         // in it.
        assertEquals(0, competencyRefItemMetadataDtoList.size()); // Nothing in
                                                                  // the array

        assertNull(competenciesItemMetadataDto.getSkippedCompetency()); // Empty
                                                                        // list
        assertNull(competenciesItemMetadataDto.getSkippedCompetencyRefId()); // Empty
                                                                             // list
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    @Ignore  //passes with ide but not maven
    public void testMalformedItemMetadataWithMalformedToolsWillReturnNullToolItemMetadataDto() throws Exception {
        // First check assessment. There is only one.
        malformedMetadataParseTestFileService.parseTestFile();
        AssessmentItemDto assessmentItemDto = malformedMetadataParseTestFileService.getPackageResults().getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);

        assertNull(itemMetadataDto.getTools());
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testAssessmentMetadataDtoIsAsExpected() throws Exception {
        // First check assessment. There is only one.
        metadataParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = metadataParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        assertEquals("assessmentmetadata", assessmentMetadataDto.getType());
        assertEquals("a001", assessmentMetadataDto.getIdentifier());
        assertEquals("1", assessmentMetadataDto.getAuthoringToolVersion());
        assertEquals("LSI", assessmentMetadataDto.getAuthor());
        assertEquals("Yes", assessmentMetadataDto.getDisplayInHistory());
        assertEquals("Yes", assessmentMetadataDto.getDisplayGeneralInstructions());
        assertTrue(StringUtils.isBlank(assessmentMetadataDto.getDisplayGeneralClosing()));
        assertEquals("No", assessmentMetadataDto.getAutoActivate());
        assertEquals("Yes", assessmentMetadataDto.getRequirePasscode());

        PrerequisiteMetadataDto prerequisiteMetadataDto = assessmentMetadataDto.getPreRequisite();
        assertNotNull(prerequisiteMetadataDto);

        List<SectionMetadataDto> section = assessmentMetadataDto.getSection();
        assertNotNull(section);

        assertEquals("The following assessment will determine your placement for Mathematics.",
                assessmentMetadataDto.getInstructions().trim());
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testAssessmentMetadataDtoNotUpdatedOn_3_17_2016_WillReturnDefaultValuesForDeliveryType()
            throws Exception {
        // First check assessment. There is only one.
        metadataParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = metadataParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        DeliveryTypeMetadataDto deliveryTypeMetadataDto = assessmentMetadataDto.getDeliveryType();
        assertNull(deliveryTypeMetadataDto);

        // Verify the above isPaper and isOnline are not default
        assertFalse(assessmentMetadataDto.isPaper());
        assertTrue(assessmentMetadataDto.isOnline());
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testAssessmentMetadataDtoUpdate_3_17_2016_IsAsExpected() throws Exception {
        // First check assessment. There is only one.
        updatedMetadata_3_17_2016_ParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = updatedMetadata_3_17_2016_ParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        DeliveryTypeMetadataDto deliveryTypeMetadataDto = assessmentMetadataDto.getDeliveryType();
        assertNotNull(deliveryTypeMetadataDto);

        assertEquals("Yes", deliveryTypeMetadataDto.getPaper());
        assertEquals("NO", deliveryTypeMetadataDto.getOnline());

        // Verify the above isPaper and isOnline are not default
        assertTrue(assessmentMetadataDto.isPaper());
        assertFalse(assessmentMetadataDto.isOnline());
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     *
     * TODO Corresponds to LSI CCC Assess Assessment Metadata Guidelines Version TBD
     */
    @Test
    public void testAssessmentMetadataDtoUpdate_8_10_2016_IsAsExpected() throws Exception {
        // First check assessment. There is only one.
        updatedMetadata_3_17_2016_ParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = updatedMetadata_8_10_2016_ParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        assertEquals("Yes", assessmentMetadataDto.getAvailable());
        assertEquals(65.0, assessmentMetadataDto.getScaleMultiplicativeTerm(), 0.0);
        assertEquals(492.21, assessmentMetadataDto.getScaleAdditiveTerm(), 0.0);
        assertEquals(Arrays.asList("ENGLISH", "ESL"), assessmentMetadataDto.getCompetencyMapDisciplines());
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     *
     * TODO Corresponds to LSI CCC Assess Assessment Metadata Guidelines Version TBD
     */
    @Test
    public void testAssessmentMetadataDtoUpdate_10_01_2016_IsAsExpected() throws Exception {
        // First check assessment. There is only one.
        updatedMetadata_10_01_2016_ParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = updatedMetadata_10_01_2016_ParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        assertEquals("Yes", assessmentMetadataDto.getGenerateAssessmentPlacement());
        assertTrue(assessmentMetadataDto.isGeneratePlacement());
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testSectionMetadataDtoListIsAsExpected() throws Exception {
        // First check assessment. There is only one.
        metadataParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = metadataParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        List<SectionMetadataDto> section = assessmentMetadataDto.getSection();
        assertNotNull(section);

        assertNotNull(section.get(0));
        assertEquals("s1001", section.get(0).getIdentifier());
        assertEquals("testlet", section.get(0).getType());

        assertNotNull(section.get(1));
        assertEquals("s1002", section.get(1).getIdentifier());
        assertEquals("itemBundle", section.get(1).getType());
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     *
     * TODO Corresponds to LSI CCC Assess Assessment Metadata Guidelines Version TBD
     */
    @Test
    public void testSectionMetadataDtoListUpdate_8_10_2016_IsAsExpected() throws Exception {
        // First check assessment. There is only one.
        updatedMetadata_8_10_2016_ParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = updatedMetadata_8_10_2016_ParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        List<SectionMetadataDto> section = assessmentMetadataDto.getSection();
        assertNotNull(section);

        assertNotNull(section.get(0));
        assertEquals("a001_testlet1", section.get(0).getIdentifier());
        assertEquals("entry-testlet", section.get(0).getType());
        assertEquals("MATH", section.get(0).getCompetencyMapDiscipline());

        assertNotNull(section.get(1));
        assertEquals("a001_testlet2", section.get(1).getIdentifier());
        assertEquals("entry-testlet", section.get(1).getType());
        //normalized
        assertEquals("ENGLISH", section.get(1).getCompetencyMapDiscipline());
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testPrerequisiteMetadataDtoIsAsExpected() throws Exception {
        // First check assessment. There is only one.
        metadataParseTestFileService.parseTestFile();
        AssessmentDto assessmentDto = metadataParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        AssessmentMetadataDto assessmentMetadataDto = assessmentDto.getAssessmentMetadata();
        assertNotNull(assessmentMetadataDto);

        PrerequisiteMetadataDto prerequisiteMetadataDto = assessmentMetadataDto.getPreRequisite();
        assertNotNull(prerequisiteMetadataDto);

        assertEquals("acaisurvey100001", prerequisiteMetadataDto.getAssessmentIdRef());
    }

    @Test
    public void testGetItemMetadataWillReturnNullForNonExistantMetadataItems() throws Exception {
        // Verify that assessmentItem 2-8 have null for metadata
        metadataParseTestFileService.parseTestFile();
        for (int i = 1; i < 8; i++) {
            AssessmentItemDto assessmentItemDto = metadataParseTestFileService.getPackageResults().getAssessmentItemDtos().get(i);
            assertNull(assessmentItemDto.getItemMetadata());
        }
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testItemMetadataDtoIsAsExpected() throws Exception {
        // There is only one assessmentItemDto with metadata
        metadataParseTestFileService.parseTestFile();
        AssessmentItemDto assessmentItemDto = metadataParseTestFileService.getPackageResults().getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);

        assertEquals("itemmetadata", itemMetadataDto.getType());
        assertEquals("i001", itemMetadataDto.getIdentifier());
        assertTrue(StringUtils.isBlank(itemMetadataDto.getAuthoringTool()));
        assertEquals("5.2", itemMetadataDto.getAuthoringToolVersion());
        assertEquals("M", itemMetadataDto.getAuthoringToolAnswerType());
        assertEquals("LSI", itemMetadataDto.getAuthor());
        assertEquals("1", itemMetadataDto.getDifficulty());
        assertEquals("1", itemMetadataDto.getWeightedCategory());
        assertEquals("False", itemMetadataDto.getContextual());

        List<String> commonCoreRefs = itemMetadataDto.getCommonCoreRef();
        assertNotNull(commonCoreRefs);

        assertTrue(StringUtils.isBlank(itemMetadataDto.getLexile()));
        assertEquals("False", itemMetadataDto.getPassage());
        assertTrue(StringUtils.isBlank(itemMetadataDto.getPassageType()));

        CompetenciesItemMetadataDto competenciesItemMetadataDto = itemMetadataDto.getCompetencies();
        assertNotNull(competenciesItemMetadataDto);

        ToolsItemMetadataDto toolsItemMetadataDto = itemMetadataDto.getTools();
        assertNotNull(toolsItemMetadataDto);
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testItemMetadataDtoIsAsExpected_8_10_2016_IsAsExpected() throws Exception {
        // There is only one assessmentItemDto with metadata
        updatedMetadata_8_10_2016_ParseTestFileService.parseTestFile();
        AssessmentItemDto assessmentItemDto = updatedMetadata_8_10_2016_ParseTestFileService.getPackageResults().getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);

        assertEquals(ItemBankStatusType.REVIEW, itemMetadataDto.getItemBankStatusType());
        assertEquals(10.532, itemMetadataDto.getCalibratedDifficulty(), 0.0);
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testCommonCoreRefListIsAsExpected() throws Exception {
        // There is only one assessmentItemDto with metadata
        metadataParseTestFileService.parseTestFile();
        AssessmentItemDto assessmentItemDto = metadataParseTestFileService.getPackageResults().getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);

        List<String> commonCoreRefs = itemMetadataDto.getCommonCoreRef();
        assertNotNull(commonCoreRefs);

        assertEquals(2, commonCoreRefs.size());
        assertEquals("6.EE.A.2a", commonCoreRefs.get(0));
        assertEquals("6.EE.A.2b", commonCoreRefs.get(1));
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testCompetenciesItemMetadataDtoIsAsExpected() throws Exception {
        // There is only one assessmentItemDto with metadata
        metadataParseTestFileService.parseTestFile();
        AssessmentItemDto assessmentItemDto = metadataParseTestFileService.getPackageResults().getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);

        CompetenciesItemMetadataDto competenciesItemMetadataDto = itemMetadataDto.getCompetencies();
        assertNotNull(competenciesItemMetadataDto);

        List<CompetencyRefItemMetadataDto> competencyRefItemMetadataDtoList = competenciesItemMetadataDto
                .getCompetencyRef();
        assertNotNull(competencyRefItemMetadataDtoList);

        assertEquals(1, competencyRefItemMetadataDtoList.size());
        assertEquals("MATH", competencyRefItemMetadataDtoList.get(0).getMapDiscipline());
        assertEquals("cmath_16-10001", competencyRefItemMetadataDtoList.get(0).getCompetencyId());
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     *
     * Corresponds to LSI CCC Assess Item Metadata Guidelines Version 1.3
     */
    @Test
    public void testCompetenciesItemMetadataDtoUpdate_1_29_2016_IsAsExpected() throws Exception {
        // There is only one assessmentItemDto with metadata
        updatedMetadata_1_29_2016_ParseTestFileService.parseTestFile();
        AssessmentItemDto assessmentItemDto = updatedMetadata_1_29_2016_ParseTestFileService.getPackageResults()
                .getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);

        CompetenciesItemMetadataDto competenciesItemMetadataDto = itemMetadataDto.getCompetencies();
        assertNotNull(competenciesItemMetadataDto);

        List<CompetencyRefItemMetadataDto> competencyRefItemMetadataDtoList = competenciesItemMetadataDto
                .getCompetencyRef();
        assertNotNull(competencyRefItemMetadataDtoList);

        assertEquals(1, competencyRefItemMetadataDtoList.size());
        assertEquals("MATH", competencyRefItemMetadataDtoList.get(0).getMapDiscipline());
        assertEquals("cmath_16-10002", competencyRefItemMetadataDtoList.get(0).getCompetencyRefId()); // Changed
                                                                                                      // to
                                                                                                      // refId;

        List<String> skippedCompetencyRefIds = competenciesItemMetadataDto.getSkippedCompetencyRefId();
        assertNotNull(skippedCompetencyRefIds);

        assertEquals(1, skippedCompetencyRefIds.size());
        assertEquals("cmath_14-10002", skippedCompetencyRefIds.get(0));
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     *
     * TODO Corresponds to LSI CCC Assess Item Metadata Guidelines Version TBD
     */
    @Test
    public void testCompetenciesItemMetadataDtoUpdate_8_10_2016_IsAsExpected() throws Exception {
        // There is only one assessmentItemDto with metadata
        AssessmentItemDto assessmentItemDto = updatedMetadata_8_10_2016_ParseTestFileService.getPackageResults()
                .getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);

        CompetenciesItemMetadataDto competenciesItemMetadataDto = itemMetadataDto.getCompetencies();
        assertNotNull(competenciesItemMetadataDto);

        List<CompetencyRefItemMetadataDto> competencyRefItemMetadataDtoList = competenciesItemMetadataDto
                .getCompetencyRef();
        assertNotNull(competencyRefItemMetadataDtoList);

        assertEquals(1, competencyRefItemMetadataDtoList.size());
        assertEquals("MATH", competencyRefItemMetadataDtoList.get(0).getCompetencyMapDiscipline()); //changed from mapDiscipline
        assertEquals("cmath_16-10002", competencyRefItemMetadataDtoList.get(0).getCompetencyRefId());

        List<String> skippedCompetencyRefIds = competenciesItemMetadataDto.getSkippedCompetencyRefId();
        assertNotNull(skippedCompetencyRefIds);

        assertEquals(1, skippedCompetencyRefIds.size());
        assertEquals("cmath_14-10002", skippedCompetencyRefIds.get(0));
    }

    /**
     * Metadata may change from its current format. Inorder to be backwards
     * compatible all of the parameters from previous versions must still exist.
     * Therefore as metadata changes add more tests, do not remove these tests.
     */
    @Test
    public void testToolsItemMetadataDtoIsAsExpected() throws Exception {
        // There is only one assessmentItemDto with metadata
        metadataParseTestFileService.parseTestFile();
        AssessmentItemDto assessmentItemDto = metadataParseTestFileService.getPackageResults().getAssessmentItemDtos().get(0);
        ItemMetadataDto itemMetadataDto = assessmentItemDto.getItemMetadata();
        assertNotNull(itemMetadataDto);

        ToolsItemMetadataDto toolsItemMetadataDto = itemMetadataDto.getTools();
        assertNotNull(toolsItemMetadataDto);

        assertEquals("No", toolsItemMetadataDto.getAllowCalculator());
        assertEquals("No", toolsItemMetadataDto.getAllowDictionary());
        assertEquals("No", toolsItemMetadataDto.getAllowThesaurus());
    }

    @Test
    public void testAssessmentAsZip() throws Exception {
        linearParseTestFileService.parseTestFile();
        PackageResults packageResults = linearParseTestFileService.getPackageResults();

        String expResult = "jca-1";
        // Fetch what is in Mongo and see what comes out.
        assertEquals("Only one assessement should be imported", packageResults.getAssessmentDtos().size(), 1);
        AssessmentDto assessmentDto =packageResults.getAssessmentDtos().get(0);
        assertEquals(expResult, assessmentDto.getIdentifier());
        assertEquals("en_US", assessmentDto.getLanguage());
        assertEquals("jca 1 test", assessmentDto.getTitle());
        assertEquals("tao", assessmentDto.getToolName());
        assertEquals("3.0.1", assessmentDto.getToolVersion());
        for (AssessmentPartDto assessmentPartDto : assessmentDto.getAssessmentParts()) {
            AssessmentPartNavigationMode assessmentPartNavigationModeDto = assessmentPartDto
                    .getAssessmentPartNavigationMode();
            // TODO need to add navigation mode import tests
            assertNotNull(assessmentPartNavigationModeDto);

            AssessmentPartSubmissionMode assessmentPartSubmissionModeDto = assessmentPartDto
                    .getAssessmentPartSubmission();
            // TODO need to add submission mode import tests
            assertNotNull(assessmentPartSubmissionModeDto);

            List<AssessmentSectionDto> assessmentSections = assessmentPartDto.getAssessmentSections();
            for (AssessmentSectionDto assessmentSectionDto : assessmentSections) {
                // TODO: assert a bunch of shiz
                assertEquals("assessmentSection-1", assessmentSectionDto.getId());
                assertEquals(new ArrayList(), assessmentSectionDto.getPreConditions());
                assertNull(assessmentSectionDto.getItemSessionControl());
                assertNull(assessmentSectionDto.getTimeLimits());
                assertEquals("Section 1", assessmentSectionDto.getTitle());

                AssessmentItemRefDto assessmentItemRefDto = assessmentSectionDto.getAssessmentItemRefs().get(0);
                assertEquals(new ArrayList(), assessmentItemRefDto.getPreConditions());
                assertEquals(new ArrayList(), assessmentItemRefDto.getBranchRules());
                assertEquals("i143338997994262272", assessmentItemRefDto.getItemIdentifier());
                assertEquals("item-1", assessmentItemRefDto.getIdentifier());
                assertEquals("[]", assessmentItemRefDto.getVariableMapping());
                assertEquals(Arrays.asList("SINGLECATEGORY"), assessmentItemRefDto.getCategories());
                assertNull(assessmentItemRefDto.getItemSessionControl());
                assertEquals(new ArrayList(), assessmentItemRefDto.getTemplateDefaultss());
                assertNull(assessmentItemRefDto.getTimeLimits());
                assertEquals(0.0, assessmentItemRefDto.getWeight(), .0001);
                assessmentItemRefDto = assessmentSectionDto.getAssessmentItemRefs().get(1);
                assertEquals(new ArrayList(), assessmentItemRefDto.getPreConditions());
                assertEquals(new ArrayList(), assessmentItemRefDto.getBranchRules());
                assertEquals("i143339018076242274", assessmentItemRefDto.getItemIdentifier());
                assertEquals("item-2", assessmentItemRefDto.getIdentifier());
                assertEquals("[]", assessmentItemRefDto.getVariableMapping());
                assertEquals(Arrays.asList("CATEGORY", "LIST", "WITH", "SPACE"), assessmentItemRefDto.getCategories());
                assertNull(assessmentItemRefDto.getItemSessionControl());
                assertEquals(new ArrayList(), assessmentItemRefDto.getTemplateDefaultss());
                assertNull(assessmentItemRefDto.getTimeLimits());
                assertEquals(0.0, assessmentItemRefDto.getWeight(), .0001);
                assessmentItemRefDto = assessmentSectionDto.getAssessmentItemRefs().get(2);
                assertEquals(new ArrayList(), assessmentItemRefDto.getPreConditions());
                assertEquals(new ArrayList(), assessmentItemRefDto.getBranchRules());
                assertEquals("i143339049565802276", assessmentItemRefDto.getItemIdentifier());
                assertEquals("item-3", assessmentItemRefDto.getIdentifier());
                assertEquals("[]", assessmentItemRefDto.getVariableMapping());
                assertEquals(new ArrayList(), assessmentItemRefDto.getCategories());
                assertNull(assessmentItemRefDto.getItemSessionControl());
                assertEquals(new ArrayList(), assessmentItemRefDto.getTemplateDefaultss());
                assertNull(assessmentItemRefDto.getTimeLimits());
                assertEquals(0.0, assessmentItemRefDto.getWeight(), .0001);

                assertEquals(new ArrayList(), assessmentSectionDto.getAssessmentSections());
                assertNull(assessmentSectionDto.getAssessmentStimulusRef());
                assertEquals(new ArrayList(), assessmentSectionDto.getBranchRules());
                assertTrue(assessmentSectionDto.getKeepTogether());
                assertNull(assessmentSectionDto.getOrdering());
                assertEquals(new ArrayList(), assessmentSectionDto.getRubricBlocks());
                assertTrue(assessmentSectionDto.getVisible());
            }

            assertNull(assessmentPartDto.getDuration());
            assertEquals("testPart-1", assessmentPartDto.getId());

            assertEquals(false, assessmentPartDto.getItemSessionControl().getAllowComment());
            assertEquals(true, assessmentPartDto.getItemSessionControl().getAllowReview());
            assertEquals(true, assessmentPartDto.getItemSessionControl().getAllowSkipping());
            assertEquals(0, assessmentPartDto.getItemSessionControl().getMaxAttempts());
            assertEquals(false, assessmentPartDto.getItemSessionControl().getShowFeedback());
            assertEquals(false, assessmentPartDto.getItemSessionControl().getShowSolution());
            assertEquals(false, assessmentPartDto.getItemSessionControl().getValidateResponses());

            assessmentPartDto.getPreConditions();
            assertEquals(new ArrayList(), assessmentPartDto.getPreConditions());
        }
        assertNull(assessmentDto.getOutcomeProcessing());
        assertNotNull(assessmentDto.getOutcomeDeclarations()); //Tested in testAssessmentOutcomeDeclaration

        List<AssessmentItemDto> assessmentItemDtoIterable = packageResults.getAssessmentItemDtos();

        assertEquals("Only four assessementItems should be imported", 4, assessmentItemDtoIterable.size());
        Iterator<AssessmentItemDto> iterator = assessmentItemDtoIterable.iterator();
        AssessmentItemDto assessmentItemDto = null;
        do {
            assessmentItemDto = iterator.next();
        } while (iterator.hasNext() && !assessmentItemDto.getIdentifier().equals("i143339060556532278"));

        // TODO: Only the first item is verified. It might be prudent to do all
        // 3 items.
        assertEquals(
                StringUtils.deleteWhitespace("<itemBody xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "          xmlns=\"http://www.imsglobal.org/xsd/imsqti_v2p1\"\n"
                        + "          xsi:schemaLocation=\"http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd\">\n"
                        + "  <div class=\"grid-row\">\n" + "      <div class=\"col-12\">\n"
                        + "        <p>The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus.</p>\n"
                        + "        <p>The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus. The following passage can be used to serve as a stimulus.The following passage can be used to serve as a stimulus. v The following passage can be used to serve as a stimulus.The following passage can be used to serve as a stimulus.The following passage can be used to serve as a stimulus.</p>\n"
                        + "      </div>\n" + "    </div>\n" + "</itemBody>"),
                StringUtils.deleteWhitespace(assessmentItemDto.getBody()));
        assertEquals("i143339060556532278", assessmentItemDto.getIdentifier());
        assertEquals(new ArrayList(), assessmentItemDto.getInteractions());
        assertNull(assessmentItemDto.getLabel());
        assertNull(assessmentItemDto.getLanguage());

        AssessmentOutcomeDeclarationDto outcomeDeclarationDto = assessmentItemDto.getOutcomeDeclarationDtos().get(0);
        assertEquals(AssessmentBaseType.FLOAT, outcomeDeclarationDto.getBaseType());
        assertEquals(AssessmentCardinality.SINGLE, outcomeDeclarationDto.getCardinality());
        assertNull(outcomeDeclarationDto.getDefaultValue());
        assertNull(outcomeDeclarationDto.getInterpretation());
        assertNull(outcomeDeclarationDto.getExternalScored());
        assertEquals("SCORE", outcomeDeclarationDto.getIdentifier());
        assertNull(outcomeDeclarationDto.getLongInterpretation());
        assertNull(outcomeDeclarationDto.getLookupTable());
        assertNull(outcomeDeclarationDto.getMasteryValue());
        assertNull(outcomeDeclarationDto.getNormalMaximum());
        assertNull(outcomeDeclarationDto.getNormalMinimum());
        assertNull(outcomeDeclarationDto.getVariableIdentifierRef());
        assertNull(outcomeDeclarationDto.getViews());

        AssessmentResponseProcessingDto responseProcessingDto = assessmentItemDto.getResponseProcessing();
        assertNull(responseProcessingDto.getExitReponse());
        assertNull(responseProcessingDto.getInclude());
        assertNull(responseProcessingDto.getLookupOutcomeValue());
        assertNull(responseProcessingDto.getResponseCondition());
        assertNull(responseProcessingDto.getResponseProcessFragment());
        assertNull(responseProcessingDto.getSetValue());
        assertNull(responseProcessingDto.getTemplate());
        assertNull(responseProcessingDto.getTemplateLocation());

        assertEquals(new ArrayList(), assessmentItemDto.getResponseVars());
        assertNull(assessmentItemDto.getStimulusRef());
        assertNull(assessmentItemDto.getTemplateProcessing());
        assertEquals(new ArrayList(), assessmentItemDto.getTemplateVars());
        assertEquals("Item title", assessmentItemDto.getTitle());
        assertNull(assessmentItemDto.getToolName());
        assertNull(assessmentItemDto.getToolVersion());
        assertNotNull(assessmentItemDto.getInteractions());
    }

    @Test
    public void testAssessmentOutcomeDeclaration() throws Exception {
        linearParseTestFileService.parseTestFile();
        AssessmentDto asessmentDto = linearParseTestFileService.getPackageResults().getAssessmentDtos().get(0);
        assertNotNull(asessmentDto);
        List<AssessmentOutcomeDeclarationDto> outcomeDeclarationDtoList = asessmentDto.getOutcomeDeclarations();
        assertEquals(2, outcomeDeclarationDtoList.size());

        AssessmentOutcomeDeclarationDto outcomeDeclarationDto1 = asessmentDto.getOutcomeDeclarations().get(0);
        assertEquals("NCORRECTA", outcomeDeclarationDto1.getIdentifier());
        assertEquals(AssessmentCardinality.SINGLE,outcomeDeclarationDto1.getCardinality());
        assertEquals(AssessmentBaseType.INTEGER, outcomeDeclarationDto1.getBaseType());
        assertEquals(100.0d, outcomeDeclarationDto1.getNormalMaximum(), .001d);
        assertEquals(-100.0d, outcomeDeclarationDto1.getNormalMinimum(), .001d);
        assertEquals("short description", outcomeDeclarationDto1.getInterpretation());
        assertEquals("data:someURI", outcomeDeclarationDto1.getLongInterpretation());
        assertEquals(99d, outcomeDeclarationDto1.getMasteryValue(), 0.01d);

        AssessmentOutcomeDeclarationDto outcomeDeclarationDto2 = asessmentDto.getOutcomeDeclarations().get(1);
        assertEquals("NCORRECTB", outcomeDeclarationDto2.getIdentifier());
        assertEquals(AssessmentCardinality.SINGLE,outcomeDeclarationDto2.getCardinality());
        assertEquals(AssessmentBaseType.FLOAT, outcomeDeclarationDto2.getBaseType());
        assertEquals(1.0d, outcomeDeclarationDto2.getNormalMaximum(), .001d);
        assertEquals(-1.0d, outcomeDeclarationDto2.getNormalMinimum(), .001d);
        assertEquals("short description 2", outcomeDeclarationDto2.getInterpretation());
        assertEquals("data:someURI/2", outcomeDeclarationDto2.getLongInterpretation());
        assertEquals(0.05d, outcomeDeclarationDto2.getMasteryValue(), 0.01d);
    }


    @Test
    public void testSingleXmlFile() throws Exception {
        choiceParseTestFileService.parseTestFile();
        PackageResults packageResults = choiceParseTestFileService.getPackageResults();
        List<AssessmentItemDto> assessmentItemDtoIterable = packageResults.getAssessmentItemDtos();
        assertEquals("Only one assessementItem should be imported", 1, assessmentItemDtoIterable.size());
        AssessmentItemDto assessmentItemDto = packageResults.getAssessmentItemDtos().get(0);
        validateAssessmentItem(assessmentItemDto, "\"NEVER LEAVE LUGGAGE UNATTENDED\"\n");
    }

    @Test
    public void testSingleXmlFileWithManifest() throws Exception {
        choiceWithManifestParseTestFileService.parseTestFile();
        PackageResults packageResults = choiceWithManifestParseTestFileService
                .getPackageResults();
        List<AssessmentItemDto> assessmentItemDtoIterable = packageResults.getAssessmentItemDtos();
        assertEquals("Only one assessementItem should be imported", 1, assessmentItemDtoIterable.size());
        AssessmentItemDto assessmentItemDto = packageResults.getAssessmentItemDtos().get(0);
        validateAssessmentItem(assessmentItemDto, " \"NEVER LEAVE LUGGAGE UNATTENDED\"\n");
    }

    @Test
    public void testSingleLSIAssessment() throws Exception {
        lsiParseTestFileService.parseTestFile();
        PackageResults packageResults = lsiParseTestFileService.getPackageResults();
        List<AssessmentItemDto> assessmentItemDtoIterable = packageResults.getAssessmentItemDtos();
        assertEquals("Only thirteen assessementItems should be imported", 13, assessmentItemDtoIterable.size());

    }

    @Test
    public void testSetOfXmlFilesInZip() throws Exception {
        mulitiItemsParseTestFileService.parseTestFile();
        PackageResults packageResults = mulitiItemsParseTestFileService
                .getPackageResults();

        List<AssessmentItemDto> assessmentItemDtoIterable = packageResults.getAssessmentItemDtos();
        assertEquals("Only one assessementItem should be imported", 3, assessmentItemDtoIterable.size());
        AssessmentItemDto assessmentItemDto = packageResults.getAssessmentItemDtos().get(0);
        validateAssessmentItem(assessmentItemDto,
                "<img src=\"media?path=TEST/item/choice/1/images/sign.png\" alt=\"NEVER LEAVE LUGGAGE UNATTENDED\"/>\n");
    }

    private void validateAssessmentItem(AssessmentItemDto assessmentItemDto, String bodyInsert) {
        // TODO: Only the first item is verified. It might be prudent to do all
        // 3 items.

        assertEquals("choice", assessmentItemDto.getIdentifier());
        assertEquals(1, assessmentItemDto.getInteractions().size());
        assertNull(assessmentItemDto.getLabel());
        assertNull(assessmentItemDto.getLanguage());

        AssessmentOutcomeDeclarationDto outcomeDeclarationDto = assessmentItemDto.getOutcomeDeclarationDtos().get(0);
        assertEquals(AssessmentBaseType.FLOAT, outcomeDeclarationDto.getBaseType());
        assertEquals(AssessmentCardinality.SINGLE, outcomeDeclarationDto.getCardinality());
        assertNotNull(outcomeDeclarationDto.getDefaultValue());
        assertNull(outcomeDeclarationDto.getInterpretation());
        assertNull(outcomeDeclarationDto.getExternalScored());
        assertEquals("SCORE", outcomeDeclarationDto.getIdentifier());
        assertNull(outcomeDeclarationDto.getLongInterpretation());
        assertNull(outcomeDeclarationDto.getLookupTable());
        assertNull(outcomeDeclarationDto.getMasteryValue());
        //Normalize
        assertEquals(1.0d, outcomeDeclarationDto.getNormalMaximum(), .0001);
        assertEquals(0.0d, outcomeDeclarationDto.getNormalMinimum(), .00001);
        assertNull(outcomeDeclarationDto.getVariableIdentifierRef());
        assertNull(outcomeDeclarationDto.getViews());

        AssessmentResponseProcessingDto responseProcessingDto = assessmentItemDto.getResponseProcessing();
        assertNull(responseProcessingDto.getExitReponse());
        assertNull(responseProcessingDto.getInclude());
        assertNull(responseProcessingDto.getLookupOutcomeValue());
        assertNull(responseProcessingDto.getResponseCondition());
        assertNull(responseProcessingDto.getResponseProcessFragment());
        assertNull(responseProcessingDto.getSetValue());
        assertNotNull(responseProcessingDto.getTemplate());
        assertNull(responseProcessingDto.getTemplateLocation());

        assertEquals(1, assessmentItemDto.getResponseVars().size());
        assertNull(assessmentItemDto.getStimulusRef());
        assertNull(assessmentItemDto.getTemplateProcessing());
        assertEquals(new ArrayList(), assessmentItemDto.getTemplateVars());
        assertEquals("Unattended Luggage", assessmentItemDto.getTitle());
        assertNull(assessmentItemDto.getToolName());
        assertNull(assessmentItemDto.getToolVersion());
        assertNotNull(assessmentItemDto.getInteractions());
        AssessmentInteractionDto interactionDto = assessmentItemDto.getInteractions().get(0);
        assertEquals(StringUtils.deleteWhitespace("<itemBody xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "          xmlns=\"http://www.imsglobal.org/xsd/imsqti_v2p1\"\n"
                + "          xsi:schemaLocation=\"http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd\">\n"
                + "  <p>Look at the text in the picture.</p>\n" + "  <p>\n" + "			" + bodyInsert + "		</p>\n"
                + "  <interaction responseIdentifier=\"RESPONSE\" uiid=\"" + interactionDto.getUiid() + "\"/>\n" +

                "</itemBody>"), StringUtils.deleteWhitespace(assessmentItemDto.getBody()));
    }

    @Test
    public void testAssessmentItemMappingResponseObjectIsAsExpected() throws Exception {
        mappingResponseTestFileService.parseTestFile();
        List<AssessmentItemDto> assessmentItemDtos = mappingResponseTestFileService.getPackageResults().getAssessmentItemDtos();
        AssessmentItemDto assessmentItemDto = assessmentItemDtos.stream()
                .filter(i -> i.getIdentifier().equals("match16")).findFirst().get();

        AssessmentResponseVarDto responseVar = assessmentItemDto.getResponseVars().stream()
                .filter(i -> i.getIdentifier().equals("RESPONSE")).findFirst().get();
        AssessmentItemResponseMappingDto mapping = responseVar.getMapping();
        assertEquals(mapping.getLowerBound(), new Double(1.0));
        assertEquals(mapping.getUpperBound(), new Double(3.0));

        Map<String, Double> map = mapping.getMapping();

        assertEquals(map.size(), 4);
        assertTrue(map.get("C R").equals(new Double(1.0)));
        assertTrue(map.get("D M").equals(new Double(0.5)));
        assertTrue(map.get("L M").equals(new Double(0.5)));
        assertTrue(map.get("P T").equals(new Double(1.0)));
    }

    @Test
    public void testSelectionIsAsExpected() throws Exception {
        selectionTestFileService.parseTestFile();

        //Verify the parent data is there
        AssessmentDto assessmentDto = selectionTestFileService.getPackageResults().getAssessmentDtos().get(0);
        assertNotNull(assessmentDto);
        assertEquals("Pooling Test", assessmentDto.getTitle());
        AssessmentPartDto assessmentPartDto = assessmentDto.getAssessmentParts().get(0);
        assertNotNull(assessmentPartDto);
        AssessmentSectionDto assessmentSectionDto = assessmentPartDto.getAssessmentSections().get(0);
        assertNotNull(assessmentSectionDto);

        //Verify import of selection
        AssessmentSelectionDto selectionDto = assessmentSectionDto.getSelection();
        assertNotNull(selectionDto);

        assertEquals(2, selectionDto.getSelect());
        assertEquals(false, selectionDto.getWithReplacement());  //default value not set in package
        assertNull(selectionDto.getExtensions()); // not supported for 1.4
    }

}
