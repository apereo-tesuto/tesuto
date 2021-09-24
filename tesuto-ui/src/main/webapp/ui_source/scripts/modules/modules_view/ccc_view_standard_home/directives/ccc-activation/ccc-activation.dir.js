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

    angular.module('CCC.View.Home').directive('cccActivation', function () {

        return {

            restrict: 'E',

            scope: {
                activation: '='
            },

            controller: [

                '$scope',
                '$element',
                '$compile',

                function ($scope, $element, $compile) {

                    /*============ MODEL ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.statusClass = function () {

                        return {
                            'status-in-progress': $scope.activation.status === 'IN_PROGRESS',
                            'status-complete': $scope.activation.status === 'COMPLETE',
                            'status-ready': $scope.activation.status === 'READY',
                            'status-paused': $scope.activation.status === 'PAUSED',
                            'status-incomplete': $scope.activation.status === 'INCOMPLETE',
                            'status-expired': $scope.activation.status === 'EXPIRED'
                        };
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [
                '<div class="ccc-activation-inner" ng-class="statusClass()" tabindex="0">',
                    '<div class="ccc-activation-status">',
                        '<i ng-if="activation.status === \'IN_PROGRESS\'" class="fa fa-hourglass-start" aria-hidden="true"></i>',
                        '<i ng-if="activation.status === \'PAUSED\'" class="fa fa-pause" aria-hidden="true"></i>',
                        '<i ng-if="activation.status === \'COMPLETE\'" class="fa fa-check" aria-hidden="true"></i>',
                        '<i ng-if="activation.status === \'READY\'" class="fa fa-play" aria-hidden="true"></i>',

                        '<i ng-if="activation.status === \'INCOMPLETE\'" class="fa fa-exclamation-triangle" aria-hidden="true"></i>',
                        '<i ng-if="activation.status === \'EXPIRED\'" class="fa fa-exclamation-triangle" aria-hidden="true"></i>',
                    '</div>',
                    '<div class="ccc-activation-details">',
                        '<div class="ccc-activation-details-student">',
                            '<ccc-user-name last-name="activation.student.lastName" first-name="activation.student.firstName" middle-initial="activation.student.middleInitial"></ccc-user-name>',
                        '</div>',
                        '<div class="ccc-activation-details-cccid">{{::activation.student.cccId}}</div>',
                        '<button ng-if="activation.deliveryType === \'PAPER\' && activation.status === \'READY\' || activation.deliveryType === \'PAPER\' && activation.status === \'IN_PROGRESS\'" class="btn btn-link btn-xs print" ccc-print-activation="activation">',
                            '<span class="icon fa fa-print" role="presentation" aria-hidden="true"></span> ',
                            '<span class="text">',
                                '<span ng-if="activation.status === \'IN_PROGRESS\'">Re</span>',
                                'print',
                            '</span>',
                        '</button>',
                    '</div>',
                '</div>'
            ].join('')
        };
    });

})();
