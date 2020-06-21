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

    angular.module('CCC.View.Home').directive('cccSubjectAreasList', function () {

        return {

            restrict: 'E',

            scope: {
                collegeCccId: '=',
                isDisabled: '=?',
                describedBy: '@?',
                disableEdit: '=?'
            },

            controller: [

                '$scope',
                'CollegeSubjectAreasAPIService',
                'CCCUtils',
                'ASSESSMENTS_DISABLED',

                function ($scope, CollegeSubjectAreasAPIService, CCCUtils, ASSESSMENTS_DISABLED) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.subjectAreas = [];
                    $scope.loading = true;

                    $scope.disableEdit = $scope.disableEdit || false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var refresh = function () {

                        $scope.subjectAreas = [];
                        $scope.loading = true;

                        if ($scope.collegeCccId) {

                            CollegeSubjectAreasAPIService.getSubjectAreasByCollegeId($scope.collegeCccId).then(function (subjectAreasData) {

                                var subjectAreas = _.map(subjectAreasData, function (subjectAreaData) {
                                    if (ASSESSMENTS_DISABLED) {
                                        return CCCUtils.coerce('MMSubjectAreaClass', subjectAreaData);
                                    } else {
                                        return CCCUtils.coerce('SubjectAreaClass', subjectAreaData);
                                    }
                                });

                                $scope.subjectAreas = _.filter(subjectAreas, function (subjectArea) {
                                    return subjectArea.archived !== true;
                                });

                                $scope.subjectAreas = _.sortBy($scope.subjectAreas, function (subjectArea) {
                                    return subjectArea.title.toLowerCase();
                                });

                            }).finally(function () {
                                $scope.loading = false;
                            });

                        } else {
                            $scope.loading = false;
                        }
                    };


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-subject-area-card.viewPublishedClicked', function (event, subjectArea) {
                        $scope.$emit('ccc-subject-areas-list.viewPublished', subjectArea);
                    });

                    $scope.$on('ccc-subject-area-card.editClicked', function (event, subjectArea) {
                        $scope.$emit('ccc-subject-areas-list.editClicked', subjectArea);
                    });

                    $scope.$on('ccc-subject-area-card.archiveClicked', function (event, subjectArea) {
                        $scope.$emit('ccc-subject-areas-list.archiveClicked', subjectArea);
                    });

                    $scope.$on('ccc-subject-areas-list.requestRefresh', refresh);

                    $scope.$watch('collegeCccId', refresh);
                }

            ],

            template: [

                '<ccc-content-loading-placeholder hide-default-no-results-text="true" class="ccc-subject-area-list-loader" ng-hide="subjectAreas.length > 0 && !loading" no-results-info="subjectAreas.length === 0 && !loading">',
                    '<span translate="CCC_VIEW_HOME.CCC-SUBJECT_AREAS-LIST.NO_SUBJECT_AREAS"></span>',
                '</ccc-content-loading-placeholder>',

                '<ul class="row">',

                    '<li ng-repeat="subjectArea in subjectAreas track by subjectArea.disciplineId" class="col-md-4 col-sm-6 col-xs-12">',
                        '<ccc-subject-area-card subject-area="subjectArea" disable-edit="disableEdit"></ccc-subject-area-card>',
                    '</li>',

                '</ul>'

            ].join('')

        };

    });

})();
