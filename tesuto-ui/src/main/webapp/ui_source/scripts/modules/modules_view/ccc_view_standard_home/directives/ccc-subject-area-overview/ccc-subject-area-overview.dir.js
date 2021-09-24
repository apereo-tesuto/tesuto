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

    angular.module('CCC.View.Home').directive('cccSubjectAreaOverview', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '=',
                college: '=',
                initialMode: '=?'   // 'published' or 'edits'
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.mode = $scope.initialMode ? $scope.initialMode : 'published';

                    $scope.loading = false;


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/

                    $scope.setMode = function (newMode) {
                        $scope.mode = newMode;
                    };

                    $scope.done = function () {
                        $scope.$emit('ccc-subject-area-overview.done');
                    };


                    /*============ LISTENERS ==============*/

                    // parent can ask me to update my subject area
                    $scope.$on('ccc-subject-area-overview.update', function (e, subjectArea) {

                        $scope.subjectArea = subjectArea;

                        // whichever view is loaded will need to propogate changes
                        $scope.$broadcast('ccc-subject-area-overview-published.update', subjectArea);
                        $scope.$broadcast('ccc-subject-area-overview-edits.update', subjectArea);
                    });

                    $scope.$on('ccc-subject-area-overview-edits.publishEdits', function (e) {
                        $scope.$emit('ccc-subject-area-overview.publish');
                    });

                    $scope.$on('ccc-subject-area-overview-edits.editConfigs', function (e) {
                        $scope.$emit('ccc-subject-area-overview.editConfigs', $scope.college, $scope.subjectArea);
                    });

                    $scope.$on('ccc-subject-area-overview-edits.editSequences', function (e) {
                        $scope.$emit('ccc-subject-area-overview.editSequences', $scope.college, $scope.subjectArea);
                    });


                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<h2 class="no-outline" ccc-focusable>{{::college.name}}</h2>',

                '<div class="summary-panes">',

                    '<ul class="nav nav-tabs" role="tablist">',
                        '<li role="presentation" ng-class="{active: mode === \'published\'}">',
                            '<a href="#" role="tab" aria-selected="{{mode === \'published\'}}" aria-controls="publishedContent" tabindex="0" ng-click="setMode(\'published\')"><span class="icon fa fa-check-square-o" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.TAB_PUBLISHED"></span></a>',
                        '</li>',
                        '<li role="presentation" ng-class="{active: mode === \'edits\'}">',
                            '<a href="#" role="tab" aria-selected="{{mode === \'edits\'}}" aria-controls="unpublishedContent" tabindex="0" ng-click="setMode(\'edits\')"><span class="icon fa fa-edit" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.TAB_EDITS"></span></a>',
                        '</li>',
                    '</ul>',

                    '<div class="tab-content">',

                        '<div role="tabpanel" class="tab-pane" id="publishedContent" ng-class="{active: mode === \'published\'}">',
                            '<ccc-subject-area-overview-published subject-area="subjectArea" ng-if="mode === \'published\'"></ccc-subject-area-overview-published>',
                        '</div>',

                        '<div role="tabpanel" class="tab-pane" id="unpublishedContent" ng-class="{active: mode === \'edits\'}">',
                            '<ccc-subject-area-overview-edits subject-area="subjectArea" ng-if="mode === \'edits\'"></ccc-subject-area-overview-edits>',
                        '</div>',

                    '</div>',

                '</div>',

                '<div class="margin-top-sm">',
                    '<button class="btn btn-primary btn-submit-button btn-icon-left" ng-click="done()"><i class="fa fa-chevron-left"></i> Done</button>',
                '</div>'

            ].join('')

        };

    });

})();
