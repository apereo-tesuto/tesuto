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

    angular.module('CCC.View.Dashboard').directive('cccDashboardMetricsRestClientStatus', function () {

        return {

            restrict: 'E',

            scope: {
                microServiceRestClientStatus: "=",
                microServiceRestClientResults: '=',
                microServicePropertyKeyToNodeMap: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    // a little dangerous but no real way around this for now
                    var MICROSERVICE_REST_CLIENT_KEY_TO_NODE_TARGET_GROUP_MAP = $scope.microServicePropertyKeyToNodeMap;

                    var processStatus = function(status) {
                    	return status.split("-SEPARATOR-").sort();
                    };
                    var processRestClientResultMetrics = function (restClients) {

                        // here we setup the array of expected health checks with a default of 0 nodes
                        var allRestClientsLookup = {};
                        var index = 0;
                        var allRestClients = _.map(MICROSERVICE_REST_CLIENT_KEY_TO_NODE_TARGET_GROUP_MAP, function (targetGroupKey, microServiceKey) {

                            var restClientObject = {
                                targetGroup: targetGroupKey,
                                status: processStatus($scope.microServiceRestClientStatus[microServiceKey], index),
                                microServiceKey: microServiceKey
                            };

                            allRestClientsLookup[targetGroupKey] = restClientObject;

                            return restClientObject;
                        });

                        return _.sortBy(allRestClients, function (restClient) {
                            return restClient.targetGroup;
                        });
                    };
                    
                    


                    /*============ MODEL =============*/

                    $scope.restClients = processRestClientResultMetrics($scope.microServiceRestClientStatus);


                    /*============ BEHAVIOR =============*/

                    $scope.openNodeTypeRestClientResults = function (restClient) {
                        $scope.$emit('ccc-dashboard-metrics-rest-client-status.restClientStatusClicked', restClient, $scope.microServiceRestClientResults[restClient.microServiceKey], $scope.microServicePropertyKeyToNodeMap[restClient.microServiceKey]);
                    };

                }
            ],

            template: [

                '<table class="table">',
                    '<thead>',
                        '<tr>',
                            '<th>Node Type</th>',
                            '<th class="text-center">Rest Clients Status</th>',
                        '</tr>',
                    '</thead>',
                    '<tbody>',
                        '<tr ng-repeat="restClient in restClients">',
                            '<td class="ccc-dashboard-metrics-rest-client-status-title" ng-click="openNodeTypeRestClientResults(restClient)" ccc-focusable><i class="fa fa-info-circle" role="button"><span class="sr-only">View Rest Client Status Results</span></i>{{::restClient.targetGroup}}</td>',
                            '<td>',
                            	'<table class="table">',
                        			'<tr ng-repeat="status in restClient.status track by $index" >',
                        				'<td class="text-left">{{::status}}</td>',
                        			'</tr>',
                            	'</table>',
                        '</tr>',
                    '</tbody>',
                '</table>'

            ].join('')

        };

    });

})();






