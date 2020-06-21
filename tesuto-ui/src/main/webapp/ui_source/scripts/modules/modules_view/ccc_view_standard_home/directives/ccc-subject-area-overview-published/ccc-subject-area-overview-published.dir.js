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

    angular.module('CCC.View.Home').directive('cccSubjectAreaOverviewPublished', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '='
            },

            controller: [

                '$scope',
                'Moment',
                'SubjectAreasAPIService',
                'MMSubjectAreasAPIService',
                'MMSubjectAreaClass',

                function ($scope, Moment, SubjectAreasAPIService, MMSubjectAreasAPIService, MMSubjectAreaClass) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.loading = false;
                    $scope.publishedSubjectArea = false;

                    $scope.isMMSubjectArea = $scope.subjectArea instanceof MMSubjectAreaClass;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var refreshMMSubjectAreaData = function () {
                        $scope.loading = true;

                        MMSubjectAreasAPIService.getPublishedVersion($scope.subjectArea.disciplineId, $scope.subjectArea.version).then(function (publishedSubjectArea) {

                            $scope.publishedSubjectArea = publishedSubjectArea;

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };

                    var refreshSubjectAreaData = function () {
                        $scope.loading = true;

                        SubjectAreasAPIService.subjectAreas.getPublishedVersion($scope.subjectArea.disciplineId, $scope.subjectArea.version).then(function (publishedSubjectArea) {

                            $scope.publishedSubjectArea = publishedSubjectArea;

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };

                    var refreshData = function () {

                        if ($scope.subjectArea.published) {

                            if ($scope.isMMSubjectArea) {
                                refreshMMSubjectAreaData();
                            } else {
                                refreshSubjectAreaData();
                            }
                        }
                    };

                    var initialize = function () {
                        refreshData();
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.getFormattedDate = function (dateValue) {
                        return new Moment(dateValue).format('M/D/YYYY');
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-subject-area-overview-edits.update', function (e, subjectArea) {
                        $scope.subjectArea = subjectArea;
                        refreshData();
                    });


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [

                '<h3>{{publishedSubjectArea.title}}</h3>',

                '<span class="publish-status not-placing" ng-if="!subjectArea.published">',
                    '<span class="status">',
                        '<span class="icon fa fa-times-circle" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.STATUS_NOT_PLACING_STUDENTS"></span>',
                    '</span>',
                    '<span class="date" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.DATE_NO_PUBLISHED_MODEL"></span>',
                '</span>',

                '<div class="publish-status published" ng-if="subjectArea.published">',
                    '<span class="status">',
                        '<span class="icon fa fa-check-circle" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.STATUS_PLACING_STUDENTS"></span>',
                    '</span>',
                    '<span class="date"><span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.DATE_PUBLISHED"></span> {{::getFormattedDate(subjectArea.publishedDate)}}</span>',
                '</div>',

                '<div ng-if="subjectArea.published">',

                    '<div class="content-section sa-configuration">',
                        '<h3 class="margin-top-sm" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.SECTION_CONFIGURATION"></h3>',
                        '<ccc-content-loading-placeholder ng-if="subjectArea.published && (loading || !loading && !publishedSubjectArea)" no-results="!loading && !publishedSubjectArea"></ccc-content-loading-placeholder>',
                        '<ccc-subject-area-summary-configs subject-area="publishedSubjectArea" ng-if="!loading"></ccc-subject-area-summary-configs>',
                    '</div>',

                    '<div class="content-section placement-sequence">',
                        '<h3 class="margin-top-md" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-OVERVIEW.SECTION_SEQUENCES"></h3>',
                        '<ccc-content-loading-placeholder ng-if="subjectArea.published && (loading || !loading && !publishedSubjectArea)" no-results="!loading && !publishedSubjectArea"></ccc-content-loading-placeholder>',
                        '<ccc-subject-area-summary-sequences subject-area="publishedSubjectArea" ng-if="!loading"></ccc-subject-area-summary-sequences>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
