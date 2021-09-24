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

    angular.module('CCC.View.Home').directive('cccSubjectAreaCourseEdit', function () {

        return {

            restrict: 'E',

            scope: {
                course: '=',
                sequence: '=',
                competency: '=',
                isMMSubjectArea: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'ModalService',

                function ($scope, $element, $timeout, ModalService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.submitted = false;
                    $scope.loading = false;

                    $scope.isMMSubjectArea = $scope.isMMSubjectArea || false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var doSubmit = function () {

                        $scope.submitted = true;

                        if ($scope['cccCourseEditForm'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            return;
                        }

                        // if nothing has changed and the form is not invalid then let's just move forward and avoid an extra save call
                        if (!$scope.cccCourseEditForm.$dirty) {
                            $scope.$emit('ccc-subject-area-course-edit.doneWithNoEdit', $scope.course);
                            return;
                        }

                        $scope.loading = true;

                        if ($scope.cccCourseEditForm.$dirty || !$scope.course.courseId) {

                            // note, the save method looks for a courseId, if it exists it updates, else it creates
                            // the create method will get the new courseId from the server and will add it back to the class instance
                            $scope.course.save().then(function () {

                                $scope.cccCourseEditForm.$setPristine();

                                // let's give the complex form logic a second to switch to pristine to get a better animation before we announce a save was completed
                                $timeout(function () {
                                    $scope.$emit('ccc-subject-area-course-edit.saved', $scope.course);
                                }, 200);

                            }).finally(function () {

                                $timeout(function () {
                                    $scope.loading = false;
                                }, 600);
                            });

                        } else {

                            $scope.$emit('ccc-subject-area-course-edit.saved', $scope.course);

                            // this prevents double clicking etc
                            $timeout(function () {
                                $scope.loading = false;
                            }, 600);
                        }
                    };

                    var resetForm = function () {

                        $scope.submitted = false;
                        $scope.loading = false;
                    };


                    /*============ BEHAVIOR ==============*/

                    // we wrap in a timeout because when a user hit's "enter"
                    // the model values may not have propagated on each model from it's view value
                    // this let's the propagation take place
                    // hitting enter on an input within a form forces a click on the submit button which is the root cause
                    $scope.attemptDoSubmit = function ($event) {
                        $timeout(doSubmit, 1);
                    };

                    $scope.cancel = function () {
                        $scope.$emit('ccc-subject-area-course-edit.cancel');
                    };

                    $scope.delete = function () {

                        var buttonConfigs = {
                            cancel: {
                                title: 'Cancel',
                                btnClass: 'btn-primary'
                            },
                            okay: {
                                title: 'Delete',
                                btnClass: 'btn-default',
                                btnIcon: 'fa-times-circle'
                            }
                        };

                        var deleteCourseModal = ModalService.openConfirmModal({
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.DELETE_COURSE.TITLE',
                            message: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.DELETE_COURSE.MESSAGE',
                            buttonConfigs: buttonConfigs,
                            type: 'warning'
                        });

                        deleteCourseModal.result.then(function () {

                            $scope.deleting = true;

                            $scope.course.delete().then(function () {

                                $scope.$emit('ccc-subject-area-course-edit.deleted', $scope.course);

                            }).finally(function () {
                                $scope.deleting = false;
                            });
                        });
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                    resetForm();
                }
            ],

            template: [

                '<form name="cccCourseEditForm" novalidate>',

                    '<div class="row">',
                        '<div class="col-md-6 col-md-offset-3">',

                            '<ccc-subject-area-course-form course="course" competency="competency" loading="loading || deleting" submitted="submitted"></ccc-subject-area-course-form>',

                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-md-6 col-md-offset-3">',

                            '<div class="row">',
                                '<div class="col-xs-12 ccc-form-submit-controls">',

                                    '<button class="btn btn-primary pull-right btn-full-width-when-small btn-submit-button" ng-disabled="loading || deleting" type="submit" ng-click="attemptDoSubmit()" ccc-focusable>',
                                        '<i class="fa fa-spin fa-spinner noanim" aria-hidden="true" ng-if="loading"></i>',

                                        '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_SAVE_COURSE_DETAILS" ng-if="!isMMSubjectArea && (cccCourseEditForm.$dirty || !course.courseId)"></span>',
                                        '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_CONFIRM_COURSE_DETAILS" ng-if="!isMMSubjectArea && !cccCourseEditForm.$dirty && course.courseId"></span>',

                                        '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_SAVE_COURSE_DETAILS_MM" ng-if="isMMSubjectArea && (cccCourseEditForm.$dirty || !course.courseId)"></span>',
                                        '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_CONFIRM_COURSE_DETAILS_MM" ng-if="isMMSubjectArea && !cccCourseEditForm.$dirty && course.courseId"></span>',

                                        '<i class="fa fa-chevron-right noanim" aria-hidden="true" ng-if="::!isMMSubjectArea"></i>',
                                    '</button>',

                                    '<button class="btn btn-default btn-full-width-when-small" ng-disabled="loading || deleting" ng-click="cancel()">',
                                        '<i class="fa fa-chevron-left"></i> ',
                                        '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_CANCEL" ng-if="cccCourseEditForm.$dirty || !course.courseId"></span>',
                                        '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_BACK" ng-if="!cccCourseEditForm.$dirty && course.courseId"></span>',
                                    '</button>',

                                    '<button class="btn btn-default btn-full-width-when-small" ng-if="course.courseId" ng-disabled="loading || deleting" ng-click="delete()">',
                                        '<i class="fa fa-times-circle noanim" ng-if="!deleting"></i> ',
                                        '<i class="fa fa-spinner fa-spin noanim" ng-if="deleting"></i> ',
                                        '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_DELETE"></span>',
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
