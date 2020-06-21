package selenium.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import selenium.page.AssessmentPreviewPlayer;
import selenium.page.BrowserSession;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/selenium/selenium-context.xml"})
public class AssessmentUploadPreviewIT implements Loggable {

    @Resource
    BrowserSession browserSession;
    String password = "password";
    String styleAttributeColor = "color";
    String styleAttributeFontFamily = "font-family";
    String styleAttributeFontSize = "font-size";
    String styleAttributeFontWeight = "font-weight";
    String styleAttributeTextAlign = "text-align";
    String styleAttributeTextTransform = "text-transform";
    String username = "admin";

    @After
    public void after() {
        log("   ... done!");
    }

    @Test
    public void assessmentPreviewCalculatorPerformsRoundingProperly() throws FileNotFoundException {
        log("Testing if the preview player calculator rounds 3.3 - 2.2 to 1.1");
        String expectedAnswer = "1.1";

        String filename = "src/test/resources/selenium/assessmentItems/calcTest.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_CALCULATOR_ANSWER);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickCalculator()
                .clickCalculatorButtons("3.3-2.2=")
                .getCalculatorAnswer()
                .executeJavascript("$('ccc-calculator .display').addClass('hinge animated');")
                .getPageElementTextAttributes(elementNames);

        String actualAnswer = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_CALCULATOR_ANSWER);
        Assert.assertEquals("The answer to 3.3 - 2.2 should be 1.1", expectedAnswer, actualAnswer);

        browserSession.asAssessmentPreviewPlayer()
                .answerItemBundleSecondQuestion("10")
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingAndRuleDoesNotBranchWhenFirstHalfOfConditionFails() throws FileNotFoundException {
        log("Testing if the preview player does not branch when first half of AND condition fails...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-and.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 2", interactionPrompt.contains("You are now in section 2"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingAndRuleDoesNotBranchWhenSecondHalfOfConditionFails() throws FileNotFoundException {
        log("Testing if the preview player does not branch when second half of AND condition fails...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-and.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(4)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 2", interactionPrompt.contains("You are now in section 2"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingAndRuleFollowsBranchCorrectly() throws FileNotFoundException {
        log("Testing if the preview player branches correctly when AND condition is true...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-and.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(5)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 3", interactionPrompt.contains("You are now in section 3"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingEqualsRuleBranchesCorrectly() throws FileNotFoundException {
        log("Testing if the preview player branches correctly when encountering an equals branch rule...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-eq.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(3)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 3", interactionPrompt.contains("You are now in section 3"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingEqualsRuleFailsToBranchWhenNotEqual() throws FileNotFoundException {
        log("Testing if the preview player does not branch when an equals rule fails...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-eq.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 2", interactionPrompt.contains("You are now in section 2"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingGreaterThanEqualsRuleBranchesCorrectly() throws FileNotFoundException {
        log("Testing if the preview player branches correctly when encountering an equals branch rule...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-gte.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(4)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 5", interactionPrompt.contains("You are now in section 5"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingGreaterThanEqualsRuleFailsToBranchWhenNotGreaterThanEquals() throws FileNotFoundException {
        log("Testing if the preview player fails to branch when score is below threshold...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-gte.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(2)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 4", interactionPrompt.contains("You are now in section 4"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingGreaterThanRuleBranchesCorrectly() throws FileNotFoundException {
        log("Testing if the preview player branches correctly when encountering an equals branch rule...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-gt.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(5)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 7", interactionPrompt.contains("You are now in section 7"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingGreaterThanRuleFailsToBranchIfScoreIsBelowThreshold() throws FileNotFoundException {
        log("Testing if the preview player fails to branch if the score is less than or equal to the branch rule trigger...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-gt.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(4)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 6", interactionPrompt.contains("You are now in section 6"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingLessThanEqualsRuleBranchesCorrectly() throws FileNotFoundException {
        log("Testing if the preview player branches correctly when encountering an equals branch rule...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-lte.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(3)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 9", interactionPrompt.contains("You are now in section 9"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingLessThanEqualsRuleFailsToBranchWhenScoreIsGreater() throws FileNotFoundException {
        log("Testing if the preview player fails to branch when score is greater than branch rule threshold...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-lte.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(4)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 8", interactionPrompt.contains("You are now in section 8"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingLessThanRuleBranchesCorrectly() throws FileNotFoundException {
        log("Testing if the preview player branches correctly when encountering an equals branch rule...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-lt.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();

        // No assert. Getting this far means we exited the test which is the goal.
    }

    @Test
    public void branchingLessThanRuleFailsToBranchWhenScoreIsAboveRuleThreshold() throws FileNotFoundException {
        log("Testing if the preview player fails to branch when score is above rule threshold...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-lt.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(3)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 10", interactionPrompt.contains("You are now in section 10"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingOrRuleBranchesCorrectlyIfFirstHalfOfOrRulePasses() throws FileNotFoundException {
        log("Testing if the preview player branches correctly when the first half of an OR condition is true...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-or.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(2)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 3", interactionPrompt.contains("You are now in section 3"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingOrRuleBranchesCorrectlyIfSecondHalfOfOrRulePasses() throws FileNotFoundException {
        log("Testing if the preview player branches correctly when the second half of an OR condition is true...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-or.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(4)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 3", interactionPrompt.contains("You are now in section 3"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Test
    public void branchingOrRuleFailsToBranchWhenOrRuleFails() throws FileNotFoundException {
        log("Testing if the preview player fails to branch when OR rule fails...");

        String filename = "src/test/resources/selenium/assessmentItems/branch-or.zip";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(3)
                .clickNext()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String interactionPrompt = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Assert.assertTrue("Interaction should be on section 2", interactionPrompt.contains("You are now in section 2"));

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickMultipleChoiceAnswers(1)
                .clickNext()
                .clickOkay();
    }

    @Override
    public void log(String message) {
        System.out.println(message);
    }

    @Before
    public void login() {
        browserSession.asLoginPage()
                .navigateTo()
                .completeLogin(username, password);
    }

    @Test
    public void uploadingAnArchiveWithMultipleItemsShouldPreviewAllItems() throws FileNotFoundException {
        log("Testing if the preview player supports multiple items in one upload");

        String filename = "src/test/resources/selenium/assessmentItems/Simple3QuestionLinear_wMedia_Styles.zip";

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        browserSession.asAssessmentPreviewPlayer()
                .clickNext()
                .waitForDuration(200) // TODO: 3/10/16 Fix me when UX implements next button disabling
                .clickNext()
                .waitForDuration(200) // TODO: 3/10/16 Fix me when UX implements next button disabling
                .clickNext()
                .clickOkay();

        // Note: No asserts here because simply navigating the pages is the test. If the appropriate
        // elements aren't available when we attempt to navigate, this test will fail.
    }

    @Test
    public void uploadingAnAssessmentWithBranchingRulesNavigatesRulesProperly() throws FileNotFoundException {
        log("Testing if the preview player can successfully navigate an assessment containing multiple branching rules...");

        String filename = "src/test/resources/selenium/assessmentItems/branch.full.zip";

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        browserSession.asAssessmentPreviewPlayer()
                .clickMultipleChoiceAnswers(3) // branch to section 3
                .clickNext()
                .clickMultipleChoiceAnswers(5) // branch to section 5
                .clickNext()
                .clickMultipleChoiceAnswers(1) // move to section 6
                .clickNext()
                .clickMultipleChoiceAnswers(1) // mote to section 7
                .clickNext()
                .clickMultipleChoiceAnswers(2) // branch to section 9
                .clickNext()
                .clickMultipleChoiceAnswers(1) // branch to exit
                .clickNext()
                .clickOkay();

        // No asserts necessary. If we failed somewhere along the way, the Okay would fail
    }

    @Test
    public void uploadingAssessmentContentPreviewsSuccessfully() throws InterruptedException, FileNotFoundException {
        log("Testing if we can successfully upload sample assessment content");
        String filename = "src/test/resources/selenium/assessmentItems/multipleChoice.xml";

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo();
        Assert.assertEquals("Title should match expected title", "Preview Assessment", browserSession.currentSessionPageTitle());

        browserSession.asAssessmentPreviewUploadPage()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        browserSession.asAssessmentPreviewPlayer();
        Assert.assertEquals("Title should match expected title", "CCC Assessment Preview", browserSession.currentSessionPageTitle());

        browserSession.asAssessmentPreviewPlayer()
                .clickFirstResponse()
                .clickNext()
                .clickOkay();

        browserSession.asAssessmentPreviewUploadPage();
        Assert.assertEquals("Title should match expected title", "Preview Assessment", browserSession.currentSessionPageTitle());
    }

    @Test
    public void uploadingExtendedTextContentPreviewsProperly() throws FileNotFoundException {
        log("Testing if the preview player displays extended text content properly");

        String filename = "src/test/resources/selenium/assessmentItems/extended_text.zip";
        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_QUESTION_TEXT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .enterResponseText("Response")
                .getQuestionText()
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals("Question text should match", "Read this postcard from your English pen-friend, Sam.", elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_QUESTION_TEXT));

        browserSession.asAssessmentPreviewPlayer()
                .clickNext()
                .clickOkay();
    }

    @Test
    public void uploadingFixedChoiceContentPreviewsProperly() throws FileNotFoundException {
        log("Testing if the preview player displays fixed choice content properly");

        String filename = "src/test/resources/selenium/assessmentItems/choice_fixed.zip";
        int responseCount = 4;
        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_QUESTION_TEXT);
        for (int count = 1; count <= responseCount; count++) {
            elementNames.add(String.format(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_RESPONSE, count));
        }

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .getQuestionText()
                .getMultipleChoices(responseCount)
                .getAssessmentItemImage()
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals("Question text should match", "Look at the text in the picture.", elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_QUESTION_TEXT));
        Assert.assertEquals("Response one should match", "You must stay with your luggage at all times.", elements.get(String.format(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_RESPONSE, 1)));
        Assert.assertEquals("Response two should match", "Do not let someone else look after your luggage.", elements.get(String.format(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_RESPONSE, 2)));
        Assert.assertEquals("Response three should match", "Remember your luggage when you leave.", elements.get(String.format(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_RESPONSE, 3)));
        Assert.assertEquals("Response four should match", "None of the above.", elements.get(String.format(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_RESPONSE, 4)));

        browserSession.asAssessmentPreviewPlayer()
                .clickFirstResponse()
                .clickNext()
                .clickOkay();
    }

    @Test
    public void uploadingInlineChoiceContentPreviewsProperly() throws FileNotFoundException {
        log("Testing if the preview player displays inline choice content properly");

        String filename = "src/test/resources/selenium/assessmentItems/inlineChoice.zip";
        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_INLINE_CHOICE_QUESTION_TEXT);

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .getInlineChoiceQuestionText()
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals("Question text should match", "Select the word that best completes the sentence.", elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_INLINE_CHOICE_QUESTION_TEXT));

        Map<Integer, String> inlineChoiceOptions = browserSession.asAssessmentPreviewPlayer()
                .clickInlineChoiceButton()
                .getInlineChoiceOptions();

        Assert.assertTrue("Inline choice options should contain choice", inlineChoiceOptions.values().contains("big"));
        Assert.assertTrue("Inline choice options should contain choice", inlineChoiceOptions.values().contains("bigger"));
        Assert.assertTrue("Inline choice options should contain choice", inlineChoiceOptions.values().contains("biggest"));

        browserSession.asAssessmentPreviewPlayer()
                .clickInlineChoiceResponse()
                .clickNext()
                .clickOkay();
    }

    @Test
    public void uploadingItemBundlePreviewsProperly() throws FileNotFoundException {
        log("Testing if the preview player properly renders item bundles");
        String filename = "src/test/resources/selenium/assessmentItems/itemBundle.zip";

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        browserSession.asAssessmentPreviewPlayer()
                .clickFirstCheckbox()
                .answerItemBundleSecondQuestion("10")
                .clickNext()
                .clickOkay();
    }

    @Test
    public void uploadingItemsWithStylesOnInteractionChoicesDisplaysStylesProperly() throws FileNotFoundException {
        log("Testing if the preview player can properly style an interaction choice...");

        String filename = "src/test/resources/selenium/assessmentItems/style-test.zip";

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        browserSession.asAssessmentPreviewPlayer()
                .clickNext()
                .waitForDuration(200) // TODO: 3/10/16 Fix me when UX implements next button disabling
                .clickNext();

        Set<String> styleAttributeNames = new HashSet<>();
        styleAttributeNames.add(styleAttributeColor);
        styleAttributeNames.add(styleAttributeFontWeight);
        styleAttributeNames.add(styleAttributeTextAlign);
        styleAttributeNames.add(styleAttributeFontFamily);
        styleAttributeNames.add(styleAttributeFontSize);
        styleAttributeNames.add(styleAttributeTextTransform);

        Map<String, String> styleAttributes = browserSession.asAssessmentPreviewPlayer()
                .getInteractionChoiceTextColor(styleAttributeNames);

        Assert.assertEquals("Font color should match", "rgba(153, 0, 51, 1)", styleAttributes.get(styleAttributeColor));
        Assert.assertEquals("Font weight should match", "700", styleAttributes.get(styleAttributeFontWeight));
        Assert.assertEquals("Text align should match", "right", styleAttributes.get(styleAttributeTextAlign));
        Assert.assertEquals("Font family should match", "arial", styleAttributes.get(styleAttributeFontFamily));
        Assert.assertEquals("Font size should match", "32px", styleAttributes.get(styleAttributeFontSize));
        Assert.assertEquals("Text transform should match", "uppercase", styleAttributes.get(styleAttributeTextTransform));

        browserSession.asAssessmentPreviewPlayer()
                .clickNext()
                .clickOkay();
    }

    @Test
    public void uploadingItemsWithStylesOnParentDivDoesNotAffectStylesDisplayed() throws FileNotFoundException {
        log("Testing if the preview player protects styles from inherited values...");

        String filename = "src/test/resources/selenium/assessmentItems/style-test.zip";

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Set<String> styleAttributeNames = new HashSet<>();
        styleAttributeNames.add(styleAttributeColor);
        styleAttributeNames.add(styleAttributeFontWeight);
        styleAttributeNames.add(styleAttributeTextAlign);
        styleAttributeNames.add(styleAttributeFontFamily);
        styleAttributeNames.add(styleAttributeFontSize);
        styleAttributeNames.add(styleAttributeTextTransform);

        Map<String, String> styleAttributes = browserSession.asAssessmentPreviewPlayer()
                .waitForDuration(200) // TODO: 3/10/16 Fix me when UX implements next button disabling
                .getInteractionPromptTextColor(styleAttributeNames);

        Assert.assertEquals("Font color should match", "rgba(68, 68, 68, 1)", styleAttributes.get(styleAttributeColor));
        Assert.assertEquals("Font weight should match", "300", styleAttributes.get(styleAttributeFontWeight));
        Assert.assertEquals("Text align should match", "left", styleAttributes.get(styleAttributeTextAlign));
        Assert.assertEquals("Font family should match", "\"Roboto Slab\",serif", styleAttributes.get(styleAttributeFontFamily));
        Assert.assertEquals("Font size should match", "16.8px", styleAttributes.get(styleAttributeFontSize));
        Assert.assertEquals("Text transform should match", "none", styleAttributes.get(styleAttributeTextTransform));

        browserSession.asAssessmentPreviewPlayer()
                .clickNext()
                .waitForDuration(200) // TODO: 3/10/16 Fix me when UX implements next button disabling
                .clickNext()
                .waitForDuration(200) // TODO: 3/10/16 Fix me when UX implements next button disabling
                .clickNext()
                .clickOkay();
    }

    @Test
    public void uploadingItemsWithStylesOnQuestionPromptDisplaysStylesProperly() throws FileNotFoundException {
        log("Testing if the preview player can properly style the question prompt...");

        String filename = "src/test/resources/selenium/assessmentItems/style-test.zip";

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        browserSession.asAssessmentPreviewPlayer()
                .clickNext();

        Set<String> styleAttributeNames = new HashSet<>();
        styleAttributeNames.add(styleAttributeColor);
        styleAttributeNames.add(styleAttributeFontWeight);
        styleAttributeNames.add(styleAttributeTextAlign);
        styleAttributeNames.add(styleAttributeFontFamily);
        styleAttributeNames.add(styleAttributeFontSize);
        styleAttributeNames.add(styleAttributeTextTransform);


        Map<String, String> styleAttributes = browserSession.asAssessmentPreviewPlayer()
                .waitForDuration(200) // TODO: 3/10/16 Fix me when UX implements next button disabling
                .getInteractionPromptTextColor(styleAttributeNames);

        Assert.assertEquals("Font color should match", "rgba(153, 0, 51, 1)", styleAttributes.get(styleAttributeColor));
        Assert.assertEquals("Font weight should match", "700", styleAttributes.get(styleAttributeFontWeight));
        Assert.assertEquals("Text align should match", "right", styleAttributes.get(styleAttributeTextAlign));
        Assert.assertEquals("Font family should match", "arial", styleAttributes.get(styleAttributeFontFamily));
        Assert.assertEquals("Font size should match", "33.6px", styleAttributes.get(styleAttributeFontSize));
        Assert.assertEquals("Text transform should match", "uppercase", styleAttributes.get(styleAttributeTextTransform));

        browserSession.asAssessmentPreviewPlayer()
                .clickNext()
                .waitForDuration(200) // TODO: 3/10/16 Fix me when UX implements next button disabling
                .clickNext()
                .clickOkay();
    }

    @Test
    public void uploadingMatchInteractionPreviewsProperly() throws FileNotFoundException {
        log("Testing if the preview player properly renders match interactions");

        String filename = "src/test/resources/selenium/assessmentItems/match-interaction.simple.zip";

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .getInteractionPromptText()
                .getPageElementTextAttributes(elementNames);

        String expectedInteractionPromptText = "Match the following characters to the Shakespeare play they appeared in:";
        String actualInteractionPromptText = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT);
        Assert.assertEquals("Assessment interaction prompt should match", expectedInteractionPromptText, actualInteractionPromptText);

        Map<Integer, String> tableColumns = browserSession.asAssessmentPreviewPlayer().getTableColumns();

        Assert.assertTrue(tableColumns.values().contains("Romeo and Juliet"));
        Assert.assertTrue(tableColumns.values().contains("A Midsummer-Night's Dream"));
        Assert.assertTrue(tableColumns.values().contains("The Tempest"));

        Map<Integer, String> tableRows = browserSession.asAssessmentPreviewPlayer().getTableRows();

        Assert.assertTrue(tableRows.values().contains("Demetrius"));
        Assert.assertTrue(tableRows.values().contains("Capulet"));
        Assert.assertTrue(tableRows.values().contains("Lysander"));
        Assert.assertTrue(tableRows.values().contains("Prospero"));

        browserSession.asAssessmentPreviewPlayer()
                .checkFirstCheckableTableElement()
                .clickNext()
                .clickOkay();
    }

    @Test
    public void uploadingScoredItemPreviewsScoringCorrectly() throws InterruptedException, FileNotFoundException {
        log("Testing if we can successfully view scoring info for upload item");

        String filename = "src/test/resources/selenium/assessmentItems/match-interaction.simple.zip";
        String expectedMinScore = "1";
        String expectedMaxScore = "3";
        String expectedRawscore = "4";
        String expectedFinalScore = "3";

        browserSession.asAssessmentPreviewUploadPage()
                .navigateTo();

        browserSession.asAssessmentPreviewUploadPage()
                .setUploadedFile(filename)
                .clickUpload()
                .clickSessionLink();

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_MIN);
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_MAX);
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_RAW);
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_FINAL);

        Map<String, String> elements = browserSession.asAssessmentPreviewPlayer()
                .clickShowScoresButton()
                .getFirstAssessmentItemScore()
                .getPageElementTextAttributes(elementNames);

        String actualMinScore = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_MIN);
        String actualMaxScore = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_MAX);
        String actualRawScore = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_RAW);
        String actualFinalScore = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_FINAL);

        Assert.assertEquals("Min score should be " + expectedMinScore, expectedMinScore, actualMinScore);
        Assert.assertEquals("Max score should be " + expectedMaxScore, expectedMaxScore, actualMaxScore);
        Assert.assertEquals("Raw score should be zero", "0", actualRawScore);
        Assert.assertEquals("Final score should be " + expectedMinScore, expectedMinScore, actualFinalScore);

        elementNames = new HashSet<>();
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_RAW);
        elementNames.add(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_FINAL);

        elements = browserSession.asAssessmentPreviewPlayer()
                .checkSpecificedCheckableTableElement(1, 2)
                .checkSpecificedCheckableTableElement(2, 1)
                .checkSpecificedCheckableTableElement(3, 1)
                .checkSpecificedCheckableTableElement(4, 3)
                .getFirstAssessmentItemScore()
                .getPageElementTextAttributes(elementNames);

        actualRawScore = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_RAW);
        actualFinalScore = elements.get(AssessmentPreviewPlayer.ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_FINAL);

        Assert.assertEquals("Raw score should be " + expectedRawscore, expectedRawscore, actualRawScore);
        Assert.assertEquals("Final score should be " + expectedFinalScore, expectedFinalScore, actualFinalScore);

        browserSession.asAssessmentPreviewPlayer()
                .clickNext()
                .clickOkay();
    }
}
