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

    angular.module('CCC.View.Home').directive('cccRemoteEventsStudents', function () {

        return {

            restrict: 'E',

            scope: {
                remoteEvent: '=',
                submitButton: '=?'
            },

            controller: [

                '$scope',
                'Moment',
                'CollisionService',
                'RemoteEventService',

                function ($scope, Moment, CollisionService, RemoteEventService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/


                    /*============ MODEL ============*/

                    $scope.submitButton = $scope.submitButton || 'CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.BUTTON_SUBMIT';

                    $scope.errorLoadingRemoteEventData = false;
                    $scope.loadingRemoteEventData = true;

                    $scope.existingStudents = [];
                    $scope.studentsToActivate = [];
                    $scope.studentsToDeactivate = [];

                    $scope.searching = false;
                    $scope.submitted = false;

                    $scope.processing = false;

                    $scope.addedStudentIds = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var focusOnInput = function () {
                        $scope.$broadcast('ccc-add-student-by-id.focus');
                    };

                    var initializeStudentGroups = function () {
                        // create a new array reference of the students that come in from the remote event
                        $scope.existingStudents = $scope.remoteEvent.metaData.students.slice(0);
                    };

                    var resetSearch = function () {
                        $scope.$broadcast('ccc-add-student-by-id.clear');
                    };

                    var updateAddedStudentIds = function () {
                        $scope.addedStudentIds = _.map($scope.studentsToActivate.concat($scope.existingStudents), function (student) {
                            return student.cccId;
                        });
                    };

                    var addStudent = function (student) {
                        $scope.studentsToActivate.unshift(student);
                        resetSearch();
                        updateAddedStudentIds();
                    };

                    var isStudentInGroup = function (studentList, studentToFind) {
                        return _.find(studentList, function (student) {
                            return student.cccId === studentToFind.cccId;
                        }) !== undefined;
                    };

                    var removeExistingStudent = function (student) {

                        $scope.existingStudents = _.reject($scope.existingStudents, function (student_in) {
                            return student_in.cccId === student.cccId;
                        });

                        $scope.studentsToDeactivate.unshift(student);

                        updateAddedStudentIds();
                    };

                    var removeRemovedStudent = function (student) {

                        $scope.studentsToDeactivate = _.reject($scope.studentsToDeactivate, function (student_in) {
                            return student_in.cccId === student.cccId;
                        });

                        // if you try to remove a student from the "removed list" and it was originally in the original list then put it back
                        if (isStudentInGroup($scope.remoteEvent.metaData.students, student)) {
                            $scope.existingStudents.unshift(student);
                        }

                        updateAddedStudentIds();
                    };

                    var removeAddedStudent = function (student) {

                        $scope.studentsToActivate = _.reject($scope.studentsToActivate, function (student_in) {
                            return student_in.cccId === student.cccId;
                        });

                        updateAddedStudentIds();
                    };

                    var removeStudent = function (studentToRemove) {

                        if (isStudentInGroup($scope.existingStudents, studentToRemove)) {
                            removeExistingStudent(studentToRemove);
                        } else if (isStudentInGroup($scope.studentsToActivate, studentToRemove)) {
                            removeAddedStudent(studentToRemove);
                        } else {
                            removeRemovedStudent(studentToRemove);
                        }

                        focusOnInput();
                    };

                    var tryAddStudent = function (student) {

                        // first we could just be trying to re-add it and it already existed
                        if (isStudentInGroup($scope.remoteEvent.metaData.students, student)) {
                            removeRemovedStudent(student);
                            resetSearch();
                            return;
                        }

                        student.activationsToCreateCandidates = [];

                        // Format assessments for activation api call
                        _.each($scope.remoteEvent.assessmentIdentifiers, function (assessment) {

                            student.activationsToCreateCandidates.push({
                            	assessmentScopedIdentifier: {
                                    identifier: assessment.identifier,
                                    namespace: assessment.namespace
                                },
                                locationId: $scope.remoteEvent.testLocationId,
                                //TODO attributes
                                userId: student.cccId,
                                deliveryType: $scope.remoteEvent.deliveryType,
                                endDate: new Moment().add(7, 'days').valueOf()
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

                    var initialize = function () {

                        RemoteEventService.attachAllMetaData($scope.remoteEvent).then(function () {

                            // we need to take the list of students passed in and feed those into the right bucket
                            initializeStudentGroups();

                            // ORDER MATTERS HERE : then and only then can we generate a list of ids that the "ccc-search-student-by-id" can use to tell if it should be added or removed
                            updateAddedStudentIds();

                        }, function () {

                            $scope.errorLoadingRemoteEventData = true;

                        }).finally(function () {

                            $scope.loadingRemoteEventData = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.hasPendingChanges = function () {
                        return !_.isEmpty($scope.studentsToActivate) || !_.isEmpty($scope.studentsToDeactivate);
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-remote-events-students.cancel');
                    };

                    // Will need to both create activations and deactivations for each
                    $scope.saveChanges = function () {

                        var studentsToAdd = _.map($scope.studentsToActivate, function (student) {
                            return student.cccId;
                        });

                        var existingStudents = _.map($scope.existingStudents, function (student) {
                            return student.cccId;
                        });

                        $scope.processing = true;

                        var allStudents = studentsToAdd.concat(existingStudents);

                        return $scope.remoteEvent.updateStudents(allStudents).then(function () {

                            $scope.$emit('ccc-remote-events-students.studentsChanged', $scope.remoteEvent, {
                                removed: $scope.studentsToDeactivate,
                                added: $scope.studentsToActivate,
                                existing: $scope.existingStudents
                            });

                        }).finally(function () {
                            $scope.processing = false;
                        });
                    };

                    $scope.removeStudent = function (student) {
                        removeStudent(student);
                        resetSearch();
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-add-student-by-id.addStudent', function (e, student) {
                        tryAddStudent(student);
                    });
                    $scope.$on('ccc-add-student-by-id.removeStudent', function (e, student) {
                        $scope.removeStudent(student);
                    });

                    $scope.$on('ccc-student-card-list.remove', function (event, student, cccStudentCardListId) {
                        removeStudent(student);
                    });


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<h2><span translate="Edit Students for "></span> <span class="emphasize">{{::remoteEvent.name}}</span></h2>',

                '<ccc-content-loading-placeholder ng-if="loadingRemoteEventData || errorLoadingRemoteEventData" no-results-info="errorLoadingRemoteEventData" hide-default-no-results-text="true">',
                    '<span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.ERROR_LOADING_REMOTE_DATA"></span>',
                '</ccc-content-loading-placeholder>',

                '<div ng-if="!loadingRemoteEventData && !errorLoadingRemoteEventData">',

                    '<ccc-activation-details-card location="remoteEvent.metaData.location" assessments="remoteEvent.metaData.assessments" is-disabled="processing"></ccc-activation-details-card>',

                    '<ccc-add-student-by-id class="margin-bottom-sm" added-student-ids="addedStudentIds" is-disabled="processing"></ccc-add-student-by-id>',

                    '<div class="row margin-bottom-xs">',
                        '<div class="col-sm-6">',
                            '<h3><span translate="Students"></span></h3>',
                        '</div>',
                        '<div class="col-sm-6 text-right">',
                            '<button type="button" class="btn btn-default btn-full-width-when-small" ng-click="cancel()" ng-disabled="processing" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.CANCEL"></button>',
                            '<button class="btn btn-primary btn-full-width-when-small btn-submit" ng-disabled="!hasPendingChanges() || processing" ng-click="saveChanges()">',
                                '<span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.SAVE_CHANGES"></span> ',
                                '<i class="noanim fa fa-spin fa-spinner" aria-hidden="true" ng-if="processing"></i>',
                            '</button>',
                        '</div>',
                    '</div>',

                    '<ccc-content-loading-placeholder ng-if="(existingStudents.length + studentsToActivate.length + studentsToDeactivate.length === 0)" no-results-info="true" hide-default-no-results-text="true">',
                        '<span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.NO_STUDENTS_INSTRUCTIONS"></span>',
                    '</ccc-content-loading-placeholder>',

                    '<div class="ccc-student-card-list-set">',

                        // ===================== REMOVE
                        '<h4 ng-if="studentsToDeactivate.length" class="ccc-section-header text-warning"><i class="fa fa-minus-circle" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.HEADER_STUDENTS_TO_REMOVE"></span> ({{studentsToDeactivate.length}})</h4>',
                        '<ccc-student-card-list class="warning" ng-class="{\'margin-bottom-md\': studentsToDeactivate.length}" students="studentsToDeactivate" is-disabled="processing" show-remove="true" id="studentsToDeactivate"></ccc-student-card-list>',

                        // ===================== ADD
                        '<h4 ng-if="studentsToActivate.length" class="ccc-section-header text-success-dark"><i class="fa fa-plus-circle" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.HEADER_STUDENTS_TO_ADD"></span> ({{studentsToActivate.length}})</h4>',
                        '<ccc-student-card-list class="success" ng-class="{\'margin-bottom-md\': studentsToActivate.length}" students="studentsToActivate" is-disabled="processing" show-remove="true" id="studentsToActivate"></ccc-student-card-list>',

                        // ===================== EXISTING
                        '<h4 ng-if="existingStudents.length" class="ccc-section-header"><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.HEADER_STUDENTS_EXISTING"></span> ({{existingStudents.length}})</h4>',
                        '<ccc-student-card-list students="existingStudents" is-disabled="processing" show-remove="true" id="existingStudents"></ccc-student-card-list>',

                        '<div class="row" ng-if="existingStudents.length + studentsToActivate.length + studentsToActivate.length > 2">',
                            '<div class="col-sm-12 text-right">',
                                '<button type="button" class="btn btn-default btn-full-width-when-small btn-submit" ng-click="cancel()" ng-disabled="processing" translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.CANCEL"></button>',
                                '<button class="btn btn-primary btn-full-width-when-small" ng-disabled="!hasPendingChanges() || processing" ng-click="saveChanges()">',
                                    '<span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS.SAVE_CHANGES"></span> ',
                                    '<i class="noanim fa fa-spin fa-spinner" aria-hidden="true" ng-if="processing"></i>',
                                '</button>',
                            '</div>',
                        '</div>',

                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
