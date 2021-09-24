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

    angular.module('CCC.View.Dashboard').directive('cccDashboardMetricsVersions', function () {

        return {

            restrict: 'E',

            scope: {
                rawMetrics: "="
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    var processBuildNumberMetrics = function (metrics) {

                        var allBuildNumbers = [];

                        _.each(metrics, function (metric) {

                            var instance = metric.Dimensions.find(function (dimension) {
                                return dimension.Name.toLowerCase() === 'instance'; // one day can likely switch everything to "Instance"
                            });

                            if (instance) {

                                allBuildNumbers.push({
                                    instance: instance.Value,
                                    buildNumber: metric.Datapoints[0] ? metric.Datapoints[0].Maximum : 'Unknown'
                                });
                            }

                        });

                        return _.sortBy(allBuildNumbers, function (buildNumb) {
                            return buildNumb.instance;
                        });
                    };


                    /*============ MODEL =============*/

                    $scope.metrics = processBuildNumberMetrics($scope.rawMetrics);

                }
            ],

            template: [

                '<table class="table">',
                    '<thead>',
                        '<tr>',
                            '<th>Instance</th>',
                            '<th class="text-center">Build Number</th>',
                        '</tr>',
                    '</thead>',
                    '<tbody>',
                        '<tr ng-repeat="metric in metrics">',
                            '<td class="ccc-dashboard-version-instance">{{::metric.instance}}</td>',
                            '<td class="ccc-dashboard-version-build text-center">{{::metric.buildNumber}}</td>',
                        '</tr>',
                    '</tbody>',
                '</table>'

            ].join('')

        };

    });

})();






