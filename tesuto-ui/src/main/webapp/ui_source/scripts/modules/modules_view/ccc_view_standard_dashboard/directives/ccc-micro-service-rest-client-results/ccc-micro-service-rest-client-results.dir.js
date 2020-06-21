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

    angular.module('CCC.View.Dashboard').directive('cccMicroServiceRestClientResults', function () {

        return {

            restrict: 'E',

            scope: {
                targetGroupName: '=',
                microServiceRestClientResults: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$window',

                function ($scope, $element, $timeout, $window) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    var transformedResults;

                    var MAX_RESULTS = Infinity;

                    var HIGHLIGHT_OPTIONS = {className: "search-results-highlight"};

                    var searchResultsContainer = $element.find('tbody');


                    /*============ MODEL =============*/

                    $scope.searchFilter = "";

                    $scope.filteredResults = [];

                    $scope.totalResults = $scope.microServiceRestClientResults.length;


                    /*============ MODEL DEPENDENT METHODS =============*/

                    var setupData = function () {
                    	var id_index = 0;
                        transformedResults = _.map($scope.microServiceRestClientResults, function (restClientResults) {

                        	id_index++;
                            return {
                            	id:id_index,
                                client: $.trim(restClientResults.className),
                                method: $.trim(restClientResults.methodName),
                                status: $.trim(restClientResults.httpStatus),
                                url: $.trim(restClientResults.endpoint),
                                call: $.trim(restClientResults.httpCallMethod),
                                duration: $.trim(restClientResults.elapsedTime),
                                message: $.trim(restClientResults.message),
                                results: $.trim(restClientResults.resultBody),
                                isCalled: restClientResults.isCalledByMicroservice,
                                searchString: ($.trim(restClientResults.className) + " - " + $.trim(restClientResults.httpCallMethod)).toLowerCase()
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

                        // sort by item client
                        results.sort(function (a, b) {
                            return b.item.client - a.item.client;
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
                    
                    $scope.showMessage = function (message) {
                    	$window.alert(message);
                    };


                    /*============ INITIALIZATION =============*/

                    initialize();
                }
            ],

            template: [

                '<h2>Micro Service: <span class="ccc-micro-service-rest-client-results-name">{{::targetGroupName}}</span></h2>',

                '<div class="ccc-dashboard-search">',
                    '<label for="ccc-dashboard-search-input" class="sr-only">Filter Properties By Rest Client or Http Call Method</label>',
                    '<div class="input-group">',
                        '<span class="input-group-addon"><i class="fa fa-search" aria-hidden="true"></i></span>',
                        '<input id="ccc-dashboard-search-input" type="text" class="form-control" placeholder="Filter by Rest Client or Call Method" ng-model="searchFilter">',
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
                            	'<th>Index</th>',
                                '<th width="10%">Rest Client</th>',
                                '<th>Java Method</th>',
                                '<th>Status</th>',
                                '<th>URL</th>',
                                '<th>Calling Method</th>',
                                '<th>Duration (s)</th>',
                                '<th>Message</th>',
                                '<th>Results</th>',
                            '</tr>',
                        '</thead>',
                        '<tbody>',
                            '<tr ng-repeat="item in filteredResults track by item.id" ng-if="item.isCalled">>',
                            	'<td>{{::item.id}}</td>',
                                '<td>{{::item.client}}</td>',
                                '<td>{{::item.method}}</td>',
                                '<td>{{::item.status}}</td>',
                                '<td>{{::item.url}}</td>',
                                '<td>{{::item.call}}</td>',
                                '<td>{{::item.duration}}</td>',
                                '<td ng-click="showMessage(item.message)" ccc-focusable><i class="fa fa-info-circle" role="button"><span class="sr-only"></span></i>{{::item.message | limitTo: 20}}</td>',
                                '<td ng-click="showMessage(item.results)" ccc-focusable><i class="fa fa-info-circle" role="button"><span class="sr-only"></span></i>{{::item.results | limitTo: 20}}</td>',
                            '</tr>',
                        '</tbody>',
                    '</table>',

                '</div>'

            ].join('')

        };

    });

})();













