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

    angular.module('CCC.View.Home').directive('cccSaWizard', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '=',
                mode: '@?'
            },

            controller: [

                '$scope',
                'WizardClass',
                'CurrentUserService',
                'ModalService',
                'SubjectAreaWizardService',

                function ($scope, WizardClass, CurrentUserService, ModalService, SubjectAreaWizardService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var wizardSteps = SubjectAreaWizardService.getSteps($scope.mode);

                    var onBeforeWizardCancel = function (continueCallback) {

                        var leaveModal = ModalService.openConfirmModal({
                            title: 'CCC_VIEW_HOME.CCC-SUBJECT_AREAS-CREATE.CONFIRM_LEAVE.TITLE',
                            message: 'CCC_VIEW_HOME.CCC-SUBJECT_AREAS-CREATE.CONFIRM_LEAVE.MESSAGE'
                        });

                        leaveModal.result.then(function () {
                            continueCallback();
                        });
                    };


                    /*============ MODEL ==============*/

                    $scope.subjectAreaCollege = false;

                    $scope.wizard = new WizardClass({
                        immutableData: {
                            mode: $scope.mode
                        },
                        model: $scope.subjectArea,
                        steps: wizardSteps,
                        onBeforeCancel: onBeforeWizardCancel,
                        autoCleanModel: false
                    });


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var loadCollege = function () {

                        CurrentUserService.getCollegeBycccId($scope.subjectArea.collegeId).then(function (college) {
                            $scope.subjectAreaCollege = college;
                        });
                    };


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-wizard.stepLoaded', function () {
                        $scope.$emit('ccc-sa-wizard.stepChanged');
                    });

                    $scope.$on('ccc-wizard.cancel', function () {
                        $scope.$emit('ccc-sa-wizard.cancel');
                    });

                    $scope.$on('ccc-wizard.complete', function () {

                        $scope.wizard.cleanModel();
                        $scope.wizard.setIsLoading(true);

                        $scope.$emit('ccc-sa-wizard.complete', $scope.subjectArea, $scope.wizard);
                    });


                    /*============ INITIALIZATION ==============*/

                    loadCollege();
                }
            ],

            template: [

                '<h2 class="margin-bottom-double">',
                    '<i class="fa fa-cogs" aria-hidden="true"></i> ',
                    '<span translate="CCC_VIEW_HOME.CCC-SUBJECT_AREAS-CREATE.ADD_SUBJECT_AREA" ng-if="::mode === \'create\'"></span>',
                    '<span translate="CCC_VIEW_HOME.CCC-SUBJECT_AREAS-CREATE.EDIT_SUBJECT_AREA" ng-if="::mode === \'edit\'"></span>',
                '</h2>',

                '<ccc-wizard wizard="wizard"></ccc-wizard>'

            ].join('')

        };

    });

})();
