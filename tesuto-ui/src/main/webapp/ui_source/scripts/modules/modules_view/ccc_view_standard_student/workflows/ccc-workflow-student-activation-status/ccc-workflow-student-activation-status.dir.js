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

    angular.module('CCC.View.Student').directive('cccWorkflowStudentActivationStatus', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$state',
                'ViewManagerEntity',

                function ($scope, $state, ViewManagerEntity) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addActivationStatusView = function () {

                        var viewScope = $scope.$new();
                        viewScope.currentAssessmentSessionId = $state.params.currentAssessmentSessionId;

                        viewScope.$on('ccc-student-activation-status.assessmentSessionLoaded', function (e, assessmentSession) {

                            if (assessmentSession) {
                                $scope.viewManager.setTitle('student-activation-status', assessmentSession.title);
                            } else {
                                $scope.viewManager.setTitle('student-activation-status', 'no title available');
                            }
                        });

                        $scope.viewManager.pushView({
                            id: 'student-activation-status',
                            title: '',
                            breadcrumb: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.BREADCRUMB.BACK',
                            scope: viewScope,
                            template: '<ccc-student-activation-status current-assessment-session-id="currentAssessmentSessionId"></ccc-student-activation-status>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addActivationStatusView();

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();




