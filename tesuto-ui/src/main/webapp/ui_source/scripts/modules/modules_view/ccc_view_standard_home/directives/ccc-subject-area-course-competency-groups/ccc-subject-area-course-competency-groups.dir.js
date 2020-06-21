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

    angular.module('CCC.View.Home').directive('cccSubjectAreaCourseCompetencyGroups', function () {

        return {

            restrict: 'E',

            scope: {
                course: '=',
                subjectArea: '='
            },

            controller: [

                '$scope',
                '$element',
                '$q',
                '$timeout',
                'ModalService',
                'SubjectAreaCourseClass',
                'CompetencyGroupClass',

                function ($scope, $element, $q, $timeout, ModalService, SubjectAreaCourseClass, CompetencyGroupClass) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.submitted = false;
                    $scope.loading = false;
                    $scope.competencyMapLoading = false;

                    $scope.autoEditing = false;

                    $scope.initialLoadComplete = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var verifyValidCompetencyGroups = function () {
                        var deferred = $q.defer();

                        var allCompetencyGroupsValid = true;
                        _.each($scope.course.competencyGroups, function (competencyGroup) {
                            allCompetencyGroupsValid = allCompetencyGroupsValid && (competencyGroup.competencyIds.length !== 0);
                        });

                        if (!allCompetencyGroupsValid) {

                            var buttonConfigs = {
                                cancel: {
                                    title: 'Stay',
                                    btnClass: 'btn-primary'
                                },
                                okay: {
                                    title: 'Leave',
                                    btnClass: 'btn-default'
                                }
                            };

                            var confirmLeaveModal = ModalService.openConfirmModal({
                                title: 'CCC_VIEW_HOME.CCC-SUBJECT-AREA-COURSE-COMPETENCY-GROUPS.INVALID_GROUPS_MODAL.TITLE',
                                message: 'CCC_VIEW_HOME.CCC-SUBJECT-AREA-COURSE-COMPETENCY-GROUPS.INVALID_GROUPS_MODAL.MESSAGE',
                                buttonConfigs: buttonConfigs,
                                type: 'warning'
                            });

                            confirmLeaveModal.result.then(function () {
                                deferred.resolve();
                            }, function () {
                                deferred.reject();
                            });

                        } else {
                            deferred.resolve();
                        }

                        return deferred.promise;
                    };

                    var doSubmit = function () {

                        $scope.submitted = true;

                        if ($scope['cccCourseCompetencyEditForm'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            return;
                        }

                        verifyValidCompetencyGroups().then(function () {
                            $scope.loading = true;
                            $scope.$emit('ccc-subject-area-course-competency-groups.complete', $scope.course);
                        });
                    };

                    var resetForm = function () {

                        $scope.loading = false;
                        $scope.initialLoadComplete = true;
                        $scope.submitted = false;
                    };


                    /*============ BEHAVIOR ==============*/

                    // we wrap in a timeout because when a user hit's "enter"
                    // the model values may not have propagated on each model from it's view value
                    // this let's the propagation take place
                    // hitting enter on an input within a form forces a click on the submit button which is the root cause
                    $scope.attemptDoSubmit = function () {
                        $timeout(doSubmit, 1);
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-subject-area-course-competency-groups.cancel');
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-subject-area-course-card.edit', function () {
                        $scope.$emit('ccc-subject-area-course-competency-groups.editCourseDetails');
                    });

                    $scope.$on('ccc-subject-area-course-competency-map.competencySelected', function (e, course, competencyGroup) {
                        $scope.$emit('ccc-subject-area-course-competency-groups.editCompetencyGroup', competencyGroup);
                    });

                    $scope.$on('ccc-subject-area-course-competency-map.courseUpdated', function () {
                        $scope.$emit('ccc-subject-area-course-competency-groups.courseUpdated', $scope.course);
                    });

                    $scope.$on('ccc-subject-area-course-competency-map.competencyGroupAdded', function (e, competencyGroup) {

                        // we are going to disable everything and delay just a split second
                        // so the user can see physically where this was added
                        $scope.autoEditing = true;
                        $timeout(function () {

                            $scope.$emit('ccc-subject-area-course-competency-groups.editCompetencyGroup', competencyGroup);

                            // we are going to give the parent some time to respond to this so this will keep things disabled for just a tick
                            // helps prevent double clicking etc.
                            $timeout(function () {
                                $scope.autoEditing = false;
                            }, 400);

                        }, 400);
                    });

                    // someone above us modified our course, so we need to notify corresponding sub components that would care
                    $scope.$on('ccc-subject-area-course-competency-groups.requestCourseUpdated', function () {
                        $scope.$broadcast('ccc-subject-area-course-competency-map.requestRefresh');
                    });

                    $scope.$on('ccc-subject-area-course-competency-map.isLoading', function (e, competencyMapLoading) {
                        $scope.competencyMapLoading = competencyMapLoading;
                    });


                    /*============ INITIALIZATION ==============*/

                    resetForm();
                }
            ],

            template: [

                '<form name="cccCourseCompetencyEditForm" novalidate>',

                    '<div class="row">',
                        '<div class="col-xs-12">',

                            '<ccc-subject-area-course-card course="course" is-disabled="loading || competencyMapLoading" tabindex="-1" class="no-outline" ccc-autofocus></ccc-subject-area-course-card>',

                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-xs-12">',

                            '<h3>Competency Groups <i class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="competencyMapLoading || autoEditing"></i></h3>',

                            '<div class="well">',

                                '<ccc-content-loading-placeholder ng-if="!initialLoadComplete"></ccc-content-loading-placeholder>',
                                '<ccc-subject-area-course-competency-map course="course" ng-if="initialLoadComplete" is-disabled="autoEditing" subject-area="subjectArea"></ccc-subject-area-course-competency-map>',

                            '</div>',

                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-xs-12">',

                            '<div class="row">',
                                '<div class="col-xs-12 ccc-form-submit-controls">',

                                    '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ng-disabled="loading || competencyMapLoading || autoEditing" type="submit" ng-click="attemptDoSubmit()">',
                                        '<i class="fa fa-check noanim" aria-hidden="true"></i>',
                                        '<i class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="loading"></i>',
                                        '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_DONE"></span>',
                                    '</button>',

                                '</div>',
                            '</div>',

                        '</div>',
                    '</div>',

                '</form>'

            ].join('')

        };

    });

})();
