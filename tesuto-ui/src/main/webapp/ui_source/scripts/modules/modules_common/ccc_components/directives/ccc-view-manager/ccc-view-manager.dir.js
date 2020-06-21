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

    angular.module('CCC.Components').directive('cccViewManager', function () {

        return {

            restrict: 'E',

            scope: {
                viewManager: "=",   // requires an instance of a ViewManagerEntity
                loading: "=?"       // you can force the view manager to put up a spinner while you do some setup before pushing in the first view
            },

            controller: [

                '$scope',
                '$element',
                'CommonLayoutService',

                function ($scope, $element, CommonLayoutService) {


                    /*============ PRIVATE VARIABLES / METHODS ==============*/

                    var setViewManagerHeight = function () {
                        $scope.viewManager.minHeight = CommonLayoutService.getContentHeight();

                        if ($scope.viewManager.minHeight) {
                            $element.css('min-height', $scope.viewManager.minHeight + 'px');
                            $element.find('.ccc-view-manager-views').css('min-height', $scope.viewManager.minHeight + 'px');
                        } else {
                            $element.css('min-height', 0);
                            $element.find('.ccc-view-manager-views').css('min-height', 0);
                        }
                    };


                    /*============ MODEL ==============*/


                    /*============ BEHAVIOR ==============*/

                    // which view should be focused?
                    $scope.viewClass = function (view) {
                        return {
                            'ccc-view-focused': view.focused,
                            'ccc-view-not-focused': !view.focused,
                            'ccc-view-skipped': view.skipped
                        };
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('CommonLayoutService.resize', setViewManagerHeight);

                    // this event intended to be parent to child
                    $scope.$on('ViewManagerWatchService.requestBack', function (event, viewManagerId) {

                        if (viewManagerId === $scope.viewManager.viewManagerId) {
                            // if this view specifies a back target we go there
                            if ($scope.viewManager.getCurrentView().backTarget) {

                                $scope.viewManager.goToView($scope.viewManager.getCurrentView().backTarget, true);

                            // otherwise, going back just means back one view
                            } else {
                                $scope.viewManager.popView();
                            }
                        }
                    });


                    /*============ INITIALIZATION ==============*/

                    setViewManagerHeight();
                }
            ],

            template: [
                '<div class="ccc-view-manager-views">',
                    '<div class="ccc-view-manager-views-loader" ng-show="loading">',
                        '<i class="fa fa-spin fa-spinner" aria-hidden="true"></i>',
                    '</div>',
                    '<ccc-view-manager-view class="ccc-view-manager-view-animate" ng-class="viewClass(view)" ng-repeat="view in viewManager.views track by view.id" view="view"></ccc-view-manager-view>',
                '</div>'
            ].join('')

        };

    });


    angular.module('CCC.Components').directive('cccViewManagerView', function () {

        return {

            restrict: 'E',

            scope: {
                view: "="
            },

            controller: [

                '$rootScope',
                '$scope',
                '$element',
                '$compile',
                '$timeout',
                'CommonLayoutService',
                'VIEW_MANAGER_TRANSITION_ANIMATION_TIME',

                function ($rootScope, $scope, $element, $compile, $timeout, CommonLayoutService, VIEW_MANAGER_TRANSITION_ANIMATION_TIME) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var footerTarget = $element.find('.ccc-view-manager-footer-target');

                    var setContentHeight = function () {
                        var contentHeight = CommonLayoutService.getContentHeight();
                        $element.height(contentHeight);
                    };

                    var setViewHeight = function () { // enables footer to be positioned at viewport bottom
                        var contentHeight = CommonLayoutService.getContentHeight();
                        // TODO: find a way to avoid footer flicker and keep footer at the bottom of pages where the height of the content allows it to do so
                        //var footerHeight = footerTarget.outerHeight(true);
                        //var viewHeight = contentHeight - footerHeight;
                        $('.ccc-view-manager-view-target').css('min-height', contentHeight);
                    };


                    /*============ MODEL ==============*/

                    // attach the $element so the view manager can look at it's size
                    $scope.view.$element = $element;


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    var focusTimeout;

                    $scope.$watch('view.focused', function (isFocused, oldIsFocused) {

                        // skip the first watch firing
                        if (isFocused !== oldIsFocused) {

                            if (isFocused) {

                                $timeout.cancel(focusTimeout);

                                // clear all classes associated with being hidden
                                $element.removeClass('ccc-view-manager-view-hidden').removeClass('ccc-view-manager-view-hidden-start');

                                // for small mobile devices, scroll for them (TODO: NOT SURE WE NEED THIS ANYMORE AFTER THE REFACTOR)
                                if ($('body').hasClass('iphone')) {
                                    $('.ccc-full-view-container .ccc-full-view-content-container').animate({ scrollTop: 0 }, 10);
                                }

                                // wait until the view has shifted (this is the animation plus the delay plus a few more ms)
                                // fixes firefox issue where the screen shifts up and hides portions of the header (FF, MacOSX)
                                $timeout(function () {

                                    var focusedElements = $element.find('[ccc-autofocus]');

                                    // this could happen if upon return to a view an item was deleted
                                    if (focusedElements.length === 0) {

                                        // try to find an element that has at least ccc-focusable
                                        var focusableElements = $element.find('[ccc-focusable]');

                                        if (focusableElements.length) {
                                            $(focusableElements[0]).focus();
                                        }

                                    } else {
                                        $(focusedElements[0]).focus();
                                    }

                                }, VIEW_MANAGER_TRANSITION_ANIMATION_TIME + 10);

                            } else {

                                $element.addClass('ccc-view-manager-view-hidden-start');

                                focusTimeout = $timeout(function () {
                                    $element.addClass('ccc-view-manager-view-hidden');
                                }, VIEW_MANAGER_TRANSITION_ANIMATION_TIME);
                            }
                        }
                    });

                    $scope.$on('CommonLayoutService.resize', function () {
                        setContentHeight();
                        setViewHeight();
                    });



                    /*============ LISTENERS ============*/

                    // we listen to the view scope for focusable elements that are clicked
                    // this way we can set the ccc-autofocus attribute appropriatly
                    // so when we push on a view and pop off a view we can return to the last
                    // item clicked that is focusable
                    // this puts context back in previous views for accessibility and avoids scrolling to some default element
                    // which maintains scroll position
                    // NOTE: We can't listen to view manager scope because they are not in the proper position to capture events up the event tree
                    $scope.view.scope.$on('ccc-focusable.clicked', function (event, focusableElement) {
                        event.stopPropagation();

                        viewElement.find('[ccc-autofocus]').removeAttr('ccc-autofocus');
                        $(focusableElement).attr('ccc-autofocus', true);
                    });


                    /*============ INITIALIZATION ==============*/

                    // compile and inject the view
                    var viewElement = $($scope.view.template);
                    viewElement.appendTo($element.find('.ccc-view-manager-view-target'));
                    $compile(viewElement)($scope.view.scope);

                    // we only want one instance of the footer, so we need to be sure we're not nested
                    if ($scope.view.footerDirective && !$scope.view.isNested) {
                        // compile and inject the footer
                        var footerElement = $('<' + $scope.view.footerDirective + '></' + $scope.view.footerDirective + '>');
                        footerElement.appendTo(footerTarget);
                        $compile(footerElement)($scope);
                    }

                    if ($scope.view.isNested) {
                        $element.addClass('ccc-view-manager-view-nested');
                    }

                    $timeout(function () {
                        setContentHeight();
                        setViewHeight();
                    });

                    setViewHeight();
                }
            ],

            template: [
                '<div ng-class="{container: !view.isNested}">',
                    '<div ng-class="{row: !view.isNested}">',
                        '<div ng-class="{\'col-xs-12\': !view.isNested}">',

                            '<div class="ccc-view-manager-view-target" ng-class="{\'ccc-view-manager-view-target-nested\': view.isNested}"></div>',

                        '</div>',
                    '</div>',
                '</div>',
                '<div class="ccc-view-manager-footer-target"></div>'
            ].join('')

        };
    });


    angular.module('CCC.Components').directive('cccViewManagerBreadcrumbs', function () {

        return {

            restrict: 'E',

            scope: {
                viewManager: "="
            },

            controller: [

                '$scope',

                function ($scope, $element, $compile) {

                    /*============ MODEL ==============*/

                    /*============ BEHAVIOR ==============*/

                    $scope.goToView = function (viewId) {
                        if (!$scope.viewManager.isNavigationDisabled) {
                            $scope.viewManager.goToView(viewId);
                        }
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [
                '<ul role="navigation">',
                    '<li ng-repeat="breadcrumb in viewManager.breadcrumbs track by breadcrumb.id" id="breadcrumb-{{breadcrumb.id}}">',
                        '<a href="#" translate="{{breadcrumb.value}}" ng-disabled="viewManager.isNavigationDisabled" ng-click="goToView(breadcrumb.id)"></a>',
                        '<span class="ccc-view-manager-breadcrumb-separator"><i class="fa fa-angle-right" aria-hidden="true"></i></span>',
                    '</li>',
                '</ul>'
            ].join('')

        };
    });

})();
