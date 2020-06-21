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

    angular.module('CCC.View.Home').directive('cccModalActivationCollision', function () {

        return {

            restrict: 'E',

            // you would typically want to pass in the modal instance so you can use it to self close etc..
            scope: {
                modal: "=",
                activation: "=",
                student: "="
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ===========*/

                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.keepActivation = function () {
                        $scope.modal.instance.dismiss();
                    };

                    $scope.newActivation = function () {
                        $scope.modal.instance.close();
                    };


                    /*============ LISTENERS ===========*/

                    /*============ INITIALIZATION ===========*/

                }
            ],

            template: [

                '<div class="alert alert-danger">',
                    '<span translate="CCC_VIEW_HOME.MODAL.ACTIVATION_COLLISION.EXISTING_ACTIVATION" translate-values="{ ACTIVATIONS: activation.length }" translate-interpolation="messageformat"></span>',
                '</div>',

                '<ccc-student-user user="student"></ccc-student-user>',

                '<ccc-activation-card ng-repeat="duplicate in activation" activation="duplicate" activation-details="true"></ccc-activation-card>',

                '<h3 translate="CCC_VIEW_HOME.MODAL.ACTIVATION_COLLISION.TITLE" translate-values="{ ACTIVATIONS: activation.length }" translate-interpolation="messageformat"></h3>',

                '<div class="row">',
                    '<div class="col-md-6">',
                        '<button ng-click="keepActivation()" class="btn btn-primary" translate="CCC_VIEW_HOME.MODAL.ACTIVATION_COLLISION.KEEP_EXISTING" translate-values="{ ACTIVATIONS: activation.length }" translate-interpolation="messageformat"></button>',
                        '<p class="help-block" translate="CCC_VIEW_HOME.MODAL.ACTIVATION_COLLISION.KEEP_EXISTING_HELP" translate-values="{ ACTIVATIONS: activation.length }" translate-interpolation="messageformat"></p>',
                    '</div>',
                    '<div class="col-md-6">',
                        '<button ng-click="newActivation()" class="btn btn-primary" translate="CCC_VIEW_HOME.MODAL.ACTIVATION_COLLISION.CREATE_NEW" translate-values="{ ACTIVATIONS: activation.length }" translate-interpolation="messageformat"></button>',
                        '<p class="help-block" translate="CCC_VIEW_HOME.MODAL.ACTIVATION_COLLISION.CREATE_NEW_HELP" translate-values="{ ACTIVATIONS: activation.length }" translate-interpolation="messageformat"></p>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
