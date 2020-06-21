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

    angular.module('CCC.View.Home').directive('cccWorkflowProctorLocation', function () {

        return {

            restrict: 'E',

            scope: {
                location: '=?'
            },

            controller: [

                '$scope',
                'LocationService',
                'ViewManagerEntity',

                function ($scope, LocationService, ViewManagerEntity) {

                    /*============ PRIVATE VARIABLES / METHODS ===========*/

                    var activationCreated = false;


                    /*============ MODEL ==============*/

                    $scope.viewManager = new ViewManagerEntity({});
                    $scope.location = LocationService.getCurrentTestCenter() || null;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addStudentProfileView = function (student) {
                        var viewScope = $scope.$new();
                        viewScope.student = student;
                        viewScope.location = $scope.location;

                        $scope.viewManager.pushView({
                            id: 'proctor-location-student-profile',
                            title: 'CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.STUDENT_PROFILE',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.STUDENT_PROFILE',
                            scope: viewScope,
                            isNested: true,
                            template: '<ccc-workflow-student-profile student="student" location="location"></ccc-workflow-student-profile>'
                        });
                    };

                    var addCheckinStudentView = function () {

                        // set our activation created flag
                        activationCreated = false;

                        var viewScope = $scope.$new();
                        viewScope.location = $scope.location;

                        // we want to know if an activation is created so we can force a refresh
                        viewScope.$on('ccc-activate-student.created', function () {
                            activationCreated = true;
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-location-create-activation',
                            title: 'CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.STUDENT_CHECKIN',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.STUDENT_CHECKIN',
                            scope: viewScope,
                            isNested: true,
                            template: '<ccc-workflow-student-activation location="location"></ccc-workflow-student-activation>'
                        });
                    };

                    var addProctorLocationView = function () {
                        var viewScope = $scope.$new();
                        viewScope.location = $scope.location;

                        viewScope.$on('ccc-proctor-location.requestCheckinStudent', addCheckinStudentView);

                        viewScope.$on('ccc-activations-by-student.studentSelected', function (e, student) {
                            addStudentProfileView(student);
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-location-details',
                            title: 'CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.PROCTOR',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.PROCTOR',
                            scope: viewScope,
                            template: '<ccc-proctor-location location="location"></ccc-proctor-location>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    $scope.viewManager.addListener('viewFocused', function (viewId) {
                        if (viewId === 'proctor-location' && activationCreated) {
                            $scope.$broadcast('ccc-proctor-location.requestRefresh');
                        }
                    });

                    $scope.$on('LocationService.currentTestCenterUpdated', function (event, location) {
                        $scope.location = location;
                        $scope.$broadcast('ccc-proctor-location.requestRefresh');
                    });

                    $scope.$on('ccc-activate-student.created', function () {
                        $scope.$broadcast('ccc-proctor-location.requestRefresh');
                    });

                    $scope.$on('ccc-workflow-student-profile.activationUpdated', function () {
                        $scope.$broadcast('ccc-proctor-location.requestRefresh');
                    });

                    $scope.$on('ActivationClass.print', function () {
                        $scope.$broadcast('ccc-proctor-location.requestRefresh');
                    });

                    $scope.$on('ccc-activation-card.deactivate', function () {
                        $scope.$broadcast('ccc-proctor-location.requestRefresh');
                    });

                    $scope.$on('ccc-activation-card.reactivate', function () {
                        $scope.$broadcast('ccc-proctor-location.requestRefresh');
                    });


                    /*============ INITIALIZATION ==============*/

                    addProctorLocationView();

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
