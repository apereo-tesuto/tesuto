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

    angular.module('CCC.View.Home').directive('cccCollegeSelector', function () {

        return {

            restrict: 'E',

            scope: {
                colleges: '=',
                loading: '=',
                isDisabled: '=',
                labelledBy: '@?'
            },

            controller: [

                '$scope',
                '$attrs',

                function ($scope, $attrs) {

                    /*============ MODEL ============*/

                    $scope.collegeRadios = [];

                    $scope.isRequired = $attrs.hasOwnProperty("required");

                    $scope.selectedCollegeIdList = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var generateCollegeRadios = function () {

                        $scope.collegeRadios = [];

                        _.each($scope.colleges, function (college) {

                            $scope.collegeRadios.push({
                                name: college.cccId,
                                title: college.name,
                                value: college.cccId,
                                id: college.cccId,
                                description: college.name,
                                checked: college.selected,
                                hidden: false
                            });
                        });

                        _.sortBy($scope.collegeRadios, function(collegeRadio) {
                            return collegeRadio.name.toLowerCase();
                        });
                    };

                    var updateSelectedColleges = function () {

                        var selectedColleges = _.filter($scope.collegeRadios, function (collegeRadio) {
                            return collegeRadio.checked;
                        });

                        $scope.selectedCollegeIdList = _.map(selectedColleges, function (selectedCollege) {
                            return selectedCollege.id;
                        });

                        _.each($scope.colleges, function (college) {
                            college.selected = $scope.selectedCollegeIdList.indexOf(college.cccId) !== -1;
                        });
                    };

                    var reset = function () {
                        generateCollegeRadios();
                        updateSelectedColleges();
                    };


                    /*============ BEHAVIOR ============*/


                    /*============ LISTENERS ============*/

                    $scope.$watch('loading', reset);

                    $scope.$on('ccc-radio-box.change', function () {

                        updateSelectedColleges();

                        // we only fire this event if a change was made internally
                        $scope.$emit('ccc-college-selector.changed');
                    });

                    $scope.$on('ccc-college-selector.requestReset', reset);


                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<ccc-content-loading-placeholder class="ccc-college-selector-loader" ng-hide="colleges.length > 0 && !loading" no-results="colleges.length === 0 && !loading"></ccc-content-loading-placeholder>',

                '<ccc-radio-box class="ccc-radio-box-group-thin" input-type="checkbox" ',
                    'ng-repeat="radio in collegeRadios track by radio.value" ',
                    'radio="radio" ',
                    'is-disabled="isDisabled" ',
                    'radio-group-id="colleges" ',
                    'labelled-by="{{labelledBy}}" ',
                    'described-by="ccc-college-list-errors" ',
                    'is-required="isRequired && !selectedCollegeIdList.length">',
                '</ccc-radio-box>',

                '<input class="hidden" autocomplete="off" name="collegeSelector" ng-model="selectedCollegeIdList" ccc-required-array="1" />',

                '<div id="ccc-college-list-errors" class="ccc-validation-messages noanim ccc-validation-messages-standalone ccc-validation-messages-colleges">',
                    '<p ng-if="isRequired && !selectedCollegeIdList.length" class="noanim">',
                        '<i class="fa fa-exclamation-triangle color-warning"></i> ',
                        '<span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.COLLEGES"></span>',
                    '</p>',
                '</div>'

            ].join('')
        };
    });

})();
