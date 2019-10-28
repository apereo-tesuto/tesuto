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

    angular.module('CCC.View.AssessmentPreview').config([

        '$stateProvider',
        '$urlRouterProvider',

        function ($stateProvider, $urlRouterProvider) {

            // here we are really just defining a route to extract the assessmentSessionId from the $state
            $stateProvider.state('assessment', {
                url: "/preview/assessment/{assessmentSessionId}",
                pageTitle: 'CCC_VIEW_ASSESSMENT_PREVIEW.PAGE_TITLE'
            });
        }
    ]);

})();
