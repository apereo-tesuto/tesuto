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
     * DIRECTIVE FOR A "STANDARD" VIEW HEADER
     */

    angular.module('CCC.View.Layout').directive('cccViewCommonHeader', function () {

        return {

            restrict: 'E',

            transclude: true,

            template: [
                '<header class="app-header" role="banner">',
                    '<div class="app-nav">',

                        '<div class="container">',
                            '<div class="row">',
                                '<div class="col-xs-12">',

                                    '<nav class="navbar navbar-default" role="navigation" id="app-main-nav" ng-transclude>',

                                        // your header will go here

                                    '</nav>',

                                '</div>',
                            '</div>',
                        '</div>',

                    '</div>',
                '</header>'
            ].join('')

        };

    });

})();
