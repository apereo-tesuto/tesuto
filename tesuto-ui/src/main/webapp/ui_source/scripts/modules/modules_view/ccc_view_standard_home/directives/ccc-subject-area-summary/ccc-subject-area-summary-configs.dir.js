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

    angular.module('CCC.View.Home').directive('cccSubjectAreaSummaryConfigs', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '='
            },

            controller: [

                '$scope',
                'CurrentUserService',
                'SubjectAreaWizardService',
                'WizardClass',
                'RulesCollegesService',

                function ($scope, CurrentUserService, SubjectAreaWizardService, WizardClass, RulesCollegesService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.loading = false;
                    $scope.subjectAreaCollege = false;

                    $scope.wizardInstance = new WizardClass({
                        immutableData: {
                            mode: 'edit'
                        },
                        model: $scope.subjectArea,
                        steps: SubjectAreaWizardService.getSteps('edit')
                    });

                    $scope.mmapEquivalents = [];

                    $scope.sequencesWithCourses = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getMapper = function (options) {

                        return function (value) {

                            var selectedOption = _.find(options, function (option) {
                                return option.id === value;
                            });

                            if (selectedOption) {
                                return selectedOption.title;
                            } else {
                                return value;
                            }
                        };
                    };

                    // some value mappers required data form the backend for the label/description, load that data here
                    var loadSummaryValueMappers = function (wizardInstance) {

                        var collegeMisCode = $scope.subjectArea.collegeId;
                        var competency = $scope.subjectArea.competencyAttributes.competencyCode;

                        RulesCollegesService.getRulesByCategory(collegeMisCode, competency, 'mmComponentPlacementLogic').then(function (results) {
                            wizardInstance.setValueMapper('mmDecisionLogic', 'competencyAttributes.mmDecisionLogic', getMapper(results));
                        });

                        RulesCollegesService.getRulesByCategory(collegeMisCode, competency, 'mmPlacementLogic').then(function (results) {
                            wizardInstance.setValueMapper('mmComponentsLogic', 'competencyAttributes.mmPlacementLogic', getMapper(results));
                        });

                        RulesCollegesService.getRulesByCategory(collegeMisCode, competency, 'mmAssignedPlacementLogic').then(function (results) {
                            wizardInstance.setValueMapper('mmComponentsLogic', 'competencyAttributes.mmAssignedPlacementLogic', getMapper(results));
                        });
                    };

                    var initialize = function () {
                        loadSummaryValueMappers($scope.wizardInstance);
                    };


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-subject-area-summary-configs.update', function (e, subjectArea) {

                        $scope.subjectArea = subjectArea;

                        // need a new wizard instance
                        $scope.wizardInstance = new WizardClass({
                            immutableData: {
                                mode: 'edit'
                            },
                            model: $scope.subjectArea,
                            steps: SubjectAreaWizardService.getSteps('edit')
                        });

                        // and attach new value mappers
                        loadSummaryValueMappers($scope.wizardInstance);
                    });


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [

                '<div class="ccc-subject-area-summary-content">',
                    '<ccc-wizard-summary wizard-instance="wizardInstance" ignore-fields="{title:true}"></ccc-wizard-summary>',
                '</div>'

            ].join('')

        };

    });

})();
