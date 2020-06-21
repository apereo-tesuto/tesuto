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

    angular.module('CCC.View.Dashboard').directive('cccDashboardMetricsStudents', function () {

        return {

            restrict: 'E',

            scope: {
                rawMetrics: "="
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/


                    /*============ MODEL =============*/

                    $scope.students = $scope.rawMetrics;

                }
            ],

            template: [

                '<table class="table">',
                    '<thead>',
                        '<tr>',
                            '<th>Student ID</th>',
                            '<th class="text-center">MIS Code</th>',
                        '</tr>',
                    '</thead>',
                    '<tbody>',
                        '<tr ng-repeat="student in students">',
                            '<td>{{::student.studentCccId}}</td>',
                            '<td class="text-center">{{::student.misCode}}</td>',
                        '</tr>',
                    '</tbody>',
                '</table>'

            ].join('')

        };

    });

})();






