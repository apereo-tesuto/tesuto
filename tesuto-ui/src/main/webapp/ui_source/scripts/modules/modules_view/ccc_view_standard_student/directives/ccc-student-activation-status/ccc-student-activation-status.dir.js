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

    angular.module('CCC.View.Student').directive('cccStudentActivationStatus', function () {

        return {

            restrict: 'E',

            scope: {
                currentAssessmentSessionId: '='
            },

            controller: [

                '$scope',
                'AssessmentSessionsAPIService',

                function ($scope, AssessmentSessionsAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ============*/

                    $scope.activation = false;
                    $scope.loading = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var doGetAssessmentSession = function () {

                        $scope.loading = true;

                        AssessmentSessionsAPIService.get([$scope.currentAssessmentSessionId]).then(function (assessmentSession) {

                            $scope.assessmentSession = assessmentSession;

                        }, function () {

                            $scope.assessmentSession = false;

                        }).finally(function () {

                            $scope.loading = false;
                            $scope.$emit('ccc-student-activation-status.assessmentSessionLoaded', $scope.assessmentSession);
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    doGetAssessmentSession();

                }
            ],

            template: [

                '<div ng-if="assessmentSession">',

                    '<div class="panel panel-default reading-width">',
                        '<div class="panel-body">',
                            '<h2 translate="CCC_STUDENT.ACTIVATIONS.FINISHED_INSTRUCTIONS.TITLE">Finished</h2>',
                            '<div class="alert alert-success" translate="CCC_STUDENT.ACTIVATIONS.FINISHED_INSTRUCTIONS.ALERT">',
                                'You did it. Congratulations on completing your assessment.',
                            '</div>',
                            '<p translate="CCC_STUDENT.ACTIVATIONS.FINISHED_INSTRUCTIONS.BODY">Please see the proctor for next steps.</p>',
                            '<button type="button" ui-sref="home" class="btn btn-primary" translate="CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.MY_ASSESSMENTS">',
                                'My Assessments ',
                                '<span class="fa fa-chevron-right" role="presentation"></span>',
                            '</button>',
                        '</div>',
                    '</div>',

                '</div>',

                '<div class="row" ng-if="loading || !loading && !assessmentSession">',
                    '<div class="col-xs-12">',
                        '<ccc-content-loading-placeholder no-results="!loading && !activation">',
                            '<span translate="CCC_STUDENT.ACTIVATIONS.NOT_FOUND">Sorry, we couldn\'t find this activation.</span>',
                        '</ccc-content-loading-placeholder>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();






