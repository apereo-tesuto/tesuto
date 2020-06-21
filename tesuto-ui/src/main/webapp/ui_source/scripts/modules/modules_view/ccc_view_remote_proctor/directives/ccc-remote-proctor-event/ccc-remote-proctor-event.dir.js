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

    angular.module('CCC.View.RemoteProctor').directive('cccRemoteProctorEvent', function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',
                'ModalService',
                'RemoteProctorService',

                function ($scope, ModalService, RemoteProctorService) {

                    /*============ PRIVATE VARIABLES ============*/

                    var getPasscode = function () {

                        var modalTitle = "Proctor Passcode";

                        ModalService.openCustomModal(
                            // title
                            modalTitle,
                            // template
                            "<ccc-modal-remote-proctor-passcode passcode='passcode' modal='modal'></ccc-modal-remote-proctor-passcode>",
                            // data
                            {passcode: $scope.remoteEvent.remotePasscode},
                            // preventClose
                            false
                        );
                    };


                    /*============ MODEL ============*/

                    $scope.remoteEvent = RemoteProctorService.getTestEvent();

                    $scope.actions = [
                        {
                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.TEST_EVENT_PASSCODE',
                            icon: 'fa-key',
                            onClick: getPasscode
                        }
                    ];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ===========*/


                }
            ],

            template: [

                '<div ng-if="!loading">',
                    '<ccc-remote-event-summary remote-event="remoteEvent" actions="actions" view-instructions="true"></ccc-remote-event-summary>',
                '</div>'

            ].join('')

        };
    });

})();
