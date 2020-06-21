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
var d3 = d3 || {};

(function () {

    angular.module('CCC.ActivityMonitor').directive('cccActivityMonitor', [function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*=============== PRIVATE VARIABLES AND METHODS ============*/

                    var updateGraph;
                    var activities = [];

                    var maxBarWidth = 40;
                    var barWidth = 0;
                    var spaceWidth = 0;
                    var totalHeight = 0;

                    var maxHeightPercent = 0.3;

                    var groupSpacePercent = 0.01;

                    var animTime = 400;

                    var groupNum;

                    var currentActivityServiceString = '';

                    var lengthInUtf8Bytes = function (str) {
                        // Matches only the 10.. bytes that are non-initial characters in a multi-byte sequence.
                        var m = encodeURIComponent(str).match(/%[89ABab]/g);
                        return str.length + (m ? m.length : 0);
                    };

                    var getActivityClass = function (activity) {

                        if (activity.eName.indexOf('player.tool') !== -1) {
                            return 'tool';
                        } else if (activity.eName.indexOf('error') !== -1) {
                            return 'error';
                        } else if (activity.eName.indexOf('app') !== -1) {
                            return 'application';
                        } else if (activity.eName.indexOf('player.navigation') !== -1) {
                            return 'navigation';
                        } else if (activity.eName.indexOf('player.interaction') !== -1) {
                            return 'interaction';
                        } else if (activity.eName.indexOf('player.navigation') !== -1) {
                            return 'navigation';
                        } else {
                            return false;
                        }
                    };

                    var updateActivities = function (activityServiceData) {

                        // store a copy of the string to display when needed for analysis of size
                        currentActivityServiceString = JSON.stringify(activityServiceData);

                        // reparse the json to avoid dirtying up the model that was sent here
                        activityServiceData = JSON.parse(currentActivityServiceString);

                        groupNum = 0;
                        activities = [];

                        // combine all persisted and unpersisted activities into one array and flag them accordingly
                        _.each(activityServiceData.persisted, function (persistedActivity) {
                            persistedActivity.persisted = true;
                            activities.push(persistedActivity);
                        });

                        _.each(activityServiceData.ready, function (readyActivity) {
                            readyActivity.persisted = false;
                            activities.push(readyActivity);
                        });

                        // loop through each of the activities, assign a group and id based of currentTaskId and class
                        var currentTaskId = false;
                        _.each(activities, function (activity) {
                            if (activity.ctId !== currentTaskId) {
                                groupNum++;
                                currentTaskId = activity.ctId;
                            }
                            activity.groupNum = groupNum;
                            activity.graphId = activity.groupNum + '-' + activity.eName;
                            activity.graphClass = getActivityClass(activity);
                        });

                        // loop through them and combine activities within groups that are the same
                        var groupActivityMap = {};
                        var activitiesReduced = [];

                        var currentIndex = 0;

                        _.each(activities, function (activity) {

                            if (!groupActivityMap[activity.graphId]) {

                                groupActivityMap[activity.graphId] = activity;

                                activity.count = 1;
                                activity.graphIndex = currentIndex;
                                currentIndex++;

                                activitiesReduced.push(activity);

                            } else {

                                groupActivityMap[activity.graphId].persisted = groupActivityMap[activity.graphId].persisted && activity.persisted;
                                groupActivityMap[activity.graphId].count++;
                            }
                        });

                        activities = activitiesReduced;
                    };

                    var updateDimensions = function () {

                        var totalWidth = $element.width();
                        spaceWidth = totalWidth * groupSpacePercent;

                        barWidth = Math.min(maxBarWidth, (totalWidth - (spaceWidth * groupNum)) / activities.length);
                        totalHeight = $element.height();
                    };


                    /*============ THE MAIN RENDER METHOD WHEN THINGS CHANGE ================*/

                    updateGraph = function () {

                        updateDimensions();

                        var svg = d3.select($scope.svg);
                        var barsArea = svg.select('.bars');

                        var maxBarHeight = totalHeight * maxHeightPercent;

                        var maxCount = d3.max(activities, function (d) {
                            return d.count;
                        });

                        /*============ REPORTED ACTIVITY ==============*/

                        var activityBars = barsArea.selectAll('g.activity')
                            .data(activities, function (d) {
                                return d.graphId;
                            });

                        var activityBarsEnter = activityBars.enter().append('g')
                            .attr('class', 'activity')
                            .attr('transform', function (d) {
                                var x = d.graphIndex * barWidth + d.groupNum * spaceWidth + barWidth / 2;
                                return 'translate(' + x + ',' + totalHeight + ')';
                            });

                        activityBarsEnter
                            .append('text')
                                .html(function (d) {
                                    return d.eName;
                                })
                                .attr("y", barWidth / 2)
                                .attr("x", 12)
                                .attr("dy", 3.5)
                                .attr("font-size", "14px")
                                .attr("fill", 'rgba(0, 0, 0, 0.35)')
                                .attr("transform", "rotate(-90)")
                                .style("text-anchor", "start");

                        activityBarsEnter
                            .append('rect')
                                .attr('class', function (d) {
                                    var typeClass = d.graphClass ? 'ccc-activity-class-' + d.graphClass : '';
                                    return typeClass + ' ' + (!d.persisted ? 'ccc-activity-not-persisted' : '');
                                })
                                .attr('width', barWidth)
                                .attr('height', 0);

                        var activityBarsUpdate = activityBars.transition().duration(animTime);
                        activityBarsUpdate
                            .attr('transform', function (d) {
                                var x = d.graphIndex * barWidth + d.groupNum * spaceWidth;
                                var y = totalHeight - ((d.count / maxCount) * maxBarHeight);
                                return 'translate(' + x + ',' + y + ')';
                            });

                        activityBarsUpdate.select('text')
                            .attr("y", barWidth / 2);

                        activityBarsUpdate.select('rect')
                            .attr('class', function (d) {
                                var typeClass = d.graphClass ? 'ccc-activity-class-' + d.graphClass : '';
                                return typeClass + ' ' + (!d.persisted ? 'ccc-activity-not-persisted' : '');
                            })
                            .attr('width', barWidth)
                            .attr('height', function (d) {
                                return (d.count / maxCount) * maxBarHeight;
                            });
                    };


                    /*============ MODEL ============*/

                    $scope.graphVisible = false;

                    $scope.svg = $element.find('svg')[0];


                    /*=============== MODEL DEPENDENT METHODS ============*/


                    /*=============== BEHAVIOR ============*/

                    $scope.toggleGraph = function () {
                        $scope.graphVisible = !$scope.graphVisible;
                        if ($scope.graphVisible) {
                            console.log("KB:", lengthInUtf8Bytes(currentActivityServiceString) * 0.001, currentActivityServiceString);
                        }
                    };


                    /*=============== LISTENERS ==============*/

                    $scope.$on('ccc-activity-monitor.updateData', function (e, activityServiceData) {
                        updateActivities(activityServiceData);
                        updateGraph();
                    });

                    $scope.$on('ccc-assess.resizeDebounced', function () {
                        updateGraph();
                    });


                    /*=============== INITIALIZATION ==============*/

                }
            ],

            template: [

                '<span class="ccc-activity-monitor-button" ng-click="toggleGraph()" ng-class="{active: graphVisible}">',
                    '<i class="fa fa-bar-chart"></i>',
                '</span>',

                '<svg class="ccc-activity-monitor-graph" ng-class="{\'ccc-activity-monitor-visible\': graphVisible}">',
                    '<g class="bars"></g>',
                '</svg>'

            ].join('')
        };
    }]);

})();
