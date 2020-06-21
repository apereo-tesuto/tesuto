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
     * ccc-background directive should be placed as a sibling to an element that has the .ccc-foreground class
     * the BackgroundContentService should be used to trigger the display of the background content
     */

    angular.module('CCC.Components').directive('cccBackground', [

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
                    'FocusService',
                    'BackgroundContentService',

                    function ($scope, $rootScope, $element, $timeout, $compile, FocusService, BackgroundContentService) {

                        /*============ PRIVATE VARIABLES AND METHODS ============*/

                        var $childScope = false;


                        /*============ MODEL ============*/

                        $scope.contentTitle = '';


                        /*============ MODEL DEPENDENT METHODS ============*/

                        var removeCurrentBackgroundContent = function (callBack) {
                            $childScope.$destroy();
                            $element.find('.ccc-background-content-inner').empty();
                        };

                        var updateBackgroundContent = function () {

                            // if we already have background content loaded, remove it
                            if ($childScope) {
                                removeCurrentBackgroundContent();
                            }

                            $scope.content = BackgroundContentService.content;

                            // if there isn't content then just stop
                            if (!BackgroundContentService.content) {

                                $scope.contentTitle = '';

                                // remove the event listern that traps focus on this dialog
                                FocusService.clearFocusTrap();
                                return;
                            }

                            // add the listener to trap focus within the dialog area
                            FocusService.setFocusTrap($element, $element.find('.ccc-form-title button'));

                            // you can pass in your own scope if you like, or we will create one for you
                            $childScope = BackgroundContentService.content.scope || $scope.$new();

                            // set the content title
                            $scope.contentTitle = BackgroundContentService.content.title || '';

                            // focus on the close button (give it a second, because it is hidden by default)
                            $timeout(function () {
                                $element.find('.ccc-form-title button').focus();
                            }, 1);

                            var contentElement = angular.element(BackgroundContentService.content.template);
                            contentElement.attr('ccc-responds-to-dialogs', 'true');

                            $element.find('.ccc-background-content-inner').append(contentElement);

                            $compile(contentElement)($childScope);
                        };


                        /*============ BEHAVIOR ============*/

                        $scope.closeBackground = function () {
                            BackgroundContentService.closeBackgroundContent();
                        };


                        /*============ LISTENERS ============*/

                        // listen to the background content service for changes
                        $scope.$watch(function () {
                            return BackgroundContentService.getBackgroundContentId();
                        }, updateBackgroundContent);


                        /*============ INITIALIZATION ===========*/

                    }
                ],

                // using dialog approach for accessibility
                // http://www.nczonline.net/blog/2013/02/12/making-an-accessible-dialog-box/

                template: [
                    '<div class="ccc-background-content ccc-bg-light ccc-fg" role="dialog">',
                        '<div class="ccc-background-content-close" ng-show="content">',
                            '<div class="container-fluid">',
                                '<div class="row">',
                                    '<div class="col-xs-12">',
                                        '<h3 class="ccc-form-title ccc-bg-make-darker">',
                                            '<span class="ccc-background-content-title"><span translate>{{contentTitle}}</span></span>',
                                            '<button ng-click="closeBackground()" class="btn btn-primary btn-icon-only sr-only-when-small"><i class="fa fa-times-circle"></i><span class="sr-only">Close Dialog Content</span></button>',
                                        '</h3>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',
                        '<div class="ccc-background-content-inner"></div>',
                    '</div>'
                ].join('')
            };

        }
    ]);

})();
