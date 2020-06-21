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

    angular.module('CCC.View.Home').directive('cccWorkflowStudentActivation', function () {

        return {

            restrict: 'E',

            scope: {
                'location': '=?',
                'initialDeliveryType': '@?'  // ONLINE or PAPER
            },

            controller: [

                '$scope',
                'ViewManagerEntity',

                function ($scope, ViewManagerEntity) {

                    /*============ PRIVATE VARIABLES =============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addCreationSummaryView = function (resultsSummary, student) {

                        var viewScope = $scope.$new();
                        viewScope.summary = resultsSummary;
                        viewScope.student = student;

                        viewScope.$on('ccc-activate-student-summary.okay', function () {
                            $scope.viewManager.goToView('proctor-student-lookup', true);
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-create-activation-summary',
                            title: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.ACTIVATION_COMPLETE',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.ACTIVATION_COMPLETE',
                            backTarget: 'proctor-student-lookup',
                            backButton: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.STUDENT_LOOKUP',
                            scope: viewScope,
                            template: '<ccc-activate-student-summary student="student" summary="summary"></ccc-activate-student-summary>'
                        });
                    };

                    var addActivateStudentView = function (student) {

                        var viewScope = $scope.$new();
                        viewScope.location = $scope.location;
                        viewScope.student = student;

                        viewScope.initialDeliveryType = $scope.initialDeliveryType;

                        viewScope.$on('LocationService.currentTestCenterUpdated', function (event, location) {
                            viewScope.location = location;
                            viewScope.$broadcast('ccc-activate-student.requestRefresh', location);
                        });

                        viewScope.$on('ccc-activate-student.processing', function () {
                            $scope.viewManager.disableNavigation();
                        });

                        viewScope.$on('ccc-activate-student.created', function (event, resultsSummary) {
                            $scope.viewManager.enableNavigation();
                            addCreationSummaryView(resultsSummary, viewScope.student);

                            $scope.$broadcast('ccc-student-lookup.request-clear');
                            $scope.viewManager.requestViewScrollUp('proctor-student-lookup');
                        });

                        viewScope.$on('ccc-activate-student.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-activate-student',
                            title: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.ACTIVATE_STUDENT',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.ACTIVATE_STUDENT',
                            scope: viewScope,
                            template: '<ccc-activate-student location="location" student="student" initial-delivery-type="{{initialDeliveryType}}"></ccc-activate-student>'
                        });
                    };

                    var addStudentLookupView = function () {

                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-student-lookup.selected', function (event, student) {
                            addActivateStudentView(student);
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-student-lookup',
                            title: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.STUDENT_LOOKUP',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.STUDENT_LOOKUP',
                            scope: viewScope,
                            template: '<ccc-student-lookup></ccc-student-lookup>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/


                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addStudentLookupView();

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
