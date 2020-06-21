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

    angular.module('CCC.View.Home').directive('cccSubjectAreaCourseCompetencyMap', function () {

        return {

            restrict: 'E',

            scope: {
                course: '=',
                subjectArea: '=',
                isDisabled: '=?'
            },

            controller: [

                '$scope',
                '$q',
                '$timeout',
                'CompetencyGroupClass',
                'BooleanGroupClass',

                function ($scope, $q, $timeout, CompetencyGroupClass, BooleanGroupClass) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    // we create a cache for the last successfully saved booleanLogic string
                    // this allows us to reset the ui if a structure save fails
                    var lastBooleanLogicString = '[]';


                    /*============ MODEL ==============*/

                    $scope.loadingCompetencyGroups = true;

                    $scope.isDisabled = false;

                    $scope.booleanGroup = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getItem = function () {

                        var competencyGroup = new CompetencyGroupClass({
                            courseId: $scope.course.courseId
                        });

                        $scope.isDisabledInternally = true;
                        $scope.$emit('ccc-subject-area-course-competency-map.loading');

                        return competencyGroup.create().then(function () {

                            $scope.course.addCompetencyGroup(competencyGroup);
                            $scope.$emit('ccc-subject-area-course-competency-map.courseUpdated');

                            return {
                                data: {
                                    competencyGroup: competencyGroup,
                                    subjectArea: $scope.subjectArea
                                },
                                directive: '<ccc-course-competency-group-list-item competency-group="data.competencyGroup" group-number="itemIndex" subject-area="data.subjectArea"></ccc-course-competency-group-list-item>'
                            };

                        }).finally(function () {

                            $scope.isDisabledInternally = false;
                            $scope.$emit('ccc-subject-area-course-competency-map.loadingComplete');
                        });
                    };

                    var getGroup = function () {

                        var competencyGroup = new CompetencyGroupClass({
                            courseId: $scope.course.courseId
                        });

                        $scope.isDisabledInternally = true;
                        $scope.$emit('ccc-subject-area-course-competency-map.loading');

                        return competencyGroup.create().then(function () {

                            $scope.course.addCompetencyGroup(competencyGroup);
                            $scope.$emit('ccc-subject-area-course-competency-map.courseUpdated');

                            return {
                                booleanItems: [{
                                    data: {
                                        competencyGroup: competencyGroup,
                                        subjectArea: $scope.subjectArea
                                    },
                                    directive: '<ccc-course-competency-group-list-item competency-group="data.competencyGroup" group-number="itemIndex" subject-area="data.subjectArea"></ccc-course-competency-group-list-item>'
                                }],
                                operator: null
                            };

                        }).finally(function () {

                            $scope.isDisabledInternally = false;
                            $scope.$emit('ccc-subject-area-course-competency-map.loadingComplete');
                        });
                    };

                    var getBooleanItemsForBooleanGroup = function (booleanGroup, competencyGroupMap) {

                        var booleanGroupArray = [];

                        var competencyGroupItem;
                        var competencyGroupOperator;

                        for (var i=0; i < booleanGroup.length; i = i + 2) {

                            competencyGroupItem = booleanGroup[i];
                            competencyGroupOperator = booleanGroup[i+1];

                            if (angular.isArray(competencyGroupItem)) {

                                booleanGroupArray.push({
                                    booleanItems: getBooleanItemsForBooleanGroup(competencyGroupItem, competencyGroupMap),
                                    operator: competencyGroupOperator ? competencyGroupOperator : null
                                });

                            } else {

                                booleanGroupArray.push({
                                    data: {
                                        competencyGroup: competencyGroupMap[competencyGroupItem],
                                        subjectArea: $scope.subjectArea
                                    },
                                    operator: competencyGroupOperator ? competencyGroupOperator : null,
                                    directive: '<ccc-course-competency-group-list-item competency-group="data.competencyGroup" group-number="itemIndex" subject-area="data.subjectArea"></ccc-course-competency-group-list-item>'
                                });
                            }
                        }

                        return booleanGroupArray;
                    };

                    var getBooleanGroupObjectForCourse = function (course) {

                        var competencyGroupMap = _.reduce(course.competencyGroups, function (memo, competencyGroup) {
                            memo[competencyGroup.competencyGroupId] = competencyGroup;
                            return memo;
                        }, {});

                        var booleanGroup;

                        try {
                            booleanGroup = JSON.parse(course.competencyGroupLogic);
                        } catch (e) {
                            booleanGroup = [];
                        }

                        var booleanItems;

                        // because if the booleanGroup is corrupted the whole UI will break, we look out for that and reset it
                        try {
                            booleanItems = getBooleanItemsForBooleanGroup(booleanGroup, competencyGroupMap);
                        } catch (e) {
                            booleanItems = [];
                        }

                        return booleanItems;
                    };

                    var updateBooleanItemsFromCourse = function () {
                        var booleanItems = getBooleanGroupObjectForCourse($scope.course);
                        $scope.booleanGroup.setBooleanItems(booleanItems);

                        lastBooleanLogicString = JSON.stringify($scope.booleanGroup.serialize());
                    };

                    var getCompetencyGroups = function () {

                        $scope.loadingCompetencyGroups = true;
                        $scope.course.fetchCompetencyGroups(true).finally(function () {

                             $scope.loadingCompetencyGroups = false;

                             lastBooleanLogicString = $scope.course.competencyGroupLogic;
                             updateBooleanItemsFromCourse();
                        });
                    };

                    var updateCourseBooleanStructure = function () {

                        var newBooleanStructure = $scope.booleanGroup.serialize();

                        $scope.course.competencyGroupLogic = JSON.stringify(newBooleanStructure);

                        $scope.isDisabledInternally = true;
                        $scope.$emit('ccc-subject-area-course-competency-map.loading');
                        $scope.course.update().then(function () {

                            lastBooleanLogicString = $scope.course.competencyGroupLogic;
                            $scope.$emit('ccc-subject-area-course-competency-map.courseUpdated');

                        // if this call fails, then we need to revert that change in the structure
                        }, function () {

                            $scope.course.competencyGroupLogic = lastBooleanLogicString;
                            updateBooleanItemsFromCourse();

                        }).finally(function () {
                            $scope.isDisabledInternally = false;
                            $scope.$emit('ccc-subject-area-course-competency-map.loadingComplete');
                        });
                    };

                    var debounced_updateCourseBooleanStructure = _.debounce(updateCourseBooleanStructure, 10);

                    var createBooleanGroup = function () {

                        $scope.booleanGroup = new BooleanGroupClass({

                            itemDeletedEvent: 'ccc-course-competency-group-list-item.deleted', // the booleanGroupClass will listen to items when they delete themselves
                            itemClickedEvent: 'ccc-course-competency-group-list-item.clicked',

                            getItem: getItem,   // the booleanGroupClass will ask us for new item data
                            getGroup: getGroup, // the booleanGroupClass will ask us for new group data

                            dataSerializer: function (itemData) {   // when we serialize the boolean logic we need to know how to serialize item data
                                return itemData.competencyGroup.competencyGroupId;
                            }
                        });

                        // anytime the structure of the boolean tree changes, we need to save it
                        // NOTE: there are cases where multiple structureChanges can occur so we debounce this call (ex, last item in a group is deleted, group also gets deleted)
                        $scope.booleanGroup.addListener('structureChange', debounced_updateCourseBooleanStructure);
                    };


                    /*============ BEHAVIOR ==============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-boolean-group.itemDeleted', function (e, competencyGroupData) {
                        $scope.course.deleteCompetencyGroup(competencyGroupData.competencyGroup);
                    });

                    $scope.$on('ccc-boolean-group.itemClicked', function (e, data) {
                        $scope.$emit('ccc-subject-area-course-competency-map.competencySelected', $scope.course, data.competencyGroup);
                    });

                    $scope.$on('ccc-boolean-group.itemAdded', function (event, item) {
                        $scope.$emit('ccc-subject-area-course-competency-map.competencyGroupAdded', item.data.competencyGroup);
                    });
                    $scope.$on('ccc-boolean-group.groupAdded', function (event, groupItem) {
                        $scope.$emit('ccc-subject-area-course-competency-map.competencyGroupAdded', groupItem.booleanItems[0].data.competencyGroup);
                    });

                    // someone above us changed our course, so we need to refresh our boolean group
                    $scope.$on('ccc-subject-area-course-competency-map.requestRefresh', function () {
                        updateBooleanItemsFromCourse();
                    });

                    // disable everything while we are deleting
                    $scope.$on('ccc-course-competency-group-list-item.deleteStarted', function () {
                        $scope.isDisabledInternally = true;
                    });
                    $scope.$on('ccc-course-competency-group-list-item.deleteComplete', function () {
                        $scope.isDisabledInternally = false;
                    });

                    $scope.$watch('isDisabledInternally', function (newIsDisabledValue) {
                        $scope.$emit('ccc-subject-area-course-competency-map.isLoading', newIsDisabledValue);
                    });


                    /*============ INITIALIZATION ==============*/

                    createBooleanGroup();

                    getCompetencyGroups();
                }
            ],

            template: [

                '<ccc-content-loading-placeholder class="ccc-subject-area-course-competency-map-loader" ng-hide="course.competencyGroups.length > 0" no-results-info="course.competencyGroups.length === 0 && !loadingCompetencyGroups" hide-default-no-results-text="true">',
                    '<div class="no-comp-groups">',
                        '<i class="fa fa fa-exclamation-triangle text-symbol-warning" aria-hidden="true"></i> ',
                        '<span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-COURSE-COMPETENCY-MAP.NO_COMPETENCY_GROUPS"></span>',
                    '</div>',
                '</ccc-content-loading-placeholder>',

                '<ccc-boolean-group boolean-group="booleanGroup" ng-if="!loadingCompetencyGroups && booleanGroup" is-disabled="isDisabled || isDisabledInternally"></ccc-boolean-group>'

            ].join('')

        };

    });

})();
