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

    angular.module('CCC.View.Home').directive('cccSubjectAreaCourseCard', function () {

        return {

            restrict: 'E',

            scope: {
                course: '=',
                isDisabled: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ==============*/

                    $scope.editCourse = function () {
                        $scope.$emit('ccc-subject-area-course-card.edit', $scope.course);
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<div class="well">',
                    '<h2 class="title name dont-break-out">',
                        '<span class="data">{{course.name}}</span>',
                    '</h2>',
                    '<h3>',
                        '<span class="subject"><span class="data">{{::course.subject}}</span></span> ',
                        '<span class="number"><span class="data">{{::course.number}}</span></span>',
                    '</h3>',
                    '<p class="description dont-break-out"><span class="data">{{::course.description}}</span></p>',
                    '<div class="cid"><strong>C-ID:</strong> <span class="data">{{::course.cid}}</span></div>',
                    '<div class="transfer-level"><strong>Transfer Level:</strong> <span class="data">{{::course.cb21Code}}</span></div>',
                    '<div class="actions">',
                        '<button class="btn btn-default btn-icon-left edit" ng-disabled="isDisabled" ng-click="editCourse()"><i class="fa fa-pencil" aria-hidden="true"></i> Edit</button>',
                    '</div>',
               '</div>'

            ].join('')

        };

    });

})();
