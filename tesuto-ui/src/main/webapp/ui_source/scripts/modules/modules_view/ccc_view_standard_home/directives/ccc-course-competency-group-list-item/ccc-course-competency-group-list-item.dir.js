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

    angular.module('CCC.View.Home').directive('cccCourseCompetencyGroupListItem', function () {

        return {

            restrict: 'E',

            scope: {
                competencyGroup: '=',
                subjectArea: '=',
                groupNumber: '=?',
                isDisabled: '=?'
            },

            controller: [

                '$scope',
                '$element',
                'ModalService',

                function ($scope, $element, ModalService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.deleting = false;

                    $scope.showSummary = false;


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ==============*/

                    $scope.toggleSummary = function (event) {
                        event.preventDefault();
                        event.stopPropagation();
                        $scope.showSummary = !$scope.showSummary;
                    };

                    $scope.competencyGroupClass = function () {
                        return {
                            'competency-group-invalid': !$scope.competencyGroup.competencyIds.length
                        };
                    };

                    $scope.itemClicked = function (event) {

                        if ($scope.isDisabled) {
                            return;
                        }

                        $scope.$emit('ccc-course-competency-group-list-item.clicked', $scope.competencyGroup);
                    };

                    $scope.delete = function (event) {

                        // so we don't trigger click on the item
                        event.preventDefault();
                        event.stopPropagation();

                        if ($scope.isDisabled) {
                            return;
                        }

                        var buttonConfigs = {
                            cancel: {
                                title: 'Cancel',
                                btnClass: 'btn-primary'
                            },
                            okay: {
                                title: 'Yes',
                                btnClass: 'btn-default'
                            }
                        };

                        var deleteModal = ModalService.openConfirmModal({
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.DELETE_COMPETENCY_GROUP.TITLE',
                            message: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-PLACEMENTS.DELETE_COMPETENCY_GROUP.MESSAGE',
                            buttonConfigs: buttonConfigs,
                            type: 'warning'
                        });

                        deleteModal.result.then(function () {

                            $scope.deleting = true;
                            $scope.$emit('ccc-course-competency-group-list-item.deleteStarted', $scope.competencyGroup);

                            $scope.competencyGroup.delete().then(function () {

                                $scope.$emit('ccc-course-competency-group-list-item.deleted', $scope.competencyGroup);

                            }).finally(function () {
                                $scope.deleting = false;
                                $scope.$emit('ccc-course-competency-group-list-item.deleteComplete', $scope.competencyGroup);
                            });
                        });
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<div ng-class="competencyGroupClass()">',

                    '<div class="group-wrapper" ng-click="itemClicked($event)" ccc-focusable>',
                        '<div class="group">',
                            '<span class="edit">',
                                '<span class="icon fa fa-pencil" role="presentation" aria-hidden="true"></span>',
                            '</span>',
                            '<div class="content dont-break-out">',
                                '<span class="name">',
                                    '{{competencyGroup.name ? competencyGroup.name : \'Group \' + (groupNumber + 1)}} ',
                                    // '<span class="percent">',
                                    //     '{{competencyGroup.percent === null ? \'- -\' : competencyGroup.percent + \' %\'}} ',
                                    //     '<span class="symbol"></span>',
                                    // '</span> ',
                                    '<span class="sr-only">Click to edit</span>',
                                    '<a class="btn-link" ng-click="toggleSummary($event)">',
                                        '<span ng-if="!showSummary" translate="CCC_VIEW_HOME.CCC-COURSE-COMPETENCY-GROUP-LIST-ITEM.SHOW_COMPETENCIES"></span>',
                                        '<span ng-if="showSummary" translate="CCC_VIEW_HOME.CCC-COURSE-COMPETENCY-GROUP-LIST-ITEM.HIDE_COMPETENCIES"></span>',
                                    '</a>',
                                '</span>',
                                '<div class="ccc-course-competency-group-list-item-invalid">',
                                    '<i class="fa fa-exclamation-triangle" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-COURSE-COMPETENCY-GROUP-LIST-ITEM.NO_COMPETENCIES"></span>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',

                    '<div class="ccc-course-competency-group-tree-summary-container" ng-if="showSummary">',
                        '<ccc-course-competency-group-tree-summary subject-area="subjectArea" competency-group="competencyGroup"></ccc-course-competency-group-tree-summary>',
                    '</div>',

                    '<span class="remove-action" ng-click="delete($event)" role="button">',
                        '<span class="sr-only">Remove Competency Group, {{competencyGroup.name ? competencyGroup.name : \'Group \' + (groupNumber + 1)}}</span>',
                        '<span class="remove-wrapper fa-stack fa">',
                            '<i class="fa fa-circle fa-stack-2x" role="presentation" aria-hidden="true"></i>',
                            '<i class="fa fa-times fa-stack-1x fa-inverse noanim" ng-if="!deleting" role="presentation" aria-hidden="true"></i>',
                            '<i class="fa fa-spin fa-spinner fa-stack-1x fa-inverse noanim" ng-if="deleting" role="presentation" aria-hidden="true"></i>',
                        '</span>',
                    '</span>',

                '</div>'

            ].join('')

        };

    });

})();
