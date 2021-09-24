/*-------------------------------------------------------------------------------
# Copyright © 2019 by California Community Colleges Chancellor's Office
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License.  You may obtain a copy
# of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.
#------------------------------------------------------------------------------*/
/*============== GLOBAL DEPENDENCIES ==============*/

(function () {

    /**
     * MAIN VIEW DIRECTIVE FOR THE ASSESSMENT PRINTING VIEW
     */

    angular.module('CCC.View.AssessmentPrintDetached').directive('cccViewAssessmentPrintDetached', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$q',
                'Moment',
                'SESSION_CONFIGS',
                'AssessmentSessionItemPrintService',
                'BookRendererClass',
                'PrintCSSService',
                'MathService',

                function ($scope, $element, $timeout, $q, Moment, SESSION_CONFIGS, AssessmentSessionItemPrintService, BookRendererClass, PrintCSSService, MathService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    var renderBook_debounced;

                    var assessmentItemList;
                    var bubbleSheetItemList;

                    var DEFAULT_PRINT_PAGE_HEIGHT = 27;

                    // some fake defaults for fake assessmentSession testing
                    var assessmentName = '';
                    var assessmentId = '';
                    var assessmentDate = new Moment.utc().format('M/D/YYYY h:mm:ss');
                    var misCode = '';
                    var assessmentVersion = '(no version)';

                    var studentCCCId = '';
                    var studentName = '';

                    if (SESSION_CONFIGS.user) {
                        studentCCCId = SESSION_CONFIGS.user.cccid;
                        studentName = SESSION_CONFIGS.user.lastName + ', ' + SESSION_CONFIGS.user.firstName;
                    }


                    /*=============== PRIVATE BOOK RENDERING METHODS =============*/

                    var getAssessmentInfo = function () {
                        return [
                            '<span>',
                                assessmentId,
                            '</span> ::: ',
                            '<span>',
                                assessmentDate,
                            '</span> ::: ',
                            '<span>',
                                misCode,
                            '</span>',
                        ].join('');
                    };

                    var assessmentBubbleSheetHeader = function (pageNum) {

                        if (pageNum === 1) {

                            return $([
                                '<div class="ccc-assessment-print-header">',
                                    '<div class="ccc-assessment-print-header-inner">',
                                        '<div class="container-fluid">',
                                            '<div class="row">',
                                                '<div class="col-xs-9">',
                                                    '<strong>',
                                                        assessmentName,
                                                    '</strong>',
                                                '</div>',
                                                '<div class="col-xs-3 text-right">',
                                                    '<strong>Version: </strong>',
                                                    assessmentVersion,
                                                '</div>',
                                            '</div>',
                                            '<div class="row row-thin">',
                                                '<div class="col-xs-12 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Student Name:</strong> <span class="ccc-assessment-print-header-manual-info-value">' + studentName + '</span></div>',
                                                '</div>',
                                            '</div>',
                                            '<div class="row row-thin">',
                                                '<div class="col-xs-6 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Student ID:</strong> <span class="ccc-assessment-print-header-manual-info-value">' + studentCCCId + '</span></div>',
                                                '</div>',
                                                '<div class="col-xs-6 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Date of Birth:</strong></div>',
                                                '</div>',
                                            '</div>',
                                            '<div class="row row-thin">',
                                                '<div class="col-xs-6 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Student College ID:</strong></div>',
                                                '</div>',
                                                '<div class="col-xs-3 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Date:</strong></div>',
                                                '</div>',
                                                '<div class="col-xs-3 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Time:</strong></div>',
                                                '</div>',
                                            '</div>',
                                            '<div class="row row-thin ccc-assessment-print-header-processor">',
                                                '<div class="col-xs-6 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Processor Name:</strong></div>',
                                                '</div>',
                                                '<div class="col-xs-3 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Date:</strong></div>',
                                                '</div>',
                                                '<div class="col-xs-3 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Time:</strong></div>',
                                                '</div>',
                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>'
                            ].join(''));

                        } else {

                            return $([
                                '<div class="ccc-assessment-print-header">',
                                    '<div class="ccc-assessment-print-header-inner">',
                                        '<div class="container-fluid">',
                                            '<div class="row">',
                                                '<div class="col-xs-9">',
                                                    '<strong>',
                                                        assessmentName,
                                                    '</strong>',
                                                '</div>',
                                                '<div class="col-xs-3 text-right">',
                                                    '<strong>Version: </strong>',
                                                    assessmentVersion,
                                                '</div>',
                                            '</div>',
                                            '<div class="row row-thin">',
                                                '<div class="col-xs-12 col-thin">',
                                                    '<div class="ccc-assessment-print-header-manual-info"><strong>Student Name:</strong> <span class="ccc-assessment-print-header-manual-info-value">' + studentName + '</span></div>',
                                                '</div>',
                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>'
                            ].join(''));
                        }
                    };

                    var assessmentQuestionsHeader = function (pageNum) {

                        return $([
                            '<div class="ccc-assessment-print-header">',
                                '<div class="ccc-assessment-print-header-inner">',
                                    '<div class="container-fluid">',
                                        '<div class="row">',
                                            '<div class="col-xs-9">',
                                                '<strong>',
                                                    assessmentName,
                                                '</strong>',
                                            '</div>',
                                            '<div class="col-xs-3 text-right">',
                                                '<strong>Version: </strong>',
                                                assessmentVersion,
                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</div>'
                        ].join(''));
                    };

                    var assessmentFooter = function (pageNum) {
                        return $([
                            '<div class="ccc-assessment-print-footer">',
                                '<div class="container-fluid">',
                                    '<div class="row">',
                                        '<div class="col-xs-8 text-left">',
                                            getAssessmentInfo(),
                                        '</div>',
                                        '<div class="col-xs-4 text-right">',
                                            ' Page ' + pageNum + ' of <span class="total-pages-container"><i class="fa fa-spin fa-spinner"></i></span>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</div>'
                        ].join(''));
                    };

                    // here we clear the area and render / rerender
                    var renderBook = function (assessmentBookItems, bubbleSheetBookItems) {

                        $scope.rendering = true;
                        $scope.hasError = false;

                        // create a new book with configurations
                        var bubbleSheetBook = new BookRendererClass($element.find('.ccc-view-assessment-print-bubble-sheet'), {
                            pageHeight:             DEFAULT_PRINT_PAGE_HEIGHT,
                            header:                 assessmentBubbleSheetHeader,
                            footer:                 assessmentFooter,
                            newsPaperColumns:       true    // for bubble sheet we want two column news paper style printing
                        });

                        // create a new book with configurations
                        var assessmentBook = new BookRendererClass($element.find('.ccc-view-assessment-print-assessment'), {
                            pageHeight:             DEFAULT_PRINT_PAGE_HEIGHT,
                            header:                 assessmentQuestionsHeader,
                            footer:                 assessmentFooter,
                            pageRowPostProcessors:  [AssessmentSessionItemPrintService.mathPostProcess, AssessmentSessionItemPrintService. imagePostProcess]
                        });

                        // we render the bubble sheet first and count it's pages so we can merge all page counts together
                        var totalBubbleSheetPages = 0;
                        var assessmentRenderPromise = bubbleSheetBook.renderItems(bubbleSheetBookItems).then(function (totalPages) {

                            totalBubbleSheetPages = totalPages;

                        }).then(function () {

                            assessmentBook.setStartingPageNumber(totalBubbleSheetPages + 1);

                            return assessmentBook.renderItems(assessmentBookItems).then(function (totalPages) {

                                var totalBubbleSheetAndAssessmentPages = totalBubbleSheetPages + totalPages;

                                // go back and fill in the total pages
                                assessmentBook.element.find('.total-pages-container').html(totalBubbleSheetAndAssessmentPages);
                                bubbleSheetBook.element.find('.total-pages-container').html(totalBubbleSheetAndAssessmentPages);
                            });
                        });

                        // render the assessment items
                        assessmentRenderPromise.then(function () {

                            // sweet, all good

                        }, function (err) {

                            // to do, what messaging can we do here?
                            $scope.hasError = true;

                        }).finally(function () {

                            $scope.rendering = false;
                        });
                    };

                    renderBook_debounced = _.debounce(function () {
                        renderBook(assessmentItemList, bubbleSheetItemList);
                    }, 500);


                    /*=========== MODEL ===========*/

                    $scope.rendering = true;
                    $scope.hasError = false;


                    /*=========== LISTENERS ===========*/


                    /*=========== INITIALIZATION ===========*/

                    // first let's force rendering mode in math for consistency in printing
                    MathService.setRenderer('HTML-CSS');

                    // allow math to render immediatly, we are using ccc-assess base styles which by default hides math rendering
                    // TODO: Perhaps the people that need math hidden should put that on right away instead?
                    $('body').addClass('show-math');

                    assessmentName = SESSION_CONFIGS.assessmentSession.title;
                    assessmentId = SESSION_CONFIGS.assessmentSession.assessmentContentId;
                    assessmentVersion = SESSION_CONFIGS.assessmentSession.assessmentVersion;
                    misCode = SESSION_CONFIGS.miscode;

                    var allTaskSets = [SESSION_CONFIGS.assessmentSession.currentTaskSet];

                    var transformedAssessmentSession = AssessmentSessionItemPrintService.convertAssessmentTaskSetsToPrintItems(allTaskSets);

                    PrintCSSService.findAndloadStyles(allTaskSets);

                    assessmentItemList = transformedAssessmentSession.assessmentItemList;
                    bubbleSheetItemList = transformedAssessmentSession.bubbleSheetItemList;

                    renderBook_debounced();
                }
            ],

            template: [
                '<div class="ccc-print-loading-modal" ng-if="rendering">',
                    '<span class="ccc-print-loader-container">',
                        '<i class="ccc-print-loader fa fa-spin fa-spinner"></i>',
                    '</span>',
                '</div>',
                '<div class="ccc-view-assessment-print-bubble-sheet"></div>',
                '<div class="ccc-view-assessment-print-assessment"></div>'
            ].join('')
        };

    });

})();
