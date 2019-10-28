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

    angular.module('CCC.View.Home').directive('cccPlacementCard', function () {

        return {

            restrict: 'E',

            scope: {
                placement: '='
            },

            controller: [

                '$scope',
                '$filter',
                'Moment',
                'SUBJECT_AREA_CB_21_CODES',

                function ($scope, $filter, Moment, SUBJECT_AREA_CB_21_CODES) {


                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var COMPONENT_CLASS_SORT_MAP = {
                        'org.cccnext.tesuto.placement.model.CCCAssessPlacementComponent': 0,
                        'org.cccnext.tesuto.placement.model.MmapPlacementComponent': 1
                    };


                    // TODO: Pull this into a re-usable module for both here and the component groups directive
                    var COMPONENT_TYPE_LABEL_MAP = {
                        'org.cccnext.tesuto.placement.model.CCCAssessPlacementComponent': 'CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LABEL_ASSESSMENT',
                        'org.cccnext.tesuto.placement.model.MmapPlacementComponent': 'CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LABEL_MM'
                    };

                    var getComponentTypeSort = function (comp) {
                        return COMPONENT_CLASS_SORT_MAP[comp.entityTargetClass];
                    };


                    /*============ MODEL ============*/

                    $scope.showDetails = false;


                    /*============= MODEL DEPENDENT METHODS ============*/

                    var orderPlacementComponents = function () {

                        $scope.placement.componentObjects.sort(function (a, b) {

                            if (getComponentTypeSort(a) !== getComponentTypeSort(b)) {

                                return getComponentTypeSort(a) - getComponentTypeSort(b);

                            } else {
                                return a.cb21.level - b.cb21.level;
                            }
                        });
                    };

                    var initialize = function () {
                        orderPlacementComponents();
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.getFormattedDate = function (date) {
                        return new Moment(date).format('MMMM D, YYYY');
                    };

                    $scope.toggleDetails = function () {
                        $scope.showDetails = !$scope.showDetails;
                    };

                    $scope.viewAssessmentResultsClick = function (assessmentComponent) {
                        $scope.$emit('ccc-placement-card.assessmentComponentClicked', assessmentComponent);
                    };

                    $scope.getComponentType = function (component) {
                        return COMPONENT_TYPE_LABEL_MAP[component.entityTargetClass];
                    };


                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="placement-card">',
                    '<div class="placement">',
                        '<div class="placement-header">',
                            '<h3 class="subject-area">{{::placement.subjectAreaName}}</h3>',
                            '<div class="date">{{::getFormattedDate(placement.assignedDate)}}</div>',
                        '</div>',
                        '<div class="placement-body">',
                            '<span class="transfer-level label label-primary">',
                                '<span ng-if="::placement.cb21.level > 0" translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LEVELS_BELOW" translate-values="{LEVELS_BELOW_VALUE: placement.cb21.level}" translate-interpolation="messageformat"></span>',
                                '<span translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LEVELS_BELOW_TRANSFER" ng-if="::placement.cb21.level === 0"></span>',
                            '</span>',
                            '<ul class="course-list">',
                                '<li ng-repeat="course in placement.courses track by $index">',
                                    '<ccc-placement-card-course course="course"></ccc-placement-card-course>',
                                '</li>',
                            '</ul>',
                            '<div ng-if="placement.componentObjects.length" class="placement-details">',
                                '<div class="details-toggle">',
                                    '<button ng-click="toggleDetails()" class="btn btn-sm btn-link" type="button">',
                                        '<span ng-show="!showDetails" class="text-label" translate="CCC_VIEW_HOME.CCC-PLACEMENT-CARD.SHOW_DETAILS"> </span>',
                                        '<span ng-show="showDetails" class="text-label" translate="CCC_VIEW_HOME.CCC-PLACEMENT-CARD.HIDE_DETAILS"> </span>',
                                        '<span class="icon fa" ng-class="{\'fa-chevron-down\': !showDetails, \'fa-chevron-up\': showDetails}" role="presentation" aria-hidden="true"></span>',
                                    '</button>',
                                '</div>',
                                '<div ng-if="showDetails" class="details-body">',
                                    '<ul class="preliminary-list">',
                                        '<li class="placement" ng-repeat="component in placement.componentObjects track by $index">',

                                            '<h6 class="type"><span translate="{{::getComponentType(component)}}"></span></h6>',
                                            '<div class="body">',
                                                '<span class="transfer-level label label-primary">',
                                                    '<span ng-if="::component.cb21.level > 0" translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LEVELS_BELOW" translate-values="{ LEVELS_BELOW_VALUE: component.cb21.level }" translate-interpolation="messageformat"></span>',
                                                    '<span translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LEVELS_BELOW_TRANSFER" ng-if="::component.cb21.level === 0"></span>',
                                                '</span> ',

                                                '<span class="date">{{::getFormattedDate(component.createdOn)}}</span>',

                                                '<p ng-if="::component.entityTargetClass === \'org.cccnext.tesuto.placement.model.CCCAssessPlacementComponent\'">',
                                                    '<span translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.ASSESSMENT_DESCRIPTION" translate-values="{DATE: getFormattedDate(component.assessmentDate)}"></span></br>',
                                                    '<a href="#" class="change-view" ng-click="viewAssessmentResultsClick(component)" ccc-focusable>',
                                                        '<span translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.VIEW_ASSESSMENT_RESULTS"></span> ',
                                                        '<span class="icon fa fa-chevron-circle-right" role="presentation" aria-hidden="true"></span>',
                                                    '</a>',
                                                '</p>',

                                                '<p ng-if="::component.entityTargetClass === \'org.cccnext.tesuto.placement.model.MmapPlacementComponent\'">{{::component.mmapRuleSetTitle}}</p>',

                                            '</div>',

                                        '</li>',
                                    '</ul>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>',

            ].join('')

        };

    });

})();
