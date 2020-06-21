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

    /*========== ENTRY POINT ON DOCUMENT READY FROM THE MAIN TEMPLATE ============*/

    angular.module('CCC.AsmtPlayer', ['CCC.Assessment']);


    /*======================== LOAD VALUES/CONSTANTS ========================*/


    /*======================== LOAD CONFIGURATIONS ========================*/

    // REGISTER I18N FILES FOR THIS MODULE
    angular.module('CCC.AsmtPlayer').config(['TranslateFileServiceProvider', function (TranslateFileServiceProvider) {
        TranslateFileServiceProvider.addTranslateFile({
            prefix: 'ui/scripts/modules/modules_common/ccc_asmt_player/i18n/locale-',
            suffix: '.json'
        });
    }]);


    /*======================== INITIALIZATION ========================*/

    // some tweaks to the route changing in the UI
    angular.module('CCC.AsmtPlayer').run([

        '$rootScope',
        '$state',

        function ($rootScope, $state) {

            // jump into the state change hooks
            $rootScope.$on("$stateChangeStart", function (event, nextRoute, currentRoute) {

                // here we make sure that the user is logged in, other wise we send them to the login page
                // if (!$CurrentUser.getStatus() && nextRoute.name !== 'login' ) {
                //     event.preventDefault();
                //     $state.go("login");
                //     return;
                // }
            });
        }
    ]);

    // configure the DefaultErrorHandlerService for AsmtPlayers
    angular.module('CCC.AsmtPlayer').run([

        '$rootScope',
        '$state',
        'ErrorHandlerService',
        'ModalService',

        function ($rootScope, $state, ErrorHandlerService, ModalService) {

            var errorChannelGroupsMap = {
                serverError:    ['broadcast', 'serverErrorModal'],
                accessDenied:   ['unauthorizedModal', 'broadcast'],
                timeout:        ['broadcast'],
                noSession:      ['broadcast', 'logout'],
                notFoundError:  ['broadcast', 'notFoundModal']
            };

            var errorChannels = {

                broadcast: function (type, err) {
                    $rootScope.$broadcast('ccc.serverError', type, err);
                },

                serverErrorModal: function (type, err) {
                    ModalService.open({
                        scope: $rootScope.$new(),
                        template: '<ccc-asmt-modal-server-error modal="modal"></ccc-asmt-modal-server-error>'
                    });
                },

                notFoundModal: function (type, err) {
                    ModalService.open({
                        scope: $rootScope.$new(),
                        template: '<ccc-asmt-modal-not-found modal="modal"></ccc-asmt-modal-not-found>'
                    });
                },

                unauthorizedModal: function (type, err) {

                    var modalScope = $rootScope.$new();
                    modalScope.type = type;
                    modalScope.err = err;

                    ModalService.open({
                        scope: modalScope,
                        template: '<ccc-modal-unauthorized modal="modal"></ccc-modal-unauthorized>'
                    });
                },

                logout: function (type, err) {
                    $state.go('logout');
                }
            };

            var errorTypeChannelsMap = {

                '-1':  errorChannelGroupsMap['serverError'],        // -1 is status when using chrome's debug tools to simulate failed network call
                '400': errorChannelGroupsMap['serverError'],        // unhandled 400 bad request
                '405': errorChannelGroupsMap['serverError'],        // unhandled 405 Method not allowed
                '500': errorChannelGroupsMap['serverError'],        // unexpected server error

                '404': errorChannelGroupsMap['notFoundError'],      // unhandled 404

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

    // set the current user from the global user object in the jsp
    angular.module('CCC.AsmtPlayer').config([

        'CurrentUserServiceProvider',
        'SESSION_CONFIGS',

        function (CurrentUserServiceProvider, SESSION_CONFIGS) {

            CurrentUserServiceProvider.setUser(SESSION_CONFIGS.user);
        }
    ]);

})();
