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
     * DIRECTIVE FOR A "STANDARD" VIEW SUBNAV
     */

    angular.module('CCC.View.Layout').directive('cccViewCommonSubnav', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$rootScope',
                '$scope',
                'ViewManagerWatchService',

                function ($rootScope, $scope, ViewManagerWatchService) {

                    /*============ PRIVATE VARIABLES / METHODS ===========*/

                    /*============ MODEL ==============*/

                    $scope.getCurrentViewTitle = ViewManagerWatchService.getCurrentViewTitle;
                    $scope.getPreviousViewTitle = ViewManagerWatchService.getPreviousViewTitle;
                    $scope.isNavigationDisabled = ViewManagerWatchService.isNavigationDisabled;
                    $scope.setPreviousView = ViewManagerWatchService.setPreviousView;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<div class="module-header">',
                    '<div class="container">',
                        '<button ng-if="getPreviousViewTitle() && !isNavigationDisabled()" ng-click="setPreviousView()" ng-disabled="isNavigationDisabled()" class="ccc-view-manager-view-back btn btn-default btn-left pane-left"><i class="fa fa-chevron-left" aria-hidden="true"></i><span class="sr-only" translate="CCC_VIEW_STANDARD_LAYOUT.SUB_NAV.BACK_TEXT"></span> <span translate="{{getPreviousViewTitle()}}"></span></button>',
                        '<h1 class="title" translate="{{getCurrentViewTitle()}}"></h1>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
