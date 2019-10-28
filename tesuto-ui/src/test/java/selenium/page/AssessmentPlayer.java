package selenium.page;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class AssessmentPlayer extends PageObject {
    public static final String ASSESSMENT_QUESTION_TEXT = "assessmentQuestionText";
    public static final String ASSESSMENT_TITLE_TEXT = "assessmentTitleText";
    WebElement assessmentItemFirstResponse;
    WebElement assessmentPlayerCheckbox;
    WebElement assessmentPlayerCompletionButton;
    WebElement assessmentPlayerEndSessionButton;
    WebElement assessmentPlayerInlineChoice;
    WebElement assessmentPlayerInlineChoiceOption;
    WebElement assessmentPlayerNextButton;
    WebElement assessmentPlayerOkayButton;
    WebElement assessmentPlayerPauseButton;
    WebElement assessmentPlayerTextArea;
    WebElement assessmentPlayerTextBox;
    WebElement assessmentPlayerValidationNextButton;
    @Resource
    @Value(value = "${base.url}")
    String baseUrl;
    WebElement matchInteractionResponseTable;

    public AssessmentPlayer answerText() {
        assessmentPlayerTextBox = findElement(ElementBys.AssessmentPlayerFirstTextBox);
        assessmentPlayerTextBox.sendKeys("Text");
        return this;
    }

    public AssessmentPlayer answerTextarea() {
        assessmentPlayerTextArea = findElement(ElementBys.AssessmentPlayerFirstTextArea);
        assessmentPlayerTextArea.sendKeys("Text");
        return this;
    }

    public AssessmentPlayer checkFirstCheckableTableElement() {
        checkSpecificedCheckableTableElement(1, 1);
        return this;
    }

    public AssessmentPlayer checkSpecificedCheckableTableElement(int row, int column) {
        matchInteractionResponseTable = findElement(ElementBys.AssessmentPlayerMatchInteractionResponseTable);
        WebElement checkbox = getTableElementByRowColumnAndSelector(matchInteractionResponseTable, row, column, "ccc-toggle-input > button");
        checkbox.click();
        return this;
    }

    public AssessmentPlayer clickCompletionButton() {
        assessmentPlayerCompletionButton = findElement(ElementBys.AssessmentPlayerCompletionButton);
        click(assessmentPlayerCompletionButton);
        switchToPreviousWindow();
        return this;
    }

    public AssessmentPlayer clickEndSession() {
        assessmentPlayerEndSessionButton = findElement(ElementBys.AssessmentPlayerEndSessionButton);
        assessmentPlayerEndSessionButton.click();
        switchToPreviousWindow();
        return this;
    }

    public AssessmentPlayer clickFirstCheckbox() {
        assessmentPlayerCheckbox = findElement(ElementBys.AssessmentPlayerFirstCheckBox);
        click(assessmentPlayerCheckbox);
        return this;
    }

    public AssessmentPlayer clickFirstInlineChoice() {
        assessmentPlayerInlineChoice = findElement(ElementBys.AssessmentPlayerFirstInlineChoice);
        click(assessmentPlayerInlineChoice);
        assessmentPlayerInlineChoiceOption = findElement(ElementBys.AssessmentPlayerFirstInlineChoiceOption);
        click(assessmentPlayerInlineChoiceOption);
        return this;
    }

    public AssessmentPlayer clickFirstResponse() {
        assessmentItemFirstResponse = findElement(ElementBys.AssessmentPlayerItemFirstResponse);
        assessmentItemFirstResponse.click();
        return this;
    }

    public AssessmentPlayer clickMultipleChoiceAnswers(int numberOfClicks) {
        for (int click = 1; click <= numberOfClicks; click++) {
            assessmentPlayerCheckbox = findElement(ElementBys.AssessmentPlayerCheckBox(click));
            assessmentPlayerCheckbox.click();
        }
        return this;
    }

    public AssessmentPlayer clickNext() {
        assessmentPlayerNextButton = findElement(ElementBys.AssessmentPlayerNextButton);
        assessmentPlayerNextButton.click();
        return this;
    }

    public AssessmentPlayer clickNextWhileEnabled() {
        assessmentPlayerNextButton = findElement(ElementBys.AssessmentPlayerNextButton);
        while (assessmentPlayerNextButton.isEnabled()) {
            assessmentPlayerNextButton.click();
        }
        return this;
    }

    public AssessmentPlayer clickOkay() {
        assessmentPlayerOkayButton = findElement(ElementBys.AssessmentPlayerOkayButton);
        assessmentPlayerOkayButton.click();
        switchToPreviousWindow();
        return this;
    }

    public AssessmentPlayer clickPause() {
        assessmentPlayerPauseButton = findElement(ElementBys.AssessmentPlayerPauseButton);
        assessmentPlayerPauseButton.click();
        return this;
    }

    public AssessmentPlayer completeAssessmentBySkipping() {
        while (true) {
            try {
                assessmentPlayerNextButton = findElement(ElementBys.AssessmentPlayerNextButton);
                if (assessmentPlayerNextButton.isEnabled()) {
                    assessmentPlayerNextButton.click();
                } else {
                    assessmentPlayerValidationNextButton = findElement(ElementBys.AssessmentPlayerValidationNextButton);
                    assessmentPlayerValidationNextButton.click();
                }
            } catch (TimeoutException | NoSuchElementException e) {
                break;
            }
        }
        return this;
    }

    /**
     * WARNING! This method expects a specific assessment type with a specific ordering of questions in order to
     * function properly. It won't work for just any assessment. I haven't figure out yet how to accomplish that. =\
     */
    public AssessmentPlayer completeBasicAssessment() {
        WebElement question1Answer = findElement(By.cssSelector("ccc-interaction-choice > div > div > div:nth-child(1) > label"));
        click(question1Answer);
        assessmentPlayerNextButton = findElement(ElementBys.AssessmentPlayerNextButton);
        click(assessmentPlayerNextButton);
        WebElement question2Answer1 = findElement(By.cssSelector("ccc-interaction:nth-of-type(1) > span > span > ccc-interaction-text-entry > span > span > input"));
        question2Answer1.sendKeys("4");
        WebElement question2Answer2 = findElement(By.cssSelector("ccc-interaction:nth-of-type(2) > span > span > ccc-interaction-text-entry > span > span > input"));
        question2Answer2.sendKeys("elebentyone");
        assessmentPlayerNextButton = findElement(ElementBys.AssessmentPlayerNextButton);
        click(assessmentPlayerNextButton);
        WebElement question3Answer = findElement(By.cssSelector("ccc-interaction-extended-text-entry > span > span.ccc-interaction-text-entry-container > textarea"));
        question3Answer.sendKeys("Send keys");
        assessmentPlayerNextButton = findElement(ElementBys.AssessmentPlayerNextButton);
        click(assessmentPlayerNextButton);
        return this;
    }

    public AssessmentPlayer completeVersioningAssessment() {
        return this;
    }

    public AssessmentPlayer getAssessmentTitle() {
        pageElements.put(ASSESSMENT_TITLE_TEXT, findElement(ElementBys.AssessmentPlayerTitle));
        return this;
    }

    public AssessmentPlayer getQuestionText() {
        pageElements.put(ASSESSMENT_QUESTION_TEXT, findElement(ElementBys.AssessmentPlayerQuestionText));
        return this;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/assessment?dev=true").toString();
    }
}
