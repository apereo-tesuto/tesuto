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

    angular.module('CCC.View.Home').directive('cccManageSubjectAreas', function () {

        return {

            restrict: 'E',

            scope: {
                college: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'ModalService',
                'CurrentUserService',

                function ($scope, $element, $timeout, ModalService, CurrentUserService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.loading = false;

                    $scope.disableEdit = !CurrentUserService.hasPermission('UPDATE_DISCIPLINE');
                    $scope.canCreateSubjectArea = CurrentUserService.hasPermission('CREATE_DISCIPLINE');


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/

                    $scope.addSubjectArea = function () {
                        $scope.$emit('ccc-manage-subject-areas.addSubjectArea');
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-subject-areas-list.viewPublished', function (e, subjectArea) {
                        $scope.$emit('ccc-manage-subject-areas.viewPublished', subjectArea);
                    });

                    $scope.$on('ccc-subject-areas-list.editClicked', function (e, subjectArea) {
                        $scope.$emit('ccc-manage-subject-areas.editClicked', subjectArea);
                    });

                    // Future feature
                    // $scope.$on('ccc-subject-areas-list.archiveClicked', function (e, subjectArea) {

                    //     var buttonConfigs = {
                    //         cancel: {
                    //             title: 'Cancel',
                    //             btnClass: 'btn-primary'
                    //         },
                    //         okay: {
                    //             title: 'Delete',
                    //             btnClass: 'btn-default',
                    //             btnIcon: 'fa-times-circle'
                    //         }
                    //     };

                    //     var deleteSubjectAreaModal = ModalService.openConfirmModal({
                    //         title: 'CCC_VIEW_HOME.WORKFLOW.MANAGE_PLACEMENTS.DELETE_SUBJECT_AREA.TITLE',
                    //         message: 'CCC_VIEW_HOME.WORKFLOW.MANAGE_PLACEMENTS.DELETE_SUBJECT_AREA.MESSAGE',
                    //         buttonConfigs: buttonConfigs,
                    //         type: 'warning'
                    //     });

                    //     // TODO: wire up to new archive endpoint
                    //     deleteSubjectAreaModal.result.then(function () {

                    //         $scope.loading = true;

                    //         subjectArea.delete().then(function () {

                    //             $scope.$emit('ccc-manage-subject-areas.subjectAreaArchived', subjectArea);

                    //             // add button is currently disabled until this promises resolves so wait a digest
                    //             $timeout(function () {
                    //                 $element.find('.ccc-manage-subject-areas-button-add').focus();
                    //             }, 1);

                    //         }).finally(function () {
                    //             $scope.loading = false;
                    //         });
                    //     });
                    // });


                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<h2 tabindex="-1" ccc-autofocus class="no-outline" id="manage-subject-areas-header"><strong>{{college.name}}</strong> <span class="sr-only">subject areas</span></h2>',
                    '</div>',
                '</div>',

                '<div class="well" ng-if="canCreateSubjectArea">',
                    '<button ng-click="addSubjectArea()" ng-disabled="loading" class="btn btn-primary ccc-manage-subject-areas-button-add" ccc-focusable>',
                        '<i class="fa fa-plus" aria-hidden="true"></i> ',
                        '<span translate="CCC_VIEW_HOME.CCC-MANAGE-SUBJECT-AREAS.BUTTON_ADD_SUBJECT_AREA"></span>',
                    '</button>',
                '</div>',

                '<div class="margin-bottom">',
                    '<ccc-subject-areas-list college-ccc-id="college.cccId" described-by="manage-subject-areas-header" is-disabled="loading" disable-edit="disableEdit"></ccc-subject-areas-list>',
                '</div>'

            ].join('')

        };

    });

})();
