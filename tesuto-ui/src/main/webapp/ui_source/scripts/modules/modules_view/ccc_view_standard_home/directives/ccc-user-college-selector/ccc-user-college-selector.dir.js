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

    angular.module('CCC.View.Home').directive('cccUserCollegeSelector', function () {

        return {

            restrict: 'E',

            scope: {
                user: '=',
                isDisabled: '=',
                labelledBy: '@?'
            },

            controller: [

                '$scope',
                'CurrentUserService',

                function ($scope, CurrentUserService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.nestedItems = [];

                    $scope.collegeListLoading = true;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getUserHasCollegeSelected = function (college) {
                        return _.find($scope.user.collegeIds, function (userCollegeId) { return college.cccId === userCollegeId; }) !== undefined;
                    };

                    var getUserHasTestLocationSelected = function (testLocation) {
                        return _.find($scope.user.testLocationIds, function (userTestLocationId) { return testLocation.id === userTestLocationId; }) !== undefined;
                    };

                    var onCollegeSelected = function (item) {
                        $scope.user.collegeIds.push(item.id);
                    };
                    var onCollegeDeselected = function (item) {
                        var foundIndex = $scope.user.collegeIds.indexOf(item.id);
                        $scope.user.collegeIds.splice(foundIndex, 1);
                    };
                    var onTestLocationSelected = function (item) {
                        $scope.user.testLocationIds.push(item.id);
                    };
                    var onTestLocationDeselected = function (item) {
                        var foundIndex = $scope.user.testLocationIds.indexOf(item.id);
                        $scope.user.testLocationIds.splice(foundIndex, 1);
                    };

                    var generateNestedItemsModel = function (collegeList) {

                        // clear the array but keep the reference
                        $scope.nestedItems.length = 0;

                        _.each(collegeList, function (college) {

                            var newCollegeItem = {

                                id: college.cccId,
                                name: college.name,
                                item: college,

                                selected: getUserHasCollegeSelected(college),
                                disabled: college.disabled || false,

                                onSelected: onCollegeSelected,
                                onDeselected: onCollegeDeselected,

                                children: []
                            };

                            _.each(college.testLocations, function (testLocation) {

                                newCollegeItem.children.push({

                                    id: testLocation.id,
                                    name: testLocation.name,
                                    item: testLocation,

                                    selected: getUserHasTestLocationSelected(testLocation),
                                    disabled: testLocation.disabled || false,

                                    onSelected: onTestLocationSelected,
                                    onDeselected: onTestLocationDeselected
                                });
                            });

                            $scope.nestedItems.push(newCollegeItem);
                        });
                    };

                    var loadCollegeList = function () {

                        $scope.collegeListLoading = true;

                        // get a copy of the current college list
                        return CurrentUserService.getCollegeList(true).then(function (collegeList) {

                            var userCollegesAndLocationsNotAccessibleToAdminUser = $scope.user.getCollegesAndLocationsNotIn(collegeList);

                            // now that we have colleges and test locations available to the user we are editing but not available to the current admin editing that user
                            // we need to addd these colleges and test locations into our nested list for the selector
                            // since the admin user doesn't have authority to alter those particular locations and colleges, we disable them
                            _.each(userCollegesAndLocationsNotAccessibleToAdminUser.colleges, function (college) {

                                college.disabled = true;
                                college.expanded = true;

                                _.each(college.testLocations, function (testingLocation) {
                                    testingLocation.disabled = true;
                                });
                            });

                            // merge in the additional colleges (that are now disabled with corresponding testLocations disabled)
                            collegeList = collegeList.concat(userCollegesAndLocationsNotAccessibleToAdminUser.colleges);

                            // next we need to attach testLocations that the user has that the admin doesn't, event though the admin does have access to their corresponding colleges
                            _.each(userCollegesAndLocationsNotAccessibleToAdminUser.testLocations, function (testLocationData) {

                                _.each(collegeList, function (college) {

                                    if (college.cccId === testLocationData.collegeId) {

                                        college.disabled = true;
                                        testLocationData.testLocation.disabled = true;
                                        college.testLocations.push(testLocationData.testLocation);
                                    }

                                });
                            });

                            // sort colleges and test locations
                            collegeList = _.sortBy(collegeList, function (college) {

                                // also sort testLocations while we are looping through colleges
                                college.testLocations = _.sortBy(college.testLocations, function (testLocation) {
                                    return testLocation.name.toLowerCase();
                                });

                                return college.name.toLowerCase();
                            });

                            generateNestedItemsModel(collegeList);

                        }).finally(function () {
                            $scope.collegeListLoading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-user-college-selector.requestReset', function (e, successCallBack, failCallBack) {

                        loadCollegeList().then(function () {

                            // loadCollegeList resets the nestedItems so tell the nested items to re-render
                            $scope.$broadcast('ccc-nested-item-selector.requestUpdate');
                            successCallBack();

                        }, failCallBack);
                    });


                    /*============ INITIALIZATION ============*/

                    loadCollegeList();
                }
            ],

            template: [

                '<ng-form name="cccUserCollegeSelectorForm">',

                    '<ccc-content-loading-placeholder class="ccc-college-selector-loader" ng-if="nestedItems.length === 0 || collegeListLoading" no-results="nestedItems.length === 0 && !collegeListLoading"></ccc-content-loading-placeholder>',

                    '<ccc-nested-item-selector class="ccc-nested-item-selector-style-two-level" items="nestedItems" ng-if="nestedItems.length > 0 && !collegeListLoading" enable-enforce-parents-selected="true" is-disabled="isDisabled" labelled-by="ccc-college-selector-errors"></ccc-nested-item-selector>',

                    '<input class="hidden" autocomplete="off" name="locationSelector" ng-model="user.collegeIds" ng-model-options="{allowInvalid: true}" ccc-required-array="1" />',

                    '<div id="ccc-college-selector-errors" class="ccc-validation-messages ccc-validation-messages-standalone ccc-validation-messages-colleges">',
                        '<p ng-if="!user.collegeIds.length">',
                            '<i class="fa fa-exclamation-triangle color-warning"></i> ',
                            '<span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.COLLEGES"></span>',
                        '</p>',
                    '</div>',

                '</ng-form>'

            ].join('')
        };
    });

})();
