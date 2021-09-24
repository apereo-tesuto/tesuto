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

    angular.module('CCC.View.Home').directive('cccRemoteEventsCreateSummary', function () {

        return {

            restrict: 'E',

            scope: {
                remoteEvent: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES / METHODS ============*/

                    var editEvent = function (remoteEvent) {
                        $scope.$emit('ccc-remote-events-create-summary.editDetails', $scope.remoteEvent);
                    };


                    /*============ MODEL ============*/

                    $scope.actions = [
                        {
                            title: 'CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.EDIT_TEST_EVENT',
                            icon: 'fa-pencil',
                            onClick: editEvent
                        }
                    ];


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.done = function () {
                        $scope.$emit('ccc-remote-events-create-summary.done', $scope.remoteEvent);
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-remote-event-summary.editStudents', function () {
                        $scope.$emit('ccc-remote-events-create-summary.editStudents', $scope.remoteEvent);
                    });
                    $scope.$on('ccc-remote-event-summary.done', $scope.done);

                    $scope.$on('ccc-remote-events-create-summary.refresh', function (e, remoteEvent) {
                        $scope.$broadcast('ccc-remote-event-summary.refresh', remoteEvent);
                    });
                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-xs-12">',

                        '<ccc-remote-event-summary remote-event="remoteEvent" actions="actions"></ccc-remote-event-summary>',

                        '<div class="ccc-form-submit-controls text-left">',

                            '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ccc-focusable ng-click="done()">',
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
