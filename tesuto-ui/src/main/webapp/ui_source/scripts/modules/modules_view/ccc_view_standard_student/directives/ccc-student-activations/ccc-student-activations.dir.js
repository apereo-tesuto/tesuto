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

    angular.module('CCC.View.Student').directive('cccStudentActivations', function () {

        return {

            restrict: 'E',

            scope: {
                student: '='
            },

            controller: [

                '$scope',
                '$element',
                '$state',
                'CCCUtils',
                'ModalService',
                'ActivationClass',
                'ActivationsAPIService',
                'CoerceActivationService',
                'CurrentStudentService',
                'ACTIVATION_STATUS_MAPS',
                'ACTIVATION_URGENCY_COMPARATOR',

                function ($scope, $element, $state, CCCUtils, ModalService, ActivationClass, ActivationsAPIService, CoerceActivationService, CurrentStudentService, ACTIVATION_STATUS_MAPS, ACTIVATION_URGENCY_COMPARATOR) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var setFirstFocus = function (targetElement) {
                        $element.find('[ccc-autofocus]').removeAttr('ccc-autofocus');
                        targetElement.attr('ccc-autofocus', 'ccc-autofocus');
                    };


                    /*============ MODEL ============*/

                    $scope.loading = true;
                    $scope.activeActivations = [];
                    $scope.completedActivations = [];
                    $scope.inactiveActivations = [];

                    $scope.collegesLoading = true;
                    $scope.collegesAvailableForPlacement = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getActivations = function () {

                        $scope.loading = true;
                        $scope.activeActivations = [];
                        $scope.completedActivations = [];
                        $scope.inactiveActivations = [];

                        ActivationsAPIService.getActivationsForStudent({username: $scope.student.username})
                            .then(function (activationList) {

                                return CoerceActivationService.attachTestLocationInfoToActivations(activationList).then(function () {

                                    var activations = CCCUtils.coerce(ActivationClass, activationList);

                                    $scope.activeActivations = _.filter(activations, function (activation) {
                                        return ACTIVATION_STATUS_MAPS['ACTIVE'][activation.status];
                                    });

                                    $scope.activeActivations.sort(ACTIVATION_URGENCY_COMPARATOR);

                                    $scope.completedActivations = _.filter(activations, function (activation) {
                                        return ACTIVATION_STATUS_MAPS['COMPLETE'][activation.status];
                                    });

                                    $scope.completedActivations = _.sortBy($scope.completedActivations, '_completedDateTimeStamp');
                                    $scope.completedActivations.reverse();

                                    $scope.inactiveActivations = _.filter(activations, function (activation) {
                                        return ACTIVATION_STATUS_MAPS['INACTIVE'][activation.status];
                                    });

                                });

                            }, function () {

                                $scope.activations = [];

                            }).finally(function () {

                                $scope.loading = false;
                                setFirstFocus($($element).find('.ccc-student-activations-header'));

                                $scope.$emit('ccc-student-activations.activationsLoaded', {
                                    active: $scope.activeActivations,
                                    completed: $scope.completedActivations,
                                    inactive: $scope.inactiveActivations
                                });
                            });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.goToActivation = function (event, activation) {

                        if (activation.deliveryType !== 'PAPER') {

                            setFirstFocus($(event.currentTarget));
                            $scope.$emit('ccc-student-activations.selected', activation);

                        } else {

                            ModalService.openAlertModal('CCC_STUDENT.ACTIVATIONS.MODAL_PAPER_WARNING.TITLE', 'CCC_STUDENT.ACTIVATIONS.MODAL_PAPER_WARNING.BODY');
                        }
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-student-activations.requestRefresh', getActivations);

                    $scope.$on('ccc-activation-card.getPlacement', function (e, activation) {
                        $scope.$emit('ccc-student-activations.getPlacement', activation);
                    });

                    $scope.$on('ccc-college-brand-list.collegeClicked', function (e, college) {
                        $scope.$emit('ccc-student-activations.getPlacementsForCollege', college);
                    });

                    $scope.$on('ccc-activation-card.showTestResults', function (e, activation, student) {
                        $scope.$emit('ccc-student-activations.showTestResults', activation);
                    });


                    /*============ INITIALIZATION ============*/

                    CurrentStudentService.getAvailableCollegesForPlacement().then(function (colleges) {
                        $scope.collegesAvailableForPlacement = colleges;
                        $scope.collegesLoading = false;
                    });

                    getActivations();
                }
            ],

            template: [

                '<div class="row">',

                    '<div class="col-sm-5 col-sm-push-7">',
                        '<h3 class="ccc-student-activations-section-title"><span class="ccc-student-activations-header" translate="CCC_STUDENT.ACTIVATIONS.MY_PLACEMENTS"></span></h3>',
                        '<p class="ccc-student-activations-section-desc" translate="CCC_STUDENT.ACTIVATIONS.MY_PLACEMENTS_HELP"></p>',

                        '<ccc-content-loading-placeholder ng-if="collegesLoading || !collegesLoading && !collegesAvailableForPlacement.length" no-results-info="true" hide-default-no-results-text="true">',
                             '<div translate="CCC_STUDENT.ACTIVATIONS.NO_COLLEGE_PLACEMENTS"></div>',
                         '</ccc-content-loading-placeholder>',

                        '<ccc-college-brand-list ng-if="!collegesLoading" colleges="collegesAvailableForPlacement"></ccc-college-brand-list>',

                    '</div>',

                    '<div class="col-sm-7 col-sm-pull-5">',

                        '<div class="ccc-student-activations-subsection">',

                            '<h3 class="ccc-student-activations-section-title"><span ccc-autofocus class="ccc-student-activations-header" tabindex="0" translate="CCC_STUDENT.ACTIVATIONS.ACTIVE"></span></h3>',
                            '<p class="ccc-student-activations-section-desc" translate="CCC_STUDENT.ACTIVATIONS.ACTIVE_HELP"></p>',
                            '<div class="ccc-student-activations-assessment-list">',
                                '<ccc-activation-card class="clickable" role="presentation" ',
                                    'activation="activation" ',
                                    'header-controls="true" ',
                                    'footer-controls="false" ',
                                    'is-student="true" ',
                                    'activation-details="true" ',
                                    'allow-logs="false" ',
                                    'ng-repeat="activation in activeActivations track by activation.activationId" ',
                                    'entity-id="{{::activation.activationId}}" ',
                                    'ng-click="goToActivation($event, activation)">',
                                '</ccc-activation-card>',
                            '</div>',

                            '<div ng-if="loading || !loading && activeActivations.length === 0">',
                                '<ccc-content-loading-placeholder no-results-info="!loading && activeActivations.length === 0" hide-default-no-results-text="true">',
                                    '<span translate="CCC_STUDENT.ACTIVATIONS.NO_ACTIVATIONS"></span>',
                                '</ccc-content-loading-placeholder>',
                            '</div>',

                        '</div>',


                        '<div class="ccc-student-activations-subsection">',
                            '<h3 translate="CCC_STUDENT.ACTIVATIONS.COMPLETE" class="ccc-student-activations-section-title"></h3>',
                            //'<p class="ccc-student-activations-section-desc" translate="CCC_STUDENT.ACTIVATIONS.COMPLETE_HELP"></p>',
                            '<div class="ccc-student-activations-assessment-list">',
                                '<ccc-activation-card ',
                                    'activation="activation" ',
                                    'header-controls="true" ',
                                    'footer-controls="false" ',
                                    'is-student="true" ',
                                    'activation-details="true" ',
                                    'allow-logs="false" ',
                                    'allow-placement="true" ',
                                    'ng-repeat="activation in completedActivations track by activation.activationId" ',
                                    'entity-id="{{::activation.activationId}}" tabindex="0">',
                                '</ccc-activation-card>',
                            '</div>',

                            '<div ng-if="loading || !loading && completedActivations.length === 0">',
                                '<ccc-content-loading-placeholder no-results-info="!loading && completedActivations.length === 0" hide-default-no-results-text="true">',
                                    '<span translate="CCC_STUDENT.ACTIVATIONS.NO_ASSESSMENTS"></span>',
                                '</ccc-content-loading-placeholder>',
                            '</div>',

                        '</div>',


                        '<div ng-if="inactiveActivations.length > 0">',
                            '<div class="ccc-student-activations-subsection">',
                                '<h3 translate="CCC_STUDENT.ACTIVATIONS.INACTIVE"  class="ccc-student-activations-section-title"></h3>',
                                '<p class="ccc-student-activations-section-desc" translate="CCC_STUDENT.ACTIVATIONS.INACTIVE_HELP"></p>',
                                '<div class="ccc-student-activations-assessment-list">',
                                    '<ccc-activation-card ',
                                        'activation="activation" ',
                                        'header-controls="false" ',
                                        'footer-controls="false" ',
                                        'is-student="true" ',
                                        'activation-details="true" ',
                                        'ng-repeat="activation in inactiveActivations track by activation.activationId" ',
                                        'entity-id="{{::activation.activationId}}" tabindex="0">',
                                    '</ccc-activation-card>',
                                '</div>',
                            '</div>',
                            '<div ng-if="loading || !loading && inactiveActivations.length === 0">',
                                '<ccc-content-loading-placeholder no-results-info="!loading && inactiveActivations.length === 0" hide-default-no-results-text="true">',
                                    '<span translate="CCC_STUDENT.ACTIVATIONS.NO_ASSESSMENTS"></span>',
                                '</ccc-content-loading-placeholder>',
                            '</div>',
                        '</div>',

                    '</div>',


                '</div>'

            ].join('')

        };

    });

})();
