package selenium.config;

import org.openqa.selenium.By;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
public class ElementBys {
    // Assessment player elements
    public static By AssessmentPlayerCompletionButton = By.cssSelector("ccc-assessment-complete > div > div > div > p > button");
    public static By AssessmentPlayerEndSessionButton = By.cssSelector("ccc-asmt-task-pause-modal > div.modal-footer > button.btn.btn-primary");
    public static By AssessmentPlayerFirstCheckBox = By.cssSelector("ccc-interaction-choice > div > div > div:nth-child(1)");
    public static By AssessmentPlayerFirstInlineChoice = By.cssSelector("ccc-interaction-inline-choice > span > span > div > button");
    public static By AssessmentPlayerFirstInlineChoiceOption = By.cssSelector("ccc-interaction-inline-choice > span > span > div > ul > li:nth-child(1)");
    public static By AssessmentPlayerFirstTextArea = By.cssSelector("span.ccc-interaction-text-entry-container > textarea");
    public static By AssessmentPlayerFirstTextBox = By.cssSelector("span.ccc-interaction-text-entry-container > input");
    public static By AssessmentPlayerItemFirstResponse = By.cssSelector("div.ccc-interaction-choice-list-item:nth-of-type(1)");
    public static By AssessmentPlayerMatchInteractionResponseTable = By.cssSelector("div.ccc-interaction-match-container > table");
    public static By AssessmentPlayerNextButton = By.cssSelector("button.ccc-asmt-next-button");
    public static By AssessmentPlayerOkayButton = By.cssSelector("button.btn-submit-button");
    public static By AssessmentPlayerPauseButton = By.cssSelector("button.ccc-asmt-pause-button");
    public static By AssessmentPlayerQuestionText = By.cssSelector("label.ccc-interaction-prompt");
    public static By AssessmentPlayerTitle = By.cssSelector("span.ccc-asmt-player-asmt-title");
    public static By AssessmentPlayerValidationNextButton = By.cssSelector("ccc-asmt-task-warning-modal > div.modal-footer > button.btn.btn-default");
    // Assessment preview player elements
    public static By AssessmentPreviewCalculatorAnswer = By.cssSelector("div.display-text");
    public static By AssessmentPreviewCalculatorButton = By.id("ccc-calculator");
    public static By AssessmentPreviewChoiceInteractionFirstCheckbox = By.cssSelector("ccc-choice-input:nth-of-type(1) > span.checkbox");
    public static By AssessmentPreviewContentQuestionText = By.cssSelector("itembody > p:nth-child(1)");
    public static By AssessmentPreviewFirstAssessmentItemScore = By.cssSelector("ccc-assessment-item-session-feedback-score > div > div >span.ccc-assessment-item-session-feedback-score-value");
    public static By AssessmentPreviewFirstAssessmentItemScoreFinal = By.cssSelector("span.ccc-assessment-item-session-feedback-final-value");
    public static By AssessmentPreviewFirstAssessmentItemScoreMax = By.cssSelector("span.ccc-assessment-item-session-feedback-max-value");
    public static By AssessmentPreviewFirstAssessmentItemScoreMin = By.cssSelector("span.ccc-assessment-item-session-feedback-min-value");
    public static By AssessmentPreviewFirstAssessmentItemScoreRaw = By.cssSelector("span.ccc-assessment-item-session-feedback-raw-value");
    public static By AssessmentPreviewInlineChoiceFirstResponse = By.cssSelector("ccc-interaction-inline-choice > span > span > div > ul > li:nth-child(3)");
    public static By AssessmentPreviewInlineChoiceList = By.cssSelector("ccc-interaction-inline-choice > span > span > div > ul");
    public static By AssessmentPreviewInlineChoiceQuestionText = By.cssSelector("itembody > div > p");
    public static By AssessmentPreviewItemBundleSecondQuestionTextBox = By.cssSelector("span.ccc-interaction-text-entry-container  > input");
    public static By AssessmentPreviewItemFirstResponse = By.cssSelector("div.ccc-interaction-choice-list-item:nth-of-type(1)");
    public static By AssessmentPreviewItemImage = By.cssSelector("ccc-assessment-item > div > div > div > itembody > p:nth-child(2) > img");
    public static By AssessmentPreviewItemResponseTextBox = By.cssSelector("span.ccc-interaction-text-entry-container > textarea");
    public static By AssessmentPreviewMatchInteractionResponseTable = By.cssSelector("div.ccc-interaction-match-container > table");
    public static By AssessmentPreviewMatchInteractionResponseTableHeader = By.cssSelector("div.ccc-interaction-match-container > table > thead");
    public static By AssessmentPreviewPlayerInteractionChoiceSpan = By.cssSelector("ccc-interaction-choice > div > div > div:nth-child(1) > span > span");
    public static By AssessmentPreviewPlayerInteractionPrompt = By.cssSelector("label.ccc-interaction-prompt");
    public static By AssessmentPreviewPlayerInteractionPromptSpan = By.cssSelector("label.ccc-interaction-prompt > span");
    public static By AssessmentPreviewPlayerNextTaskButton = By.cssSelector("ccc-asmt-task-warning-modal > div.modal-footer > button.btn.btn-default");
    public static By AssessmentPreviewPlayerOkayButton = By.cssSelector("button.btn-submit-button:nth-of-type(2)");
    public static By AssessmentPreviewShowScoresButton = By.cssSelector("button.ccc-asmt-score-button");
    public static By AssessmentPreviewTextEntryTextBox = By.cssSelector("ccc-interaction-text-entry > span > span > input");
    // Assessment upload elements
    public static By AssessmentUploadButton = By.id("submitFile");
    public static By AssessmentUploadFile = By.id("file");
    public static By AssessmentUploadSessionLink = By.cssSelector("#records_table > tbody > tr > td:nth-child(2) > a");
    // IDP Login elements
    public static By IDPLoginPassword = By.id("inputJPassword");
    public static By IDPLoginSubmitButton = By.cssSelector("button");
    public static By IDPLoginUsername = By.id("inputJUsername");
    // Landing page elements
    public static By LandingFirstNameLastNameButton = By.id("dropdownMenu1");
    public static By LandingGenerateNewPasscodeButton = By.cssSelector("button.btn-generate-new-passcode");
    public static By LandingGenerateNewPrivatePasscodeButton = By.id("private");
    public static By LandingGenerateNewPublicPasscodeButton = By.id("public");
    public static By LandingLogout = By.id("ccc-header-nav-sign-out");
    public static By LandingPasscodesLink = By.id("ccc-header-nav-passcodes");
    public static By LandingProctorDashboardButton = By.cssSelector("button.ccc-action-button-proctorDashboard");
    public static By LandingStudentLookup = By.id("ccc-header-student-lookup");
    // Login elements
    public static By LoginForgotPassword = By.id("forgt_password");
    public static By LoginPassword = By.id("j_password");
    public static By LoginRememberMe = By.id("_spring_security_remember_me");
    public static By LoginSubmit = By.id("login_button");
    public static By LoginUsername = By.id("j_username");
    // Proctor elements
    public static By ProctorActivationAccommodations = By.id("accommodations");
    public static By ProctorActivationAddAccommodationsButton = By.cssSelector("ccc-activate-student > div > div.activation-options > div:nth-child(3) > div > button");
    public static By ProctorActivationAssessmentErrorText = By.cssSelector("div.ccc-notification > span:nth-child(3)");
    public static By ProctorActivationAssessmentTypeOnline = By.cssSelector("#label-assessment-type + div > label:nth-of-type(1)");
    public static By ProctorActivationCancel = By.id("ccc-create-student-activation-button-cancel");
    public static By ProctorActivationCreate = By.cssSelector("div.activation-options > div.actions > div > button");
    public static By ProctorActivationFirstAccommodation = By.cssSelector("ccc-accommodations-list > ccc-radio-box:nth-of-type(2) > label > input");
    public static By ProctorActivationFirstAssessment = By.cssSelector("ccc-radio-box:nth-of-type(1) > label > input");
    public static By ProctorActivationFirstAssessmentTitle = By.cssSelector("ccc-radio-box:nth-of-type(1) > label");
    public static By ProctorActivationLocation = By.id("location");
    public static By ProctorActivationLocationErrorText = By.id("locationErrors");
    public static By ProctorActivationOtherAccommodation = By.cssSelector("ccc-accommodations-list > ccc-radio-box:nth-of-type(6) > label > input");
    public static By ProctorActivationOtherAccommodationTextBox = By.cssSelector("ccc-accommodations-list > div > div > textarea");
    public static By ProctorActivationPaperTab = By.id("paper-assessments");
    public static By ProctorActivationPrintLink = By.cssSelector("div.new-activity.alert.alert-success > ul > li > span > a");
    public static By ProctorActivationStudentProfileButton = By.cssSelector("button.ccc-view-manager-view-back");
    public static By ProctorActivationSummaryAccommodations = By.cssSelector("span.ccc-create-student-activation-summary-accommodations");
    public static By ProctorActivationSummaryAccommodationsContainer = By.cssSelector("ul.accommodations-list");
    public static By ProctorActivationSummaryActivatedState = By.cssSelector("div.details > strong.activated");
    public static By ProctorActivationSummaryAssessmentTitlesContainer = By.cssSelector("ul.assessments");
    public static By ProctorActivationSummaryAssessmentType = By.cssSelector("span.ccc-create-student-activation-summary-type");
    public static By ProctorActivationSummaryFirstAssessmentFailureTitleText = By.cssSelector("div.alert-warning:nth-of-type(1) > div > strong");
    public static By ProctorActivationSummaryFirstAssessmentMessageText = By.cssSelector("div.alert-details:nth-of-type(1)");
    public static By ProctorActivationSummaryFirstAssessmentSuccessTitleText = By.cssSelector("div.alert-success:nth-of-type(1) > div > strong");
    public static By ProctorActivationSummaryLocation = By.cssSelector("div.details > span.location");
    public static By ProctorActivationSummaryStudentName = By.cssSelector("ccc-activate-student-summary > ul > li > div > div.student");
    public static By ProctorActivationSystemError = By.cssSelector("div.ccc-notification > span:nth-of-type(2)");
    public static By ProctorDashboardFacetFirstAssessment = By.cssSelector("div.ccc-facet-list-facets > div:nth-child(1) > div.ccc-facet-list-facet-values > div.ccc-facet-list-facet-value:nth-of-type(1) > label");
    public static By ProctorDashboardFacetFirstAssessmentCheckBox = By.cssSelector("div.ccc-facet-list-facets > div:nth-child(1) > div.ccc-facet-list-facet-values > div.ccc-facet-list-facet-value:nth-of-type(1) > input");
    public static By ProctorDashboardFacetFirstAssessmentResultCount = By.cssSelector("div.ccc-facet-list-facets > div:nth-child(1) > div.ccc-facet-list-facet-values > div.ccc-facet-list-facet-value:nth-of-type(1) > span");
    public static By ProctorDashboardFacetListContainer = By.tagName("ccc-facet-list");
    public static By ProctorDashboardLastProctoredLocation = By.cssSelector("span.ccc-location-card-label");
    public static By ProctorDashboardSearch = By.id("ccc-activation-faceted-search");
    public static By ProctorDashboardSearchResultContainer = By.cssSelector("ccc-grouped-items");
    public static By ProctorDashboardSearchResultFirstPrintLink = By.cssSelector("ccc-activation-faceted-search > div > div > ccc-grouped-items > div > ul > li:nth-child(1) > ccc-grouped-item > ccc-activation > div > div.ccc-activation-details > button");
    public static By ProctorDashboardSearchResultGroup1Student1Id = By.cssSelector("ccc-grouped-items > div:nth-child(1) > ul > li:nth-child(1) > ccc-grouped-item > ccc-activation > div > div.ccc-activation-details > div.ccc-activation-details-cccid");
    public static By ProctorDashboardSearchResultGroup1Student1Name = By.cssSelector("ccc-grouped-items > div:nth-child(1) > ul > li:nth-child(1) > ccc-grouped-item > ccc-activation > div > div.ccc-activation-details > div.ccc-activation-details-student");
    public static By ProctorDashboardSearchResultGroup2Student1Id = By.cssSelector("ccc-grouped-items > div:nth-child(2) > ul > li:nth-child(1) > ccc-grouped-item > ccc-activation > div > div.ccc-activation-details > div.ccc-activation-details-cccid");
    public static By ProctorDashboardSearchResultGroup2Student1Name = By.cssSelector("ccc-grouped-items > div:nth-child(2) > ul > li:nth-child(1) > ccc-grouped-item > ccc-activation > div > div.ccc-activation-details > div.ccc-activation-details-student");
    public static By ProctorDashboardSearchResultHeader0 = By.id("ccc-grouped-items-group-header-0");
    public static By ProctorDashboardSearchResultHeader1 = By.id("ccc-grouped-items-group-header-1");
    public static By ProctorDashboardTestingSiteLocationCardsContainer = By.tagName("ccc-view-manager-view");
    public static By ProctorDashboardTestingSiteLocationFirstCard = By.cssSelector("ccc-view-manager-view > div > ccc-proctor-dashboard > div > div.col-md-9 > div > div:nth-child(2) > ccc-location-card");
    public static By ProctorPrivatePasscode = By.cssSelector("div.ccc-proctor-passcodes.private > h3 > strong");
    public static By ProctorPrivatePasscodeTab = By.id("private-passcode");
    public static By ProctorPublicPasscode = By.cssSelector("div.ccc-proctor-passcodes.public > h3 > strong");
    public static By ProctorPublicPasscodeTab = By.id("public-passcode");
    public static By ProctorStudentProfileActivationListUL = By.cssSelector("ul.ccc-student-profile-activation-list");
    public static By ProctorStudentProfileNewActivationButton = By.cssSelector("ccc-student-profile > div > button");
    public static By ProctorStudentSearchAdvancedTab = By.cssSelector("ccc-student-lookup > form > div > div.row > div > ul > li:nth-child(2) > a");
    public static By ProctorStudentSearchAge = By.id("age");
    public static By ProctorStudentSearchByCCCIdErrorText = By.id("cccIdErrors");
    public static By ProctorStudentSearchByCCCIdTab = By.cssSelector("ccc-student-lookup > form > div > div.row > div > ul > li:nth-child(1) > a");
    public static By ProctorStudentSearchCCCId = By.id("cccId");
    public static By ProctorStudentSearchEmail = By.id("email");
    public static By ProctorStudentSearchErrorBirthDateText = By.id("birthDateErrors");
    public static By ProctorStudentSearchErrorEmailText = By.id("emailNameErrors");
    public static By ProctorStudentSearchErrorFirstNameText = By.id("firstNameErrors");
    public static By ProctorStudentSearchErrorLastNameText = By.id("lastNameErrors");
    public static By ProctorStudentSearchErrorMiddleInitialText = By.id("middleInitialErrors");
    public static By ProctorStudentSearchErrorPhoneNumberText = By.cssSelector("#phoneErrors > p > span");
    public static By ProctorStudentSearchFirstName = By.id("firstName");
    public static By ProctorStudentSearchLastName = By.id("lastName");
    public static By ProctorStudentSearchMiddleInitial = By.id("middleInitial");
    public static By ProctorStudentSearchNoResults = By.cssSelector("ccc-content-loading-placeholder > div > div > span");
    public static By ProctorStudentSearchPhoneNumber = By.id("phone");
    public static By ProctorStudentSearchResult = By.cssSelector("ul.user-list > li:nth-of-type(1) > ccc-user:nth-of-type(1)");
    public static By ProctorStudentSearchResultContainer = By.cssSelector("ul.user-list");
    public static By ProctorStudentSearchResultList = By.cssSelector("ccc-student-results > ul");
    public static By ProctorStudentSearchResultName = By.cssSelector("ul.user-list > li:nth-of-type(1) > ccc-user:nth-of-type(1) > div.identity > h3:nth-of-type(1)");
    public static By ProctorStudentSearchSubmit = By.id("ccc-student-lookup-submit");
    public static By StudentDashboardAssessmentInstructionsTitleText = By.cssSelector("ccc-student-start-activation-instructions > div > div:nth-child(1) > h2");
    // Student Dashboard elements
    public static By StudentDashboardAssessmentReady = By.cssSelector("ccc-student-activations > div:nth-child(2) > ul > li:nth-child(1) > ccc-student-activation > span.label-ready");
    public static By StudentDashboardBeginAssessment = By.cssSelector("ccc-student-start-activation-instructions > div > div:nth-child(1) > div > button.btn-submit-button");
    public static By StudentDashboardBeginAssessmentBackButton = By.cssSelector("ccc-view-manager-view.ccc-view-focused button.btn-primary");
    public static By StudentDashboardContinue = By.cssSelector("ccc-student-start-activation > form > div.form-submit-controls > button.btn-submit-button");
    public static By StudentDashboardCurrentAssessmentsTable = By.cssSelector("ccc-student-activations > div:nth-child(2) > ul");
    public static By StudentDashboardFirstAssessment = By.cssSelector("ul > li:nth-child(1) > ccc-student-activation");
    public static By StudentDashboardHistoricalAssessmentsTable = By.cssSelector("ccc-student-activations > div:nth-child(4) > ul");
    public static By StudentDashboardInstructionsNextButton = By.cssSelector("button.next");
    public static By StudentDashboardInstructionsReadyButton = By.cssSelector("button.btn-success");
    public static By StudentDashboardModalErrorText = By.cssSelector("#cccProctorCodeErrors > p > span");
    public static By StudentDashboardMyAssessmentsButton = By.cssSelector("ccc-student-activation-status > div > div > div > button");
    public static By StudentDashboardProctorCode = By.id("proctorCode");
    public static By StudentDashboardProctorCodeError = By.id("cccProctorCodeErrors");
    public static By StudentDashboardStudentCCCID = By.cssSelector("p.cccid > span.id");

    public static By AssessmentPlayerCheckBox(int checkBoxIndex) {
        return By.cssSelector(String.format("div.ccc-interaction-choice-item-wrapper:nth-of-type(%s)", checkBoxIndex));
    }

    public static By AssessmentPreviewCheckBox(int checkBoxIndex) {
        return By.cssSelector(String.format("div.ccc-interaction-choice-item-wrapper:nth-of-type(%s)", checkBoxIndex));
    }

    public static By AssessmentPreviewContentQuestionChoice(int responseIndex) {
        return By.cssSelector(String.format("ccc-interaction-choice > div > div > div:nth-child(%s) > span", responseIndex));
    }

    public static By DeactivateButton(int buttonIndex) {
        return By.cssSelector(String.format("ccc-student-profile > div:nth-child(2) > ul > li:nth-child(%s) > div.actions > button.ccc-student-profile-deactivate", buttonIndex));
    }

    public static By ProctorDashboardFacetByIndex(int facetIndex) {
        return By.cssSelector(String.format("div.ccc-facet-list-facet:nth-of-type(%s) > div > div > input", facetIndex));
    }

    public static By ProctorDashboardStudentSearchDiv(int headerIndex) {
        return By.cssSelector(String.format("div.ccc-grouped-items-group:nth-of-type(%s) > ul:nth-of-type(1)", headerIndex));
    }

    public static By ProctorDashboardTestSite(int siteIndex) {
        return By.cssSelector(String.format("ccc-proctor-dashboard > div > div.col-md-9 > div > div:nth-child(%s) > ccc-location-card", 1 + siteIndex));
    }

    public static By ProctorStudentProfileActivationPrintButton(int activationIndex) {
        return By.cssSelector(String.format("ccc-student-profile > div:nth-child(2) > ul > li:nth-child(%s) > div.actions > button.ccc-student-profile-print", activationIndex));
    }

    public static By ProctorStudentProfileActivationStatus(int activationIndex) {
        return By.cssSelector(String.format("ccc-student-profile > div:nth-child(2) > ul > li:nth-child(1) > div.title-row > span", activationIndex));
    }

    public static By ProctorStudentSearchResult(String index) {
        return By.cssSelector(String.format("ccc-user:nth-of-type(%s)", index));
    }

    public static By ProctorStudentSearchResultId(String userId) {
        return By.cssSelector(String.format("#user-%s", userId));
    }

    public static By ProctorStudentSearchResultName(String userId) {
        return By.cssSelector(String.format("#user-%s > ccc-user:nth-of-type(1) > div.identity > h3:nth-of-type(1)", userId));
    }

    public static By StudentAssessment(int assessmentIndex) {
        return By.cssSelector(String.format("ccc-student-activations > div:nth-child(2) > ul > li:nth-child(%s) > ccc-student-activation", assessmentIndex));
    }

    public static By StudentAssessmentPrintIcon(int assessmentIndex) {
        return By.cssSelector(String.format("ccc-student-activations > div:nth-child(2) > ul > li:nth-child(%s) > ccc-student-activation > div > div > div > h4 > span > span.icon", assessmentIndex));
    }
}