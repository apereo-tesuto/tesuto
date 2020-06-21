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
(function () {

    angular.module('CCC.View.Home').directive('cccAssessmentScoringResults', function () {

        return {

            restrict: 'E',

            scope: {
                assessmentSessionId: '=',
                assessedDate: '=',
                activation: '='
            },

            controller: [

                '$scope',
                '$element',
                '$translate',
                'ModalService',
                'NotificationService',
                'NavigationFreezeService',
                'StudentsAPIService',
                'AssessmentSessionScoringService',
                'AssessmentSessionsAPIService',
                'FakeData',
                'Moment',

                function ($scope, $element, $translate, ModalService, NotificationService, NavigationFreezeService, StudentsAPIService, AssessmentSessionScoringService, AssessmentSessionsAPIService, FakeData, Moment) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var assessmentSessionId = $scope.assessmentSessionId;

                    var getAssessedDate = function () {
                        _.each($scope.activation.statusChangeHistory, function (statusChange) {
                            if (statusChange.newStatus === 'ASSESSED') {
                                $scope.assessedDate = Moment(statusChange.changeDate).toISOString();
                            }
                        });
                    };


                    /*=============== PRIVATE BOOK RENDERING METHODS =============*/

                    // here we clear the area and render / rerender
                    var initializeBubbleSheet = function (scoringModel) {
                        $scope.itemSessionScoringObjects = scoringModel;
                        $scope.rendering = false;
                    };


                    /*=========== MODEL ===========*/

                    $scope.rendering = true;

                    $scope.userName = '';
                    $scope.userLoaded = false;

                    $scope.completing = false;

                    $scope.itemSessionScoringObjects = [];

                    $scope.assessment = false;
                    var taskSetId;

                    var populateItemSessionErrors = function (errorString) {

                        var itemSessionIdsWithErrors = /\[(.*)\]/g.exec(errorString)[1].split(', ');

                        _.each($scope.itemSessionScoringObjects, function (itemSessionScoringObject) {
                            itemSessionScoringObject.isInvalid = itemSessionIdsWithErrors.indexOf(itemSessionScoringObject.itemSessionId) !== -1;
                        });

                        // let everyone know there is a situation
                        $scope.$emit('ccc-assessment-scoring-results.invalidItemSessionsFound');

                        // show the modal
                        ModalService.openAlertModal('CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING-RESULTS.INVALID_ITEMS_MODAL.TITLE', 'CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING-RESULTS.INVALID_ITEMS_MODAL.MESSAGE', 'warning');
                    };

                    var loadAssessment = function () {

                        // for quick testing a flag for developers
                        var useFakeData = false;

                        if (useFakeData) {

                            FakeData.getAllFakeTasksItems().then(function (allTaskSets) {
                                initializeBubbleSheet(AssessmentSessionScoringService.getScoringModelFromTaskSets(allTaskSets));
                                $scope.userLoaded = true;
                            });

                        } else {

                            StudentsAPIService.studentListSearch([$scope.assessment.userId]).then(function (students) {

                                if (students && students.length) {
                                    $scope.userName = students[0].lastName + ', ' + students[0].firstName;
                                } else {
                                    $scope.userName = 'WARNING: No results for ('+ $scope.assessment.userId + ')';
                                    $scope.hasError = true;
                                }

                                $scope.userLoaded = true;

                                var allTaskSets = [$scope.assessment.currentTaskSet];
                                initializeBubbleSheet(AssessmentSessionScoringService.getScoringModelFromTaskSets(allTaskSets));
                            });
                        }
                    };

                    var loadAssessmentSession = function () {

                        if (!$scope.assessedDate) {
                            getAssessedDate();
                        }

                        return AssessmentSessionsAPIService.getAsProcessor(assessmentSessionId, $scope.assessedDate).then(function (results) {
                            if (!results.currentTaskSet) {

                                NotificationService.open({
                                    icon: 'fa fa-exclamation-triangle',
                                    title: ' Error.',
                                    message: 'Assessment session does not have a current task.'
                                },
                                {
                                    delay: 0,
                                    type: "danger",
                                    allow_dismiss: true
                                });

                            } else {

                                $scope.assessment = results;
                                taskSetId = $scope.assessment.currentTaskSet.taskSetId;
                            }
                        });
                    };

                    var showRouteBlockingModal = function () {

                        var routeBlockingModal = ModalService.openConfirmModal({
                            title: 'CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING-RESULTS.PROGRESS_WARNING.TITLE',
                            message: 'CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING-RESULTS.PROGRESS_WARNING.MESSAGE'
                        });

                        routeBlockingModal.result.then(function () {
                            NavigationFreezeService.stopBlocking(true);
                        });
                    };

                    var initialize = function () {

                        var windowBlockerTitle = $translate.instant('CCC_VIEW_HOME.CCC-ASSESSMENT-SCORING-RESULTS.PROGRESS_WARNING.TITLE');

                        // we wouldn't want the proctor to lose progress
                        NavigationFreezeService.startBlocking(showRouteBlockingModal, windowBlockerTitle);

                        loadAssessmentSession().then(function () {
                            loadAssessment();

                            // once the assessment session is loaded, the activation status changes from 'READY' to 'PENDING_SCORING'
                            $scope.$emit('ccc-assessment-scoring-results.statusChange');
                        });
                    };


                    /*=========== BEHAVIOR ==============*/

                    $scope.submit = function () {

                        var responses = AssessmentSessionScoringService.generateResponseModelFromScoringModel($scope.itemSessionScoringObjects);

                        $scope.completing = true;

                        AssessmentSessionsAPIService.completePaperAssessment(assessmentSessionId, taskSetId, responses).then(function () {
                            NavigationFreezeService.stopBlocking();
                            $scope.$emit('ccc-assessment-scoring-results.complete');
                        }, function (err) {

                            // we could recieve a string from the server with information about what to fix
                            if (err.status === 406) {
                                populateItemSessionErrors(err.data[0]);
                            }

                            $scope.completing = false;
                        });
                    };


                    /*=========== LISTENERS ===========*/

                    $scope.$on('$destroy', function () {
                        NavigationFreezeService.stopBlocking();
                    });


                    /*=========== INITIALIZATION ===========*/

                    initialize();
                }
            ],

            template: [

                '<h2>Assessment Score Entry</h2>',

                '<div class="ccc-assessment-scoring-data">',
                    '<div class="row">',
                        '<div class="col-md-4">',
                            '<div><strong>{{userName}}</strong><i class="fa fa-spin fa-spinner noanim" ng-if="!userLoaded"></i></div>',
                            '<div>{{::assessment.userId}}</div>',
                        '</div>',
                        '<div class="col-md-8 text-right">',
                            '<div><strong>{{::assessment.title}}</strong></div>',
                            '<div>{{::assessment.assessmentSessionId}}</div>',
                        '</div>',
                    '</div>',
                '</div>',

                '<ccc-content-loading-placeholder ng-if="!userLoaded"></ccc-content-loading-placeholder>',

                '<div class="ccc-assessment-scoring-results-target" ng-form="bubbleSheetResults">',
                    '<ccc-assessment-item-score-entry ng-repeat="itemSessionScoringObject in itemSessionScoringObjects track by $index" item="itemSessionScoringObject">Bubble Item</ccc-assessment-item-score-entry>',
                '</div>',

                '<div class="ccc-assessment-scoring-results-controls">',
                    '<button class="btn btn-primary btn-submit-button btn-full-width-when-small" ng-click="submit()" ng-disabled="!userLoaded || completing">Submit Scores</button>',
                '</div>'

            ].join('')

        };

    });

})();
