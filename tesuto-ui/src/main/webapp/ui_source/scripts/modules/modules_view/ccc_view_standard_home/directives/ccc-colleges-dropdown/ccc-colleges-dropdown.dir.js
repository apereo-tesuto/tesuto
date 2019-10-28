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

    angular.module('CCC.View.Home').directive('cccCollegesDropdown', function () {

        var collegeDropDownId = 0;
        var getCollegeDropDownId = function () {
            return collegeDropDownId++;
        };

        return {

            restrict: 'E',

            scope: {
                selectedCccId: "=",
                hideOnNoChoices: '=?',
                isDisabled: '=?',
                autoSelectFirstCollege: '=?',
                isRequired: '=?',
                listStyle: '@?',
                showAllColleges: '=?' // it's possible this user has a permission to view all colleges so let's configure this component to allow it
            },

            controller: [

                '$scope',
                '$element',
                'CurrentUserService',

                function ($scope, $element, CurrentUserService) {


                    /*============ MODEL ============*/

                    $scope.collegeList = [];
                    $scope.selectedCollege = false;
                    $scope.loading = true;

                    $scope.isRequired = $scope.isRequired || false;

                    $scope.autoSelectFirstCollege = $scope.autoSelectFirstCollege || false;

                    $scope.hideOnNoChoices = $scope.hideOnNoChoices ? true : false;

                    $scope.listStyle = $scope.listStyle || '';

                    $scope.showAllColleges = $scope.showAllColleges || false;

                    $scope.collegeDropDownId = getCollegeDropDownId();


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var setSelectedCollege = function (college, firstLoad) {
                        $scope.selectedCollege = college;
                        $scope.$emit('ccc-colleges-list.collegeSelected', $scope.selectedCollege, firstLoad);
                    };

                    var setInitiallySelectedCollege = function () {

                        _.each($scope.collegeList, function (college) {
                            if (college.cccId === $scope.selectedCccId) {
                                setSelectedCollege(college, true);
                            }
                        });

                        if (!$scope.selectedCollege && $scope.autoSelectFirstCollege) {
                            if ($scope.collegeList.length) {
                                setSelectedCollege($scope.collegeList[0], true);
                            }
                        }
                    };

                    var loadCollegeList = function () {

                        CurrentUserService.getCollegeList($scope.showAllColleges).then(function (collegeList) {

                            // Alphabetically sort by college name (ascending)
                            collegeList.sort(function (a, b) {
                                if (a.name < b.name) {
                                    return -1;
                                }
                                if (a.name > b.name) {
                                    return 1;
                                }
                                return 0;
                            });

                            $scope.collegeList = collegeList;
                            setInitiallySelectedCollege();

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.setSelectedCollege = setSelectedCollege;


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/

                    // propagate the ccc-autofocus attribute down to the button
                    if ($element.attr('ccc-autofocus')) {
                        $element.removeAttr('ccc-autofocus');
                        $element.find('button').attr('ccc-autofocus','ccc-autofocus');
                    }

                    loadCollegeList();
                }
            ],

            template: [

                '<ng-form name="collegeDropdownField">',

                    '<div ccc-dropdown-focus class="btn-group" ng-class="{\'ng-invalid\': isRequired && !selectedCollege}" ng-hide="hideOnNoChoices && collegeList.length <= 1" name="selectedCollege" ccc-validation-badge="collegeDropdownField" ccc-validation-badge-style="fullWidth">',
                        '<button ng-disabled="loading || isDisabled" type="button" class="btn dropdown-toggle" ng-class="{\'btn-primary\': listStyle === \'primary\', \'btn-default\': listStyle !== \'primary\'}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                            '<span ng-if="loading">Loading <span class="fa fa-spinner fa-spin" aria-hidden="true"></span></span>',
                            '<span ng-if="!loading && !selectedCollege">--- Please select a college --- </span>',
                            '<span ng-if="!loading && selectedCollege">{{selectedCollege.name}}</span>',
                            '<span ng-if="!loading" class="caret" aria-hidden="true"></span>',
                        '</button>',
                        '<ul class="dropdown-menu">',
                            '<li ng-repeat="college in collegeList track by college.cccId" ng-class="{selected: college.cccId === selectedCollege.cccId}">',
                                '<a href="#" ng-click="setSelectedCollege(college)">{{college.name}}</a>',
                            '</li>',
                        '</ul>',
                        '<input type="hidden" ng-model="selectedCollege" ccc-validation-expression="!isRequired || selectedCollege" name="selectedCollege">',
                    '</div>',

                    '<div id="selectedCollegeErrors{{::collegeDropDownId}}" class="ccc-validation-messages noanim" ng-messages="collegeDropdownField.selectedCollege.$error">',
                        '<p ng-message="cccValidationExpression" class="noanim">',
                            '<span translate="CCC_VIEW_HOME.CCC-COLLEGE-DROPDOWN.REQUIRED"></span>',
                        '</p>',
                    '</div>',

                '</ng-form>'

            ].join('')
        };
    });

})();
