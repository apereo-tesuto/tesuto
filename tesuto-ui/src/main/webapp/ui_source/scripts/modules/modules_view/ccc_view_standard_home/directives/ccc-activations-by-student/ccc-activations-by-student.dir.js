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

    angular.module('CCC.View.Home').directive('cccActivationsByStudent', function () {

        return {

            restrict: 'E',

            scope: {
                students: "=?" // pass them in optionally or fire and event to update the list
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ============*/

                    $scope.students = $scope.students || [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.studentSelected = function (student) {
                        $scope.$emit('ccc-activations-by-student.studentSelected', student);
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-activations-by-student.dataUpdated', function (e, updatedStudents) {
                        $scope.students = updatedStudents;
                    });


                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<ul class="list-unstyled">',
                    '<li ccc-focusable ng-repeat="student in students track by student.cccId" ng-click="studentSelected(student)">',
                        '<div class="ccc-activation-inner">',
                            '<div class="student">',
                                '<div class="row">',
                                    '<div class="col col-md-6 col-lg-4">',
                                        '<div class="student-name">',
                                            '<ccc-user-name last-name="student.lastName" first-name="student.firstName" middle-initial="student.middleInitial"></ccc-user-name>',
                                        '</div>',
                                        '<div class="cccid">{{::student.cccId}}</div>',
                                    '</div>',
                                    '<div class="col col-md-6 col-lg-8">',
                                        '<div class="activations">',
                                            '<div class="row">',
                                                '<div ng-repeat="activation in student.activations track by activation.id" class="col col-md-6">',
                                                    '<div class="activation">',
                                                        '<div class="test-name dont-break-out">{{::activation.name}}</div>',
                                                        '<span class="test-status label label-default" translate="CCC_ACTIVATIONS.CARD.STATUS.{{::activation.status}}">{{::activation.status}}</span>',
                                                        // ready status
                                                        '<span ng-if="::activation.status === \'READY\'" class="icon-label label label-default test-status-icon label-info"><span class="icon fa fa-fw fa-play" role="presentation" aria-hidden="true"></span></span>',
                                                        // in-progress status
                                                        '<span ng-if="::activation.status === \'IN_PROGRESS\'" class="icon-label label label-default test-status-icon label-primary"><span class="icon fa fa-fw fa-repeat" role="presentation" aria-hidden="true"></span></span>',
                                                        // paused status
                                                        '<span ng-if="::activation.status === \'PAUSED\'" class="icon-label label label-warning test-status-icon label-primary"><span class="icon fa fa-fw fa-pause" role="presentation" aria-hidden="true"></span></span>',
                                                        // complete status
                                                        '<span ng-if="::activation.status === \'COMPLETE\'" class="icon-label label label-success test-status-icon label-primary"><span class="icon fa fa-fw fa-check" role="presentation" aria-hidden="true"></span></span>',
                                                    '</div>',
                                                '</div>',
                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</li>',
                '</ul>'

            ].join('')

        };

    });

})();
