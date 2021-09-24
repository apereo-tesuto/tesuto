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

    angular.module('CCC.View.Home').directive('cccSubjectAreaOverviewEdits', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '='
            },

            controller: [

                '$scope',
                'Moment',
                'CurrentUserService',

                function ($scope, Moment, CurrentUserService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.userCanEdit = CurrentUserService.hasPermission('UPDATE_DISCIPLINE');


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/

                    $scope.getFormattedDate = function (dateValue) {
                        return new Moment(dateValue).format('M/D/YYYY');
                    };

                    $scope.publishEdits = function () {
                        $scope.$emit('ccc-subject-area-overview-edits.publishEdits', $scope.subjectArea);
                    };

                    $scope.editConfigs = function () {
                        $scope.$emit('ccc-subject-area-overview-edits.editConfigs', $scope.subjectArea);
                    };

                    $scope.editSequences = function () {
                        $scope.$emit('ccc-subject-area-overview-edits.editSequences', $scope.subjectArea);
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-subject-area-overview-edits.update', function (e, subjectArea) {
                        $scope.subjectArea = subjectArea;
                        $scope.$broadcast('ccc-subject-area-summary-configs.update', subjectArea);
                        $scope.$broadcast('ccc-subject-area-summary-sequences.update', subjectArea);
                    });


                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<h3>{{subjectArea.title}}</h3>',

                '<div class="publish-status unpublished changes" ng-if="subjectArea.dirty">',
                    '<span class="status">',
                        '<span class="icon fa fa-warning" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.STATUS_UNPUBLISHED_EDITS"></span>',
                    '</span>',
                    '<span class="date"><span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.DATE_LAST_EDITED"></span> {{::getFormattedDate(subjectArea.lastEditedDate)}}</span>',
                    '<button class="btn btn-success action change-view" ng-click="publishEdits()" ng-if="userCanEdit">',
                        '<span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.ACTION_PUBLISH"></span> <span class="icon fa fa-chevron-right" role="presentation" aria-hidden="true"></span>',
                    '</button>',
                '</div>',

                '<div class="publish-status unpublished no-changes" ng-if="!subjectArea.dirty">',
                    '<span class="status">',
                        '<span class="icon fa fa-warning" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.STATUS_NO_EDITS"></span>',
                    '</span>',
                    '<span class="date" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.DATE_SINCE_LAST_PUBLISH"></span>',
                '</div>',

                '<div class="content-section sa-configuration">',

                    '<h3 class="margin-top-sm" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.SECTION_CONFIGURATION"></h3>',
                    '<div class="well well-sm action-bar top" ng-if="userCanEdit">',
                        '<button class="btn btn-primary" ng-click="editConfigs()" ccc-focusable>',
                            '<span class="icon fa fa-pencil" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.ACTION_EDIT_CONFIGS"></span>',
                        '</button>',
                    '</div>',

                    '<ccc-subject-area-summary-configs subject-area="subjectArea" ng-if="subjectArea.dirty"></ccc-subject-area-summary-configs>',

                '</div>',

                '<div class="content-section placement-sequence">',

                    '<h3 class="margin-top-md" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.SECTION_SEQUENCES"></h3>',
                    '<div class="well well-sm action-bar top" ng-if="userCanEdit">',
                        '<button class="btn btn-primary" ng-click="editSequences()" ccc-focusable>',
                            '<span class="icon fa fa-pencil" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.ACTION_EDIT_SEQUENCES"></span>',
                        '</button>',
                    '</div>',

                    '<ccc-subject-area-summary-sequences subject-area="subjectArea" ng-if="subjectArea.dirty"></ccc-subject-area-summary-sequences>',

                '</div>'

            ].join('')

        };

    });

})();
