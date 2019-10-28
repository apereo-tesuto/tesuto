package selenium.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class AssessmentPreviewPlayer extends PageObject {
    public static final String ASSESSMENT_PREVIEW_CALCULATOR_ANSWER = "assessmentPreviewCalculatorAnswer";
    public static final String ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_FINAL = "assessmentPreviewFirstItemScoreFinal";
    public static final String ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_MAX = "assessmentPreviewFirstItemScoreMax";
    public static final String ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_MIN = "assessmentPreviewFirstItemScoreMin";
    public static final String ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_RAW = "assessmentPreviewFirstItemScoreRaw";
    public static final String ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_TEXT = "assessmentPreviewFirstItemScoreText";
    public static final String ASSESSMENT_PREVIEW_INLINE_CHOICE_QUESTION_TEXT = "assessmentPreviewInlineChoiceQuestionText";
    public static final String ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT = "assessmentPreviewMatchInteractionPrompt";
    public static final String ASSESSMENT_PREVIEW_QUESTION_TEXT = "assessmentPreviewQuestionText";
    public static final String ASSESSMENT_PREVIEW_RESPONSE = "assessmentPreviewResponse%s";
    public static final String ASSESSMENT_TITLE_TEXT = "assessmentTitleText";
    WebElement assessmentItemFirstResponse;
    WebElement assessmentItemImage;
    WebElement assessmentItemResponseTextBox;
    WebElement assessmentPreviewInteractionChoice;
    WebElement assessmentPreviewInteractionPrompt;
    WebElement assessmentPreviewPlayerCheckBox;
    WebElement assessmentPreviewPlayerNextButton;
    WebElement assessmentPreviewPlayerNextTaskButton;
    WebElement assessmentPreviewPlayerOkayButton;
    WebElement assessmentPreviewTextEntryTextBox;
    @Resource
    @Value(value = "#{selenium['base.url']}")
    String baseUrl;
    WebElement calculatorButton;
    WebElement choiceInteractionFirstCheckbox;
    WebElement inlineChoiceButton;
    WebElement inlineChoiceOptionsList;
    WebElement inlineChoiceResponse;
    WebElement matchInteractionResponseTable;
    WebElement matchInteractionResponseTableHeader;
    WebElement showScoresButton;

    public AssessmentPreviewPlayer answerItemBundleSecondQuestion(String answer) {
        assessmentItemResponseTextBox = findElement(ElementBys.AssessmentPreviewItemBundleSecondQuestionTextBox);
        assessmentItemResponseTextBox.sendKeys(answer);
        return this;
    }

    public AssessmentPreviewPlayer checkFirstCheckableTableElement() {
        checkSpecificedCheckableTableElement(1, 1);
        return this;
    }

    public AssessmentPreviewPlayer checkSpecificedCheckableTableElement(int row, int column) {
        matchInteractionResponseTable = findElement(ElementBys.AssessmentPreviewMatchInteractionResponseTable);
        WebElement checkbox = getTableElementByRowColumnAndSelector(matchInteractionResponseTable, row, column, "ccc-toggle-input > button");
        checkbox.click();
        return this;
    }

    public AssessmentPreviewPlayer clickCalculator() {
        calculatorButton = findElement(ElementBys.AssessmentPreviewCalculatorButton);
        calculatorButton.click();
        return this;
    }

    public AssessmentPreviewPlayer clickCalculatorButtons(String buttons) {
        for (char ch : buttons.toCharArray()) {
            calculatorButton = findElement(By.id("calculator-button-" + ch));
            calculatorButton.click();
        }
        return this;
    }

    public AssessmentPreviewPlayer clickFirstCheckbox() {
        choiceInteractionFirstCheckbox = findElement(ElementBys.AssessmentPreviewChoiceInteractionFirstCheckbox);
        choiceInteractionFirstCheckbox.click();
        return this;
    }

    public AssessmentPreviewPlayer clickFirstResponse() {
        assessmentItemFirstResponse = findElement(ElementBys.AssessmentPreviewItemFirstResponse);
        assessmentItemFirstResponse.click();
        return this;
    }

    public AssessmentPreviewPlayer clickGoToTheNextTask() {
        assessmentPreviewPlayerNextTaskButton = findElement(ElementBys.AssessmentPreviewPlayerNextTaskButton);
        assessmentPreviewPlayerNextTaskButton.click();
        return this;
    }

    public AssessmentPreviewPlayer clickInlineChoiceButton() {
        inlineChoiceButton = findElement(By.id("inline-choice-"));
        click(inlineChoiceButton);
        return this;
    }

    public AssessmentPreviewPlayer clickInlineChoiceResponse() {
        inlineChoiceResponse = findElement(ElementBys.AssessmentPreviewInlineChoiceFirstResponse);
        click(inlineChoiceResponse);
        return this;
    }

    public AssessmentPreviewPlayer clickMultipleChoiceAnswers(int numberOfClicks) {
        for (int click = 1; click <= numberOfClicks; click++) {
            waitForDuration(200);
            assessmentPreviewPlayerCheckBox = findElement(ElementBys.AssessmentPreviewCheckBox(click));
            waitForDuration(200);
            assessmentPreviewPlayerCheckBox.click();
        }
        return this;
    }

    public AssessmentPreviewPlayer clickNext() {
        assessmentPreviewPlayerNextButton = findElement(ElementBys.AssessmentPlayerNextButton);
        assessmentPreviewPlayerNextButton.click();
        return this;
    }

    public AssessmentPreviewPlayer clickOkay() {
        assessmentPreviewPlayerOkayButton = findElement(ElementBys.AssessmentPreviewPlayerOkayButton);
        assessmentPreviewPlayerOkayButton.click();
        switchToPreviousWindow();
        return this;
    }

    public AssessmentPreviewPlayer clickShowScoresButton() {
        showScoresButton = findElement(ElementBys.AssessmentPreviewShowScoresButton);
        showScoresButton.click();
        return this;
    }

    public AssessmentPreviewPlayer enterResponseText(String text) {
        assessmentItemResponseTextBox = findElement(ElementBys.AssessmentPreviewItemResponseTextBox);
        assessmentItemResponseTextBox.sendKeys(text);
        return this;
    }

    public AssessmentPreviewPlayer enterTextAnswer(String textEntry) {
        assessmentPreviewTextEntryTextBox = findElement(ElementBys.AssessmentPreviewTextEntryTextBox);
        setText(assessmentPreviewTextEntryTextBox, textEntry);
        return this;
    }

    @Override
    public AssessmentPreviewPlayer executeJavascript(String javascript) {
        return (AssessmentPreviewPlayer) super.executeJavascript(javascript);
    }

    public AssessmentPreviewPlayer getAssessmentItemImage() {
        assessmentItemImage = findElement(ElementBys.AssessmentPreviewItemImage);
        return this;
    }

    public AssessmentPreviewPlayer getAssessmentTitle() {
        pageElements.put(ASSESSMENT_TITLE_TEXT, findElement(ElementBys.AssessmentPlayerTitle));
        return this;
    }

    public AssessmentPreviewPlayer getCalculatorAnswer() {
        pageElements.put(ASSESSMENT_PREVIEW_CALCULATOR_ANSWER, findElement(ElementBys.AssessmentPreviewCalculatorAnswer));
        return this;
    }

    public AssessmentPreviewPlayer getFirstAssessmentItemScore() {
        waitForVisibilityOfElement(ElementBys.AssessmentPreviewFirstAssessmentItemScore);
        pageElements.put(ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_MIN, findElement(ElementBys.AssessmentPreviewFirstAssessmentItemScoreMin));
        pageElements.put(ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_MAX, findElement(ElementBys.AssessmentPreviewFirstAssessmentItemScoreMax));
        pageElements.put(ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_RAW, findElement(ElementBys.AssessmentPreviewFirstAssessmentItemScoreRaw));
        pageElements.put(ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_FINAL, findElement(ElementBys.AssessmentPreviewFirstAssessmentItemScoreFinal));
        pageElements.put(ASSESSMENT_PREVIEW_FIRST_ITEM_SCORE_TEXT, findElement(ElementBys.AssessmentPreviewFirstAssessmentItemScore));
        return this;
    }

    public Map<Integer, String> getInlineChoiceOptions() {
        inlineChoiceOptionsList = findElement(ElementBys.AssessmentPreviewInlineChoiceList);
        return getCollectionTextAttributesByCssSelector(inlineChoiceOptionsList, "span");
    }

    public AssessmentPreviewPlayer getInlineChoiceQuestionText() {
        pageElements.put(ASSESSMENT_PREVIEW_INLINE_CHOICE_QUESTION_TEXT, findElement(ElementBys.AssessmentPreviewInlineChoiceQuestionText));
        return this;
    }

    public Map<String, String> getInteractionChoiceTextColor(Set<String> styleAttributesNames) {
        assessmentPreviewInteractionChoice = findElement(ElementBys.AssessmentPreviewPlayerInteractionChoiceSpan);
        return getElementCssValues(assessmentPreviewInteractionChoice, styleAttributesNames);
    }

    public AssessmentPreviewPlayer getInteractionPromptText() {
        pageElements.put(ASSESSMENT_PREVIEW_MATCH_INTERACTION_PROMPT, findElement(ElementBys.AssessmentPreviewPlayerInteractionPrompt));
        return this;
    }

    public Map<String, String> getInteractionPromptTextColor(Set<String> styleAttributeNames) {
        assessmentPreviewInteractionPrompt = findElement(ElementBys.AssessmentPreviewPlayerInteractionPromptSpan);
        return getElementCssValues(assessmentPreviewInteractionPrompt, styleAttributeNames);
    }

    public AssessmentPreviewPlayer getMultipleChoices(int numberOfChoices) {
        for (int choice = 1; choice <= numberOfChoices; choice++) {
            pageElements.put(String.format(ASSESSMENT_PREVIEW_RESPONSE, choice), findElement(ElementBys.AssessmentPreviewContentQuestionChoice(choice)));
        }
        return this;
    }

    public AssessmentPreviewPlayer getQuestionText() {
        pageElements.put(ASSESSMENT_PREVIEW_QUESTION_TEXT, findElement(ElementBys.AssessmentPreviewContentQuestionText));
        return this;
    }

    public Map<Integer, String> getTableColumns() {
        matchInteractionResponseTableHeader = findElement(ElementBys.AssessmentPreviewMatchInteractionResponseTableHeader);
        return getCollectionTextAttributesByCssSelector(matchInteractionResponseTableHeader, "th > span");
    }

    public Map<Integer, String> getTableRows() {
        matchInteractionResponseTable = findElement(ElementBys.AssessmentPreviewMatchInteractionResponseTable);
        return getCollectionTextAttributesByCssSelector(matchInteractionResponseTable, "span");
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/assessmentpreview?dev=true").toString();
    }

    @Override
    public AssessmentPreviewPlayer waitForDuration(int milliseconds) {
        super.waitForDuration(milliseconds);
        return this;
    }
}
