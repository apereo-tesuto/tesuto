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

    angular.module('CCC.View.Home').directive('cccWorkflowSubjectAreaSequence', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '='
            },

            controller: [

                '$scope',
                'ViewManagerEntity',
                'SubjectAreaCourseClass',

                function ($scope, ViewManagerEntity, SubjectAreaCourseClass) {


                    /*============ PRIVATE VARIABLES =============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ MODEL DEPENDENT VIEW MANAGMENT ============*/

                    var addEditCourseView = function (sequence, originalCourse, mode) {

                        // only track the original cb21Code if there is potential this course is moving sequences
                        var originalcb21Code = originalCourse.courseId ? originalCourse.cb21Code : '';
                        var originalCourseGroup = originalCourse.courseGroup ? originalCourse.courseGroup : 1;
                        var courseToEdit = originalCourse.clone();

                        var viewScope = $scope.$new();

                        viewScope.subjectArea = $scope.subjectArea;

                        viewScope.sequence = sequence;
                        viewScope.course = courseToEdit;
                        viewScope.mode = mode;

                        viewScope.$on('ccc-workflow-subject-area-course.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-workflow-subject-area-course.deleted', function (e, courseToDelete) {

                            $scope.subjectArea.deleteCourse(courseToDelete, false).then(function () {
                                $scope.$broadcast('ccc-subject-area-sequence.refresh', [{cb21Code: originalCourse.cb21Code, courseGroup: originalCourse.courseGroup}], true);
                                $scope.$emit('ccc-workflow-subject-area-sequence.sequenceUpdated', $scope.subjectArea);
                            });

                            $scope.viewManager.popView();
                        });

                        // when a course is saved we need to update the subject area
                        // we delete it without persisting the delete and re-add it in case they changed the sequence it was associated with it
                        // the subject area can then internally move the course between it's own sequence objects
                        viewScope.$on('ccc-workflow-subject-area-course.saved', function () {
                            // things can shift around so let's update our model from the server
                            $scope.$broadcast('ccc-subject-area-sequence.refresh', [{cb21Code: viewScope.course.cb21Code, courseGroup: viewScope.course.courseGroup}]);
                            $scope.$emit('ccc-workflow-subject-area-sequence.sequenceUpdated', $scope.subjectArea);
                        });

                        // when all the editing craziness is done, highlight the sequences that changed and pop the view
                        viewScope.$on('ccc-workflow-subject-area-course.complete', function () {

                            $scope.$broadcast('ccc-subject-area-sequence.requestHighlightSequences', [{cb21Code: originalcb21Code, courseGroup: originalCourseGroup}, {cb21Code: courseToEdit.cb21Code, courseGroup: courseToEdit.courseGroup}]);
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'subject-area-sequence-course-edit',
                            title: 'Create Course',
                            breadcrumb: 'Create Course',
                            scope: viewScope,
                            isNested: true,
                            template: '<ccc-workflow-subject-area-course mode="{{mode}}" subject-area="subjectArea" sequence="sequence" course="course"></ccc-workflow-subject-area-course>'
                        });
                    };

                    var addEditExplanationView = function (sequence) {

                        var viewScope = $scope.$new();

                        viewScope.sequence = sequence;

                        viewScope.$on('ccc-subject-area-sequence-explanation.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-subject-area-sequence-explanation.updated', function (e, sequence) {

                            $scope.$broadcast('ccc-subject-area-sequence.refresh', [{cb21Code: sequence.cb21Code, courseGroup: sequence.courseGroup}]);
                            $scope.$emit('ccc-workflow-subject-area-sequence.sequenceUpdated', $scope.subjectArea);
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'edit-explanation-view',
                            title: 'Edit Explanation',
                            breadcrumb: 'Edit Explanation',
                            scope: viewScope,
                            template: '<ccc-subject-area-sequence-explanation sequence="sequence"></ccc-subject-area-sequence-explanation>'
                        });
                    };

                    var addSequenceView = function () {

                        var viewScope = $scope.$new();

                        viewScope.subjectArea = $scope.subjectArea;

                        viewScope.$on('ccc-subject-area-sequence.editExplanation', function (e, sequence) {

                            // note, we pass in a clone so if the user cancels we don't change the original sequence
                            addEditExplanationView(sequence.clone());
                        });

                        viewScope.$on('ccc-subject-area-sequence.addCourse', function (e, sequence, cb21Code, courseGroup) {

                            var newCourse = new SubjectAreaCourseClass({
                                disciplineId: $scope.subjectArea.disciplineId,
                                cb21Code: cb21Code,
                                courseGroup: courseGroup
                            });

                            addEditCourseView(sequence, newCourse, 'create');
                        });

                        viewScope.$on('ccc-subject-area-sequence.courseClicked', function (e, sequence, course) {
                            addEditCourseView(sequence, course , 'edit');
                        });

                        viewScope.$on('ccc-subject-area-sequence.deleteCourse', function (e, sequence, course) {

                            $scope.subjectArea.deleteCourse(course, true).then(function () {
                                $scope.$broadcast('ccc-subject-area-sequence.refresh', [{cb21Code: course.cb21Code, courseGroup: course.courseGroup}], true);
                                $scope.$emit('ccc-workflow-subject-area-sequence.sequenceUpdated', $scope.subjectArea);
                            });
                        });

                        viewScope.$on('ccc-subject-area-sequence-view.done', function () {
                            $scope.$emit('ccc-workflow-subject-area-sequence.done');
                        });


                        viewScope.$on('ccc-subject-area-sequence-view.sequenceUpdated', function () {
                            $scope.$emit('ccc-workflow-subject-area-sequence.sequenceUpdated', $scope.subjectArea);
                        });

                        $scope.viewManager.pushView({
                            id: 'subject-area-sequence-view',
                            title: $scope.subjectArea.title,
                            breadcrumb: $scope.subjectArea.title,
                            scope: viewScope,
                            template: '<ccc-subject-area-sequence-view subject-area="subjectArea"></ccc-subject-area-sequence-view>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addSequenceView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
