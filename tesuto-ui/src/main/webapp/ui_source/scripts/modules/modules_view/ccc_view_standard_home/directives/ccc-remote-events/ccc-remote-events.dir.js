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

    angular.module('CCC.View.Home').directive('cccRemoteEvents', function () {

        return {

            restrict: 'E',

            scope: {
                'initialTestEventId': '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$q',
                'Moment',
                'CurrentUserService',
                'EventsAPIService',
                'LocationService',

                function ($scope, $element, $timeout, $q, Moment, CurrentUserService, EventsAPIService, LocationService) {

                    /*============ PRIVATE VARIABLES ============*/

                    var currentCollegeId = false;

                    var viewAnimationFinished = $q.defer();
                    var viewAnimationFinishedPromise = viewAnimationFinished.promise;


                    /*============ MODEL ============*/

                    $scope.loading = false;

                    $scope.initialTestEventLoading = false;

                    $scope.upcomingEvents = [];
                    $scope.pastEvents = [];

                    LocationService.setCurrentCollegeIdFromCurrentTestCenter();

                    $scope.initialCollegeId = LocationService.getCurrentCollegeId();

                    $scope.userCanCreateEvent = CurrentUserService.hasPermission('CREATE_TEST_EVENT');
                    $scope.userCanUpdateEvent = CurrentUserService.hasPermission('UPDATE_TEST_EVENT');


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var checkInitialTestEventId = function () {

                        if ($scope.initialTestEventId !== null) {

                            var activeInitialTestEvent = _.find($scope.upcomingEvents, function(testEvent) {
                                return testEvent.testEventId === $scope.initialTestEventId;
                            });

                            if (activeInitialTestEvent) {

                                $scope.initialTestEventLoading = true;

                                viewAnimationFinishedPromise.then(function () {
                                    $scope.$emit('ccc-remote-events.activeTestEventSelected', activeInitialTestEvent);
                                    $scope.initialTestEventLoading = false;
                                });
                            }

                            $scope.initialTestEventId = null;
                        }
                    };

                    var refreshData = function () {

                        $scope.loading = true;

                        EventsAPIService.getEventsByCollegeId(currentCollegeId).then(function(events) {

                            var startOfToday = new Moment.utc().startOf('day').valueOf();

                            var upcomingEvents = _.filter(events, function (event) {
                                return new Moment.utc(event.endDate).valueOf() >= startOfToday && !event.canceled;
                            });

                            $scope.upcomingEvents = upcomingEvents.sort(function (event1, event2) {
                                var date1 = new Moment.utc(event1.startDate).startOf('day').valueOf();
                                var date2 = new Moment.utc(event2.startDate).startOf('day').valueOf();
                                if (date1 === date2) {
                                    return event1.name.localeCompare(event2.name);
                                } else {
                                    return (date1 < date2) ? -1 : (date1 > date2) ? 1 : 0;
                                }
                            });

                            var pastEvents = _.filter(events, function (event) {
                                return new Moment.utc(event.endDate).valueOf() < startOfToday || event.canceled;
                            });

                            $scope.pastEvents = pastEvents.sort(function (event1, event2) {
                                var date1 = new Moment.utc(event1.startDate).startOf('day').valueOf();
                                var date2 = new Moment.utc(event2.startDate).startOf('day').valueOf();
                                if (date1 === date2) {
                                    return event1.name.localeCompare(event2.name);
                                } else {
                                    return (date1 < date2) ? -1 : (date1 > date2) ? 1 : 0;
                                }
                            });

                            checkInitialTestEventId();

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.addRemoteEvent = function (e) {
                        $scope.$emit('ccc-remote-event.addEvent', currentCollegeId);
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-remote-events.refresh', refreshData);

                    $scope.$on('ccc-test-event-list.eventClicked', function (e, testEvent, cccTestEventListId) {

                        if ($scope.loading || $scope.initialTestEventLoading) {
                            return;
                        }

                        if (cccTestEventListId === 'upcoming') {
                            $scope.$emit('ccc-remote-events.activeTestEventSelected', testEvent);
                        }
                    });

                    // the initial college id is passed to the college dropdown directive, then it fires an event
                    // here is where we will set the private currentCollegeId that is used thereafter
                    $scope.$on('ccc-colleges-list.collegeSelected', function (e, college) {
                        currentCollegeId = college.cccId;
                        LocationService.setCurrentCollegeId(currentCollegeId);
                        refreshData();
                    });


                    /*============ INITIALIZATION ===========*/

                    $timeout(function () {
                        viewAnimationFinished.resolve();
                    }, 600);
                }
            ],

            template: [

                '<h2 translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS.TITLE"></h2>',

                '<div class="row">',
                    '<div class="col-sm-6">',
                        '<ccc-colleges-dropdown class="btn-full-width-when-small" list-style="primary" auto-select-first-college="true" selected-ccc-id="initialCollegeId" is-disabled="loading || initialTestEventLoading"></ccc-colleges-dropdown>',
                    '</div>',
                    '<div class="col-sm-6 text-right">',
                        '<button class="btn btn-primary btn-icon-left btn-full-width-when-small" ccc-focusable ng-click="addRemoteEvent($event)" ng-if="userCanCreateEvent" ng-disabled="loading || initialTestEventLoading">',
                            '<i class="fa fa-plus" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS.BUTTON_ADD_EVENT"></span>',
                        '</button>',
                    '</div>',
                '</div>',


                '<h3 class="margin-top-md" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS.UPCOMING"></h3>',

                '<ccc-content-loading-placeholder ng-if="!upcomingEvents.length" no-results-info="!loading && !upcomingEvents.length" hide-default-no-results-text="true">',
                    '<div><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS.UPCOMING_EMPTY"></span> <a href="#" role="button" ng-click="addRemoteEvent($event)" ng-if="userCanCreateEvent"><i class="fa fa-plus-circle" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS.BUTTON_ADD_EVENT"></span></a></div>',
                '</ccc-content-loading-placeholder>',

                '<ccc-test-event-list id="upcoming" ng-if="!loading && upcomingEvents.length" test-events="upcomingEvents"></ccc-test-event-list>',


                '<h3 class="margin-top-md" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS.PAST"></h3>',

                '<ccc-content-loading-placeholder ng-if="!pastEvents.length" no-results-info="!loading && !pastEvents.length" hide-default-no-results-text="true">',
                    '<div translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS.PAST_EMPTY"></div>',
                '</ccc-content-loading-placeholder>',

                '<ccc-test-event-list id="past" ng-if="!loading && pastEvents.length" test-events="pastEvents" class="ccc-test-event-list-inactive"></ccc-test-event-list>'

            ].join('')

        };

    });

})();
