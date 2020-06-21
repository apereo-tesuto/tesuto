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

    angular.module('CCC.View.Home').directive('cccPaperPencilScoring', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES / METHODS =============*/


                    /*============ MODEL =============*/

                    $scope.actions = [
                        {
                            id: 'studentLookup',
                            icon: 'ui/resources/images/icons/zoom.png',
                            title: 'Student Lookup',
                            description: 'Find a student that already has a paper/pencil activation.'
                        },
                        {
                            id: 'createActivation',
                            icon: 'ui/resources/images/icons/admissions.png',
                            title: 'Create Activation',
                            description: 'Create a new paper/pencil activation for a single student.'
                        },
                        {
                            id: 'bulkActivation',
                            icon: 'ui/resources/images/icons/attendance_list.png',
                            title: 'Bulk Activation',
                            description: 'Bulk activate multiple students for the same paper/pencil assessments.'
                        }
                    ];


                    /*============ MODEL DEPENDENT METHODS =============*/

                    /*============ BEHAVIOR =============*/


                    /*============ LISTENERS =============*/

                    $scope.$on('ccc-action-button-descriptions-list.actionClicked', function (e, action) {
                        $scope.$emit('ccc-paper-pencil-scoring.actionClicked', action.id);
                    });

                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-sm-8 col-sm-offset-2">',
                        '<ccc-action-button-descriptions-list actions="actions" tabindex="0" ccc-focusable class="no-outline"></ccc-action-button-descriptions-list>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();





