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
// SESSION_CONFIGS : assume the jsp loaded some values into a global sessionConfigs object to be turned into a constant later
var sessionConfigs = sessionConfigs || {};
// GLOBAL API_VERSION_MAP should be injected into the page/JSP
var API_VERSION_MAP = API_VERSION_MAP || {};
// GLOBAL DEFAULT_VERSION_PATH should be injected into page/jsp
var DEFAULT_VERSION = DEFAULT_VERSION || '';
// GLOBAL VARIABLE FOR TRIGGERING SERVER ERRORS
var FORCE_SERVER_ERROR = false;  // other values could be 401, 403, 404, 500, etc..

(function () {

    /*========== PRIVATE HELPERS ============*/

    // GET A HOLD OF QUERY STRING FLAGS FOR CONFIG PHASE
    var queryString = {};
    _.each(window.location.search.substr(1).split("&"), function (pair) {
        if (pair === "") {
            return;
        }
        var parts = pair.split("=");
        queryString[parts[0]] = parts[1] && window.decodeURIComponent(parts[1].replace(/\+/g, " "));
    });

    /*========== ENTRY POINT ON DOCUMENT READY FROM THE MAIN TEMPLATE ============*/

    angular.module('CCC.Assess', [
        'ngResource', 'ngCookies', 'ngSanitize', 'ngMessages', 'ngAnimate', 'ngAria', // angular modules
        'ui.router', // angular-ui modules
        'ui.bootstrap', // angular-ui bootstrap component modules
        'ngDragDrop',  // drag and drop
        'AngularCloneable', // from: https://github.com/gitsome/AngularCloneable ::: adds recursive clonability for javascript factory classe in angular. Great for creating and editing entities (CRUD)
        'pascalprecht.translate', // internationalization library
        'LocalStorageModule', // local storage service
        'ngIdle' // detect idle sessions
    ]);


    /*======================== LOAD VALUES/CONSTANTS ========================*/

    // QUERY PARAMS WHITE LIST
    angular.module('CCC.Assess').value('QUERY_PARAM_WHITE_LIST', [
        'dev',      // may enable dev related flags
        'focused',  // enable focused view where chrome, navigation, is reviewed (good for iframing in a standard view)
        'usesource' // set to 'true' to force JSPS to load the source javascript files instead of minified
    ]);

    // STANDARDIZED REGEX PATTERNS
    angular.module('CCC.Assess').value('CCC_REGEX_STORE', {

        EMAIL: /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$/,
        SYSTEM_USER: /^[^\\\/:*?"<>| "';%@]+$/
    });


    /*======================== LOAD CONFIGURATIONS ========================*/

    // SESSION_CONFIGS : Set the SESSION_CONFIGS constant from the global sessionConfigs injected into the JSP
    angular.module('CCC.Assess').constant('SESSION_CONFIGS', sessionConfigs);

    // ANGULAR PERFORMANCE BOOST
    angular.module('CCC.Assess').config(['$compileProvider', function ($compileProvider) {

        if (queryString['dev'] !== "true") {
            $compileProvider.debugInfoEnabled(false);
        }
    }]);

    // REGISTER I18N FILES FOR THIS MODULE
    angular.module('CCC.Assess').config(['TranslateFileServiceProvider', function (TranslateFileServiceProvider) {
        TranslateFileServiceProvider.addTranslateFile({
            prefix: 'ui/scripts/modules/modules_common/ccc_assess/i18n/locale-',
            suffix: '.json'
        });
    }]);


    // LOCALIZATION SETUP
    angular.module('CCC.Assess').config([

        '$translateProvider',

        function ($translateProvider) {
            // LOCALIZATION SANITIZATION : http://angular-translate.github.io/docs/#/guide/19_security
            $translateProvider.useSanitizeValueStrategy('sanitize');
            // LOCALIZATION PLURALIZATION : https://angular-translate.github.io/docs/#/guide/14_pluralization
            $translateProvider.addInterpolation('$translateMessageFormatInterpolation');
        }
    ]);

    // LOCAL STORAGE SETUP
    angular.module('CCC.Assess').config([

        'localStorageServiceProvider',

        function (localStorageServiceProvider) {
            localStorageServiceProvider
            .setPrefix('ccc-assess')
            .setStorageType('localStorage');
        }
    ]);

    // ANIMATION CONFIGS
    angular.module('CCC.Assess').config([

        '$provide',
        '$animateProvider',

        function ($provide, $animateProvider) {

            // do not animate classes which match this pattern
            // so if you don't want animations... include noanim in the classname
            $animateProvider.classNameFilter(/^((?!noanim).)*$/i);
        }
    ]);

    // EXTEND THE TOOLTIP TRIGGERS
    angular.module('CCC.Assess').config([

        '$uibTooltipProvider',

        function ($uibTooltipProvider) {
            $uibTooltipProvider.setTriggers({
                'show': 'hide'
            });
        }
    ]);

    // ROOT LEVEL LOCATION CONFIGURATIONS
    angular.module('CCC.Assess').config([

        '$locationProvider',
        '$stateProvider',
        '$urlRouterProvider',

        function ($locationProvider, $stateProvider, $urlRouterProvider) {
            $locationProvider.html5Mode(true);
        }
    ]);


    /*======================== CONFIGURATIONS AND CONSTANTS FOR VERSIONING APIS ========================*/

    // load in API_VERSIONS that would be a global javascript array loaded into the page
    angular.module('CCC.Assess').constant('API_VERSION_MAP', API_VERSION_MAP);
    angular.module('CCC.Assess').constant('DEFAULT_VERSION', DEFAULT_VERSION);

    // then during configuration we load the API_VERSION_MAP into the VersionableAPIClassProvider
    angular.module('CCC.Assess').config([

        'API_VERSION_MAP',
        'DEFAULT_VERSION',
        'VersionableAPIClassProvider',

        function (API_VERSION_MAP, DEFAULT_VERSION, VersionableAPIClassProvider) {

            VersionableAPIClassProvider.setAPIVersionMap(API_VERSION_MAP);
            VersionableAPIClassProvider.setDefaultVersion(DEFAULT_VERSION);
        }
    ]);


    /*======================== CONFIGURATIONS FOR TRIGGERING ERRORS FOR TESTING =======================*/

    angular.module('CCC.Assess').config([

        '$httpProvider',

        function ($httpProvider) {

            var errorCodeMap = {
                200: 'OK',
                401: 'Unauthenticated',
                403: 'Unauthorized',
                204: 'No Content',
                404: 'Not Found',
                405: 'Method Not Allowed',
                500: 'Unexpected Server Error'
            };
            $httpProvider.defaults.withCredentials = true;
            $httpProvider.interceptors.push([

                '$q',

                function ($q) {

                    return {
                        request: function (config) {

                            // we allow template calls regardless of the force server so things like modals can pop
                            if (!FORCE_SERVER_ERROR || config.url.indexOf('.html') !== -1) {
                                return config;
                            } else {

                                config.status = FORCE_SERVER_ERROR + '';
                                config.statusText = errorCodeMap[FORCE_SERVER_ERROR];
                                return $q.reject(config);
                            }
                        }
                    };
                }
            ]);
            delete $httpProvider.defaults.headers.post['Content-type'];
        }
    ]);


    /*======================== OVERRIDES TO THIRD PARTY MODULES ========================*/

    // fix an IE bug related to the translate directive
    // https://github.com/angular-translate/angular-translate/issues/925
    angular.module('pascalprecht.translate').config([

        '$provide',

        function ($provide) {
            $provide.decorator('translateDirective', [

                '$delegate',

                function ($delegate) {
                    var directive = $delegate[0];
                    directive.terminal = true;
                    return $delegate;
                }
            ]);
        }
    ]);

    // Configuration and initialization for Angular Idle
    angular.module('CCC.Assess').config([

        'IdleProvider',
        'KeepaliveProvider',
        'SESSION_CONFIGS',

        function (IdleProvider, KeepaliveProvider, SESSION_CONFIGS) {
            // configure Idle settings
            if (!SESSION_CONFIGS.uiIdleTimeoutDuration) {
                throw new Error('uiIdleTimeoutDuration not set within SESSION_CONFIGS');
            }
            IdleProvider.idle(SESSION_CONFIGS.uiIdleTimeoutDuration); // seconds until considered idle
            IdleProvider.timeout(30); // seconds after idle until timeout
            IdleProvider.interrupt('mousedown keydown touchstart touchmove'); // events that will cancel idle timer
            IdleProvider.keepalive(false); // heartbeat is turned off

            KeepaliveProvider.http('/service/v1/SessionCheck'); // This may need VersionableAPIClass
        }
    ])
    .run([

        'Idle',
        'IdleService',

        function (Idle, IdleService) {
            Idle.watch();
            IdleService = IdleService; // initialize custom CCC IdleService
        }
    ]);


    /*======================== INITIALIZATION ========================*/

    // Fire a debounced event to listen to resizes, this helps reduce memory leaks from components all trying to add
    // event handlers to the window object and not cleaning them up
    angular.module('CCC.Assess').run([

        '$rootScope',

        function ($rootScope) {

            var debouncedLayoutUpdate = _.debounce(function () {
                $rootScope.$broadcast('CCC.window.resize');
            }, 300);

            $(window).resize(debouncedLayoutUpdate);
        }
    ]);

    // This keeps a predetermined list of query params in the url regardless of state change (QUERY_PARAM_WHITE_LIST)
    angular.module('CCC.Assess').run([

        '$rootScope',
        '$state',
        '$location',
        'QUERY_PARAM_WHITE_LIST',

        function ($rootScope, $state, $location, QUERY_PARAM_WHITE_LIST) {

            // list of query params that should persist between view changes
            var queryParamWhiteList = QUERY_PARAM_WHITE_LIST;
            var queryParamsCache = {};

            var updateQueryParamCache = function () {
                queryParamsCache = {};
                var searchParams = $location.search();

                angular.forEach(queryParamWhiteList, function (whiteListItem) {
                    if (searchParams[whiteListItem]) {
                        queryParamsCache[whiteListItem] = searchParams[whiteListItem];
                    }
                });
            };

            var resetWhiteListQueryParams = function () {
                var prop;
                for (prop in queryParamsCache) {
                    if (queryParamsCache.hasOwnProperty(prop)) {
                        $location.search(prop, queryParamsCache[prop]);
                    }
                }
            };

            // store current white listed query params
            $rootScope.$on('$stateChangeStart', function (e, toState, toParams) {
                updateQueryParamCache();
            });

            // add those back in after the state change
            $rootScope.$on('$stateChangeSuccess', function () {
                resetWhiteListQueryParams();
            });
        }
    ]);

    // Generic browser events / resizing / etc...
    angular.module('CCC.Assess').run([

        '$rootScope',

        function ($rootScope) {


            /*============ RESIZING ==============*/

            var resizeDebouncedBroadcast = function () {
                $rootScope.$broadcast('ccc-assess.resizeDebounced', $(window).width(), $(window).height());
                $rootScope.$apply();
            };

            var resizeDebounced = _.debounce(resizeDebouncedBroadcast, 300);

            // listen to browser resize events and fire event
            $(window).resize(function () {
                $rootScope.$broadcast('ccc-assess.resize', $(window).width(), $(window).height());
                resizeDebounced();
            });

        }
    ]);

    // XSRF
    angular.module('CCC.Assess').config([

        '$httpProvider',

        function ($httpProvider) {

            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");

            $httpProvider.interceptors.push(function () {
                return {
                    request: function (config) {

                        config.headers[header] = token;
                        return config;
                    }
                };
            });
        }
    ]);

})();
