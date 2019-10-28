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

    angular.module('CCC.View.Dashboard').directive('cccMicroServiceProperties', function () {

        return {

            restrict: 'E',

            scope: {
                targetGroupName: '=',
                microServiceProperties: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    var transformedResults;

                    var MAX_RESULTS = Infinity;

                    var HIGHLIGHT_OPTIONS = {className: "search-results-highlight"};

                    var searchResultsContainer = $element.find('tbody');


                    /*============ MODEL =============*/

                    $scope.searchFilter = "";

                    $scope.filteredResults = [];

                    $scope.totalResults = $scope.microServiceProperties.length;


                    /*============ MODEL DEPENDENT METHODS =============*/

                    var setupData = function () {

                        transformedResults = _.map($scope.microServiceProperties, function (microServiceProperty) {

                            var microServicePropertySplit = microServiceProperty.split(":");

                            return {
                                key: $.trim(microServicePropertySplit[0]),
                                value: $.trim(microServicePropertySplit[1]),
                                searchString: ($.trim(microServicePropertySplit[0]) + " - " + $.trim(microServicePropertySplit[1])).toLowerCase()
                            };
                        });
                    };

                    var matchRank; // synchronous so don't need to worry about making this private within the function, faster to declare here
                    var getMatchRank = function (item, searchTokens) {

                        matchRank = 0;
                        _.each(searchTokens, function (searchToken) {
                            if (item.searchString.indexOf(searchToken) !== -1) {
                                matchRank++;
                            }
                        });

                        return matchRank;
                    };

                    var filterItemsBySearch = function (itemList, searchTokens) {

                        var results = [];

                        var matchRank;
                        _.each(itemList, function (item) {

                            matchRank = getMatchRank(item, searchTokens);

                            if (matchRank) {
                                results.push({
                                    rank: matchRank,
                                    item: item
                                });
                            }
                        });

                        // sort by rank then by timestamp
                        results.sort(function (a, b) {
                            return b.rank - a.rank;
                        });

                        // strip off just the item is returned
                        return _.map(results, function (result) {
                            return result.item;
                        });
                    };

                    var updateFilteredResults = function () {

                        var searchTokens = ($scope.searchFilter.toLowerCase()).split(' ');

                        searchResultsContainer.unhighlight(HIGHLIGHT_OPTIONS);

                        if ($.trim($scope.searchFilter)) {
                            $scope.filteredResults = filterItemsBySearch(transformedResults, searchTokens).slice(0, MAX_RESULTS);
                        } else {
                            $scope.filteredResults = transformedResults.slice(0, MAX_RESULTS);
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

                '<h2>Micro Service: <span class="ccc-micro-service-properties-name">{{::targetGroupName}}</span></h2>',

                '<div class="ccc-dashboard-search">',
                    '<label for="ccc-dashboard-search-input" class="sr-only">Filter Properties By Property or Value</label>',
                    '<div class="input-group">',
                        '<span class="input-group-addon"><i class="fa fa-search" aria-hidden="true"></i></span>',
                        '<input id="ccc-dashboard-search-input" type="text" class="form-control" placeholder="Filter by Property or Value" ng-model="searchFilter">',
                    '</div>',
                '</div>',

                '<div class="ccc-dashboard-search-results">',

                    '<div class="ccc-dashboard-search-results-meta">',
                        '<div class="ccc-dashboard-search-results-warning" ng-if="!filteredResults.length"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> No results found. Try changing your filter.</div>',
                        '<div ng-if="filteredResults.length">Showing {{filteredResults.length}} of {{::totalResults}} results.</div>',
                    '</div>',

                    '<table class="table table-striped">',
                        '<thead>',
                            '<tr>',
                                '<th width="50%">Property</th>',
                                '<th>Value</th>',
                            '</tr>',
                        '</thead>',
                        '<tbody>',
                            '<tr ng-repeat="item in filteredResults track by item.key">',
                                '<td>{{::item.key}}</td>',
                                '<td>{{::item.value}}</td>',
                            '</tr>',
                        '</tbody>',
                    '</table>',

                '</div>'

            ].join('')

        };

    });

})();













