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

    /**
     * Wrap the UI Bootstrap Modal Service
     * We do this so we can pepper on some additional logic to get the modal instance within a directive passed in
     * Additionally we wrap it and create some event enhancements for when a modal is opened or closed (accessibility)
     */

    angular.module('CCC.Components').service('ModalService', [

        '$rootScope',
        '$uibModal',
        '$timeout',

        function ($rootScope, $uibModal, $timeout) {

            /*============ SERVICE DECLARATION ============*/

            var ModalService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var modalId = 0;
            var getNewModalId = function () {
                modalId++;
                return 'ccc-modal-id-' + modalId;
            };

            // for modals that have an id, we can track them to avoid opening another
            var modalReferenceMap = {};


            /*============ SERVICE DEFINITION ============*/

            ModalService = {

                open: function (modalOptions) {

                    // let everyone know we are opening so they can possibly make themselves aria-hidden (ccc-responds-to-dialog directive)
                    $rootScope.$broadcast('ccc.dialog.onBeforeOpen');

                    modalOptions.scope = modalOptions.scope || $rootScope.$new();

                    var modalIdProvided = modalOptions.id;
                    var modalId;
                    if (modalIdProvided) {
                        modalId = modalOptions.id;
                    } else {
                        modalId = getNewModalId();
                    }

                    // if the id is already in our reference map then don't open and return current instance
                    // this means that modals opened repeatedly cannot chain on to the result promise
                    // these must be dumb modals that are notifications with no logic associated with cancel or close
                    if (modalReferenceMap[modalId]) {

                        // if the original has already rendered, add a shake effect
                        if (modalReferenceMap[modalId].element) {

                            modalReferenceMap[modalId].element.addClass('shake animated');
                            $timeout(function () {
                                modalReferenceMap[modalId].element.removeClass('shake animated');
                            }, 1000);
                        }

                        return false;
                    }

                    modalOptions.scope.modal = {
                        id: modalId,
                        labelId: modalId + '-label',
                        contentId: modalId + '-content',
                        instance: $uibModal.open(modalOptions)
                    };

                    modalOptions.scope.modal.instance.rendered.then(function () {

                        setTimeout(function () {

                            // todo, refactor modal so we don't have to do things like this to get a dom reference
                            modalOptions.scope.modal.instance.element = $('#' + modalOptions.scope.modal.contentId).parents('.modal-dialog');

                            // as of right now the modal directive is responsible for setting focus on the correct item
                            // the bootstrap library will look for autofocus also, doing it again here is redundant, but helps keep the contract in case we switch libraries
                            modalOptions.scope.modal.instance.element.find('[autofocus]').focus();

                            // for accessibility purposes we add some aria attributes
                            modalOptions.scope.modal.instance.element.attr('aria-hidden', 'false');

                        }, 1);
                    });

                    var onDialogClosed = function () {
                        // let everyone know we closed on successful resolution
                        $rootScope.$broadcast('ccc.dialog.closed');

                        // remove the reference
                        if (modalIdProvided) {
                            delete modalReferenceMap[modalIdProvided];
                        }

                        // if it was configured to be triggered from a source element, return focus there
                        if (modalOptions.sourceElement) {
                            $(modalOptions.sourceElement).focus();
                        }
                    };

                    // tack on a promise handler to help normalize open and close events
                    modalOptions.scope.modal.instance.result.then(onDialogClosed, onDialogClosed);

                    // if they provide an id to ensure the modal is unique
                    // then return false because unique modals cannot chain logic onto the complete or cancel promise
                    // this ensures any developers in the future get an error if they attempt this scenario
                    if (modalIdProvided) {

                        // first store the refence in our map
                        modalReferenceMap[modalIdProvided] = modalOptions.scope.modal.instance;

                        return false;

                    } else {
                        return modalOptions.scope.modal.instance;
                    }
                },

                openAlertModal: function (title, message, alertType) {

                    var modalScope = $rootScope.$new();
                    modalScope.message = message;
                    modalScope.headline = title;
                    modalScope.alertType = alertType || 'info';

                    return ModalService.open({
                        scope: modalScope,
                        template: '<ccc-modal-alert modal="modal" message="message" headline="headline" alert-type="alertType"></ccc-modal-alert>'
                    });
                },

                openConfirmModal: function (modalConfigs) {

                    var defaults = {
                        title: 'Confirm',
                        message: 'Are you sure?',
                        type: 'default',
                        buttonConfigs: {},
                        onBeforeConfirm: function (completeConfirm, cancelConfirm) {
                            completeConfirm();
                        }
                    };

                    var options = $.extend(true, {}, defaults, modalConfigs);

                    var modalScope = $rootScope.$new();
                    modalScope.message = options.message;
                    modalScope.headline = options.title;
                    modalScope.buttonConfigs = options.buttonConfigs;
                    modalScope.type = options.type;
                    modalScope.onBeforeConfirm = options.onBeforeConfirm;

                    var modalInstance = ModalService.open({
                        scope: modalScope,
                        template: '<ccc-modal-confirm modal="modal" type="type" headline="headline" message="message" button-configs="buttonConfigs" on-before-confirm="onBeforeConfirm"></ccc-modal-confirm>'
                    });

                    return modalInstance;
                },

                openCustomModal: function (title, template, data, preventClose) {

                    var modalScope = $rootScope.$new();
                    modalScope.headline = title;
                    modalScope.template = template;
                    modalScope.data = data || {};

                    return ModalService.open({
                        scope: modalScope,
                        template: '<ccc-modal-custom modal="modal" headline="headline" template="template" data="data"></ccc-modal-custom>',
                        backdrop: preventClose ? 'static' : true,
                        keyboard: preventClose ? false : true
                    });
                }
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return ModalService;

        }
    ]);

})();

