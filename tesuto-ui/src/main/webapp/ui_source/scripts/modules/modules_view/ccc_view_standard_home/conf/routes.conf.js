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

    /*========== ROUTES ============*/

    angular.module('CCC.View.Home').config([

        '$stateProvider',
        '$urlRouterProvider',
        'RoutePermissionsServiceProvider',
        'ASSESSMENTS_DISABLED',

        function ($stateProvider, $urlRouterProvider, RoutePermissionsServiceProvider, ASSESSMENTS_DISABLED) {

            /*============ PRIVATE VARIABLES / METHODS ============*/

            var assessmentsDisabledResolver = ['$q', '$timeout', 'ASSESSMENTS_DISABLED', '$state', function ($q, $timeout, ASSESSMENTS_DISABLED, $state) {

                var redirectDeferred = $q.defer();

                // timeout required for the promise rejection to work
                $timeout(function () {

                    if (ASSESSMENTS_DISABLED) {
                        $state.go('home', {}, { reload: true });
                        redirectDeferred.reject();
                    } else {
                        redirectDeferred.resolve();
                    }
                });

                return redirectDeferred.promise;
            }];


            /*============ CATCH ALLS AND FALL BACKS ============*/

            // our fallback is just to go to home
            $urlRouterProvider.otherwise("/home");


            /*============ LOGIN / LOGOUT ROUTES ============*/

            // handle login passthrough to the server
            $stateProvider.state('login', {
                url: "/login",
                resolve: {
                    redirect: ['$window', '$timeout', function ($window, $timeout) {
                        // this is an actual backend endpoint so lets go there
                        $timeout(function () { // wrap it in a timeout because of a safari bug
                            $window.location.reload(true);
                        }, 1);
                    }]
                }
            });

            // handle logout passthrough to the server
            $stateProvider.state('logout', {
                url: "/logout",
                resolve: {
                    redirect: ['$window', '$timeout', function ($window, $timeout) {
                        // this is an actual backend endpoint so lets go there
                        $timeout(function () { // wrap it in a timeout because of a safari bug
                            $window.location.reload(true);
                        }, 1);
                    }]
                }
            });


            /*============ MAIN ROUTES ============*/

            // home page route
            $stateProvider.state('home', {
                url: "/home",
                defaultView: true,
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.HOME',
                template: '<ccc-route-home></ccc-route-home>',
                resolve: {
                    counselorOnlyRedirect: ['$q', '$timeout', 'CurrentUserService', '$state', function ($q, $timeout, CurrentUserService, $state) {

                        var homeDeferred = $q.defer();

                        // timeout required for the promise rejection to work
                        $timeout(function () {

                            if (CurrentUserService.isOnlyCounselor()) {
                                $state.go('studentLookup', {cccId: null}, { reload: true });
                                homeDeferred.reject();
                            } else {
                                homeDeferred.resolve();
                            }
                        });

                        return homeDeferred.promise;
                    }]
                }
            });

            // student lookup
            $stateProvider.state('studentLookup', {
                url: "/home/studentlookup",
                permissions: ['FIND_STUDENT'],
                params: { cccId: null },
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.STUDENT_LOOKUP',
                template: '<ccc-route-student-lookup></ccc-route-student-lookup>'
            });

            // proctor location
            $stateProvider.state('proctorLocation', {
                url: "/home/proctorlocation",
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.HOME',
                template: '<ccc-route-proctor-location></ccc-route-proctor-location>',
                resolve: {
                    assessmentDisabledResolver: assessmentsDisabledResolver
                }
            });

            // assessment scoring
            $stateProvider.state('paperPencil', {
                url: "/home/paperpencil",
                defaultView: true,
                permissions: ['PRINT_ASSESSMENT'],
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.PAPER_PENCIL',
                template: '<ccc-route-paper-pencil></ccc-route-paper-pencil>',
                resolve: {
                    assessmentDisabledResolver: assessmentsDisabledResolver
                }
            });

            // view not available (this one should be configured last and theoretically should be close to impossible to trigger)
            $stateProvider.state('viewNotAvailable', {
                url: "/home/unavailable",
                defaultView: true,
                pageTitle: 'CCC_VIEW_STANDARD_LAYOUT.PAGE_TITLES.NOT_AVAILABLE',
                template: '<ccc-view-common-not-available></ccc-view-common-not-available>'
            });

            // create activations (this is a standalone version of the create activation workflow, normally it is embedded in another workflow)
            $stateProvider.state('createActivation', {
                url: "/home/createactivation",
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.CREATE_ACTIVATION',
                template: '<ccc-route-create-activation></ccc-route-create-activation>',
                resolve: {
                    assessmentDisabledResolver: assessmentsDisabledResolver
                }
            });

            // bulk activations
            $stateProvider.state('bulkActivation', {
                url: "/home/bulkactivation",
                params: { initialDeliveryType: null, onCompleteState: null },
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.BULK_ACTIVATION',
                template: '<ccc-route-bulk-activation></ccc-route-bulk-activation>',
                resolve: {
                    assessmentDisabledResolver: assessmentsDisabledResolver
                }
            });

            // manage users
            $stateProvider.state('manageUsers', {
                url: "/home/manageusers",
                permissions: ['CREATE_USER'],
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.MANAGE_USERS',
                template: '<ccc-route-manage-users></ccc-route-manage-users>'
            });

            // manage locations
            $stateProvider.state('manageLocations', {
                url: "/home/managelocations",
                permissions: ['VIEW_DISTRICTS_AND_COLLEGES_FOR_CURRENT_USER_WITH_ALL_TEST_LOCATIONS'],
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.MANAGE_LOCATIONS',
                template: '<ccc-route-manage-locations></ccc-route-manage-locations>'
            });

            // manage placements
            $stateProvider.state('managePlacements', {
                url: "/home/manageplacements",
                permissions: ['GET_DISCIPLINE'],
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.MANAGE_PLACEMENTS',
                template: '<ccc-route-manage-placements></ccc-route-manage-placements>'
            });

            // remote events
            $stateProvider.state('remoteEvents', {
                url: "/home/remoteevents",
                permissions: ['VIEW_TEST_EVENT'],
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.REMOTE_EVENTS',
                template: '<ccc-route-remote-events></ccc-route-remote-events>',
                params: { testEventId: null },
                resolve: {
                    assessmentDisabledResolver: assessmentsDisabledResolver
                }
            });

            // class report
            $stateProvider.state('classReport', {
                url: "/home/classreport",
                permissions: ['VIEW_INSTRUCTOR_CLASS_REPORT'],
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.CLASS_REPORT',
                template: '<ccc-route-class-report></ccc-route-class-report>',
                resolve: {
                    assessmentDisabledResolver: assessmentsDisabledResolver
                }
            });

            // student result
            $stateProvider.state('studentResult', {
                url: "/home/studentresult",
                permissions: ['FIND_STUDENT'],
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.STUDENT_RESULT',
                template: '<ccc-route-student-result></ccc-route-student-result>',
                params: { student: null }
            });

            // help
            $stateProvider.state('help', {
                url: "/home/help",
                pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.HELP',
                template: '<ccc-route-help></ccc-route-help>'
            });


            /*============ FOOTER LINKS ============*/

            // terms of use
            $stateProvider.state('termsOfUse', {
                url: "/home/termsofuse",
                pageTitle: 'Terms of Use',
                template: '<ccc-terms-of-use></ccc-terms-of-use>'
            });

            // privacy statement
            $stateProvider.state('privacyStatement', {
                url: "/home/privacystatement",
                pageTitle: 'Privacy Statement',
                template: '<ccc-privacy-statement></ccc-privacy-statement>'
            });

            // accessibility
            $stateProvider.state('accessibility', {
                url: "/home/accessibility",
                pageTitle: 'Accessibility',
                template: '<ccc-accessibility></ccc-accessibility>'
            });
        }
    ]);

})();
