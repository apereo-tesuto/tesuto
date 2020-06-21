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

    angular.module('CCC.View.Home').directive('cccRemoteEventsStudentsSummary', function () {

        return {

            restrict: 'E',

            scope: {
                remoteEvent: '=',
                studentSummary: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    /*============ MODEL ============*/

                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.done = function () {
                        $scope.$emit('ccc-remote-events-students-summary.done');
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [

                '<div class="alert alert-success alert-with-icon" role="alert" tabindex="0" ccc-autofocus>',
                    '<p class="alert-body">',
                        '<span class="icon fa fa-check-circle" role="presentation" aria-hidden="true"></span>',
                        '<span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS-SUMMARY.SUCCESS"></span>',
                    '</p>',
                '</div>',

                '<ccc-activation-details-card location="remoteEvent.metaData.location" assessments="remoteEvent.metaData.assessments" is-disabled="processing"></ccc-activation-details-card>',

                '<div class="ccc-student-card-list-set">',

                    // ===================== REMOVE
                    '<h4 ng-if="studentSummary.removed.length" class="ccc-section-header text-warning"><i class="fa fa-minus-circle" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS-SUMMARY.HEADER_STUDENTS_REMOVED"></span> ({{studentSummary.removed.length}})</h4>',
                    '<ccc-student-card-list class="warning" ng-class="{\'margin-bottom-md\': studentSummary.removed.length}" students="studentSummary.removed" id="studentsRemoved"></ccc-student-card-list>',

                    // ===================== ADD
                    '<h4 ng-if="studentSummary.added.length" class="ccc-section-header text-success-dark"><i class="fa fa-plus-circle" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS-SUMMARY.HEADER_STUDENTS_ADDED"></span> ({{studentSummary.added.length}})</h4>',
                    '<ccc-student-card-list class="success" ng-class="{\'margin-bottom-md\': studentSummary.added.length}" students="studentSummary.added" id="studentsAdded"></ccc-student-card-list>',

                '</div>',

                '<button class="btn btn-primary btn-submit-button" ng-click="done()"><i class="fa fa-check" aria-hidden="true"></i><span translate="CCC_VIEW_HOME.CCC-REMOTE-EVENTS-STUDENTS-SUMMARY.OKAY"></span></button>'

            ].join('')
        };
    });

})();
