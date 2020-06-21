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

    angular.module('CCC.View.Home').directive('cccRouteBulkActivation', function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',
                '$stateParams',

                function ($scope, $stateParams) {

                    /*============= MODEL ==============*/

                    // we have the ability to pass in state this route to change the initialDeliveryType for assessment selection in this workflow
                    $scope.initialDeliveryType = $stateParams.initialDeliveryType;

                    // normally the workflow for bulk activation when it's done will just go home, but we can pass in an alternate state as well that it can go to when it's done
                    $scope.onCompleteState = $stateParams.onCompleteState;

                }
            ],

            template: [

                    '<ccc-workflow-bulk-activation initial-delivery-type="{{::initialDeliveryType}}" on-complete-state="{{::onCompleteState}}"></ccc-workflow-bulk-activation>'

            ].join('')

        };

    });

})();
