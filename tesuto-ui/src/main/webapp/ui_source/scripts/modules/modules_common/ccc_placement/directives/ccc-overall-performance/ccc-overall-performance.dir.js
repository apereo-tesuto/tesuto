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

    angular.module('CCC.Placement').directive('cccOverallPerformance', function () {

        return {

            restrict: 'E',

            scope: {
                testResults: '=',
                isClassReport: '=?'
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    $scope.isClassReport = $scope.isClassReport || false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============= BEHAVIOR =============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<div class="overall-measure">',
                    '<div class="well margin-bottom-double">',
                        '<div class="performance-graph">',
                            '<div class="graph bar">',
                                '<div class="graph-labels">',
                                    '<div translate="CCC_PLACEMENT.CCC_OVERALL_PERFORMANCE.LABEL.NOVICE" ng-class="{\'measured-above\': testResults[0].position > 1, \'measure\': testResults[0].position === 1}" class="graph-label below-average"></div>',
                                    '<div translate="CCC_PLACEMENT.CCC_OVERALL_PERFORMANCE.LABEL.BELOW_INT" ng-class="{\'measured-above\': testResults[0].position > 2, \'measure\': testResults[0].position === 2}" class="graph-label below-middle"></div>',
                                    '<div translate="CCC_PLACEMENT.CCC_OVERALL_PERFORMANCE.LABEL.INT" ng-class="{\'measured-above\': testResults[0].position > 3, \'measure\': testResults[0].position === 3}" class="graph-label average"></div>',
                                    '<div translate="CCC_PLACEMENT.CCC_OVERALL_PERFORMANCE.LABEL.ABOVE_INT" ng-class="{\'measured-above\': testResults[0].position > 4, \'measure\': testResults[0].position === 4}" class="graph-label above-middle"></div>',
                                    '<div translate="CCC_PLACEMENT.CCC_OVERALL_PERFORMANCE.LABEL.ADVANCED" ng-class="{\'measured-above\': testResults[0].position > 5, \'measure\': testResults[0].position === 5}" class="graph-label above-average"></div>',
                                    '<div ng-class="{\'one\': testResults[0].position === 1, \'two\': testResults[0].position === 2, \'three\': testResults[0].position === 3, \'four\': testResults[0].position === 4, \'five\': testResults[0].position === 5}" class="marker">',
                                        '<span class="sr-only">Your overall performance is {{testResults[0].results.competenciesForMap.performance}}</span>',
                                    '</div>',
                                '</div>',
                                '<div class="graph-bars">',
                                    '<div ng-class="{\'measured-above\': testResults[0].position > 1 && !isClassReport, \'measure\': testResults[0].position === 1}" class="graph-bar below-average">',
                                        '<span ng-if="isClassReport" class="class-percentage">',
                                            '<span class="number">{{testResults[0].percents[1]}}</span>',
                                            '<span class="percent">%</span>',
                                        '</span>',
                                    '</div>',
                                    '<div ng-class="{\'measured-above\': testResults[0].position > 2 && !isClassReport, \'measure\': testResults[0].position === 2}" class="graph-bar below-middle">',
                                        '<span ng-if="isClassReport" class="class-percentage">',
                                            '<span class="number">{{testResults[0].percents[2]}}</span>',
                                            '<span class="percent">%</span>',
                                        '</span>',
                                    '</div>',
                                    '<div ng-class="{\'measured-above\': testResults[0].position > 3 && !isClassReport, \'measure\': testResults[0].position === 3}" class="graph-bar average">',
                                        '<span ng-if="isClassReport" class="class-percentage">',
                                            '<span class="number">{{testResults[0].percents[3]}}</span>',
                                            '<span class="percent">%</span>',
                                        '</span>',
                                    '</div>',
                                    '<div ng-class="{\'measured-above\': testResults[0].position > 4 && !isClassReport, \'measure\': testResults[0].position === 4}" class="graph-bar above-middle">',
                                        '<span ng-if="isClassReport" class="class-percentage">',
                                            '<span class="number">{{testResults[0].percents[4]}}</span>',
                                            '<span class="percent">%</span>',
                                        '</span>',
                                    '</div>',
                                    '<div ng-class="{\'measured-above\': testResults[0].position > 5 && !isClassReport, \'measure\': testResults[0].position === 5}" class="graph-bar above-average">',
                                        '<span ng-if="isClassReport" class="class-percentage">',
                                            '<span class="number">{{testResults[0].percents[5]}}</span>',
                                            '<span class="percent">%</span>',
                                        '</span>',
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
