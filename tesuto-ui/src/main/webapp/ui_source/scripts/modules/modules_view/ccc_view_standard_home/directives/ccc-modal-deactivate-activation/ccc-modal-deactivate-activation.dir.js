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

    angular.module('CCC.View.Home').directive('cccModalDeactivateActivation', function () {

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
                'Moment',
                'CCCUtils',

                function ($scope, Moment, CCCUtils) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ===========*/

                    /*============ MODEL DEPENDENT METHODS ============*/

                    var refresh = function () {
                        _.each($scope.activation, function (activation) {
                            activation = CCCUtils.coerce('ActivationClass', activation);
                            $scope.activationEndDate = Moment.utc(activation.endDate).fromNow();
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.cancel = function () {
                        $scope.modal.instance.dismiss();
                    };

                    $scope.deactivate = function () {
                        $scope.modal.instance.close();
                    };


                    /*============ LISTENERS ===========*/

                    /*============ INITIALIZATION ===========*/

                    // Allows code reuse between single / batch deactivations
                    if (!(_.isArray($scope.activation))) {
                        $scope.activation = [$scope.activation];
                    }

                    refresh();

                }
            ],

            template: [

                // NOTE: [autofocus] for modals to ensure focus trap
                '<div id="{{modal.labelId}}">',

                    '<div class="modal-header">',
                        '<h3 class="modal-title" translate="CCC_VIEW_HOME.MODAL.DEACTIVATE_ACTIVATION.DEACTIVATE_ACTIVATION"></h3>',
                    '</div>',

                    '<div class="modal-content-wrapper">',
                        // NOTE: Here we add in the contentID
                        '<div class="modal-body" id="{{modal.contentId}}">',
                            '<ccc-student-user user="student"></ccc-student-user>',
                            '<ccc-activation-card ng-repeat="item in activation track by $index" activation="item" activation-details="true"></ccc-activation-card>',
                        '</div>',
                    '</div>',
                '</div>',

                '<div class="modal-footer">',
                    '<button autofocus tabindex="0" class="btn btn-default" ng-click="cancel()">',
                        '<span translate="CCC_VIEW_HOME.MODAL.DEACTIVATE_ACTIVATION.CANCEL"></span>',
                    '</button>',
                    '<button tabindex="0" class="btn btn-primary" ng-click="deactivate()">',
                        '<span translate="CCC_VIEW_HOME.MODAL.DEACTIVATE_ACTIVATION.DEACTIVATE"></span> ',
                    '</button>',
                '</div>'

            ].join('')

        };

    });

})();
