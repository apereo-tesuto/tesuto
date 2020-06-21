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

    angular.module('CCC.View.Home').directive('cccPlacementComponentGroups', function () {

        return {

            restrict: 'E',

            scope: {
                componentGroups: '='
            },

            controller: [

                '$scope',
                'Moment',

                function ($scope, Moment) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var COMPONENT_TYPE_LABEL_MAP = {
                        'org.cccnext.tesuto.placement.model.CCCAssessPlacementComponent': 'CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LABEL_ASSESSMENT',
                        'org.cccnext.tesuto.placement.model.MmapPlacementComponent': 'CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LABEL_MM'
                    };


                    /*============= MODEL =============*/

                    /*============= MODEL DEPENDENT METHODS =============*/


                    /*============= BEHAVIOR =============*/

                    $scope.viewAssessmentResultsClick = function (component) {
                        $scope.$emit('ccc-placement-component-groups.assessmentComponentClicked', component);
                    };

                    $scope.getComponentType = function (component) {
                        return COMPONENT_TYPE_LABEL_MAP[component.entityTargetClass];
                    };

                    $scope.getFormattedDate = function (date) {
                        return new Moment(date).format('MMMM D, YYYY');
                    };


                    /*============= LISTENERS =============*/

                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [

                '<div class="ccc-placement-component-group-container" ng-repeat="group in componentGroups">',

                    '<h5 class="subject-area">{{group.group}}</h5>',

                    '<ul class="preliminary-list">',

                        '<li class="placement" ng-repeat="component in group.components">',
                            '<h6 class="type"><span translate="{{::getComponentType(component)}}"></span> <span class="date">{{::getFormattedDate(component.createdOn)}}</span></h6>',
                            '<div class="body">',

                                '<span class="transfer-level label label-primary">',
                                    '<span ng-if="::component.cb21.level > 0" translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LEVELS_BELOW" translate-values="{LEVELS_BELOW_VALUE: component.cb21.level}" translate-interpolation="messageformat"></span>',
                                    '<span translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.LEVELS_BELOW_TRANSFER" ng-if="::component.cb21.level === 0"></span>',
                                '</span>',

                                '<p ng-if="::component.entityTargetClass === \'org.cccnext.tesuto.placement.model.CCCAssessPlacementComponent\'">',
                                    '<span translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.ASSESSMENT_DESCRIPTION" translate-values="{DATE: getFormattedDate(component.assessmentDate)}"></span> ',
                                    '<a href="#" class="change-view" ng-click="viewAssessmentResultsClick(component)" ccc-focusable>',
                                        '<span translate="CCC_VIEW_HOME.CCC-PLACEMENT-COMPONENT-GROUPS.VIEW_ASSESSMENT_RESULTS"></span> ',
                                        '<span class="icon fa fa-chevron-circle-right" role="presentation" aria-hidden="true"></span>',
                                    '</a>',
                                '</p>',

                                '<p ng-if="::component.entityTargetClass === \'org.cccnext.tesuto.placement.model.MmapPlacementComponent\'">{{::component.mmapRuleSetTitle}}</p>',

                            '</div>',
                        '</li>',
                    '</ul>',
                '</div>'

            ].join('')

        };

    });

})();
