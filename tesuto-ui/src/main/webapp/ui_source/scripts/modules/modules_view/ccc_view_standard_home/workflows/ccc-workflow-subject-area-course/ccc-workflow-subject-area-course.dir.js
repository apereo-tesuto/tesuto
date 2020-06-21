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

    angular.module('CCC.View.Home').directive('cccWorkflowSubjectAreaCourse', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '=',
                sequence: '=',
                course: '=',
                mode: '@'
            },

            controller: [

                '$scope',
                '$state',
                'ViewManagerEntity',
                'MMSubjectAreaClass',

                function ($scope, $state, ViewManagerEntity, MMSubjectAreaClass) {

                    /*============ PRIVATE VARIABLES =============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addEditCompetencyGroupView = function (course, competencyGroup) {

                        var viewScope = $scope.$new();

                        // since we are editing, pass in a clone that we can ignore
                        viewScope.competencyGroup = competencyGroup.clone();
                        viewScope.subjectArea = $scope.subjectArea;

                        viewScope.$on('ccc-course-competency-group-edit.complete', function (e, editedCompetencyGroup) {

                            course.deleteCompetencyGroup(competencyGroup);
                            course.addCompetencyGroup(editedCompetencyGroup);

                            $scope.$emit('ccc-workflow-subject-area-course.saved', course);

                            $scope.viewManager.popView();

                            // we swapped the course after editing it's clone so notify the competency-groups view we went behind its back and upated the course
                            $scope.$broadcast('ccc-subject-area-course-competency-groups.requestCourseUpdated');
                        });

                        viewScope.$on('ccc-course-competency-group-edit.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'ccc-subject-area-course-competency-group-edit',
                            title: 'Competency Group',
                            breadcrumb: 'Competency Group',
                            scope: viewScope,
                            template: '<ccc-course-competency-group-edit subject-area="subjectArea" competency-group="competencyGroup"></ccc-course-competency-group-edit>'
                        });
                    };

                    var addEditCompetencyGroupsView = function () {

                        var viewScope = $scope.$new();

                        viewScope.course = $scope.course;
                        viewScope.subjectArea = $scope.subjectArea;

                        viewScope.$on('ccc-subject-area-course-competency-groups.cancel', function () {
                            $scope.$emit('ccc-workflow-subject-area-course.cancel');
                        });

                        viewScope.$on('ccc-subject-area-course-competency-groups.editCourseDetails', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-subject-area-course-competency-groups.complete', function (e, course) {
                            $scope.$emit('ccc-workflow-subject-area-course.complete');
                        });

                        viewScope.$on('ccc-subject-area-course-competency-groups.editCompetencyGroup', function (e, competencyGroup) {
                            addEditCompetencyGroupView(viewScope.course, competencyGroup);
                        });

                        viewScope.$on('ccc-subject-area-course-competency-groups.courseUpdated', function (e, course) {
                            $scope.$emit('ccc-workflow-subject-area-course.saved', course);
                        });

                        $scope.viewManager.pushView({
                            id: 'ccc-subject-area-course-competency-groups',
                            title: 'Course Competencies',
                            breadcrumb: 'Course Competencies',
                            scope: viewScope,
                            template: '<ccc-subject-area-course-competency-groups subject-area="subjectArea" course="course"></ccc-subject-area-course-competency-groups>'
                        });
                    };


                    var addEditCourseView = function () {

                        var viewScope = $scope.$new();

                        viewScope.isMMSubjectArea = $scope.subjectArea instanceof MMSubjectAreaClass;
                        viewScope.sequence = $scope.sequence;
                        viewScope.course = $scope.course;
                        viewScope.transferLevels = $scope.subjectArea.getTransferLevels();
                        viewScope.competency = $scope.subjectArea.competencyMapDiscipline;

                        viewScope.$on('ccc-subject-area-course-edit.cancel', function () {
                            $scope.$emit('ccc-workflow-subject-area-course.cancel');
                        });

                        viewScope.$on('ccc-subject-area-course-edit.deleted', function (e) {
                            $scope.$emit('ccc-workflow-subject-area-course.deleted', $scope.course);
                        });

                        viewScope.$on('ccc-subject-area-course-edit.saved', function (course) {

                            $scope.$emit('ccc-workflow-subject-area-course.saved', $scope.course);

                            if (!viewScope.isMMSubjectArea) {
                                addEditCompetencyGroupsView();
                            } else {
                                $scope.$emit('ccc-workflow-subject-area-course.complete');
                            }
                        });

                        viewScope.$on('ccc-subject-area-course-edit.doneWithNoEdit', function () {
                            if (!viewScope.isMMSubjectArea) {
                                addEditCompetencyGroupsView();
                            } else {
                                $scope.$emit('ccc-workflow-subject-area-course.complete');
                            }
                        });

                        var viewTitle = 'Course Details';

                        $scope.viewManager.pushView({
                            id: 'subject-area-sequence-course-edit',
                            title: viewTitle,
                            breadcrumb: viewTitle,
                            scope: viewScope,
                            template: '<ccc-subject-area-course-edit course="course" sequence="sequence" competency="competency" transfer-levels="transferLevels" is-m-m-subject-area="isMMSubjectArea"></ccc-subject-area-course-edit>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addEditCourseView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
