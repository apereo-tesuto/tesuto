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

    angular.module('CCC.View.AssessmentPlayer').config([

        '$stateProvider',
        '$urlRouterProvider',

        function ($stateProvider, $urlRouterProvider) {

            // handle logout passthrough to the server
            $stateProvider.state('logout', {
                url: "/logout",
                resolve: {
                    redirect: ['$window', '$timeout', function ($window, $timeout) {
                        // this is an actual backend endpoint so lets go there
                        $timeout(function () { // wrap it in a timeout because of a safari bug
                            $window.location.reload(true);
                        }, 1);
                    }]
                }
            });

            // TODO: Get backend to go to /assessment and not upload/assessment this will avoid the need
            // for the next route definition
            $stateProvider.state('assessment', {
                url: '/assessment',
                template: '<ccc-view-assessment-player></ccc-view-assessment-player>',
                pageTitle: 'CCC_VIEW_ASSESSMENT_PLAYER.PAGE_TITLE',
                params: { assessmentSessionId: '' }
            });

            $stateProvider.state('assessmentUpload', {
                url: '/assessmentUpload',
                template: '<ccc-view-assessment-player></ccc-view-assessment-player>',
                pageTitle: 'CCC_VIEW_ASSESSMENT_PLAYER.PAGE_TITLE',
                params: { assessmentSessionId: '' }
            });

            // here we are really just defining a route to extract the assessmentSessionId from the $state
            $stateProvider.state('assessmentComplete', {
                url: '/assessment-complete',
                template: '<ccc-view-assessment-complete></ccc-view-assessment-complete>',
                pageTitle: 'CCC_VIEW_ASSESSMENT_PLAYER.COMPLETE.PAGE_TITLE',
                params: { assessmentTitle: '', assessmentSessionId: '' }
            });
        }
    ]);

})();
