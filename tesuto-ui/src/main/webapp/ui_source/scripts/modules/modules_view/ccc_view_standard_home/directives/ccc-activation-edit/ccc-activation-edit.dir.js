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

    angular.module('CCC.View.Home').directive('cccActivationEdit', function () {

        return {

            restrict: 'E',

            scope: {
                activation: '=',
                student: '=',
                location: '=?'
            },

            controller: [

                '$scope',
                '$translate',
                'CurrentUserService',
                'CollisionService',

                function ($scope, $translate, CurrentUserService, CollisionService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/


                    /*============= MODEL =============*/

                    $scope.user = CurrentUserService.getUser();

                    $scope.submitted = false;
                    $scope.processing = false;
                    $scope.showAccommodations = false;

                    var updateActivation = function () {

                        return $scope.activation.update().then(function () {
                            $scope.$emit('ccc-activation-edit.success');
                        });
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.attemptEditActivation = function () {

                        $scope.$broadcast('ccc-activation-edit.editActivation', $scope.student);
                        $scope.submitted = true;
                        $scope.processing = true;

                        var accommodationsError = false;

                        _.each($scope.activation.attributes.accommodations, function (accommodation) {
                            if (accommodation.code === 'O' && $scope.activation.attributes.accommodationsOther === '') {
                                accommodationsError = true;
                            }
                        });

                        if (accommodationsError) {
                            $scope.$broadcast('ccc-activation-edit.accommodationsError');
                            $scope.processing = false;
                            return;
                        }

                        // Potentially remove since it's not possible to create a collision at this point.
                        CollisionService.getDuplicateActivations([$scope.activation], $scope.student).then(function (duplicateActivations) {

                            if (duplicateActivations.length) {

                                return CollisionService.openResolutionModal(duplicateActivations, $scope.student).then(function (deactivatedActivations) {

                                    // success (deactivate)
                                    return updateActivation();

                                }, function (isUserCancel) {

                                    // cancel (pop view)
                                    if (isUserCancel === true) {
                                        $scope.$emit('ccc-activation-edit.cancel');
                                    }
                                });

                            } else {
                                return updateActivation();
                            }

                        }).finally(function () {
                            $scope.processing = false;
                        });
                    };


                    /*============= LISTENERS =============*/

                    $scope.$on('ccc-accommodations-list.chosenAccommodations', function (e, chosenAccommodations, otherAccommodations) {
                        $scope.activation.attributes.accommodations = chosenAccommodations;
                        $scope.activation.attributes.accommodationsOther = otherAccommodations;
                    });


                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-md-6 student margin-bottom-double">',
                        '<ccc-student-user class="ccc-user-as-header ccc-user" user="student"></ccc-student-user>',
                    '</div>',

                    '<div class="col-md-6 activation-options">',
                        '<div class="row">',
                            '<div class="col-md-12">',
                                '<h3 class="section-title" translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.LOCATION"></h3>',
                                '<p class="well"><strong>{{::activation.collegeName}} – {{::activation.locationLabel}}</strong></p>',
                            '</div>',
                        '</div>',

                        '<div class="row margin-bottom-sm">',
                            '<div class="col-sm-12">',
                                '<h3 class="section-title" translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ASSESSMENTS"></h3>',
                                '<p class="well"><strong>{{::activation.assessmentTitle}}</strong></p>',
                            '</div>',
                        '</div>',

                        '<div class="row">',
                            '<div class="col-md-12">',
                                '<h3 class="section-title" translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ACCOMMODATIONS.ACCOMMODATIONS"></h3>',
                                '<p class="alert alert-info" translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ACCOMMODATIONS.PRIVACY_MSG"></p>',
                                '<button ng-click="showAccommodations = !showAccommodations" class="btn btn-default btn-full-width-when-small">',
                                    '<span class="fa fa-plus" ng-if="!showAccommodations"></span> ',
                                    '<span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ACCOMMODATIONS.ADD_ACCOMMODATIONS" ng-if="!showAccommodations"></span>',
                                    '<span class="fa fa-times" ng-if="showAccommodations"></span> ',
                                    '<span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ACCOMMODATIONS.REMOVE_ACCOMMODATIONS" ng-if="showAccommodations"></span>',
                                '</button>',
                                '<ccc-accommodations-list ng-hide="!showAccommodations" show-accommodations="showAccommodations" student="student" prepopulated-accommodations="activation.attributes"></ccc-accommodations-list>',
                            '</div>',
                        '</div>',

                        '<div class="row actions">',
                            '<div class="col-md-12">',
                                '<hr />',
                                '<button class="btn btn-primary btn-submit-button btn-full-width-when-small" ng-click="attemptEditActivation()">',
                                '<span class="fa fa-check noanim" aria-hidden="true" ng-if="!processing"></span>',
                                '<span class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="processing"></span>',
                                ' <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.SAVE"></span></button>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
