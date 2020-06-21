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

    angular.module('CCC.View.Home').directive('cccDistrictList', function () {

        return {

            restrict: 'E',

            scope: {
                districts: '='
            },

            controller: [

                '$scope',
                '$timeout',
                'ASSESSMENTS_DISABLED',

                function ($scope, $timeout, ASSESSMENTS_DISABLED) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    $scope.openCollegeMap =  {};
                    $scope.collegeUsersMap = {};

                    $scope.isAssessmentsDisabled = ASSESSMENTS_DISABLED;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var loadCollegeUsers = function (college) {

                        if (!$scope.collegeUsersMap[college.cccId]) {

                            $scope.collegeUsersMap[college.cccId] = {
                                loading: true,
                                users: false
                            };

                            $timeout(function () {
                                $scope.collegeUsersMap[college.cccId].loading = false;
                                $scope.collegeUsersMap[college.cccId].users = [{}];
                            }, 1000);
                        }
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.toggleCollegeOpen = function (college) {

                        if ($scope.openCollegeMap[college.cccId]) {
                            $scope.openCollegeMap[college.cccId] = false;
                        } else {
                            $scope.openCollegeMap[college.cccId] = true;
                            loadCollegeUsers(college);
                        }
                    };

                    $scope.addLocationToCollege = function (college) {
                        $scope.$emit('ccc-district-list.addLocationToCollege', college);
                    };

                    $scope.addUser = function () {
                        $scope.$emit('ccc-district-list.addUser');
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-college-test-locations.editLocation', function (e, testLocation) {
                        $scope.$emit('ccc-district-list.editLocation', testLocation);
                    });

                    $scope.$on('ccc-college-test-locations.addUser', $scope.addUser);

                    $scope.$on('ccc-college-users.addUser', $scope.addUser);

                    $scope.$on('ccc-college-users.userClicked', function (e, user) {
                        $scope.$emit('ccc-district-list.userClicked', user);
                    });

                    $scope.$on('ccc-college-test-locations.userClicked', function (e, user) {
                        $scope.$emit('ccc-district-list.userClicked', user);
                    });


                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<div class="district" ng-repeat="district in districts track by district.cccId">',
                    '<div class="district-header">',
                        '<h3 class="title" id="district-{{::district.cccId}}" tabindex="0"><span class="sr-only">district,</span> {{::district.name}}</h3>',
                    '</div>',
                    '<div class="district-body">',

                        '<div class="content">',

                            '<div class="list-container college-list-container">',
                                '<h6 class="title">',
                                    '<img class="icon" src="ui/resources/images/icons/university.png" alt="{{::(\'CCC_GLOBAL.ICONS.UNIVERSITY\'|translate)}}" role="presentation" aria-hidden="true">',
                                    'Colleges',
                                '</h6>',
                                '<button class="btn btn-primary change-view hide">Add College</button>',
                                '<ul class="list-unstyled college-list expand-collapse-list level-one">',
                                    '<li ng-repeat="college in district.colleges track by college.cccId" ng-class="{open: openCollegeMap[college.cccId]}">',
                                        '<div class="expand-collapse-item">',
                                            '<button class="btn btn-sm btn-default btn-icon-only expand-collapse-toggle" ng-click="toggleCollegeOpen(college)" aria-expanded="{{openCollegeMap[college.cccId] === true}}" aria-labelledby="college-{{::college.cccId}}">',
                                                '<span class="toggle-open" ng-if="!openCollegeMap[college.cccId]">',
                                                    '<i class="fa fa-caret-right" aria-hidden="true"></i>',
                                                    '<span class="sr-only" translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.SR_TOGGLE_OPEN"></span>',
                                                '</span>',
                                                '<span class="toggle-closed" ng-if="openCollegeMap[college.cccId]">',
                                                    '<i class="fa fa-caret-down" aria-hidden="true"></i>',
                                                    '<span class="sr-only" translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.SR_TOGGLE_CLOSED"></span>',
                                                '</span>',
                                            '</button>',
                                            '<h4 class="title">',
                                                '<span id="college-{{::college.cccId}}" aria-describedby="district-{{::district.cccId}}">{{::college.name}}</span>',
                                            '</h4>',
                                        '</div>',

                                        '<div class="expand-collapse-body" ng-if="openCollegeMap[college.cccId]">',

                                            '<div class="list-container test-location-container" ng-if="::!isAssessmentsDisabled">',

                                                '<h6 class="title">',
                                                    '<img class="icon" src="ui/resources/images/icons/school.png" alt="{{::(\'CCC_GLOBAL.ICONS.SCHOOL\'|translate)}}" role="presentation" aria-hidden="true">',
                                                    ' <span translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.TEST_LOCATIONS.TITLE"></span>',
                                                    '<button class="btn btn-primary btn-sm list-container-add-button pull-right" ng-click="addLocationToCollege(college)" aria-describedby="college-{{::college.cccId}}">',
                                                        '<span class="fa fa-plus-square" role="presentation" aria-hidden="true"></span> ',
                                                        '<span translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.TEST_LOCATIONS.ADD_LOCATION"></span>',
                                                    '</button>',
                                                '</h6>',

                                                // College Test Locations
                                                '<ccc-college-test-locations test-locations="college.testLocations"></ccc-college-test-locations>',

                                            '</div>',

                                            '<div class="row">',
                                                '<div class="col col-md-12">',
                                                    '<div class="list-container user-list-container">',

                                                        '<h6 class="title">',
                                                            '<img class="icon" src="ui/resources/images/icons/group.png" alt="{{::(\'CCC_GLOBAL.ICONS.PEOPLE_GROUP\'|translate)}}" role="presentation" aria-hidden="true">',
                                                            ' <span translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.USERS.TITLE"></span>',
                                                            '<button ng-click="addUser()" class="btn btn-primary btn-sm list-container-add-button pull-right" aria-describedby="college-{{::college.cccId}}">',
                                                                '<span class="fa fa-plus-square" role="presentation" aria-hidden="true"></span> ',
                                                                '<span translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.USERS.ADD_USER"></span>',
                                                            '</button>',
                                                        '</h6>',

                                                        // College User List
                                                        '<ccc-college-users college="college"></ccc-college-users>',

                                                    '</div>',
                                                '</div>',
                                            '</div>',

                                        '</div>',
                                    '</li>',
                                '</ul>',

                            '</div>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
