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

    angular.module('CCC.View.Student').directive('cccStudentCollegeSelectionView', function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',
                '$element',
                'CurrentStudentService',

                function ($scope, $element, CurrentStudentService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.colleges = [];
                    $scope.loading = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var refresh = function () {
                        $scope.loading = true;

                        CurrentStudentService.getAvailableCollegesForPlacement().then(function (colleges) {

                            $scope.colleges = colleges;

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.setSelectedCollege = function (e, college) {
                        $scope.$emit('ccc-student-college-selection-view.collegeSelected', college);
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/

                    refresh();
                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-md-8 col-md-offset-2">',

                        '<div class="row margin-bottom">',
                            '<div class="col-xs-12">',

                                '<h2 ccc-autofocus tabindex="0" class="no-outline" id="student-placement-college-selector-header">Please Select a College</h2>',

                            '</div>',
                        '</div>',

                        '<div class="row margin-bottom">',
                            '<div class="col-xs-12">',

                                '<ccc-content-loading-placeholder ng-if="colleges.length === 0" no-results="colleges.length === 0 && !loading"></ccc-content-loading-placeholder>',

                                '<div ng-if="colleges.length && !loading">',
                                    '<ul class="nav nav-pills nav-stacked">',
                                        '<li ng-repeat="college in colleges track by college.cccId" class="ccc-list-item-big" ng-class="{selected: college.cccId === selectedCollege.cccId}">',
                                            '<a href="#" class="btn btn-default college text-left" ng-click="setSelectedCollege($event, college)" role="button" aria-describedby="student-placement-college-selector-header">{{college.name}}</a>',
                                        '</li>',
                                    '</ul>',
                                '</div>',

                            '</div>',
                        '</div>',

                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
