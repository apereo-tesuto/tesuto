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

    angular.module('CCC.View.Home').directive('cccWorkflowPaperPencil', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$state',
                'ViewManagerEntity',

                function ($scope, $state, ViewManagerEntity) {

                    /*============ PRIVATE VARIABLES =============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({parentViewManager: $scope.parentViewManager});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addPrintView = function () {

                        var viewScope = $scope.$new();

                        $scope.viewManager.pushView({
                            id: 'workflow-paper-pencil-print',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-PAPER-PENCIL.TITLE_PRINT',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-PAPER-PENCIL.TITLE_PRINT',
                            scope: viewScope,
                            template: '<ccc-paper-pencil-print></ccc-paper-pencil-print>'
                        });

                    };

                    var addScoreView = function () {

                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-paper-pencil-scoring.actionClicked', function (e, actionId) {
                            if (actionId === 'studentLookup') {
                                $state.go('studentLookup', {cccId: null});
                            }
                            if (actionId === 'bulkActivation') {
                                $state.go('bulkActivation', {initialDeliveryType: 'PAPER', onCompleteState: 'paperPencil'});
                            }
                            if (actionId === 'createActivation') {
                                $state.go('createActivation');
                            }
                        });

                        $scope.viewManager.pushView({
                            id: 'workflow-paper-pencil-score',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-PAPER-PENCIL.TITLE_SCORE',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORFLOW-PAPER-PENCIL.TITLE_SCORE',
                            scope: viewScope,
                            template: '<ccc-paper-pencil-scoring></ccc-paper-pencil-scoring>'
                        });
                    };

                    var addPaperPencilView = function () {

                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-paper-pencil-landing.actionClicked', function (e, action) {

                            if (action === 'print') {

                                addPrintView();

                            } else if (action === 'score') {

                                addScoreView();
                            }
                        });

                        $scope.viewManager.pushView({
                            id: 'workflow-paper-pencil-landing',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-PAPER-PENCIL.TITLE',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-PAPER-PENCIL.TITLE',
                            scope: viewScope,
                            template: '<ccc-paper-pencil-landing></ccc-paper-pencil-landing>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addPaperPencilView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
