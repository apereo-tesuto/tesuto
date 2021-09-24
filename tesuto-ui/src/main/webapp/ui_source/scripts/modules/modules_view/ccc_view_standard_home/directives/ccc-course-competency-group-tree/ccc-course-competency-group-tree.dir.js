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

    angular.module('CCC.View.Home').directive('cccCourseCompetencyGroupTree', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '=',
                competencyGroup: '=',
                isDisabled: '=',
                submitted: '=',
                labelledBy: '@?'
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

                    $scope.collegeListLoading = true;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getIsItemSelected = function (competency) {
                        return _.find($scope.competencyGroup.competencyIds, function (competencyId) { return competency.identifier === competencyId; }) !== undefined;
                    };

                    var onSelected = function (item) {
                        $scope.competencyGroup.competencyIds.push(item.id);
                    };
                    var onDeselected = function (item) {
                        var foundIndex = $scope.competencyGroup.competencyIds.indexOf(item.id);
                        $scope.competencyGroup.competencyIds.splice(foundIndex, 1);
                    };

                    var forEachItem = function (itemData) {
                        itemData.selected = getIsItemSelected(itemData.item);
                        itemData.onSelected = onSelected;
                        itemData.onDeselected = onDeselected;
                    };

                    var loadCompetencyTree = function () {

                        $scope.loading = true;

                        CompetencyMapsModelService.getNestedItemsModel($scope.subjectArea.competencyAttributes.competencyCode, $scope.subjectArea.competencyMapVersion, forEachItem).then(function (nestedItems) {
                            $scope.nestedItems = nestedItems;
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

                '<ng-form name="cccCourseCompetencyTreeForm" ccc-show-errors="cccCourseCompetencyTreeForm.locationSelector.$dirty || submitted">',

                    '<ccc-content-loading-placeholder class="ccc-course-competency-tree-loader" ng-if="nestedItems.length === 0 || loading" no-results="nestedItems.length === 0 && !loading"></ccc-content-loading-placeholder>',

                    '<ccc-nested-item-selector class="ccc-nested-item-selector-style-infinite-boxes" ng-if="nestedItems.length > 0 && !loading" items="nestedItems" disable-collapse="true" enable-enforce-parents-deselected="true" is-disabled="isDisabled" labelled-by="ccc-college-selector-errors"></ccc-nested-item-selector>',

                    '<input class="hidden" autocomplete="off" name="locationSelector" ng-model="competencyGroup.competencyIds" ng-model-options="{allowInvalid: true}" ccc-required-array="1" />',

                    '<div id="ccc-course-competency-tree-errors" class="ccc-validation-messages ccc-validation-messages-standalone ccc-validation-messages-course-competency-tree">',
                        '<p ng-if="!competencyGroup.competencyIds.length">',
                            '<i class="fa fa-exclamation-triangle color-warning"></i> ',
                            '<span translate="CCC_VIEW_HOME.COMPETENCY_GROUP_FORM.ERROR.COMPETENCIES"></span>',
                        '</p>',
                    '</div>',

                '</ng-form>'

            ].join('')

        };

    });

})();
