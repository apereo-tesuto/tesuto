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

    angular.module('CCC.View.Home').directive('cccActivationDetailsCard', function () {

        return {

            restrict: 'E',

            scope: {
                assessments: '=?',
                location: '=?',
                isDisabled: '=?',
                allowEdit: '=?'
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    /*============ MODEL ============*/

                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.edit = function () {
                        $scope.$emit('ccc-activation-details-card.edit');
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [

                '<div class="well" ng-click="edit()">',

                    '<div class="edit" ng-if="allowEdit">',
                        '<button class="btn btn-sm btn-link" ng-disabled="isDisabled">',
                            '<span class="icon fa fa-pencil" role="presentation" aria-hidden="true"></span>',
                            '<span translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_SEARCH.EDIT"></span>',
                        '</button>',
                    '</div>',

                    '<div class="row location">',
                        '<div class="col-xs-2 col-sm-1">',
                            '<span class="at text-right" translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_SEARCH.AT"></span>',
                        '</div>',
                        '<div class="col-xs-8 col-sm-9"><h3>{{location.name}}</h3></div>',
                    '</div>',

                    '<div class="row assessments">',
                        '<div class="col-xs-2 col-sm-1">',
                            '<span class="for text-right" translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.STUDENT_SEARCH.FOR"></span>',
                        '</div>',
                        '<div class="col-xs-8 col-sm-9">',
                            '<span class="tag" ng-repeat="assessment in assessments">',
                                '<span class="label label-info">{{assessment.title}}</span>',
                            '</span>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')
        };
    });

})();

