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

    angular.module('CCC.View.Home').directive('cccCccidSearch', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'StudentsAPIService',

                function ($scope, StudentsAPIService) {


                    /*============ PRIVATE VARIABLES AND METHODS =============*/


                    /*============ MODEL =============*/

                    $scope.searching = false;
                    $scope.cccId = '';


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var doSearch = function () {

                        StudentsAPIService.studentSearch({cccId: $scope.cccId}).then(function (students) {

                            $scope.students = students;

                            if ($scope.students.length > 0) {

                                $scope.$emit('ccc-cccid-search.studentFound', $scope.students[0]);

                            } else {

                                $scope.$emit('ccc-cccid-search.studentNotFound', $scope.cccId);
                            }

                        }, function (err) {

                            $scope.$emit('ccc-cccid-search.error', err);

                        }).finally(function () {

                            $scope.searching = false;
                            $scope.searchComplete = true;
                            $scope.cccId = '';

                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.doSearch = doSearch;

                    $scope.inputKeyUp = function ($event) {
                        if ($event.which === 13) {
                            doSearch();
                        }
                    };

                    $scope.doAdvancedSearch = function (e) {
                        e.preventDefault();
                        $scope.$emit('ccc-cccid-search.advancedSearchClicked');
                    };

                }
            ],

            template: [
                '<form novalidate autocomplete="off">',
                    '<span class="input-group">',
                        '<label id="cccid-search-label" class="input-group-addon"><i aria-hidden="true" class="fa fa-search"></i><span class="sr-only">student search</span></label>',
                        '<input class="form-control" type="text" id="cccid-search" aria-labelledby="cccid-search-label" ng-model="cccId" placeholder="Student ID" ng-keyup="inputKeyUp($event)"/>',
                    '</span>',
                    '<button class="ccc-cccid-search-button btn btn-default" ng-click="doSearch()">Search</button>',
                '</form>',
                '<a class="advanced-search-button" role="button" href="#" ng-click="doAdvancedSearch($event)">Advanced Search</a>',
            ].join('')

        };

    });

})();




