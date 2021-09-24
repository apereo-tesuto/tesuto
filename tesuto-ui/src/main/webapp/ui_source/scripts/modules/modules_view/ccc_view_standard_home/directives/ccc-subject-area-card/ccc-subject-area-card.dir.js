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

    angular.module('CCC.View.Home').directive('cccSubjectAreaCard', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '=',
                disableEdit: '=?'
            },

            controller: [

                '$scope',
                'Moment',

                function ($scope, Moment) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.subjectAreaTitle = $scope.subjectArea.published ? $scope.subjectArea.publishedTitle : $scope.subjectArea.title;
                    $scope.subjectAreaTitle = $scope.subjectAreaTitle || 'Error, No Published Title';

                    $scope.disableEdit = $scope.disableEdit || false;


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/

                    $scope.getFormattedDate = function (dateValue) {
                        return new Moment(dateValue).format('M/D/YYYY');
                    };

                    $scope.viewPublishedVersion = function () {
                        $scope.$emit('ccc-subject-area-card.viewPublishedClicked', $scope.subjectArea);
                    };

                    $scope.editSubjectArea = function () {
                        $scope.$emit('ccc-subject-area-card.editClicked', $scope.subjectArea);
                    };

                    $scope.archiveSubjectArea = function () {
                        $scope.$emit('ccc-subject-area-card.archiveClicked', $scope.subjectArea);
                    };


                    /*============ LISTENERS ==============*/

                }
            ],

            template: [

                '<div class="wrapper">',
                    '<div class="subject-area-header">',
                        '<h3 class="title" id="subject-area-title-{{::subjectArea.disciplineId}}">{{::subjectAreaTitle}}<span class="text-fade"></span></h3>',
                    '</div>',
                    '<div class="subject-area-body">',

                        '<div class="published" ng-if="subjectArea.published">',
                            '<span class="icon fa fa-check-circle" role="presentation" aria-hidden="true"></span>',
                            '<div class="status" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.STATUS_PLACING_STUDENTS"></div>',
                            '<div class="date"><span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.DATE_PUBLISHED"></span> {{::getFormattedDate(subjectArea.publishedDate)}}</div>',
                            '<button class="btn btn-link btn-link-no-initial-underline btn-sm action" ng-click="viewPublishedVersion()"><span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.VIEW"></span> <span class="fa fa-chevron-right" role="presentation" aria-hidden="true"></span><span class="sr-only">, {{::subjectAreaTitle}}</span></button>',
                        '</div>',

                        '<div class="published not-placing" ng-if="!subjectArea.published">',
                            '<span class="icon fa fa-times-circle" role="presentation" aria-hidden="true"></span>',
                            '<div class="status" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.STATUS_NOT_PLACING_STUDENTS"></div>',
                            '<div class="date" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.DATE_NO_PUBLISHED_MODEL"></div>',
                        '</div>',

                        '<div class="unpublished changes" ng-if="subjectArea.dirty">',
                            '<span class="icon fa fa-warning" role="presentation" aria-hidden="true"></span>',
                            '<div class="status" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.STATUS_UNPUBLISHED_EDITS"></div>',
                            '<div class="date"><span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.DATE_LAST_EDITED"></span> {{::getFormattedDate(subjectArea.lastEditedDate)}}</div>',
                            '<button class="btn btn-link btn-link-no-initial-underline btn-sm action" ng-click="editSubjectArea()">',
                                '<span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.EDIT" ng-if="!disableEdit"></span> ',
                                '<span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.VIEW" ng-if="disableEdit"></span> ',
                                '<span class="fa fa-chevron-right" role="presentation" aria-hidden="true"></span>',
                                '<span class="sr-only">, {{::subjectAreaTitle}}</span>',
                            '</button>',
                        '</div>',

                        '<div class="unpublished no-changes" ng-if="!subjectArea.dirty">',
                            '<div class="status" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.STATUS_NO_EDITS"></div>',
                            '<div class="date" translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.DATE_NO_EDITS"></div>',
                            '<button class="btn btn-link btn-link-no-initial-underline btn-sm action" ng-click="editSubjectArea()" ng-if="!disableEdit">',
                                '<span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-CARD.EDIT"></span> ',
                                '<span class="fa fa-chevron-right" role="presentation" aria-hidden="true"></span>',
                                '<span class="sr-only">, {{::subjectAreaTitle}}</span>',
                            '</button>',
                        '</div>',

                        // maybe one day, ready and styled
                        // '<div class="text-left">',
                        //     '<a class="btn btn-sm btn-link btn-icon-left btn-link-no-initial-underline btn-default archive" ng-click="archiveSubjectArea()"><i class="fa fa-archive" aria-hidden="true"></i> Archive</a>',
                        // '</div>',

                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
