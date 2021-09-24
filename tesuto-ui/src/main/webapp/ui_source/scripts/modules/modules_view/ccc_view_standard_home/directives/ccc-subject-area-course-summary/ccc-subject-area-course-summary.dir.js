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

    angular.module('CCC.View.Home').directive('cccSubjectAreaCourseSummary', function () {

        return {

            restrict: 'E',

            scope: {
                cb21code: '@',
                course: '=',
                subjectArea: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.loadingCompetencyGroups = true;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============= BEHAVIOR =============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    $scope.course.fetchCompetencyGroups().finally(function () {
                        $scope.loadingCompetencyGroups = false;
                    });
                }
            ],

            template: [

                '<ccc-subject-area-course-card-light course="course"></ccc-subject-area-course-card-light>',

                '<ccc-content-loading-placeholder ng-if="!loadingCompetencyGroups && !course.competencyGroups.length" no-results-info="true" hide-default-no-results-text="true">',
                    '<span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-COURSE-SUMMARY.NO_RESULTS"></span>',
                '</ccc-content-loading-placeholder>',

                '<div ng-repeat="competencyGroup in course.competencyGroups">',
                    '<h5>{{competencyGroup.name ? competencyGroup.name : \'Competency Group (No name provided)\'}}</h5>',
                    '<ccc-course-competency-group-tree-summary subject-area="subjectArea" competency-group="competencyGroup"></ccc-course-competency-group-tree-summary>',
                '</div>'

            ].join('')

        };

    });

})();
