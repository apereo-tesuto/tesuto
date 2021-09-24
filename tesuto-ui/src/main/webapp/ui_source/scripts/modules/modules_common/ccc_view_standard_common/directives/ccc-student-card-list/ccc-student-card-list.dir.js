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

    angular.module('CCC.View.Common').directive('cccStudentCardList', function () {

        return {

            restrict: 'E',

            scope: {
                students: '=?',
                showRemove: '=?',
                isDisabled: '=?'
            },

            controller: [

                '$scope',
                '$element',

                function ($scope, $element) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var studentCardListId = $element.attr('id') ? $element.attr('id') : false;


                    /*============ MODEL ============*/

                    $scope.showRemove = $scope.showRemove || false;


                    /*============ BEHAVIOR ============*/

                    $scope.remove = function (student) {
                        $scope.$emit('ccc-student-card-list.remove', student, studentCardListId);
                    };

                    $scope.normalizeStudents = function (students) {
                        var index = 0; //reset every time we normalize;
                        _.each(students, function (student) {
                            student.index = index++;
                        });
                        return students;
                    };

                    $scope.normalizeIndex = function(index) {
                        return index + 1;
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-student-card-list.update', function (e, students) {
                        $scope.students = students;
                    });


                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [
                '<ul class="list-unstyled search-result student-cards">',
                    '<li class="user" ng-repeat="student in normalizeStudents(students) track by $index">',
                        '<div class="row card-data">',
                            '<div class="number-wrapper">',
                                '<div class="list-number">',
                                    '<span class="value number">{{normalizeIndex(student.index)}}</span>',
                                '</div>',
                            '</div>',
                            '<div class="card-wrapper">',
                                '<div class="col-sm-10">',
                                    '<ccc-student-card student="student"></ccc-student-card>',
                                '</div>',
                                '<div class="col-sm-2" ng-if="showRemove === true">',
                                    '<button class="btn btn-default btn-full-width-when-small remove text-right" aria-label="Remove" ng-click="remove(student)" ng-disabled="isDisabled">',
                                        '<span class="visible-xs-inline"><span translate="CCC_VIEW_STANDARD_COMMON.CCC-STUDENT-CARD-LIST.REMOVE"></span> </span>',
                                        '<span class="icon fa fa-times" role="presentation" aria-hidden="true"></span> ',
                                    '</button>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</li>',
                '</ul>'
            ].join('')
        };
    });

})();
