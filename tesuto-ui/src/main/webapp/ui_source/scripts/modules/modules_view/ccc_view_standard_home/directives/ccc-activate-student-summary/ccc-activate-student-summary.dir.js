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

    angular.module('CCC.View.Home').directive('cccActivateStudentSummary', function () {

        return {

            restrict: 'E',

            scope: {
                student: '=',
                summary: '='
            },

            controller: [

                '$rootScope',
                '$scope',
                '$filter',
                '$q',
                'Moment',
                'CCCUtils',
                'ActivationClass',
                'ModalService',
                'CurrentUserService',
                'PrintActivationService',

                function ($rootScope, $scope, $filter, $q, Moment, CCCUtils, ActivationClass, ModalService, CurrentUserService, PrintActivationService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var STATUS_CLASS_MAP = {
                        'IN_PROGRESS': 'status-in-progress',
                        'COMPLETE': 'status-complete',
                        'DEACTIVATED': 'status-deactivated',
                        'EXPIRED': 'status-expired',
                        'READY': 'status-ready'
                    };


                    /*============= MODEL =============*/

                    $scope.controls = true;
                    $scope.activations = [];

                    $scope.allowDeactivate = CurrentUserService.hasPermission('CANCEL_ACTIVATION');
                    $scope.allowReactivate = CurrentUserService.hasPermission('REACTIVATE_ACTIVATION');
                    $scope.allowPrint = CurrentUserService.hasPermission('PRINT_ASSESSMENT_SESSION');
                    $scope.allowScore = false;

                    var updateActivationList = function () {
                        _.each($scope.summary.results, function (activation) {

                            $scope.activations.push({
                                activationId: activation.activationId,
                                assessmentTitle: activation.assessmentTitle,
                                assessmentScopedIdentifier: {
                                    identifier: activation.assessmentScopedIdentifier.identifier,
                                    namespace: activation.assessmentScopedIdentifier.namespace
                                },
                                attributes: {
                                    accommodations: $scope.summary.accommodations,
                                    accommodationsOther: $scope.summary.accommodationsOther
                                },
                                endDate: activation.endDate,
                                deliveryType: activation.deliveryType,
                                creatorName: $scope.summary.activatedBy,
                                createDate: $scope.summary.createDate,
                                locationLabel: $scope.summary.location,
                                locationId: activation.locationId,
                                collegeName: activation.collegeName,
                                status: 'READY',
                                statusChangeOn: new Moment.utc().valueOf(),
                                statusChangeOnFormattedLong: $filter('date')(activation.statusChangeOn, 'd MMM yyyy h:mm a'),
                                timeSpentOnAssessment: 0,
                                userId: $scope.student.cccId
                            });
                        });

                        $scope.activations = CCCUtils.coerce(ActivationClass, $scope.activations);
                    };

                    var checkActivationStatus = function () {
                        // checking to see if all activations have been deactivated
                        var status = [];

                        _.each($scope.activations, function (activation) {
                            status.push(activation.status);
                        });

                        if (_.uniq(status).length === 1) {
                            $scope.controls = false;
                        }
                    };


                    /*============= MODEL DEPENDENT METHODS =============*/

                    /*============= BEHAVIOR =============*/

                    $scope.deactivateAll = function () {

                        var modalScope = $rootScope.$new();
                        modalScope.activation = _.filter($scope.activations, function (activation) { return activation.status !== "DEACTIVATED"; });
                        modalScope.student = $scope.student;

                        var completionModal = ModalService.open({
                            scope: modalScope,
                            template: '<ccc-modal-deactivate-activation modal="modal" student="student" activation="activation"></ccc-modal-deactivate-activation>'
                        });

                        completionModal.result.then(function () {
                            var promises = [];

                            _.each(modalScope.activation, function (activation) {
                                promises.push(activation.deactivate());
                            });

                            $q.all(promises).then(function () {
                                $scope.$broadcast('ccc-activation-card.requestRefresh');
                                checkActivationStatus();
                                $scope.$emit('ccc-activate-student-summary.deactivated');
                            });
                        });
                    };

                    $scope.okay = function () {
                        $scope.$emit('ccc-activate-student-summary.okay');
                    };


                    /*============= LISTENERS =============*/

                    $scope.$on('ccc-activation-card.requestPrint', function (e, activation) {
                        PrintActivationService.print(activation);
                    });

                    $scope.$on('ccc-activation-card.deactivate', checkActivationStatus);

                    $scope.$on('ccc-activation-card.reactivate', function (e, activation) {
                        activation.status = 'READY';
                        activation.statusClass = STATUS_CLASS_MAP[activation.status];
                        $scope.$broadcast('ccc-activation-card.requestRefresh');
                    });


                    /*============ INITIALIZATION ==============*/

                    updateActivationList();

                }
            ],

            template: [

                '<div class="ccc-activate-student-summary-success ccc-success-box alert alert-success" id="ccc-activate-student-summary-success" role="alert">',
                    '<h2><span class="fa fa-check-square icon" role="presentation" aria-hidden="true"></span> Activation Created</h2>',
                '</div>',
                '<div ng-if="controls && allowDeactivate" class="well well-sm">Made a mistake? <a href="#" ng-click="deactivateAll()" class="btn-link"><span class="fa fa-times" role="presentation" aria-hidden="true"></span> Deactivate all assessments in this activation</a>. You can also deactivate individual assessments.</div>',

                '<div class="row">',
                    '<div class="col-md-4 col-sm-6 ccc-anim-fade-in">',
                        '<ccc-student-user class="ccc-user" user="student"></ccc-student-user>',
                    '</div>',
                '</div>',

                '<div class="ccc-activate-student-summary-activations">',
                    '<ccc-activation-card allow-logs="true" allow-print="allowPrint" allow-score="allowScore" allow-deactivate="allowDeactivate" allow-reactivate="allowReactivate" ng-repeat="activation in activations track by activation.activationId" activation="activation" student="student" footer-controls="true" activation-details="true" ng-class="{\'no-controls\': !controls}"></ccc-activation-card>',
                '</div>',

                '<div class="row row-form-buttons">',
                    '<div class="col-xs-12">',
                        '<button ccc-autofocus class="btn btn-primary btn-submit-button" ng-click="okay()" aria-describedby="ccc-activate-student-summary-success">',
                            '<i class="fa fa-check" aria-hidden="true"></i>',
                            '<span translate="Done"></span>',
                        '</button>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
