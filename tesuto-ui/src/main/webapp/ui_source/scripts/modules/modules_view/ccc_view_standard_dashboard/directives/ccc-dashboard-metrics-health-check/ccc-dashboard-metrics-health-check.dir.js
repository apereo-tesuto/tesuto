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

    angular.module('CCC.View.Dashboard').directive('cccDashboardMetricsHealthCheck', function () {

        return {

            restrict: 'E',

            scope: {
                healthCheckMetrics: "=",
                microServiceProperties: '=',
                microServicePropertyKeyToNodeMap: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    // a little dangerous but no real way around this for now
                    var MICROSERVICE_PROPERTY_KEY_TO_NODE_TARGET_GROUP_MAP = $scope.microServicePropertyKeyToNodeMap;

                    var processHealthCheckMetrics = function (metrics) {

                        // here we setup the array of expected health checks with a default of 0 nodes
                        var allHealthChecksLookup = {};
                        var allHealthChecks = _.map(MICROSERVICE_PROPERTY_KEY_TO_NODE_TARGET_GROUP_MAP, function (targetGroupKey, microServiceKey) {

                            var healthCheckObject = {
                                targetGroup: targetGroupKey,
                                totalNodes: 0,
                                microServiceKey: microServiceKey
                            };

                            allHealthChecksLookup[targetGroupKey] = healthCheckObject;

                            return healthCheckObject;
                        });

                        // then we loop through the actual metrics and fill in the details
                        _.each(metrics, function (metric) {

                            var targetGroup = metric.Dimensions.find(function (dimension) {
                                return dimension.Name === 'TargetGroup';
                            });

                            if (targetGroup) {
                                var targetGroupKey = (targetGroup.Value.split('/')[1]).replace(/TargetGroup/gi, '');
                                allHealthChecksLookup[targetGroupKey].totalNodes = metric.Datapoints[0] ? metric.Datapoints[0].Average : 0;
                            }

                        });

                        return _.sortBy(allHealthChecks, function (healthcheck) {
                            return healthcheck.targetGroup;
                        });
                    };
                    


                    /*============ MODEL =============*/

                    $scope.metrics = processHealthCheckMetrics($scope.healthCheckMetrics);


                    /*============ BEHAVIOR =============*/

                    $scope.openNodeTypeProperties = function (metric) {
                        $scope.$emit('ccc-dashboard-metrics-health-check.microServiceClicked', metric, $scope.microServiceProperties[metric.microServiceKey], $scope.microServicePropertyKeyToNodeMap[metric.microServiceKey]);
                    };

                }
            ],

            template: [

                '<table class="table">',
                    '<thead>',
                        '<tr>',
                            '<th>Node Type</th>',
                            '<th class="text-center">Node Count</th>',
                        '</tr>',
                    '</thead>',
                    '<tbody>',
                        '<tr ng-repeat="metric in metrics">',
                            '<td class="ccc-dashboard-metrics-health-check-title" ng-click="openNodeTypeProperties(metric)" ccc-focusable><i class="fa fa-info-circle" role="button"><span class="sr-only">View Properties</span></i>{{::metric.targetGroup}}</td>',
                            '<td class="text-center">{{::metric.totalNodes}}</td>',
                        '</tr>',
                    '</tbody>',
                '</table>'

            ].join('')

        };

    });

})();






