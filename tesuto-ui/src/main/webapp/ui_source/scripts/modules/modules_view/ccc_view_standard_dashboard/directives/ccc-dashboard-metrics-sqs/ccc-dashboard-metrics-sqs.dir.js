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

    angular.module('CCC.View.Dashboard').directive('cccDashboardMetricsSqs', function () {

        return {

            restrict: 'E',

            scope: {
                rawMetrics: "="
            },

            controller: [

                '$scope',
                'MetricsGraphService',

                function ($scope, MetricsGraphService) {


                    /*============= PRIVATE VARIABLES AND METHODS =========*/

                    var currentOption = false;

                    var getQueueName = function (metric) {
                        return _.find(metric.Dimensions, function (dimension) {
                            return dimension.Name === "QueueName";
                        }).Value;
                    };

                    var getSQSMetricsGroups = function (metrics) {

                        var groups = _.groupBy(metrics, function(metric) {
                            return getQueueName(metric);
                        });

                        return _.map(groups, function (group) {
                            return {
                                id: getQueueName(group[0]),
                                data: group
                            };
                        });
                    };


                    /*============= CHART TYPE RENDERERS ============*/

                    var renderMessageCountChart = function (messageData) {

                        MetricsGraphService.renderChart("messages", [{key: "Messages sent to queue", data: messageData, statistic: "Sum"}], {
                            bindto: '#chart-sqs-messages',
                            color: {pattern: ['#28a745']}
                        });
                    };

                    var renderVisibleMessagesChart = function (messageData) {

                        MetricsGraphService.renderChart("visibleMessages", [{key: "Messages in queue", data: messageData, statistic: "Sum"}], {
                            bindto: '#chart-sqs-visible-messages',
                            color: {pattern: ['#28a745']}
                        });
                    };

                    var renderMessageAgeChart = function (messageData) {

                        MetricsGraphService.renderChart("messageAge", [{key: "Oldest Msg Age (Max)", data: messageData, statistic: "Maximum"}, {key: "Msg Age (Avg)", data: messageData, statistic: "Average"}], {
                            bindto: '#chart-sqs-message-age',
                            color: {pattern: ['#ffc107', '#28a745']}
                        });
                    };


                    /*============= MODEL ============*/

                    $scope.options = [];


                    /*============= INITIALIZATION HELPER METHODS ============*/

                    var setupOptions = function () {

                        var metricGroups = getSQSMetricsGroups($scope.rawMetrics);

                        $scope.options = _.map(metricGroups, function (metricGroup) {

                            var metricGroupId = metricGroup.id;

                            return {
                                id: metricGroupId,
                                name: metricGroupId,
                                data: metricGroup.data
                            };
                        });

                        currentOption = $scope.options[0];
                    };

                    var renderCharts = function (metrics) {

                        renderMessageCountChart(MetricsGraphService.getDataByLabel(metrics, "NumberOfMessagesSent"));
                        renderVisibleMessagesChart(MetricsGraphService.getDataByLabel(metrics, "ApproximateNumberOfMessagesVisible"));
                        renderMessageAgeChart(MetricsGraphService.getDataByLabel(metrics, "ApproximateAgeOfOldestMessage"));
                    };

                    var updateCharts = function () {
                        if (currentOption && currentOption.data) {
                            renderCharts(currentOption.data);
                        }
                    };

                    var initialize = function () {
                        setupOptions();
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

                    /*============ LISTENERS ============*/

                    $scope.$on('$destroy', function () {
                        MetricsGraphService.destroyChart(["messages", "visibleMessages", "messageAge"]);
                    });


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="panel-content-dropdown">',

                    '<span id="sqs-dropdown-label" class="dropdown-label" role="label">Select Queue</span>',

                    '<ccc-item-dropdown ',
                        'id="sqsChoice" ',
                        'initial-items="options" ',
                        'initial-item-id="options[0].id" ',
                        'get-item-id="getOptionId" ',
                        'get-item-name="getOptionName" ',
                        'aria-labelledby="sqs-dropdown-label" ',
                    '></ccc-item-dropdown>',
                '</div>',

                '<div class="panel-content">',

                    '<div class="row">',
                        '<div class="col-md-4">',
                            '<h3 class="metrics-chart-label">Messages Sent to Queue</h3>',
                            '<div id="chart-sqs-messages" class="metrics-chart-short"></div>',
                        '</div>',
                        '<div class="col-md-4">',
                            '<h3 class="metrics-chart-label">Messages in the Queue</h3>',
                            '<div id="chart-sqs-visible-messages" class="metrics-chart-short"></div>',
                        '</div>',
                        '<div class="col-md-4">',
                            '<h3 class="metrics-chart-label">Message Age (Avg and Max)</h3>',
                            '<div id="chart-sqs-message-age" class="metrics-chart-short"></div>',
                        '</div>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();






