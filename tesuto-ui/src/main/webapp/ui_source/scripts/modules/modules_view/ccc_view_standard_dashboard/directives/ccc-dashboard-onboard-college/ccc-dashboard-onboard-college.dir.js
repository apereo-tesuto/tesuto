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

    angular.module('CCC.View.Dashboard').directive('cccDashboardOnBoardCollege', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'OnboardCollegeAPIService',

                function ($scope, OnboardCollegeAPIService) {


                    /*============ PRIVATE VARIABLES AND METHODS =============*/


                    /*============ MODEL =============*/

                    $scope.searching = false;
                    $scope.miscode = '';
                    $scope.description = '';


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var doOnboardCollege = function () {
                       	$scope.searching = true;
                       	OnboardCollegeAPIService.onboardCollege($scope.miscode, $scope.description).then(function () {


                        }, function (err) {

                            $scope.$emit('ccc-on-board-college.error', err);

                        }).finally(function () {


                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.doOnboardCollege = doOnboardCollege;

                }
                
            ],

            template: [
                '<form novalidate autocomplete="off">',
                '<div class="form-group row">',
                	'<label for="miscode" class="col-md-4 col-sm-3 control-label form-inline-first-label"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i>College Miscode</label>',
                	'<div class="col-md-8 col-sm-5" submitted">',
                    	'<input type="text" id="miscode" class="form-control" name="miscode" placeholder="MISCODE" ',
                        	'required ',
                        	'ng-model-options="{ debounce: 100 }" ',
                        	'ng-disabled="searching" ',
                        	'ng-model="miscode" ',
                        '> ',
                    '</div>',
                '</div>',
                '<div class="form-group row">',
                	'<label for="miscode" class="col-md-4 col-sm-3 control-label form-inline-first-label"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i>College Description</label>',
                	'<div class="col-md-8 col-sm-5" submitted">',
                		'<input type="text" id="description" class="form-control" name="description" placeholder="DESCRIPTION" ',
                		'required ',
                		'ng-model-options="{ debounce: 100 }" ',
                		'ng-disabled="searching" ',
                		'ng-model="description" ',
                	 '> ',
                '</div>',
                '</div>',
                    '<button class="ccc-on-board-college-button btn btn-default" ng-click="doOnboardCollege()">Onboard College</button>',
                '</form>',
            ].join('')

        };

    });

})();




