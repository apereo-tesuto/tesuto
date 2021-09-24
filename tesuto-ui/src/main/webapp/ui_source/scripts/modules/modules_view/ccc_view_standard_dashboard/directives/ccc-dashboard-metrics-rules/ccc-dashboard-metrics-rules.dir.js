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
var moment = moment || {};

(function () {

    angular.module('CCC.View.Dashboard').directive('cccDashboardMetricsRules', function () {

        return {

            restrict: 'E',

            scope: {
                rawMetrics: "="
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    var collegeRulesData;

                    var MAX_COLLEGE_RESULTS = 25;

                    var HIGHLIGHT_OPTIONS = {className: "search-results-highlight"};

                    var searchResultsContainer = $element.find('tbody');


                    /*============ MODEL =============*/

                    $scope.searchFilter = "";

                    $scope.filteredCollegeResults = [];

                    $scope.totalResults = $scope.rawMetrics.length;


                    /*============ MODEL DEPENDENT METHODS =============*/

                    var setupData = function () {

                        collegeRulesData = _.map($scope.rawMetrics, function (collegeData) {

                            collegeData.timestamp = parseFloat(collegeData.timestamp);

                            return {
                                id: collegeData.id,
                                cccMisCode: collegeData.cccMisCode,
                                description: collegeData.description,
                                timestamp: collegeData.timestamp,
                                formattedDate: new moment(new Date(collegeData.timestamp)).format("MMM Do YYYY, h a"),
                                fromTodayDate: new moment(new Date(collegeData.timestamp)).fromNow(),
                                searchString: (collegeData.cccMisCode + ":" + collegeData.description).toLocaleLowerCase()
                            };
                        });

                        collegeRulesData = _.sortBy(collegeRulesData, function (collegeData) {
                            return -1 * collegeData.timestamp;
                        });
                    };

                    var matchRank; // synchronous so don't need to worry about making this private within the function, faster to declare here
                    var getMatchRank = function (college, searchTokens) {

                        matchRank = 0;
                        _.each(searchTokens, function (searchToken) {
                            if (college.searchString.indexOf(searchToken) !== -1) {
                                matchRank++;
                            }
                        });

                        return matchRank;
                    };

                    var filterCollegeBySearch = function (collegeList, searchTokens) {

                        var results = [];

                        var matchRank;
                        _.each(collegeList, function (college) {

                            matchRank = getMatchRank(college, searchTokens);

                            if (matchRank) {
                                results.push({
                                    rank: matchRank,
                                    collegeData: college
                                });
                            }
                        });

                        // sort by rank then by timestamp
                        results.sort(function (a, b) {
                            if (a.rank === b.rank) {
                                return b.collegeData.timestamp - a.collegeData.timestamp;
                            } else {
                                return b.rank - a.rank;
                            }
                        });

                        // strip off just the college data to keep the same structure for display
                        return _.map(results, function (result) {
                            return result.collegeData;
                        });
                    };

                    var updateFilteredResults = function () {

                        var searchTokens = ($scope.searchFilter.toLowerCase()).split(' ');

                        searchResultsContainer.unhighlight(HIGHLIGHT_OPTIONS);

                        if ($.trim($scope.searchFilter)) {
                            $scope.filteredCollegeResults = filterCollegeBySearch(collegeRulesData, searchTokens).slice(0, MAX_COLLEGE_RESULTS);
                        } else {
                            $scope.filteredCollegeResults = collegeRulesData.slice(0, MAX_COLLEGE_RESULTS);
                        }

                        $timeout(function () {
                            searchResultsContainer.highlight(searchTokens, HIGHLIGHT_OPTIONS);
                        });
                    };

                    var debounced_updateFilteredResults = _.debounce(function () {

                        updateFilteredResults();
                        $scope.$apply();

                    }, 50);

                    var initialize = function () {
                        setupData();
                        updateFilteredResults();
                    };


                    /*============ LISTENERS =============*/

                    $scope.$watch('searchFilter', function (newValue, oldValue) {
                        if (oldValue !== newValue) {
                            debounced_updateFilteredResults();
                        }
                    });


                    /*============ INITIALIZATION =============*/

                    initialize();
                }
            ],

            template: [

                '<div class="ccc-dashboard-search">',
                    '<label for="ccc-dashboard-search-input" class="sr-only">Filter by college</label>',
                    '<div class="input-group">',
                        '<span class="input-group-addon"><i class="fa fa-search" aria-hidden="true"></i></span>',
                        '<input id="ccc-dashboard-search-input" type="text" class="form-control" placeholder="Filter by college" ng-model="searchFilter">',
                    '</div>',
                '</div>',

                '<div class="ccc-dashboard-search-results">',

                    '<div class="ccc-dashboard-search-results-meta">',
                        '<div class="ccc-dashboard-search-results-warning" ng-if="!filteredCollegeResults.length"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> No results found. Try changing your filter.</div>',
                        '<div ng-if="filteredCollegeResults.length">Showing {{filteredCollegeResults.length}} of {{::totalResults}} results.</div>',
                    '</div>',

                    '<table class="table table-striped">',
                        '<thead>',
                            '<tr>',
                                '<th>Date</th>',
                                '<th></th>',
                                '<th>College</th>',
                                '<th class="text-center">MIS Code</th>',
                            '</tr>',
                        '</thead>',
                        '<tbody>',
                            '<tr ng-repeat="college in filteredCollegeResults track by college.cccMisCode">',
                                '<td>{{::college.formattedDate}}</td>',
                                '<td>{{::college.fromTodayDate}}</td>',
                                '<td class="ccc-dashboard-search-results-title">{{::college.description}}</td>',
                                '<td class="text-center"><span class="badge">{{::college.cccMisCode}}</span></td>',
                            '</tr>',
                        '</tbody>',
                    '</table>',

                '</div>'

            ].join('')

        };

    });

})();






