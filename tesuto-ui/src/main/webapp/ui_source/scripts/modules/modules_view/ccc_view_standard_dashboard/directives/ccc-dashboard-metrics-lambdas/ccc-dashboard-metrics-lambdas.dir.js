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


(function () {

    angular.module('CCC.View.Dashboard').directive('cccDashboardMetricsLambdas', function () {

        return {

            restrict: 'E',

            scope: {
                rawMetrics: "="
            },

            controller: [

                '$scope',
                'MetricsGraphService',

                function ($scope, MetricsGraphService) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    var currentOption = false;

                    var getLambdaFunctionData = function (metrics) {
                        return _.groupBy(metrics, function(metric) {

                            var functionNameDimension = _.find(metric.Dimensions, function (dimension) {
                                return dimension.Name === 'FunctionName';
                            });

                            return functionNameDimension.Value;
                        });
                    };


                    /*============ MODEL =============*/

                    $scope.options = [];


                    /*============= CHART TYPE RENDERERS ============*/

                    var renderInvocationsChart = function (invocationRawData) {

                        MetricsGraphService.renderChart("invocation", [{data: invocationRawData, statistic: "Sum", key: "Invocations"}], {
                            bindto: '#chart-lambdas-invocations',
                            color: {
                                pattern: ['#28a745']
                            }
                        });
                    };

                    var renderDurationsChart = function (durationRawData) {

                        MetricsGraphService.renderChart("duration", [{data: durationRawData, statistic: "Average", key: "Average Duration"}], {
                            bindto: '#chart-lambdas-duration',
                            color: {pattern: ['#337ab7']},
                            tooltip: {
                                format: {
                                    title: function (d) { return d; },
                                    value: function (v) { return Math.round(v) + 'ms'; }
                                }
                            }
                        });
                    };

                    var renderErrorsChart = function (errorRawData) {

                        MetricsGraphService.renderChart("errors", [{data: errorRawData, statistic: "Sum", key: "Errors"}], {
                            bindto: '#chart-lambdas-errors',
                            color: {pattern: ['#dc3545']}
                        });
                    };

                    var renderThrottlesChart = function (throttleRawData) {

                        MetricsGraphService.renderChart("throttles", [{data: throttleRawData, statistic: "Sum", key: "Throttles"}], {
                            bindto: '#chart-lambdas-throttles',
                            color: {pattern: ['#ffc107']}
                        });
                    };


                    /*============= INITIALIZATION HELPER METHODS ============*/

                    var setupOptions = function (metrics) {

                        var lambdaFunctionDataList = getLambdaFunctionData(metrics);

                        $scope.options = _.map(lambdaFunctionDataList, function (lambdaFunctionData) {

                            var functionNameDimension = _.find(lambdaFunctionData[0].Dimensions, function (dimension) {
                                return dimension.Name === 'FunctionName';
                            });

                            return {
                                id: functionNameDimension.Value,
                                name: functionNameDimension.Value,
                                data: lambdaFunctionData
                            };
                        });

                        currentOption = $scope.options[0];
                    };

                    var renderCharts = function (lambdaMetrics) {
                        renderInvocationsChart(MetricsGraphService.getDataByLabel(lambdaMetrics, "Invocations"));
                        renderDurationsChart(MetricsGraphService.getDataByLabel(lambdaMetrics, 'Duration'));
                        renderErrorsChart(MetricsGraphService.getDataByLabel(lambdaMetrics, "Errors"));
                        renderThrottlesChart(MetricsGraphService.getDataByLabel(lambdaMetrics, "Throttles"));
                    };

                    var updateCharts = function () {
                        if (currentOption && currentOption.data) {
                            renderCharts(currentOption.data);
                        }
                    };

                    var initialize = function () {
                        setupOptions($scope.rawMetrics);
                        updateCharts();
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.getOptionId = function (option) {
                        return option.id;
                    };
                    $scope.getOptionName = function (option) {
                        return option.id;
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-item-dropdown.itemSelected', function (e, dropdownId, itemId, item) {
                        currentOption = item;
                        updateCharts();
                    });

                    $scope.$on('$destroy', function () {
                        MetricsGraphService.destroyChart(["invocation", "duration", "errors", "throttles"]);
                    });


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="panel-content-dropdown">',

                    '<span id="lambda-dropdown-label" class="dropdown-label" role="label">Select Lambda</span>',

                    '<ccc-item-dropdown ',
                        'id="lambdaChoice" ',
                        'initial-items="options" ',
                        'initial-item-id="options[0].id" ',
                        'get-item-id="getOptionId" ',
                        'get-item-name="getOptionName" ',
                        'aria-labelledby="lambda-dropdown-label" ',
                    '></ccc-item-dropdown>',
                '</div>',

                '<div class="panel-content">',

                    '<div class="row">',
                        '<div class="col-md-6">',
                            '<h3 class="metrics-chart-label">Invocations</h3>',
                            '<div id="chart-lambdas-invocations" class="metrics-chart"></div>',
                        '</div>',
                        '<div class="col-md-6">',
                            '<h3 class="metrics-chart-label">Errors</h3>',
                            '<div id="chart-lambdas-errors" class="metrics-chart"></div>',
                        '</div>',
                    '</div>',


                    '<div class="row">',
                        '<div class="col-md-6">',
                            '<h3 class="metrics-chart-label">Duration</h3>',
                            '<div id="chart-lambdas-duration" class="metrics-chart"></div>',
                        '</div>',
                        '<div class="col-md-6">',
                            '<h3 class="metrics-chart-label">Throttles</h3>',
                            '<div id="chart-lambdas-throttles" class="metrics-chart"></div>',
                        '</div>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();






