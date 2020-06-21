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

    /**
     * Add this directive to the title attribute
     * Then provide pageTitle property to your stateProvider configurations
     * $stateProvider.state('home', {
     *      url: "/home",
     *      pageTitle: 'CCC_VIEW_HOME.PAGE_TITLES.HOME',
     *      template: '<ccc-route-proctor-location></ccc-route-proctor-location>'
     * });
     */

    angular.module('CCC.Components').directive('cccUpdatePageTitle', function () {

        return {

            restrict: 'A',

            controller: [

                '$element',
                '$rootScope',
                '$timeout',
                '$translate',

                function ($element, $rootScope, $timeout, $translate) {

                    /*============ PRIVATE VARIABLES ============*/

                    var listener = function (event, toState) {

                        var title = 'CCC_COMP.DEFAULT_PAGE_TITLE';
                        if (toState.pageTitle) {
                            title = toState.pageTitle;
                        }

                        $timeout(function () {
                            $translate(title).then(function (translation) {
                                $element.text(translation);
                            });
                        }, 0, false);
                    };


                    /*============ MODEL ============*/


                    /*============ LISTENERS ============*/

                    $rootScope.$on('$stateChangeSuccess', listener);


                    /*============ INITIALIZATION ============*/

                }
            ]
        };

    });

})();
