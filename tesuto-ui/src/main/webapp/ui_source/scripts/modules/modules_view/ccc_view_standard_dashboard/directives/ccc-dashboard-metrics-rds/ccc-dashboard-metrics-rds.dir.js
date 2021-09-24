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

    angular.module('CCC.View.Dashboard').directive('cccDashboardMetricsRds', function () {

        return {

            restrict: 'E',

            scope: {
                rawMetrics: "="
            },

            controller: [

                '$scope',
                'MetricsGraphService',

                function ($scope, MetricsGraphService) {

                    /*============= PRIVATE VARIABLES AND METHODS ============*/

                    var latencyChart;


                    /*============= CHART TYPE RENDERERS ============*/

                    var renderLatencyChart = function (readData, writeData) {

                        latencyChart = MetricsGraphService.renderChart("latency", [{key: "Read", data: readData, statistic: "Average"}, {key: "Write", data: writeData, statistic: "Average"}], {
                            bindto: '#chart-rds-read-write',
                            color: {pattern: ['#28a745', '#337ab7']},
                            axis: {
                                y: {
                                    tick: {
                                        fit: true,
                                        count: 4,
                                        format: function (y) {
                                            if (y >= 0 ) {
                                                return Math.round(y * 1000) + 'ms';
                                            } else {
                                                return "";
                                            }
                                        }
                                    }
                                }
                            },
                            tooltip: {
                                format: {
                                    title: function (d) { return d; },
                                    value: function (v) { return (v * 1000).toFixed(2) + 'ms'; }
                                }
                            }
                        });
                    };

                    var renderCPUChart = function (cpuRawData) {

                        MetricsGraphService.renderChart("cpu", [{data: cpuRawData, statistic: "Average", key: "CPU"}], {
                            bindto: '#chart-rds-cpu',
                            color: {pattern: ['#ffc107']},
                            axis: {
                                y: {
                                    max: 100,
                                    tick: {
                                        values: [0, 50, 100],
                                        format: function (y) {
                                            if (y >= 0 ) {
                                                return y + '%';
                                            } else {
                                                return "";
                                            }
                                        }
                                    }
                                }
                            },
                            tooltip: {
                                format: {
                                    title: function (d) { return d; },
                                    value: function (v) { return (v).toFixed(2) + '%'; }
                                }
                            }
                        });
                    };


                    /*============= INITIALIZATION HELPER METHODS ============*/

                    var renderCharts = function () {
                        renderLatencyChart(MetricsGraphService.getDataByLabel($scope.rawMetrics, "ReadLatency"), MetricsGraphService.getDataByLabel($scope.rawMetrics, "WriteLatency"));
                        renderCPUChart(MetricsGraphService.getDataByLabel($scope.rawMetrics, "CPUUtilization"));
                    };

                    var initialize = function () {
                        renderCharts();
                    };


                    /*============ BEHAVIOR ============*/


                    /*============ LISTENERS ===========*/

                    $scope.$on('$destroy', function () {
                        MetricsGraphService.destroyChart(["cpu", "latency"]);
                    });


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-md-6">',
                        '<h3 class="metrics-chart-label">Read & Write Latency</h3>',
                        '<div id="chart-rds-read-write" class="metrics-chart-short"></div>',
                    '</div>',
                    '<div class="col-md-6">',
                        '<h3 class="metrics-chart-label">CPU Utilization</h3>',
                        '<div id="chart-rds-cpu" class="metrics-chart-short"></div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();






