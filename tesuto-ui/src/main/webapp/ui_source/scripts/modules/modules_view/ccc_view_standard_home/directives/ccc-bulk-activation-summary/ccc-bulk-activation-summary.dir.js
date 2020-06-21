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

    angular.module('CCC.View.Home').directive('cccBulkActivationSummary', function () {

        return {

            restrict: 'E',

            scope: {
                assessments: '=',
                students: '=',
                location: '='
            },

            controller: [

                '$scope',
                '$element',

                function ($scope, $element) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    /*============ MODEL ============*/

                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.done = function () {
                        $scope.$emit('ccc-bulk-activation-summary.done');
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [

                '<div class="alert alert-success alert-with-icon" role="alert">',
                    '<p class="alert-body">',
                        '<span class="icon fa fa-check-circle" role="presentation" aria-hidden="true"></span>',
                        '<span translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.SUMMARY.SUCCESS"></span>',
                    '</p>',
                '</div>',

                '<h2 translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.SUMMARY.CREATED"></h2>',

                '<div class="bulk-activation-student-search-location-card well">',

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
                '</div>',

                '<h2 translate="CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.SUMMARY.FOR"></h2>',

                '<ccc-student-card-list students="students"></ccc-student-card-list>',

                '<button class="btn btn-primary btn-submit-button" ng-click="done()"><i class="fa fa-check" aria-hidden="true"></i><span translate="Done" class="ng-scope">Done</span></button>'

            ].join('')
        };
    });

})();
