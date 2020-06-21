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

    angular.module('CCC.View.Home').directive('cccSequenceCourseList', function () {

        return {

            restrict: 'E',

            scope: {
                sequence: '=',
                isDisabled: '=?',
                readOnly: '@?'
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.readOnly = ($scope.readOnly === 'true' ? true : false);

                    $scope.allowDelete = !$scope.readOnly;


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ==============*/

                    $scope.courseClicked = function (course) {
                        $scope.$emit('ccc-sequence-course-list.courseClicked', $scope.sequence, course);
                    };

                    $scope.delete = function (course) {
                        $scope.$emit('ccc-sequence-course-list.deleteCourse', $scope.sequence, course);
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<ul>',
                    '<li ng-repeat="course in sequence.courses track by course.courseId">',
                        '<button ng-click="courseClicked(course)" ccc-focusable ng-disabled="isDisabled" class="btn btn-inverted btn-full-width dont-break-out">',
                            '{{course.subject}} {{course.number}}',
                        '</button>',
                        '<span ng-if="allowDelete" class="ccc-sequence-course-list-delete" ng-click="delete(course)" role="button">',
                            '<i class="fa fa-times-circle noanim" ng-if="!course.deleting"></i>',
                            '<i class="fa fa-spin fa-spinner noanim" ng-if="course.deleting"></i>',
                            '<span class="sr-only"><span translate="CCC_VIEW_HOME.SEQUENCE_COURSE_LIST.DELETE_COURSE"></span> {{course.subject}} {{course.number}}</span>',
                        '</span>',
                    '</li>',
                '</ul>'

            ].join('')
        };

    });

})();
