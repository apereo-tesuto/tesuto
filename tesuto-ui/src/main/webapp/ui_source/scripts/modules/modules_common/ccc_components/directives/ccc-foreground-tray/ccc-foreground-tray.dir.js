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
     * ccc-foreground directive should be placed as a sibling to an element that has the .ccc-foreground class
     * the ForegroundTrayService should be used to trigger the display of the foreground content
     */

    angular.module('CCC.Components').directive('cccForegroundTray', [

        '$compile',

        function ($compile) {

            return {

                restrict: 'E',

                scope: {},

                controller: [

                    '$scope',
                    '$rootScope',
                    '$element',
                    '$timeout',
                    '$compile',
                    'ForegroundTrayService',

                    function ($scope, $rootScope, $element, $timeout, $compile, ForegroundTrayService) {

                        /*============ PRIVATE VARIABLES AND METHODS ============*/

                        var $childScope = false;


                        /*============ MODEL ============*/

                        $scope.contentTitle = '';


                        /*============ MODEL DEPENDENT METHODS ============*/

                        var removeCurrentForegroundTray = function (callBack) {
                            $childScope.$destroy();
                            $element.find('.ccc-foreground-tray-content-inner').empty();
                        };

                        var updateForegroundTray = function () {

                            // if we already have foreground content loaded, remove it
                            if ($childScope) {
                                removeCurrentForegroundTray();
                            }

                            $scope.content = ForegroundTrayService.content;

                            // if there isn't content then just stop
                            if (!ForegroundTrayService.content) {
                                return;
                            }

                            // you can pass in your own scope if you like, or we will create one for you
                            $childScope = ForegroundTrayService.content.scope || $scope.$new();

                            // set the content title
                            $scope.contentTitle = ForegroundTrayService.content.title || '';

                            var contentElement = angular.element(ForegroundTrayService.content.template);
                            contentElement.attr('ccc-responds-to-dialogs', 'true'); //TODO: does this neeed this directive?

                            $element.find('.ccc-foreground-tray-content-inner').append(contentElement);

                            $compile(contentElement)($childScope);
                        };


                        /*============ BEHAVIOR ============*/

                        $scope.close = function () {
                            ForegroundTrayService.close();
                        };


                        /*============ LISTENERS ============*/

                        // listen to the foreground content service for changes
                        $scope.$watch(function () {
                            return ForegroundTrayService.getForegroundTrayId();
                        }, updateForegroundTray);


                        /*============ INITIALIZATION ===========*/

                    }
                ],

                template: [
                    '<div class="ccc-foreground-tray-content" aria-hidden="{{content ? \'false\' : \'true\'}}">',

                        '<div class="ccc-foreground-tray-content-container">',

                            // TODO: make a final decision on if we should show the close button
                            // '<div class="ccc-foreground-tray-content-close">',
                            //     '<div class="container-fluid">',
                            //         '<div class="row">',
                            //             '<div class="col-xs-12">',
                            //                 '<h3 class="ccc-form-title ccc-bg-make-darker">',
                            //                     '<button ng-click="close()" class="btn btn-default btn-full-width"><span>Close</span> <i class="fa fa-chevron-right"></i></button>',
                            //                 '</h3>',
                            //             '</div>',
                            //         '</div>',
                            //     '</div>',
                            // '</div>',

                            '<div class="ccc-foreground-tray-content-inner"></div>',
                        '</div>',

                    '</div>'
                ].join('')
            };

        }
    ]);

})();
