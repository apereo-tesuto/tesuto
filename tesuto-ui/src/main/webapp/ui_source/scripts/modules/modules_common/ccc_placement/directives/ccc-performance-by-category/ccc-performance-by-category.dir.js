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

    angular.module('CCC.Placement').directive('cccPerformanceByCategory', function () {

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

                '<table class="table competency-table">',
                    '<thead>',
                        '<tr>',
                            '<th translate="CCC_PLACEMENT.CCC_PERFORMANCE_BY_CATEGORY.LABEL.CATEGORY"></th>',
                            '<th translate="CCC_PLACEMENT.CCC_PERFORMANCE_BY_CATEGORY.LABEL.LOW"></th>',
                            '<th translate="CCC_PLACEMENT.CCC_PERFORMANCE_BY_CATEGORY.LABEL.MEDIUM"></th>',
                            '<th translate="CCC_PLACEMENT.CCC_PERFORMANCE_BY_CATEGORY.LABEL.HIGH"></th>',
                        '</tr>',
                    '</thead>',
                    '<tbody>',
                        '<tr ng-repeat="item in testResults[0].results.competenciesByTopic">',
                            '<td>',
                                '<h4>{{item.title}}</h4>',
                            '</td>',
                            '<td>',
                                '<span ng-if="item.performance === \'Low\' && !isClassReport">',
                                    '<span class="icon fa fa-check-circle" role="presentation" aria-hidden="true"></span>',
                                    '<span class="sr-only">Yes</span>',
                                '</span>',
                                '<span ng-if="isClassReport">',
                                    '<span ng-class="{highest : item.performance === \'Low\'}" class="percentage">',
                                        '<span ng-repeat="percent in item.percents track by $index">',
                                            '<span ng-if="percent.key === \'Low\'">{{::percent.value}}</span>',
                                        '</span>',
                                        '<span class="percent">%</span>',
                                    '</span>',
                                '</span>',
                            '</td>',
                            '<td>',
                                '<span ng-if="item.performance === \'Medium\' && !isClassReport">',
                                    '<span class="icon fa fa-check-circle" role="presentation" aria-hidden="true"></span>',
                                    '<span class="sr-only">Yes</span>',
                                '</span>',
                                '<span ng-if="isClassReport">',
                                    '<span ng-class="{highest : item.performance === \'Medium\'}" class="percentage">',
                                        '<span ng-repeat="percent in item.percents track by $index">',
                                            '<span ng-if="percent.key === \'Medium\'">{{::percent.value}}</span>',
                                        '</span>',
                                        '<span class="percent">%</span>',
                                    '</span>',
                                '</span>',
                            '</td>','<td>',
                                '<span ng-if="item.performance === \'High\' && !isClassReport">',
                                    '<span class="icon fa fa-check-circle" role="presentation" aria-hidden="true"></span>',
                                    '<span class="sr-only">Yes</span>',
                                '</span>',
                                '<span ng-if="isClassReport">',
                                    '<span ng-class="{highest : item.performance === \'High\'}" class="percentage">',
                                        '<span ng-repeat="percent in item.percents track by $index">',
                                            '<span ng-if="percent.key === \'High\'">{{::percent.value}}</span>',
                                        '</span>',
                                        '<span class="percent">%</span>',
                                    '</span>',
                                '</span>',
                            '</td>',
                        '</tr>',
                    '</tbody>',
                '</table>',

            ].join('')

        };
    });

})();
