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

    angular.module('CCC.View.Home').directive('cccCourseCompetencyGroupTreeSummary', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '=',
                competencyGroup: '='
            },

            controller: [

                '$scope',
                '$timeout',
                'CompetencyMapsModelService',

                function ($scope, $timeout, CompetencyMapsModelService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ============*/

                    $scope.loading = true;

                    $scope.nestedItems = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var containsSelectedItemRecursive = function (item) {
                        return item.selected || _.filter(item.children, function (child) {
                            return containsSelectedItemRecursive(child);
                        }).length;
                    };

                    var pruneNestedItems = function (children) {

                        var newChildren = [];

                        _.each(children, function (child) {

                            if (containsSelectedItemRecursive(child)) {
                                newChildren.push(child);
                                child.children = pruneNestedItems(child.children);
                            }
                        });

                        return newChildren;
                    };

                    var getIsItemSelected = function (competency) {
                        return _.find($scope.competencyGroup.competencyIds, function (competencyId) { return competency.identifier === competencyId; }) !== undefined;
                    };

                    var forEachItem = function (itemData) {
                        itemData.selected = getIsItemSelected(itemData.item);
                    };

                    var loadCompetencyTree = function () {

                        $scope.loading = true;

                        CompetencyMapsModelService.getNestedItemsModel($scope.subjectArea.competencyAttributes.competencyCode, $scope.subjectArea.competencyMapVersion, forEachItem).then(function (nestedItems) {
                            $scope.nestedItems = pruneNestedItems(nestedItems);
                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    loadCompetencyTree();
                }
            ],

            template: [

                '<ccc-content-loading-placeholder class="ccc-course-competency-tree-loader" ng-if="nestedItems.length === 0 || loading" no-results="nestedItems.length === 0 && !loading" hide-default-no-results-text="true">',
                    '<span translate="CCC_VIEW_HOME.CCC-COURSE-COMPETENCY-GROUP-TREE-SUMMARY.NO_RESULTS"></span>',
                '</ccc-content-loading-placeholder>',
                '<ccc-nested-item-selector ',
                    'class="ccc-nested-item-selector-style-infinite-boxes" ',
                    'ng-if="nestedItems.length > 0 && !loading" ',
                    'items="nestedItems" ',
                    'disable-collapse="true" ',
                    'enable-enforce-parents-deselected="false" ',
                    'enable-enforce-parents-selected="false" ',
                    'disable-parent-auto-select="true" ',
                    'disable-child-auto-deselect="true" ',
                    'is-disabled="true" ',
                '></ccc-nested-item-selector>',

            ].join('')

        };

    });

})();
