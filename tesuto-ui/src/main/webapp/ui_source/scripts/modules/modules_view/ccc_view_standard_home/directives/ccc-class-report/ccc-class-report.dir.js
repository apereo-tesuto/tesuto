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

    angular.module('CCC.View.Home').directive('cccClassReport', function () {

        return {

            restrict: 'E',

            scope: {
                report: '='
            },

            controller: [

                '$scope',
                '$element',
                '$filter',
                'CurrentUserService',
                'Moment',

                function ($scope, $element, $filter, CurrentUserService, Moment) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    // Creating a new temporary locale in order to display singular times in a non-standard format. We reset it back to english once the report renders.

                    Moment.locale('ccc-class-report', {
                        relativeTime : {
                            future: "in %s",
                            past:   "%s ago",
                            s  : 'a few seconds',
                            ss : '%d seconds',
                            m:  "1 minute",
                            mm: "%d minutes",
                            h:  "1 hour",
                            hh: "%d hours",
                            d:  "1 day",
                            dd: "%d days",
                            M:  "1 month",
                            MM: "%d months",
                            y:  "1 year",
                            yy: "%d years"
                        }
                    });


                    /*============ MODEL ==============*/

                    $scope.currentUser = CurrentUserService.getUser().displayName;

                    $scope.date = $filter('date')(new Date(), 'MMMM d, yyyy');

                    var roundPercentagesTo100 = function (numberList) {

                        // Based off https://stackoverflow.com/questions/13483430/how-to-make-rounded-percentages-add-up-to-100

                        var off = 100 - _.reduce(numberList, function(acc, x) {
                            return acc + Math.round(x.value);
                        }, 0);

                        return _.chain(numberList).sortBy(function(x) {
                            return Math.round(x.value) - x.value;
                        }).map(function(x, i) {
                            return {
                                key: x.key,
                                value: Math.round(x.value) + (off > i) - (i >= (numberList.length + off))
                            };
                        }).value();
                    };

                    var parseOverallPerformanceData = function (performanceData) {

                        var POSITION_MAP = {
                            'Novice': 1,
                            'Below Intermediate': 2,
                            'Intermediate': 3,
                            'Above Intermediate': 4,
                            'Advanced': 5
                        };

                        var parsedData = {
                            position: null, // 1-5
                            results: {
                                competenciesForMap: {
                                    performance: null // Novice, Below Intermediate, etc...
                                }
                            },
                            percents: { // Corresponds to POSITION_MAP
                                1: null,
                                2: null,
                                3: null,
                                4: null,
                                5: null
                            }
                        };

                        var percentageList = [];

                        _.each(performanceData, function (data, key) {

                            // Sets a performance text description for screen readers
                            if (data.highlighted) {
                                parsedData.results.competenciesForMap.performance = key;
                            }

                            // Populate a list of percentages for further rounding
                            if (key !== "uncounted") {
                                percentageList.push({
                                    key: key,
                                    value: (data.count / $scope.report.studentsAssessedCount) * 100
                                });
                            }
                        });

                        // Returns list of rounded percents that will total 100
                        percentageList = roundPercentagesTo100(percentageList);

                        // Populate our parsedData percents object
                        _.each(percentageList, function (item) {

                            parsedData.percents[POSITION_MAP[item.key]] = item.value;
                        });

                        // Set the overall performance bar highlight position
                        parsedData.position = POSITION_MAP[parsedData.results.competenciesForMap.performance];

                        // Set a general use overall performance name
                        $scope.overallPerformanceName = parsedData.results.competenciesForMap.performance;

                        return [parsedData];
                    };

                    var parsePerformanceByCategoryData = function (categoryData) {

                        var parsedData = {
                            results: {
                                competenciesByTopic: []
                            }
                        };

                        _.each(categoryData, function (category, key) {

                            if (key !== 'uncounted') {

                                var title = key;
                                var performance;
                                var percentageList = [];

                                _.each(category, function (data, key) {

                                    // Determine who gets the highlight
                                    if (data.highlighted) {
                                        performance = key;
                                    }

                                    // Populate a list of category percentages for further rounding
                                    if (key !== 'uncounted') {

                                        percentageList.push({
                                            key: key,
                                            value: (data.count / $scope.report.studentsAssessedCount) * 100
                                        });
                                    }
                                });

                                // Returns list of rounded percents that will total 100
                                percentageList = roundPercentagesTo100(percentageList);

                                // Done parsing, push it to our list
                                parsedData.results.competenciesByTopic.push({
                                    title: title,
                                    performance: performance,
                                    percents: percentageList
                                });
                            }

                        });

                        return [parsedData];
                    };

                    var parseReportData = function (reportData) {

                        // Generate students assessed percentage
                        reportData.studentsAssessedPercent = Math.round(reportData.studentsAssessedCount / reportData.totalStudentCount) * 100;

                        // Format median time since assessed
                        reportData.medianDaysSinceAssessed = Moment().subtract(reportData.medianDaysSinceAssessed, 'days').fromNow(true);

                        // Whip overallPerformance into a shape expected by <ccc-overall-performance>
                        reportData.overallPerformance = parseOverallPerformanceData(reportData.overallPerformanceCountMap);

                        // Whip performanceByCategory into a shape expected by <ccc-performance-by-category>
                        reportData.performanceByCategory = parsePerformanceByCategoryData(reportData.competencyCategoryCountMap);
                    };


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    parseReportData($scope.report);

                    // Set Moment locale back to english now that we're done displaying the report.
                    Moment.locale('en');

                }
            ],

            template: [

                '<div class="discipline">',
                    '<div class="discipline-header">',
                        '<h2 class="title">{{::report.course}}</h2>',
                        '<div class="header-details assessment-attempt">',
                            '<span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.TITLE.REPORT" translate-values="{USER: currentUser, DATE: date, SECTIONID: report.sectionId}"></span>',
                        '</div>',
                    '</div>',
                    '<div class="discipline-body">',
                        '<div class="row">',
                            '<div class="col col-md-12">',
                                '<div class="class-report-details highlight-blocks highlight-primary">',
                                    '<div class="block-container">',
                                        '<div class="block number-of-students">',
                                            '<h3 class="title">{{::report.totalStudentCount}} <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.TITLE.STUDENTS"></span></h3>',
                                            '<h4 class="subtitle" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.TITLE.REQUESTED"></h4>',
                                        '</div>',
                                    '</div>',
                                    '<div class="block-container">',
                                        '<div class="block number-of-students">',
                                            '<h3 class="title">{{::report.studentsAssessedCount}} <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.TITLE.STUDENTS"></span> | {{::report.studentsAssessedPercent}}%</h3>',
                                            '<h4 class="subtitle" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.TITLE.HAVE_DATA" translate-values="{CSA: report.courseSubjectArea}"></h4>',
                                        '</div>',
                                    '</div>',
                                    '<div class="block-container">',
                                        '<div class="block avg-time-since-assessment">',
                                            '<h3 class="title">{{::report.medianDaysSinceAssessed}}</h3>',
                                            '<h4 class="subtitle" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.TITLE.MEDIAN_TIME" translate-values="{CSA: report.courseSubjectArea}"></h4>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',
                        '<hr />',
                        '<div class="row">',
                            '<div class="col col-md-12 overall-measure">',
                                '<h3 class="title" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.TITLE.OVERALL" translate-values="{STUDENT_REPORTING_COUNT: report.studentsAssessedCount}"></h3>',
                                '<p translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.DESC.OVERALL" translate-values="{CSA: report.courseSubjectArea, COLLEGE: report.college}"></p>',
                                '<ccc-overall-performance test-results="report.overallPerformance" is-class-report="true"></ccc-overall-performance>',
                            '</div>',
                        '</div>',
                        '<div class="row">',
                            '<div class="col col-md-12">',
                                '<div class="measure-details">',
                                    '<div class="measure-details-header">',
                                        '<h3 class="highest-percentage-performance" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.DESC.MAJORITY" translate-values="{STUDENT_REPORTING_COUNT: report.studentsAssessedCount, STUDENT_COUNT: report.totalStudentCount, PERFORMANCE: overallPerformanceName}"></h3>',
                                    '</div>',
                                    '<div class="measure-details-body">',
                                        '<div class="row">',
                                            '<div class="col col-md-6">',
                                                '<h3 class="title">{{::overallPerformanceName}} <span translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.TITLE.BY_CATEGORY"></span></h3>',
                                                '<p translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.DESC.BY_CATEGORY"></p>',
                                                '<ccc-performance-by-category test-results="report.performanceByCategory" is-class-report="true"></ccc-performance-by-category>',
                                            '</div>',
                                            '<div class="col col-md-6">',
                                                '<h3 class="title" translate="CCC_VIEW_HOME.CCC-CLASS-REPORT.TITLE.OVERALL_TYPICALLY" translate-values="{PERFORMANCE: overallPerformanceName}"></h3>',
                                                '<ccc-competency-results is-student="true" competency-map-results="report.selectedOrderedRestrictedViewCompetencies"></ccc-competency-results>',
                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>',

            ].join('')

        };

    });

})();
