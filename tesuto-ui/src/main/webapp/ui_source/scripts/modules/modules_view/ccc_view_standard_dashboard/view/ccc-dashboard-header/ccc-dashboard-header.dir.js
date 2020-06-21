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

    angular.module('CCC.View.Dashboard').directive('cccDashboardHeader', function () {

        return {

            restrict: 'E',

            controller: [

                '$scope',
                '$element',
                '$location',

                function ($scope, $element, $location) {

                    /*============ MODEL ============*/

                    /*============ BEHAVIOR ============*/

                    /*============ INITIALIZATION ============*/

                    // let's hide the collapsed responsive menu when they select something
                    // this is important when the menu is collapsed on small devices
                    $($element).find('.nav a').on('click', function () {
                        if ($($element).find('.navbar-toggle').css('display') !== 'none') {
                            $($element).find(".navbar-toggle").trigger("click");
                        }
                    });
                }
            ],

            template: [
                '<ccc-view-common-header-logo></ccc-view-common-header-logo>',

                '<div class="collapse navbar-collapse" id="ccc-home-navigation" role="menu">',

                    '<ul class="nav navbar-nav navbar-right ccc-user-navigation" role="group">',

                        '<li ccc-dropdown-focus role="menuitem">',
                            '<button class="btn btn-default dropdown-toggle app-nav-button" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                                'Admin User ',
                                '<span class="caret"></span>',
                            '</button>',
                            '<ul class="dropdown-menu" role="group">',
                                '<li role="menuitem"><a href="#" id="ccc-header-nav-sign-out" ui-sref="logout"><span class="fa fa-sign-out"></span><span translate="CCC_VIEW_STANDARD_LAYOUT.HEADER.SIGN_OUT"></span></a></li>',
                            '</ul>',
                        '</li>',

                    '</ul>',
                '</div>'
            ].join('')

        };

    });

})();


