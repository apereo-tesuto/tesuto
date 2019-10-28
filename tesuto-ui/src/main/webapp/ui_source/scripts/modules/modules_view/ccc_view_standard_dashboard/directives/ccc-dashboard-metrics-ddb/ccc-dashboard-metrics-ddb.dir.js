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
var d3 = d3 || {};
var c3 = c3 || {};
var $ = $ || {};

(function () {

    angular.module('CCC.View.Dashboard').directive('cccDashboardMetricsDdb', function () {

        return {

            restrict: 'E',

            scope: {
                rawMetrics: "="
            },

            controller: [

                '$scope',
                'MetricsGraphService',

                function ($scope, MetricsGraphService) {

                    /*============= CHART TYPE RENDERERS ============*/

                    var renderSuccessfulLatencyChart = function (latencyData) {

                        MetricsGraphService.renderChart("successLatency", [{key: "Request Latency", data: latencyData, statistic: "Average"}], {
                            bindto: '#chart-ddb-latency',
                            color: {pattern: ['#28a745']},
                            tooltip: {
                                format: {
                                    title: function (d) { return d; },
                                    value: function (v) { return Math.round(v) + 'ms'; }
                                }
                            }
                        });
                    };


                    /*============= INITIALIZATION HELPER METHODS ============*/

                    var renderCharts = function () {
                        renderSuccessfulLatencyChart(MetricsGraphService.getDataByLabel($scope.rawMetrics, "SuccessfulRequestLatency"));
                    };

                    var initialize = function () {
                        renderCharts();
                    };


                    /*============ BEHAVIOR ============*/


                    /*============ LISTENERS ============*/

                    $scope.$on('$destroy', function () {
                        MetricsGraphService.destroyChart("successLatency");
                    });

                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-md-12">',
                        '<h3 class="metrics-chart-label">Request Latency</h3>',
                        '<div id="chart-ddb-latency" class="metrics-chart-short"></div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();






