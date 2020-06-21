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
     * MAIN VIEW DIRECTIVE FOR A "STANDARD" VIEW
     * This initializes any global services, and lays out the main initial markup
     * The <ui-view> element is in this layout, so the view should provide it's own configs for routes
     */

    angular.module('CCC.View.Layout').directive('cccViewCommonLayout', function () {

        return {

            restrict: 'E',

            scope: {
                headerDirective: '@', // pass in a custom header directive
                subnavDirective: '@' // pass in a custom subnav directive
            },

            controller: [

                '$scope',
                '$element',
                '$compile',
                '$timeout',
                'BackgroundContentService',
                'CommonLayoutService',

                function ($scope, $element, $compile, $timeout, BackgroundContentService, CommonLayoutService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    var headerTarget = $element.find('.ccc-view-header-target');
                    var subnavTarget = $element.find('.ccc-view-subnav-target');
                    var uiViewTarget = $element.find('.ccc-view-common-content-inner');

                    // We delay the insertion of UI View to initialize layout before views load
                    var insertUiView = function () {
                        var uiViewElem = $('<ui-view></ui-view>');
                        uiViewElem.appendTo(uiViewTarget);
                        $compile(uiViewElem)($scope);
                    };


                    /*=========== MODEL ===========*/

                    /*=========== LISTENERS ===========*/

                    /*=========== INITIALIZATION ===========*/

                    // load the configured header directive
                    var configuredHeaderElem = $('<' + $scope.headerDirective + '></' + $scope.headerDirective + '>');
                    var headerElem = $('<ccc-view-common-header></ccc-view-common-header>');
                    configuredHeaderElem.appendTo(headerElem);
                    headerElem.appendTo(headerTarget);
                    $compile(headerElem)($scope);

                    // load the configured subnav directive
                    var configuredSubnavElem = $('<' + $scope.subnavDirective + '></' + $scope.subnavDirective + '>');
                    configuredSubnavElem.appendTo(subnavTarget);
                    $compile(configuredSubnavElem)($scope);

                    BackgroundContentService.initialize();

                    $timeout(function () {
                        CommonLayoutService.updateContentHeight();
                        insertUiView();
                    });
                }
            ],

            template: [

                // this is the container for background content that shows when the foreground slides to the left or the right
                '<ccc-background></ccc-background>',

                // the foreground main content
                '<div class="ccc-full-view-container ccc-foreground ccc-fg" ccc-responds-to-dialogs>',

                    '<div class="ccc-full-view-content-container ccc-view-common-content-container">',

                        '<div class="ccc-view-header-target"></div>',

                        '<div class="ccc-view-subnav-target"></div>',

                        '<div class="ccc-view-common-content">',

                            // this is where all notification will show up
                            '<div class="ccc-view-common-content-notifications-container container">',
                                '<div class="ccc-view-common-content-notifications"></div>',
                            '</div>',

                            // this is the directive where the current url content will be injected
                            '<main role="main" class="ccc-view-common-content-inner"></main>',

                        '</div>',

                    '</div>',

                '</div>',

                // a container for overlays that will always be on top of the main content and background
                '<div class="ccc-full-view-overlay-container"></div>'

            ].join('')

        };

    });

})();
