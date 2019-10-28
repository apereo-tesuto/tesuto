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

    angular.module('CCC.Activations').directive('cccActivationLogs', function () {

        return {

            restrict: 'E',

            scope: {
                activation: "="
            },

            controller: [

                '$scope',
                'ActivationLogService',

                function ($scope, ActivationLogService) {


                    /*============= PRIVATE VARIABLES AND METHODS =============*/


                    /*============ MODEL ============*/

                    $scope.logs = [];
                    $scope.loading = true;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    // activations don't have usernames for some history items, so fetch those first
                    $scope.activation.hydrateStatusChangeHistory().then(function () {

                        return ActivationLogService.getLogsForActivation($scope.activation).then(function (logs) {
                            $scope.logs = logs;
                        });

                    }).finally(function () {
                        $scope.loading = false;
                    });
                }
            ],

            template: [
                '<div aria-live="polite" aria-relevant="additions">',
                    '<ccc-content-loading-placeholder ng-if="loading"></ccc-content-loading-placeholder>',
                    '<table ng-if="!loading">',
                        '<caption translate="CCC_ACTIVATIONS.LOGS.TABLE_CAPTION"></caption>',
                        '<thead>',
                            '<tr>',
                                '<th translate="CCC_ACTIVATIONS.LOGS.HEADERS.TIMESTAMP" class="time-stamp-cell"></th>',
                                '<th translate="CCC_ACTIVATIONS.LOGS.HEADERS.LOG_ENTRY"></th>',
                            '</tr>',
                        '</thead>',
                        '<tbody>',
                            '<tr ng-repeat="log in logs">',
                                '<td>{{::log.timestampReadable}}</td>',
                                '<td>{{::log.message}}<span ng-if="log.reason && log.showReason"> ({{::log.reason}})</span></td>',
                            '</tr>',
                        '</tbody>',
                    '</table>',
                '</div>'
            ].join('')

        };
    });

})();
