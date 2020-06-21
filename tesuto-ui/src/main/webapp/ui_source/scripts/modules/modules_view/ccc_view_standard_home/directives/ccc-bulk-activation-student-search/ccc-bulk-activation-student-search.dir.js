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

    angular.module('CCC.View.Home').directive('cccBulkActivationStudentSearch', function () {

        return {

            restrict: 'E',

            scope: {
                assessments: '=?',
                location: '=?',
                studentsToActivate: '=?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'Moment',
                'StudentsAPIService',
                'LocationService',
                'BatchActivationsAPIService',
                'CollisionService',

                function ($scope, $element, $timeout, Moment, StudentsAPIService, LocationService, BatchActivationsAPIService, CollisionService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/


                    /*============ MODEL ============*/

                    $scope.assessments = $scope.assessments || [];
                    $scope.studentsToActivate = $scope.studentsToActivate || [];
                    $scope.location = $scope.location ? $scope.location : LocationService.getCurrentTestCenter();

                    $scope.allowEdit = $scope.allowEdit || false;

                    $scope.searching = false;
                    $scope.submitted = false;

                    $scope.loading = false;

                    $scope.addedStudentIds = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var resetSearch = function () {
                        $scope.$broadcast('ccc-add-student-by-id.clear');
                    };

                    var updateAddedStudentIds = function () {
                        $scope.addedStudentIds = _.map($scope.studentsToActivate, function (student) {
                            return student.cccId;
                        });
                    };

                    var addStudent = function (student) {
                        $scope.studentsToActivate.unshift(student);
                        resetSearch();
                        updateAddedStudentIds();
                        $scope.$emit('ccc-bulk-activation-student-search.studentsChanged', $scope.studentsToActivate);
                    };

                    var tryAddStudent = function (student) {

                        student.activationsToCreateCandidates = [];

                        // Format assessments for activation api call
                        _.each($scope.assessments, function (assessment) {

                            student.activationsToCreateCandidates.push({
                            	assessmentScopedIdentifier: {
                                    identifier: assessment.identifier,
                                    namespace: assessment.namespace
                                },
                                locationId: $scope.location.id,
                                //TODO attributes
                                userId: student.cccId,
                                deliveryType: assessment.deliveryType,
                                startDate: Moment().valueOf(),
                                endDate: assessment.deliveryType === 'PAPER' ?
                                    Moment().add(7, 'days').endOf('day').valueOf() :
                                    Moment().add(3, 'months').endOf('day').valueOf()
                            });
                        });

                        CollisionService.getDuplicateActivations(student.activationsToCreateCandidates, student).then(function (duplicateActivations) {

                            if (duplicateActivations.length) {

                                CollisionService.openResolutionModal(duplicateActivations, student).then(function (deactivatedActivations) {

                                    // success (deactivate)
                                    addStudent(student);

                                }, function (isUserCancel) {

                                    // cancel
                                    resetSearch();
                                });

                            } else {

                                addStudent(student);
                            }
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.edit = function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        $scope.$emit('ccc-bulk-activation-student-search.edit');
                    };

                    $scope.isStudentsToActivate = function () {
                        return !_.isEmpty($scope.studentsToActivate);
                    };

                    $scope.removeStudent = function (student) {

                        $scope.studentsToActivate = _.reject($scope.studentsToActivate, function (student_in) {
                            return student_in.cccId === student.cccId;
                        });

                        resetSearch();
                        updateAddedStudentIds();
                        $scope.$emit('ccc-bulk-activation-student-search.studentsChanged', $scope.studentsToActivate);
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-bulk-activation-student-search.cancel');
                    };

                    $scope.createActivations = function () {

                        $scope.loading = true;
                        $scope.submitted = true;

                        var bulkActivationsToCreate = [];

                        _.each($scope.studentsToActivate, function (student) {
                            _.each(student.activationsToCreateCandidates, function (activationToCreate) {
                                bulkActivationsToCreate.push(activationToCreate);
                            });
                        });

                        BatchActivationsAPIService.createActivations(bulkActivationsToCreate).then(function () {

                            $scope.$emit('ccc-bulk-activation-student-search.created', $scope.assessments, $scope.studentsToActivate, $scope.location);
                            $scope.assessments = [];
                            $scope.studentsToActivate = [];
                            $scope.$emit('ccc-bulk-activation-student-search.studentsChanged', $scope.studentsToActivate);

                        }).finally(function () {
                            $scope.loading = false;
                            $scope.submitted = false;
                        });
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-add-student-by-id.addStudent', function (e, student) {
                        tryAddStudent(student);
                    });
                    $scope.$on('ccc-add-student-by-id.removeStudent', function (e, student) {
                        $scope.removeStudent(student);
                    });

                    $scope.$on('ccc-student-card-list.remove', function (event, student) {
                        $scope.removeStudent(student);
                    });

                    $scope.$on('ccc-activation-details-card.edit', function () {
                        $scope.$emit('ccc-bulk-activation-student-search.edit');
                    });


                    /*============ INITIALIZATION ============*/

                    updateAddedStudentIds();
                }
            ],

            template: [

                '<h2 class="sr-only">Bulk Activation Summary</h2>',

                '<ccc-activation-details-card location="location" assessments="assessments" allow-edit="true" is-disabled="loading"></ccc-activation-details-card>',

                '<ccc-add-student-by-id added-student-ids="addedStudentIds" is-disabled="loading" class="margin-bottom-sm"></ccc-add-student-by-id>',

                '<div class="row margin-bottom-xs">',
                    '<div class="col-sm-3">',
                        '<h3><span translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_SEARCH.STUDENT"></span> ({{studentsToActivate.length}})</h3>',
                    '</div>',
                    '<div class="col-sm-9 text-right">',
                        '<button class="btn btn-primary btn-full-width-when-small" ng-disabled="!isStudentsToActivate() || loading || submitted" ng-click="createActivations()">Activate Students</button>',
                        '<button type="button" class="btn btn-default btn-full-width-when-small" ng-click="cancel()">Cancel</button>',
                    '</div>',
                '</div>',

                '<ccc-student-card-list students="studentsToActivate" is-disabled="loading" show-remove="true"></ccc-student-card-list>',

                '<div class="row student-list" ng-show="studentsToActivate.length >= 5">',
                    '<div class="col-sm-12 text-right">',
                        '<button class="btn btn-primary btn-full-width-when-small" ng-disabled="!isStudentsToActivate() || loading || submitted" ng-click="createActivations()">Activate Students</button>',
                        '<button type="button" class="btn btn-default btn-full-width-when-small" ng-click="cancel()">Cancel</button>',
                    '</div>',
                '</div>'

            ].join('')
        };
    });

})();
