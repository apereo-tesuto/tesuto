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

    angular.module('CCC.View.Home').directive('cccPlacementCardCourse', function () {

        return {

            restrict: 'E',

            scope: {
                course: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ============*/

                    $scope.showCourseDetails = false;


                    /*============= MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.toggleCourseDetails = function () {
                        $scope.showCourseDetails = !$scope.showCourseDetails;
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<a href="" ng-click="toggleCourseDetails()" type="button" class="course-button">',

                    '<span class="subject">{{::course.subject}}</span> ',
                    '<span class="number">{{::course.number}}</span> ',

                    '<div class="well" ng-show="showCourseDetails">',
                        '<p>{{::course.description}}</p>',
                    '</div>',
                '</a>'

            ].join('')

        };

    });

})();
