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

    angular.module('CCC.View.Home').directive('cccActivateStudent', function () {

        return {

            restrict: 'E',

            scope: {
                student: '=?',
                location: '=?',
                initialDeliveryType: '@?' // optional, could be ONLINE or PAPER
            },

            controller: [

                '$scope',
                '$q',
                '$translate',
                'ActivationsAPIService',
                'CoerceActivationService',
                'CurrentUserService',
                'Moment',
                'CollisionService',
                'NotificationService',

                function ($scope, $q, $translate, ActivationsAPIService, CoerceActivationService, CurrentUserService, Moment, CollisionService, NotificationService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    /*============= MODEL =============*/

                    $scope.student = $scope.student || '';
                    $scope.location = $scope.location || '';
                    $scope.user = CurrentUserService.getUser();

                    $scope.submitted = false;
                    $scope.processing = false;
                    $scope.showAccommodations = false;

                    $scope.activationData = {
                        assessments: [],
                        locationId: $scope.location.id,
                        userId: $scope.student.cccId,
                        attributes: {
                            accommodations: '',
                            accommodationsOther: ''
                        },
                        deliveryType: '',
                        endDate: null
                    };


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var getActivationsToCreate = function (student) {

                        var activationsToCreate = [];

                        _.each($scope.activationData.assessments, function (assessment) {

                            activationsToCreate.push({
                            	assessmentScopedIdentifier: {
                                    identifier: assessment.identifier,
                                    namespace: assessment.namespace
                                },
                                locationId: $scope.location.id,
                                attributes: {
                                    accommodations: JSON.stringify($scope.activationData.attributes.accommodations),
                                    accommodationsOther: $scope.activationData.attributes.accommodationsOther
                                },
                                userId: student.cccId,
                                deliveryType: assessment.deliveryType,
                                startDate: Moment().valueOf(),
                                endDate: Moment().add(7, 'days').endOf('day').valueOf() // we used have different values based on assessment.deliveryType === 'PAPER'
                            });
                        });

                        return activationsToCreate;
                    };

                    var getAssessmentNameById = function (assessmentScopedIdentifier) {
                        var assessmentName = [];
                        _.each($scope.activationData.assessments, function (assessment) {
                            if (assessmentScopedIdentifier.identifier === assessment.identifier) {
                                assessmentName.push({
                                    title: assessment.title
                                });
                            }
                        });

                        return assessmentName[0].title;
                    };

                    var getActivationId = function (results) {
                        var responseHeader = results.headers()['location'];
                        var parsedHeader = responseHeader.split('/');
                        var activationId = parsedHeader[parsedHeader.length - 1];
                        return activationId;
                    };

                    var getActivationPromise = function (activation) {
                        var deferred = $q.defer();

                        // we want resolved promises regardless, and just pass on the results
                        ActivationsAPIService.createActivation(activation).then(function (results) {

                            CoerceActivationService.attachTestLocationInfoToActivations([activation]).then(function () {
                                $scope.$emit('ccc-activate-student.processing');
                                deferred.resolve({results: results, activation: activation, activationId: getActivationId(results)});
                            });

                        }, function (err) {

                            return deferred.reject(err);
                        });

                        return deferred.promise;
                    };

                    var createActivations = function (activationsToCreate) {
                        var activationPromises = [];

                        _.each(activationsToCreate, function (activation) {
                            activationPromises.push(getActivationPromise(activation));
                        });

                        // After all creation promises have been resolved
                        return $q.all(activationPromises).then(function (results) {

                            var detailedResults = _.map(results, function (activationData) {

                                return {
                                    status: activationData.results.status,
                                    statusText: activationData.results.statusText,
                                    assessmentTitle: getAssessmentNameById(activationData.activation.assessmentScopedIdentifier),
                                    assessmentScopedIdentifier: activationData.activation.assessmentScopedIdentifier,
                                    locationId: activationData.activation.locationId,
                                    deliveryType: activationData.activation.deliveryType,
                                    activationId: activationData.activationId,
                                    collegeName: activationData.activation.collegeName,
                                    startDate: activationData.activation.startDate,
                                    endDate: activationData.activation.endDate
                                };
                            });

                            var resultsSummary = {
                                results: detailedResults,
                                accommodationsOther: $scope.activationData.attributes.accommodationsOther,
                                location: $scope.location.name,
                                accommodations: JSON.stringify($scope.activationData.attributes.accommodations),
                                deliveryType: $scope.activationData.deliveryType,
                                activatedBy: $scope.user.displayName,
                                createDate: Moment().valueOf()
                            };

                            $scope.$emit('ccc-activate-student.created', resultsSummary);
                        });
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.attemptCreateActivation = function () {

                        $scope.$broadcast('ccc-activate-student.createActivation', $scope.student);
                        $scope.submitted = true;
                        $scope.processing = true;

                        var activationsToCreateCandidates = getActivationsToCreate($scope.student);

                        var accommodationsError = false;

                        _.each($scope.activationData.attributes.accommodations, function (accommodation) {
                            if (accommodation.code === 'O' && $scope.activationData.attributes.accommodationsOther === '') {
                                accommodationsError = true;
                            }
                        });

                        if (accommodationsError || activationsToCreateCandidates.length === 0) {
                            if (accommodationsError) {
                                $scope.$broadcast('ccc-activate-student.accommodationsError');
                            }
                            if (activationsToCreateCandidates.length === 0) {
                                $scope.$broadcast('ccc-activate-student.assessmentsError');
                            }
                            $scope.processing = false;
                            return;
                        }

                        if (_.isNull($scope.location) || $scope.location === '') {

                            $scope.notifyMinError = NotificationService.open({
                                icon: 'fa fa-exclamation-triangle',
                                title: $translate.instant('CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.NO_LOCATION'),
                                message: '',
                                uid: 'ccc-activate-student.error'
                            },
                            {
                                delay: 0,
                                type: "warning",
                                allow_dismiss: true
                            });

                            $scope.processing = false;
                            return;
                        }

                        CollisionService.getDuplicateActivations(activationsToCreateCandidates, $scope.student).then(function (duplicateActivations) {

                            if (duplicateActivations.length) {

                                CollisionService.openResolutionModal(duplicateActivations, $scope.student).then(function (deactivatedActivations) {

                                    // success (deactivate)
                                    createActivations(activationsToCreateCandidates);

                                }, function (isUserCancel) {

                                    // cancel (pop view)
                                    if (isUserCancel === true) {
                                        $scope.$emit('ccc-activate-student.cancel');
                                    }
                                });

                            } else {
                                createActivations(activationsToCreateCandidates);
                            }

                        }).finally(function () {
                            $scope.processing = false;
                        });
                    };


                    /*============= LISTENERS =============*/

                    $scope.$on('ccc-assessments-list.selectedAssessments', function (e, selectedAssessments) {
                        $scope.activationData.assessments = selectedAssessments;
                    });

                    $scope.$on('ccc-accommodations-list.chosenAccommodations', function (e, chosenAccommodations, otherAccommodations) {
                        $scope.activationData.attributes.accommodations = chosenAccommodations;
                        $scope.activationData.attributes.accommodationsOther = otherAccommodations;
                    });

                    $scope.$on('ccc-locations-list.testCenterUpdated', function (e, testCenter) {
                        $scope.location = testCenter;
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
                        '<ccc-label-required></ccc-label-required>',
                        '<div class="row margin-bottom-md">',
                            '<div class="col-md-12">',
                                '<h3 class="section-title"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.LOCATION"></span></h3>',
                                '<ccc-locations-list ccc-autofocus class="btn-full-width" ng-class="{ error: submitted && !location }"></ccc-locations-list>',
                            '</div>',
                        '</div>',

                        '<div class="row margin-bottom-sm">',
                            '<div class="col-md-12">',
                                '<h3 class="section-title"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ASSESSMENTS"></span></h3>',
                                '<ccc-assessments-list initial-delivery-type="{{initialDeliveryType}}"></ccc-assessments-list>',
                            '</div>',
                        '</div>',

                        '<div class="row">',
                            '<div class="col-md-12">',
                                '<h3 class="section-title" translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ACCOMMODATIONS.ACCOMMODATIONS"></h3>',
                                '<button ng-click="showAccommodations = !showAccommodations" class="btn btn-default btn-full-width-when-small">',
                                    '<span class="fa fa-plus" ng-if="!showAccommodations"></span> ',
                                    '<span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ACCOMMODATIONS.ADD_ACCOMMODATIONS" ng-if="!showAccommodations"></span>',
                                    '<span class="fa fa-times" ng-if="showAccommodations"></span> ',
                                    '<span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ACCOMMODATIONS.REMOVE_ACCOMMODATIONS" ng-if="showAccommodations"></span>',
                                '</button>',
                                '<ccc-accommodations-list ng-hide="!showAccommodations" show-accommodations="showAccommodations" student="student"></ccc-accommodations-list>',
                            '</div>',
                        '</div>',

                        '<div class="row actions">',
                            '<div class="col-md-12">',
                                '<hr />',
                                '<button class="btn btn-primary btn-submit-button btn-full-width-when-small" ng-click="attemptCreateActivation()" ng-disabled="submitted || processing">',
                                '<span class="fa fa-check noanim" aria-hidden="true" ng-if="!processing"></span>',
                                '<span class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="processing"></span>',
                                ' <span translate="CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ACTIVATE"></span></button>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
