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

    /*============ ONLY ONE ROUTE RIGHT NOW FOR ASSESSMENT DELIVERY ============*/

    angular.module('CCC.View.AssessmentPrototype').config([

        '$stateProvider',
        '$urlRouterProvider',

        function ($stateProvider, $urlRouterProvider) {

            // here we are really just defining a route to extract the assessmentSessionId from the $state
            $stateProvider.state('assessment', {

                url: "/prototype_assessment?allowSkipping&validateResponses&startingTaskSetIndex",
                pageTitle: 'CCC_VIEW_ASSESSMENT_PLAYER.PAGE_TITLE',

                // for this assessment player we load in the template after everything resolves so we can change configs before things get moving
                template: '<ccc-view-assessment-prototype></ccc-view-assessment-prototype>',

                resolve: {
                    'configs': [

                        '$stateParams',
                        'PrototypeTaskSetService',
                        'FakeData',

                        function ($stateParams, PrototypeTaskSetService, FakeData) {

                            var startingTaskSetIndex = $stateParams.startingTaskSetIndex !== undefined ? parseInt($stateParams.startingTaskSetIndex) : 0;
                            FakeData.setInitialTaskSetIndex(startingTaskSetIndex);

                            PrototypeTaskSetService.useAllowSkippingOverride = $stateParams.allowSkipping !== undefined;
                            PrototypeTaskSetService.allowSkippingOverrideValue = $stateParams.allowSkipping === "true" ? true : false;

                            PrototypeTaskSetService.useValidateResponsesOverride = $stateParams.validateResponses !== undefined;
                            PrototypeTaskSetService.validateResponsesOverrideValue = $stateParams.validateResponses === "true" ? true : false;
                        }
                    ]
                }
            });

            $stateProvider.state('assessmentComplete', {
                url: "/assessment-complete",
                template: '<ccc-view-assessment-complete></ccc-view-assessment-complete>',
                pageTitle: 'CCC_VIEW_ASSESSMENT_PLAYER.COMPLETION.PAGE_TITLE',
                params: { assessmentTitle: '', assessmentSessionId: '' }
            });
        }
    ]);

})();
