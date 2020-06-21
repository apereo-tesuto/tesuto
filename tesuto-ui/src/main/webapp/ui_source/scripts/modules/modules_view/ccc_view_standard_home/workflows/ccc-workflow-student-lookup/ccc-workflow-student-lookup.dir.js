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

    angular.module('CCC.View.Home').directive('cccWorkflowStudentLookup', function () {

        return {

            restrict: 'E',

            scope: {
                'location': '=?',
                'cccId': '=?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'ViewManagerEntity',
                'LocationService',

                function ($scope, $element, $timeout, ViewManagerEntity, LocationService) {

                    /*============ MODEL ==============*/

                    $scope.location = LocationService.getCurrentTestCenter() || null;

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addStudentProfileView = function (student) {

                        var viewScope = $scope.$new();

                        viewScope.student = student;
                        viewScope.location = $scope.location;

                        $scope.viewManager.pushView({
                            id: 'proctor-student-profile',
                            title: 'Student Profile',
                            breadcrumb: 'Student Profile',
                            scope: viewScope,
                            showBackButton: true,
                            isNested: true,
                            template: '<ccc-workflow-student-profile student="student" location="location"></ccc-workflow-student-profile>'
                        });
                    };

                    var addStudentSearchView = function () {

                        var viewScope = $scope.$new();

                        viewScope.initialCccId = $scope.cccId;

                        $timeout(function () {
                            $element.find('#cccId').focus();
                        }, 1);

                        viewScope.$on('ccc-student-lookup.selected', function (event, student) {
                            addStudentProfileView(student);
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-student-search',
                            title: 'Student Lookup',
                            breadcrumb: 'Lookup',
                            showBackButton: false,
                            scope: viewScope,
                            template: '<ccc-student-lookup initial-cccid="initialCccId"></ccc-student-lookup>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                    addStudentSearchView();

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
