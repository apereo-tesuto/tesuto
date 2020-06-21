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

    angular.module('CCC.View.Home').directive('cccCollegesList', function () {

        return {

            restrict: 'E',

            scope: {
                selectedCccId: "=",
                isDisabled: '=?',
                describedBy: '@?',
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'CurrentUserService',

                function ($scope, $element, $timeout, CurrentUserService) {

                    /*============ MODEL ============*/

                    $scope.collegeList = [];
                    $scope.selectedCollege = false;
                    $scope.loading = true;

                    $scope.hideOnNoChoices = $scope.hideOnNoChoices ? true : false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var loadCollegeList = function () {

                        CurrentUserService.getCollegeList().then(function (collegeList) {

                            $scope.collegeList = _.sortBy(collegeList, function (college) {
                                return college.name.toLowerCase();
                            });

                            $scope.$emit('ccc-colleges-list.collegesLoaded', [$scope.collegeList[0]]);

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.setSelectedCollege = function (event, college) {

                        $scope.selectedCollege = college;
                        $scope.$emit('ccc-colleges-list.selected', $scope.selectedCollege, $(event.target));
                    };


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

                '<ul class="nav nav-pills nav-stacked">',
                    '<li ng-repeat="college in collegeList track by college.cccId" class="ccc-list-item-big" ng-class="{selected: college.cccId === selectedCollege.cccId}">',
                        '<a href="#" class="btn btn-default college text-left" ng-click="setSelectedCollege($event, college)" role="button" aria-describedby="{{::describedBy}}">{{college.name}}</a>',
                    '</li>',
                '</ul>'

            ].join('')
        };
    });

})();
