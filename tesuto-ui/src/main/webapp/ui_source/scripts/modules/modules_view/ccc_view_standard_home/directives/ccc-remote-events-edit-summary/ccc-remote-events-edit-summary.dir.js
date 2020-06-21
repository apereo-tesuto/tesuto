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

    angular.module('CCC.View.Home').directive('cccRemoteEventsEditSummary', function () {

        return {

            restrict: 'E',

            scope: {
                remoteEvent: '='
            },

            controller: [

                '$scope',
                '$timeout',
                'ModalService',
                'NotificationService',
                'RemoteEventService',
                'EventsAPIService',

                function ($scope, $timeout, ModalService, NotificationService, RemoteEventService, EventsAPIService) {

                    /*============ PRIVATE VARIABLES ============*/

                    var editEvent = function () {
                        $scope.$emit('ccc-remote-events-edit-summary.editDetails', $scope.remoteEvent);
                    };

                    var cancelEvent = function (remoteEvent) {

                        var buttonConfigs = {
                            cancel: {
                                title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.CANCEL_MODAL.BUTTON_CANCEL',
                                btnClass: 'btn-primary'
                            },
                            okay: {
                                title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.CANCEL_MODAL.BUTTON_CONFIRM',
                                btnIcon: 'fa-check-circle',
                                btnClass: 'btn-default'
                            }
                        };

                        var cancelEventModal = ModalService.openConfirmModal({

                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.CANCEL_MODAL.TITLE',
                            message: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.CANCEL_MODAL.MESSAGE',
                            buttonConfigs: buttonConfigs,
                            type: 'warning',

                            onBeforeConfirm: function (completeConfirm, cancelConfirm) {

                                $scope.loading = true;

                                remoteEvent.cancel().then(function () {

                                    $scope.$emit('ccc-remote-events-edit-summary.eventCancelled', remoteEvent);
                                    completeConfirm();

                                }, function () {

                                    completeConfirm();

                                }).finally(function () {

                                    // keep things disabled in case this view transitions afterwards
                                    $timeout(function () {
                                        $scope.loading = false;
                                    }, 600);
                                });
                            }
                        });

                        return cancelEventModal;
                    };

                    var resendEmail = function (remoteEvent) {

                        var buttonConfigs = {
                            cancel: {
                                title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.EMAIL_MODAL.BUTTON_CANCEL',
                                btnClass: 'btn-default'
                            },
                            okay: {
                                title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.EMAIL_MODAL.BUTTON_CONFIRM',
                                btnIcon: 'fa-check-circle',
                                btnClass: 'btn-primary'
                            }
                        };

                        ModalService.openConfirmModal({

                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.EMAIL_MODAL.TITLE',
                            message: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.EMAIL_MODAL.MESSAGE',
                            buttonConfigs: buttonConfigs,
                            type: 'info',

                            onBeforeConfirm: function (completeConfirm, cancelConfirm) {

                                EventsAPIService.resendEmail(remoteEvent.testEventId).then(function () {

                                    NotificationService.open({
                                        icon: 'fa fa-thumbs-up',
                                        title: ' Success.',
                                        message: ' Email was sent.'
                                    },
                                    {
                                        delay: 5000,
                                        type: "success",
                                        allow_dismiss: true
                                    });

                                }).finally(completeConfirm);
                            }
                        });
                    };

                    var getPasscode = function (wasReset) {

                        var modalTitle = "Proctor Passcode";
                        if (wasReset) {
                            modalTitle = "Proctor Passcode Reset";
                        }

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

                    var resetPasscode = function () {

                        var buttonConfigs = {
                            cancel: {
                                title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.RESET_PASSCODE_MODAL.BUTTON_CANCEL',
                                btnClass: 'btn-default'
                            },
                            okay: {
                                title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.RESET_PASSCODE_MODAL.BUTTON_CONFIRM',
                                btnIcon: 'fa-check-circle',
                                btnClass: 'btn-primary'
                            }
                        };

                        ModalService.openConfirmModal({

                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.RESET_PASSCODE_MODAL.TITLE',
                            message: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.RESET_PASSCODE_MODAL.MESSAGE',
                            buttonConfigs: buttonConfigs,
                            type: 'info',

                            onBeforeConfirm: function (completeConfirm, cancelConfirm) {

                                EventsAPIService.resetPasscode($scope.remoteEvent.testEventId).then(function (newPasscode) {

                                    $scope.remoteEvent.remotePasscode = newPasscode;
                                    getPasscode(true);

                                }).finally(completeConfirm);
                            }
                        });
                    };



                    /*============ MODEL ============*/

                    $scope.loading = false;

                    $scope.actions = [
                        {
                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.EDIT_TEST_EVENT',
                            icon: 'fa-pencil',
                            onClick: editEvent
                        },
                        {
                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.CANCEL_EVENT',
                            icon: 'fa-times-circle',
                            onClick: cancelEvent
                        },
                        {
                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.TEST_EVENT_EMAIL',
                            icon: 'fa-send',
                            onClick: resendEmail
                        },
                        {
                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.TEST_EVENT_PASSCODE',
                            icon: 'fa-key',
                            onClick: function () {
                                getPasscode();
                            }
                        },
                        {
                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.TEST_EVENT_PASSCODE_RESET',
                            icon: 'fa-refresh',
                            onClick: resetPasscode
                        }
                    ];


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.done = function () {
                        $scope.$emit('ccc-remote-events-edit-summary.done', $scope.remoteEvent);
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-remote-event-summary.editStudents', function () {
                        $scope.$emit('ccc-remote-events-edit-summary.editStudents', $scope.remoteEvent);
                    });

                    $scope.$on('ccc-remote-event-summary.done', $scope.done);

                    $scope.$on('ccc-remote-events-edit-summary.refresh', function (e, remoteEvent, preventMetaDataRefresh) {

                        // after edit, a clone can be passed in to replace the original reference
                        if (remoteEvent) {
                            $scope.remoteEvent = remoteEvent;
                        }

                        $scope.loading = true;

                        // sometimes all you need is to refresh the metadata on the existing reference
                        RemoteEventService.attachAllMetaData($scope.remoteEvent, !preventMetaDataRefresh).finally(function () {
                            $scope.loading = false;
                        });

                        $scope.$broadcast('ccc-remote-event-summary.refresh', remoteEvent, preventMetaDataRefresh);
                    });
                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-xs-12">',

                        '<ccc-remote-event-summary remote-event="remoteEvent" actions="actions" is-disabled="loading"></ccc-remote-event-summary>',

                        '<div class="ccc-form-submit-controls text-left">',

                            '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ccc-focusable ng-click="done()" ng-disabled="loading">',
                                '<i class="fa fa-check" aria-hidden="true"></i> ',
                                '<span translate="Done"></span>',
                            '</button>',
                        '</div>',

                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
