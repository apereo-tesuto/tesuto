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

    angular.module('CCC.Components').directive('cccErrorDetails', function () {

        return {

            restrict: 'E',

            scope: {
                err: "="
            },

            controller: ['$scope', function ($scope) {


                /*============ MODEL ============*/

                $scope.showing = false;


                /*============ BEHAVIOR ============*/

                $scope.toggleShow = function () {
                    $scope.showing = !$scope.showing;
                };

            }],

            template: [
                '<div><a href="#" ng-click="toggleShow()"><i class="fa fa-info-circle" aria-hidden="true"></i> <span translate="CCC_COMP.MODALS.BUTTONS.SHOW_DETAILS"></span></a></div>',
                '<pre ng-if="showing">{{err | json}}</pre>'
            ].join('')

        };

    });

})();
