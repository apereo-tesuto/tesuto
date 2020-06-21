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

    angular.module('CCC.View.AssessmentPrint').directive('cccViewAssessmentPrint', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$q',
                'SESSION_CONFIGS',
                'StudentsAPIService',
                'AssessmentSessionItemPrintService',
                'BookRendererClass',
                'PrintCSSService',
                'MathService',
                'FakeData',

                function ($scope, $element, $timeout, $q, SESSION_CONFIGS, StudentsAPIService, AssessmentSessionItemPrintService, BookRendererClass, PrintCSSService, MathService, FakeData) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    var renderBook_debounced;

                    var assessmentItemList;
                    var bubbleSheetItemList;

                    var DEFAULT_PRINT_PAGE_HEIGHT = 27;

                    // some fake defaults for fake assessmentSession testing
                    var userName = SESSION_CONFIGS && SESSION_CONFIGS.user ? SESSION_CONFIGS.user.displayName : 'TEST';
                    var userId = 'A10005';
                    var assessmentName = 'English Placement (Paper)';
                    var assessmentSessionId = '42ddcabd-3132-46d8-ac1b-9bc8ab6f283b';


                    /*=============== PRIVATE BOOK RENDERING METHODS =============*/

                    var assessmentHeader = function (pageNum) {

                        return $([
                            '<div class="ccc-assessment-print-header">',
                                '<div class="ccc-assessment-print-header-inner">',
                                    '<div class="container-fluid">',
                                        '<div class="row">',
                                            '<div class="col-xs-4">',
                                                '<div><strong>',
                                                    userName,
                                                '</strong></div>',
                                                '<div>',
                                                    userId,
                                                '</div>',
                                            '</div>',
                                            '<div class="col-xs-8 text-right">',
                                                '<div><strong>',
                                                    assessmentName,
                                                '</strong></div>',
                                                '<div>',
                                                    assessmentSessionId,
                                                '</div>',
                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</div>'
                        ].join(''));
                    };

                    var assessmentFooter = function (pageNum) {
                        return $('<div class="ccc-assessment-print-footer"> Page ' + pageNum + ' of <span class="total-pages-container"><i class="fa fa-spin fa-spinner"></i></span></div>');
                    };

                    // here we clear the area and render / rerender
                    var renderBook = function (assessmentBookItems, bubbleSheetBookItems) {

                        $scope.rendering = true;
                        $scope.hasError = false;

                        // create a new book with configurations
                        var assessmentBook = new BookRendererClass($element.find('.ccc-view-assessment-print-assessment'), {
                            pageHeight:             DEFAULT_PRINT_PAGE_HEIGHT,
                            header:                 assessmentHeader,
                            footer:                 assessmentFooter,
                            pageRowPostProcessors:  [AssessmentSessionItemPrintService.mathPostProcess, AssessmentSessionItemPrintService. imagePostProcess]
                        });

                        // create a new book with configurations
                        var bubbleSheetBook = new BookRendererClass($element.find('.ccc-view-assessment-print-bubble-sheet'), {
                            pageHeight:             DEFAULT_PRINT_PAGE_HEIGHT,
                            header:                 assessmentHeader,
                            footer:                 assessmentFooter,
                            newsPaperColumns:       true    // for bubble sheet we want two column news paper style printing
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

                    var useFakeData = !SESSION_CONFIGS.assessmentSession || !SESSION_CONFIGS.assessmentSession.currentTaskSet;

                    if (useFakeData) {

                        FakeData.getAllFakeTasksItems().then(function (allTaskItems) {

                            var transformedAssessmentSession = AssessmentSessionItemPrintService.convertAssessmentTaskSetsToPrintItems(allTaskItems);

                            PrintCSSService.findAndloadStyles(allTaskItems);

                            assessmentItemList = transformedAssessmentSession.assessmentItemList;
                            bubbleSheetItemList = transformedAssessmentSession.bubbleSheetItemList;

                            renderBook_debounced();
                        });

                    } else {

                        userName = SESSION_CONFIGS.user.displayName;
                        userId = SESSION_CONFIGS.assessmentSession.userId;
                        assessmentName = SESSION_CONFIGS.assessmentSession.title;
                        assessmentSessionId = SESSION_CONFIGS.assessmentSession.assessmentSessionId;

                        StudentsAPIService.studentListSearch([userId]).then(function (students) {

                            if (students && students.length) {
                                userName = students[0].displayName;
                            } else {
                                userName = 'WARNING: No results for ('+ userId + ')';
                                $scope.hasError = true;
                            }

                            var allTaskSets = [SESSION_CONFIGS.assessmentSession.currentTaskSet];

                            var transformedAssessmentSession = AssessmentSessionItemPrintService.convertAssessmentTaskSetsToPrintItems(allTaskSets);

                            PrintCSSService.findAndloadStyles(allTaskSets);

                            assessmentItemList = transformedAssessmentSession.assessmentItemList;
                            bubbleSheetItemList = transformedAssessmentSession.bubbleSheetItemList;

                            renderBook_debounced();
                        });
                    }
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
