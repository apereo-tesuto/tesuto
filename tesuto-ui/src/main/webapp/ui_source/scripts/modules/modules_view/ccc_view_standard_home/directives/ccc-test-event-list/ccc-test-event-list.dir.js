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

    angular.module('CCC.View.Home').directive('cccTestEventList', function () {

        return {

            restrict: 'E',

            scope: {
                testEvents: '='
            },

            controller: [

                '$scope',
                '$element',
                'Moment',
                'RemoteEventService',

                function ($scope, $element, Moment, RemoteEventService) {

                    /*============ PRIVATE VARIABLES ============*/

                    var testEventListId = $element.attr('id') || '';


                    /*============ MODEL ============*/


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var initialize = function () {

                        _.each($scope.testEvents, function (testEvent) {
                            RemoteEventService.attachLocationMetaData(testEvent);
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.eventClicked = function (event) {
                        $scope.$emit('ccc-test-event-list.eventClicked', event, testEventListId);
                    };

                    $scope.getTestEventMonth = function (event) {
                        return new Moment.utc(event.startDate).format('MMM');
                    };

                    $scope.getTestEventDay = function (event) {
                        return new Moment.utc(event.startDate).format('D');
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ===========*/

                    initialize();
                }
            ],

            template: [

                '<ul class="event-list">',
                    '<li ng-repeat="event in testEvents track by event.testEventId" tabindex="0" ccc-focusable ng-click="eventClicked(event)">',
                        '<h4 class="title">{{::event.name}}</h4>',
                        '<div class="date">',
                            '<span class="month">{{::getTestEventMonth(event)}}</span>',
                            '<span class="day">{{::getTestEventDay(event)}}</span>',
                        '</div>',
                        '<div class="proctor">',
                            '<span class="name">',
                                '<span class="firstname">{{::event.proctorFirstName}}</span> ',
                                '<span class="lastname">{{::event.proctorLastName}}</span>',
                            '</span>',
                            '<span aria-hidden="true" class="info-spacer">|</span>',
                            '<span class="location">{{::event.metaData.location.name}}</span>',
                        '</div>',
                    '</li>',
                '</ul>'

            ].join('')

        };

    });

})();
