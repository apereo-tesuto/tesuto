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

    angular.module('CCC.View.Home').directive('cccWorkflowAssessmentScoring', function () {

        return {

            restrict: 'E',

            scope: {
                activation: '=',
                student: '=',
                parentViewManager: '='  // NOTE: This workflow should always be nested in another workflow because it doesn't have a good starting view to go back to when the workflow is complete
            },

            controller: [

                '$scope',
                'ViewManagerEntity',
                'NotificationService',

                function ($scope, ViewManagerEntity, NotificationService) {

                    /*============ PRIVATE VARIABLES / METHODS ===========*/


                    /*============ MODEL ==============*/

                    $scope.viewManager = new ViewManagerEntity({parentViewManager: $scope.parentViewManager});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addAssessmentScoringView = function (assessmentSessionId, assessedDate) {

                        // this workflow should really only be used in a nested workflow, because it doesn't really have a good starting view to go back to
                        // so here if this workflow isn't nested in another workflow, throw an error
                        var nestedErrorHandler = function () {
                            throw new Error('ccc-workflow-assessment-scoring.noParentWorkflowDetected');
                        };

                        var viewScope = $scope.$new();

                        viewScope.assessmentSessionId = assessmentSessionId;
                        viewScope.assessedDate = assessedDate;
                        viewScope.activation = $scope.activation;

                        viewScope.$on('ccc-assessment-scoring-results.invalidItemSessionsFound', function () {
                            $scope.viewManager.requestViewScrollUp('assessment-scoring-results', 200);
                        });

                        viewScope.$on('ccc-assessment-scoring-results.complete', function () {

                            $scope.$emit('ccc-workflow-assessment-scoring.complete', assessmentSessionId);

                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Success.',
                                message: 'Assessment was scored and marked complete.'
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        viewScope.$on('ccc-assessment-scoring-results.statusChange', function () {
                            $scope.$emit('ccc-workflow-assessment-scoring.statusChange');
                        });

                        $scope.viewManager.pushView({
                            id: 'assessment-scoring-results',
                            title: 'Score Assessment',
                            backButton: $scope.viewManager.parentBackButton('ERROR: No Parent Workflow'),
                            backAction: $scope.viewManager.parentBackAction(nestedErrorHandler),
                            scope: viewScope,
                            template: '<ccc-assessment-scoring-results assessment-session-id="assessmentSessionId" assessed-date="assessedDate" activation="activation"></ccc-assessment-scoring-results>'
                        });
                    };

                    var addAssessmentSetupView = function () {

                        var viewScope = $scope.$new();

                        viewScope.activation = $scope.activation;
                        viewScope.student = $scope.student;

                        viewScope.$on('ccc-assessment-scoring-setup.assessmentSessionLoaded', function (event, assessmentSessionId, assessedDate) {

                            $scope.activation.currentAssessmentSessionId = assessmentSessionId;

                            addAssessmentScoringView(assessmentSessionId, assessedDate);
                        });

                        viewScope.$on('ccc-assessment-scoring-setup.cancel', function () {
                            $scope.$emit('ccc-workflow-assessment-scoring.cancel');
                        });

                        $scope.viewManager.pushView({
                            id: 'assessment-scoring',
                            title: 'Assessment Scoring',
                            breadcrumb: 'Cancel',
                            scope: viewScope,
                            template: '<ccc-assessment-scoring-setup activation="activation" student="student"></ccc-assessment-scoring-setup>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    if ($scope.activation.currentAssessmentSessionId) {
                        addAssessmentScoringView($scope.activation.currentAssessmentSessionId);
                    } else {
                        addAssessmentSetupView();
                    }
                }
            ],

            template: [

                '<section>',
                    '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>',
                '</section>'

            ].join('')
        };
    });

})();
