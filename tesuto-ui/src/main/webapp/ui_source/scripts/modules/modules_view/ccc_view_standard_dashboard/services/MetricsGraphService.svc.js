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

    /**
     * Wraps some common data extraction / transformation we use on the AWS stats
     * Also provides a graph renderer method that cen be reused
     */
    angular.module('CCC.View.Dashboard').service('MetricsGraphService', [

        function () {

            /*============ SERVICE DECLARATION ============*/

            var MetricsGraphService = {};


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var LABEL_COUNT_X = 5;
            var LABEL_COUNT_Y = 3;

            var DEFAULT_GRAPH_CONFIGS = {
                legend: {
                    show: false
                },
                padding: {right: 15},
                axis: {
                    x: {
                        type: 'timeseries',
                        tick: {
                            fit: true,
                            count: LABEL_COUNT_X,
                            format: function (x) {
                                var xDate = new moment(new Date(x));
                                return xDate.format("hA");
                            }
                        }
                    },
                    y: {
                        min: 0,
                        tick: {
                            fit: true,
                            count: LABEL_COUNT_Y,
                            format: function (y) {
                                if (y >= 0 ) {
                                    return Math.round(y);
                                } else {
                                    return "";
                                }
                            }
                        },
                        padding: {top: 10, bottom: 10}
                    }
                },
                tooltip: {
                    format: {
                        title: function (d) { return d; },
                        value: function (v) { return v; }
                    }
                }
            };

            /*============= DATA MANIPULATION HELPERS ============*/

            var fillInMissingDataPoints = function (startTime, endTime, period, data) {

                var filledInData = {x:[], y:[]};

                var isClose = function (p1, p2) {
                    return Math.abs(p2 - p1) < (period / 2);
                };

                for (var i = startTime; i <= endTime; i = i + period) {
                    if (isClose(data.x[0], i)) {

                        filledInData.x.push(data.x[0]);
                        filledInData.y.push(data.y[0]);

                        data.x.shift();
                        data.y.shift();

                    } else {
                        filledInData.x.push(new moment(new Date(i)).startOf('minute').valueOf());
                        filledInData.y.push(0);
                    }
                }

                return filledInData;
            };

            var getGraphData = function (rawData, statisticName) {

                if (!rawData) {
                    return [];
                }

                // sort the data points
                rawData.Datapoints = _.sortBy(rawData.Datapoints, function (dp) {
                    return new Date(dp.Timestamp).getTime();
                });

                // transform the data points into x and y coordinat lists
                var iData = _.reduce(rawData.Datapoints, function (memo, dataPoint) {

                    // we round to nearest minute in hopes that the chart will line up the same data points across functions, the hover works better
                    memo.x.push(new moment(new Date(dataPoint.Timestamp)).startOf('minute').valueOf());
                    memo.y.push(dataPoint[statisticName]);
                    return memo;

                }, {x: [], y: []});

                // figure out the data window and period for use in filling in missing data points
                var minX = new Date(rawData.StartTime).getTime();
                var maxX = new Date(rawData.EndTime).getTime();
                var period = rawData.Period;

                return fillInMissingDataPoints(minX, maxX, period * 1000, iData);
            };


            /*============= MAIN CHART DATA GATHERING AND RENDERING LOGIC ============*/

            var chartInstances = {};

            /**
             * @param {*} chartId
             * @param {*} dataSetConfigsList should take the form [{data: [], key: "string", graphConfigs: {}}], you can list as many as you want inside
             * @param {*} graphConfigs
             */
            var renderChart = function (chartId, dataSetConfigsList, graphConfigs) {

                var chartInstance = chartInstances[chartId];

                var chartData = {
                    types: {},
                    xs: {},
                    columns: []
                };

                _.each(dataSetConfigsList, function (dataSetConfigs) {

                    var rawData = dataSetConfigs.data;
                    var statistic = dataSetConfigs.statistic;
                    var chartKey = dataSetConfigs.key;

                    var finalGraphData = getGraphData(rawData, statistic);

                    chartData.types[chartKey] = 'area';
                    chartData.xs[chartKey] = chartKey + "x";
                    chartData.columns.push([chartData.xs[chartKey]].concat(finalGraphData.x));
                    chartData.columns.push([chartKey].concat(finalGraphData.y));
                });

                if (chartInstance) {

                    chartInstance.load({
                        columns: chartData.columns
                    });

                } else {

                    setTimeout(function () {
                        chartInstances[chartId] = c3.generate($.extend(true, {}, DEFAULT_GRAPH_CONFIGS, graphConfigs, {data: chartData}));
                    });
                }
            };


            /*============ SERVICE DEFINITION AND PUBLIC METHODS ============*/

            MetricsGraphService.renderChart = renderChart;

            MetricsGraphService.destroyChart = function (chartIds) {

                // lazy overloading, accepts one id or an array of ids
                chartIds = _.isString(chartIds) ? [chartIds] : chartIds;

                _.each(chartIds, function (chartId) {

                    var chartInstance = chartInstances[chartId];

                    if (chartInstance) {
                        delete chartInstances[chartId];
                        chartInstance.destroy();
                    }
                });
            };

            // helper to search through a list of AWS data for a particular label name
            MetricsGraphService.getDataByLabel = function (datums, labelName) {
                return _.find(datums, function (lambdaFunctionDataItem) {
                    return lambdaFunctionDataItem.Label === labelName;
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return MetricsGraphService;
        }
    ]);

})();
