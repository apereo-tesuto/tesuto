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

    angular.module('CCC.View.Home').directive('cccCourseCompetencyGroupEdit', function () {

        return {

            restrict: 'E',

            scope: {
                competencyGroup: '=',
                subjectArea: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'ModalService',

                function ($scope, $element, $timeout, ModalService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var doSubmit = function () {

                        $scope.submitted = true;

                        if ($scope['cccCourseCompetencyGroupEditForm'].$invalid) {
                            $($element.find('input.ng-invalid, textarea.ng-invalid')[0]).focus();
                            return;
                        }

                        $scope.loading = true;

                        $scope.competencyGroup.save().then(function () {

                            $scope.$emit('ccc-course-competency-group-edit.complete', $scope.competencyGroup, $scope.subjectArea);

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    $scope.attemptDoSubmit = function () {
                        $timeout(doSubmit, 1);
                    };

                    $scope.cancel = function () {

                        var hasSelectedCompetencies = $scope.competencyGroup.competencyIds.length;

                        if ($scope.cccCourseCompetencyGroupEditForm.$dirty) {

                            var buttonConfigs = {
                                cancel: {
                                    title: 'CCC_COMP.MODALS.BUTTONS.STAY',
                                    btnClass: 'btn-primary'
                                },
                                okay: {
                                    title: 'CCC_COMP.MODALS.BUTTONS.LEAVE',
                                    btnClass: 'btn-default'
                                }
                            };

                            var confirmDiscardChangesModal = ModalService.openConfirmModal({
                                title: 'CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.MODAL_UNSAVED.TITLE',
                                message: 'CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.MODAL_UNSAVED.MESSAGE',
                                buttonConfigs: buttonConfigs,
                                type: 'warning'
                            });

                            confirmDiscardChangesModal.result.then(function () {
                                $scope.$emit('ccc-course-competency-group-edit.cancel');
                            });

                        } else {

                            if (!hasSelectedCompetencies) {

                                var ignoreButtonConfigs = {
                                    cancel: {
                                        title: 'CCC_COMP.MODALS.BUTTONS.STAY',
                                        btnClass: 'btn-primary'
                                    },
                                    okay: {
                                        title: 'CCC_COMP.MODALS.BUTTONS.LEAVE',
                                        btnClass: 'btn-default'
                                    }
                                };

                                var confirmIgnoreInvalidModal = ModalService.openConfirmModal({
                                    title: 'CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.MODAL_NO_COMPETENCIES.TITLE',
                                    message: 'CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.MODAL_NO_COMPETENCIES.MESSAGE',
                                    buttonConfigs: ignoreButtonConfigs,
                                    type: 'warning'
                                });

                                confirmIgnoreInvalidModal.result.then(function () {
                                    $scope.$emit('ccc-course-competency-group-edit.cancel');
                                });

                            } else {
                                $scope.$emit('ccc-course-competency-group-edit.cancel');
                            }
                        }
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<form name="cccCourseCompetencyGroupEditForm" novalidate>',

                    '<div class="row margin-bottom">',
                        '<div class="col-sm-6">',

                            '<h2 translate="CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.EDIT_DETAILS"></h2>',
                            '<ccc-course-competency-group-form competency-group="competencyGroup" submitted="submitted" is-disabled="loading"></ccc-course-competency-group-form>',

                        '</div>',
                    '</div>',

                    '<div class="row margin-bottom">',
                        '<div class="col-sm-8">',

                            '<h3 translate="CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.EDIT_COMPETENCIES"></h3>',
                            '<ccc-course-competency-group-tree subject-area="subjectArea" competency-group="competencyGroup" is-disabled="loading" submitted="submitted"></ccc-course-competency-group-tree>',

                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-xs-12">',

                            '<div class="row">',
                                '<div class="col-xs-12 ccc-form-submit-controls">',

                                    '<button class="btn btn-primary btn-full-width-when-small btn-submit-button" ng-disabled="loading || !cccCourseCompetencyGroupEditForm.$dirty" type="submit" ng-click="attemptDoSubmit()">',
                                        '<i class="fa fa-save noanim" aria-hidden="true"></i>',
                                        '<i class="fa fa-spin fa-spinner" aria-hidden="true" ng-if="loading"></i>',
                                        '<span translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_EDIT_COMPETENCY_GROUP"></span>',
                                    '</button>',

                                    '<button class="btn btn-default btn-full-width-when-small" ng-disabled="loading" ng-click="cancel()" translate="CCC_VIEW_HOME.SUBJECT_AREA_COURSE_FORM.BUTTON_CANCEL"></button>',

                                '</div>',
                            '</div>',

                        '</div>',
                    '</div>',

                '</form>'

            ].join('')

        };

    });

})();
