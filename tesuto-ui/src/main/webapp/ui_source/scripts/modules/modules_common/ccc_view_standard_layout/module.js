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

    /*========== MODULE FOR TYPICAL VIEW SETUP AND DIRECTIVES / SERVICES ============*/

    angular.module('CCC.View.Layout', [
        // Needs common dependencies (which includes some run configs and components like the modal)
        'CCC.Assess',

        // all standard views get access to the angular-input-masks functionality
        'ui.utils.masks'
    ]);


    /*======================== LOAD VALUES/CONSTANTS ========================*/


    /*======================== LOAD CONFIGURATIONS ========================*/

    // REGISTER I18N FILES FOR THIS MODULE
    angular.module('CCC.View.Layout').config(['TranslateFileServiceProvider', function (TranslateFileServiceProvider) {
        TranslateFileServiceProvider.addTranslateFile({
            prefix: 'ui/scripts/modules/modules_common/ccc_view_standard_layout/i18n/locale-',
            suffix: '.json'
        });
    }]);


    /*======================== INITIALIZATION ========================*/

    // look out for the focused mode and add a corresponding class
    angular.module('CCC.View.Layout').run([

        '$location',

        function ($location) {
            if ($location.search().focused === "true") {
                $('body').addClass('ccc-view-standard-focused');
            }
        }
    ]);

    // configure the notification plugin defaults
    angular.module('CCC.View.Layout').run([

        function () {

            $.notifyDefaults({
                element: '.ccc-view-common-content-notifications',
                allow_dismiss: true,
                delay: 4000,
                offset: {
                    x: 0,
                    y: -38
                },
                showProgressbar: false,
                template: '<div data-notify="container" class="ccc-notification alert alert-{0}" role="alert">' +
                    '<button type="button" aria-hidden="true" class="close" data-notify="dismiss">×</button>' +
                    '<span data-notify="icon"></span> ' +
                    '<span data-notify="title">{1}</span> ' +
                    '<span data-notify="message">{2}</span>' +
                    '<div class="progress" data-notify="progressbar">' +
                        '<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>' +
                    '</div>' +
                    '<a href="{3}" target="{4}" data-notify="url"></a>' +
                '</div>'
            });
        }
    ]);

    // configure the DefaultErrorHandlerService
    angular.module('CCC.View.Layout').run([

        '$rootScope',
        '$state',
        'ErrorHandlerService',
        'ModalService',
        'NotificationService',

        function ($rootScope, $state, ErrorHandlerService, ModalService, NotificationService) {

            var errorChannelGroupsMap = {
                serverError:    ['serverErrorModal', 'broadcast'],
                accessDenied:   ['unauthorizedModal', 'broadcast'],
                timeout:        ['timeoutModal', 'broadcast'],
                noSession:      ['logout', 'broadcast']
            };

            var errorChannels = {

                broadcast: function (type, err) {
                    $rootScope.$broadcast('ccc.serverError', type, err);
                },

                serverErrorModal: function (type, err) {

                    // pop up a notification with a link that will show more details of the server error
                    NotificationService.open({
                        icon: 'fa fa-exclamation-triangle',
                        title: ' An unexpected server error occurred.',
                        message: '<a href="#">View details.</a>'
                    },
                    {
                        delay: 0,
                        type: "danger",
                        allow_dismiss: true,
                        onShow: function () {
                            var notification = $(this);
                            notification.find('a').click(function () {
                                var modalScope = $rootScope.$new();
                                modalScope.type = type;
                                modalScope.err = err;

                                ModalService.open({
                                    scope: modalScope,
                                    size: 'lg',
                                    template: '<ccc-modal-server-error-json-only modal="modal" err="err"></ccc-modal-server-error-json-only>'
                                });
                            });
                        }
                    });
                },

                timeoutModal: function (type, err) {

                    var modalScope = $rootScope.$new();

                    ModalService.open({
                        scope: modalScope,
                        template: '<ccc-modal-timeout modal="modal"></ccc-modal-timeout>'
                    });
                },

                unauthorizedModal: function (type, err) {

                    var modalScope = $rootScope.$new();
                    modalScope.type = type;
                    modalScope.err = err;

                    ModalService.open({
                        id: 'ccc-modal-unauthorized',
                        scope: modalScope,
                        template: '<ccc-modal-unauthorized modal="modal" err="err"></ccc-modal-unauthorized>'
                    });
                },

                logout: function (type, err) {
                    $state.go('logout');
                }
            };

            var errorTypeChannelsMap = {

                '400': errorChannelGroupsMap['serverError'],        // unhandled 400 bad request
                '204': errorChannelGroupsMap['serverError'],        // unhandled 204
                '404': errorChannelGroupsMap['serverError'],        // unhandled 404
                '405': errorChannelGroupsMap['serverError'],        // unhandled 405 Method not allowed
                '500': errorChannelGroupsMap['serverError'],        // unexpected server error

                '401': errorChannelGroupsMap['noSession'],          // not logged in
                '403': errorChannelGroupsMap['accessDenied'],       // logged in but permission denied

                '502': errorChannelGroupsMap['timeout'],            // bad gateway
                '408': errorChannelGroupsMap['timeout'],            // server timeout
                '503': errorChannelGroupsMap['timeout'],            // service unavailable
                '504': errorChannelGroupsMap['timeout']             // gateway timeout
            };

            ErrorHandlerService.setErrorChannels(errorChannels);
            ErrorHandlerService.setErrorTypeChannelsMap(errorTypeChannelsMap);
        }

    ]);

})();
