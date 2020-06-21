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

    angular.module('CCC.View.Home').directive('cccRemoteEventsDetailsEdit', function () {

        return {

            restrict: 'E',

            scope: {
                remoteEvent: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES ============*/


                    /*============ MODEL ============*/

                    $scope.submitted = false;

                    $scope.loading = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var doSubmit = function () {

                        $scope.submitted = true;

                        if ($scope['remoteEventCreateForm'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid, select.ng-invalid')[0]).focus();
                            return;
                        }

                        $scope.loading = true;

                        if ($scope.remoteEventCreateForm.$dirty || $scope.remoteEvent.testEventId === false) {

                            // note, the save method looks for an existin testEventId, if it exists it updates, else it creates
                            // the create method will get the new testEventId from the server and will add it back to the class instance
                            $scope.remoteEvent.save().then(function () {

                                $scope.remoteEventCreateForm.$setPristine();

                                // let's give the complex form logic a second to switch to pristine (which can take some time on large forms) to get a better animation before we announce a save was completed
                                $timeout(function () {
                                    $scope.$emit('ccc-remote-events-details-edit.saved', $scope.remoteEvent);
                                }, 200);

                            }).finally(function () {

                                $timeout(function () {
                                    $scope.loading = false;
                                }, 600);
                            });

                        // here no change was made so just move along
                        } else {

                            $scope.$emit('ccc-remote-events-details-edit.saved', $scope.remoteEvent);

                            // this prevents double clicking etc
                            $timeout(function () {
                                $scope.loading = false;
                            }, 600);
                        }
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.attemptDoSubmit = function () {
                        $timeout(doSubmit, 1);
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-remote-events-details-edit.cancel');
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-remote-event-form.cancel', function () {
                        $scope.$emit('ccc-remote-events-add.cancel');
                    });
                }
            ],

            template: [

                '<form name="remoteEventCreateForm" novalidate>',

                    '<div class="row">',
                        '<div class="col-md-8 col-md-offset-2">',

                            '<ccc-remote-event-form remote-event="remoteEvent" submitted="submitted" is-disabled="loading"></ccc-remote-event-form>',

                            '<div class="ccc-form-submit-controls text-left">',

                                '<button class="btn btn-primary pull-right" ccc-focusable type="submit" ng-click="attemptDoSubmit()" ng-disabled="loading || !remoteEventCreateForm.$dirty">',
                                    '<i class="fa fa-save noanim" aria-hidden="true" ng-if="!loading || !submitted"></i> ',
                                    '<i class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="loading && submitted"></i> ',
                                    '<span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-ADD.BUTTON_SAVE"></span> ',
                                '</button>',

                                '<button class="btn btn-default btn-icon-left btn-full-width-when-small" ng-click="cancel()" ng-disabled="loading">',
                                    '<i class="fa fa-chevron-left" aria-hidden="true"></i> ',
                                    '<span translate="Cancel"></span> ',
                                '</button>',
                            '</div>',

                        '</div>',
                    '</div>',

                '</form>'
            ].join('')

        };

    });

})();
