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

    angular.module('CCC.View.Home').directive('cccCollegeSelectorPills', function () {

        return {

            restrict: 'E',

            scope: {
                student: '=',
                selectedCollegeId: '='
            },

            controller: [

                '$scope',
                'CurrentUserService',

                function ($scope, CurrentUserService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var currentUser = CurrentUserService.getUser();


                    /*============ MODEL ============*/

                    $scope.collegeList = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getCurrentUserCollegeList = function () {

                        var currentUserCollegeList = _.map(currentUser.colleges, function (college) {
                            return {
                                id: college.cccId,
                                name: college.name,
                                selected: (college.cccId === $scope.selectedCollegeId)
                            };
                        });

                        return currentUserCollegeList;
                    };

                    var getStudentCollegeList = function () {

                        var studentCollegeList = [];

                        _.each($scope.student.collegeStatuses, function (value, key) {
                            if (value !== 0) {
                                studentCollegeList.push(key);
                            }
                        });

                        return studentCollegeList;
                    };

                    var generateCollegeList = function () {

                        var userColleges = getCurrentUserCollegeList();
                        var studentColleges = getStudentCollegeList();

                        _.each(userColleges, function (userCollege) {
                            _.each(studentColleges, function (studentCollege) {

                                if (userCollege.id === studentCollege) {
                                    $scope.collegeList.push(userCollege);
                                }
                            });
                        });

                        $scope.collegeList = _.sortBy($scope.collegeList, 'name');

                        var selectedCollege = _.find($scope.collegeList, function (college) {
                            return college.selected;
                        });

                        if (selectedCollege === undefined) {
                            $scope.collegeSelected(null, $scope.collegeList[0]);
                        }
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.collegeSelected = function (e, selectedCollege) {

                        _.each($scope.collegeList, function (college) {
                            college.selected = false;
                        });

                        selectedCollege.selected = true;

                        $scope.$emit('ccc-college-selector-pills.collegeSelected', selectedCollege);
                    };


                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    generateCollegeList();
                }
            ],

            template: [

                '<ul class="nav nav-pills">',
                    '<li ng-repeat="college in collegeList track by college.id" ng-class="{\'active\':college.selected}" ng-click="collegeSelected(e, college)">',
                        '<a href="#">{{college.name}}</a>',
                    '</li>',
                '</ul>'

            ].join('')
        };
    });

})();
