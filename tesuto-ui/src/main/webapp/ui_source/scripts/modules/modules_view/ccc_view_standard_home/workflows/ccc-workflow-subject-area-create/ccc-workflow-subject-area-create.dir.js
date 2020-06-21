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

    angular.module('CCC.View.Home').directive('cccWorkflowSubjectAreaCreate', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '='
            },

            controller: [

                '$scope',
                'ViewManagerEntity',
                'SubjectAreasAPIService',

                function ($scope, ViewManagerEntity, SubjectAreasAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ VIEW LOADING METHODS ============*/

                    var addSubjectAreaCreateView = function (subjectArea) {
                        var viewScope = $scope.$new();

                        viewScope.subjectArea = subjectArea;

                        viewScope.$on('ccc-sa-wizard.cancel', function () {
                            $scope.$emit('ccc-workflow-subject-area-create.cancel');
                        });

                        viewScope.$on('ccc-sa-wizard.complete', function (e, subjectArea, wizardInstance) {

                            SubjectAreasAPIService.subjectAreas.create(subjectArea.serialize()).then(function () {

                                $scope.$emit('ccc-workflow-subject-area-create.complete', subjectArea);

                            // if an error occurs free up the wizard so they can try again if they choose
                            }, function () {
                                wizardInstance.setIsLoading(false);
                            });
                        });

                        viewScope.$on('ccc-sa-wizard.stepChanged', function () {
                            $scope.viewManager.requestViewScrollUp('manage-placements-subject-area-create-view', 200);
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-placements-subject-area-create-view',
                            title: 'Add Subject Area',
                            breadcrumb: 'Add Suject Area',
                            scope: viewScope,
                            template: '<ccc-sa-wizard subject-area="subjectArea" mode="create"></ccc-sa-wizard>'
                        });
                    };


                    /*============ VIEW LOADER DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addSubjectAreaCreateView($scope.subjectArea);
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager" loading="loading"></ccc-view-manager>'
            ].join('')

        };

    });

})();
