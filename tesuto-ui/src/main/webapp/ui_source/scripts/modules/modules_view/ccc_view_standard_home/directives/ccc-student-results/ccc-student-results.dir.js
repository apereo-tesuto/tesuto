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

    angular.module('CCC.View.Home').directive('cccStudentResults', function () {

        return {

            restrict: 'E',

            scope: {
                students: '=',
                activationControls: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    /*============= MODEL =============*/

                    /*============= MODEL DEPENDENT METHODS =============*/

                    /*============= BEHAVIOR =============*/

                    $scope.selectStudent = function ($event, student) {
                        var studentElement = $($event.currentTarget);
                        $scope.$emit('ccc-student-results.selected', student, studentElement);
                    };


                    /*============= LISTENERS =============*/

                }
            ],

            template: [
                '<ul class="row user-list">',
                    '<li class="col-md-4 col-sm-6 ccc-anim-fade-in" ng-repeat="student in students | orderBy:[\'lastName\',\'firstName\',\'middleName\',\'dateOfBirth\'] track by student.cccId" data-user-id="{{student.cccId}}">',
                        '<ccc-student-user user="student" activation-controls="activationControls" class="ccc-user clickable" tabindex="0" ng-click="selectStudent($event, student)"></ccc-student-user>',
                    '</li>',
                '</ul>'

            ].join('')

        };

    });

})();




