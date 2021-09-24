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
     * Entity that the viewManagerDirective will use
     */

    angular.module('CCC.Components').constant('VIEW_MANAGER_TRANSITION_ANIMATION_TIME', 450);

    angular.module('CCC.Components').factory('ViewManagerEntity', [

        '$rootScope',
        '$timeout',
        'ObservableEntity',
        'VIEW_MANAGER_TRANSITION_ANIMATION_TIME',

        function ($rootScope, $timeout, ObservableEntity, VIEW_MANAGER_TRANSITION_ANIMATION_TIME) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var uid = 0;

            var getNewUid = function () {
                uid++;
                return uid;
            };

            var globalDefaults = {
                footerDirective: ''
            };


            /*============ CLASS DECLARATION ============*/

            var ViewManagerEntity = function (entityData) {

                // default properties
                var defaults = {
                    parentViewManager: false,
                    footerDirective: globalDefaults.footerDirective,
                    backAction: false
                };

                // we simply tack on merged properties during initialization
                var that = this;
                // we extend the observalbe class to add on event system to this class
                ObservableEntity.call(that);
                // tack on merged properties during initialization
                $.extend(true, that, defaults, entityData, {views: [], breadcrumbs: []});


                /*============ PRIVATE VARIABLES AND METHODS ============*/

                var updateMinHeight = function () {
                    // reset the min-height after
                    $timeout(function () {
                        that.minHeight = 0;
                    }, VIEW_MANAGER_TRANSITION_ANIMATION_TIME);
                };

                var updateBreadCrumbs = function () {
                    that.breadcrumbs = [];
                    _.each(that.views, function (view) {
                        that.breadcrumbs.push({
                            value: view.breadcrumb,
                            id: view.id
                        });
                    });
                };

                var setFocusedView = function (view) {

                    _.each(that.views, function (thisView) {
                        thisView.focused = false;
                    });

                    if (view.firstView) {
                        view.focused = true;
                    } else {
                        $timeout(function () {
                            view.focused = true;
                        }, 1);
                    }

                    updateMinHeight();
                    updateBreadCrumbs();

                    that.fireEvent('viewFocused', [view.id]);
                };


                /*============ PUBLIC METHODS ============*/

                // we set a unique id to each instance created
                that.viewManagerId = getNewUid();

                that.pushView = function (view_configs) {

                    // set the scope for this view, either configured or created new
                    view_configs.scope = view_configs.scope ? view_configs.scope : $rootScope.$new();
                    view_configs.showBackButton = view_configs.showBackButton !== false;
                    view_configs.backTarget = view_configs.backTarget || false;
                    view_configs.backAction = view_configs.backAction || false;
                    view_configs.backButton = view_configs.backButton || '';    // this overrides the default back button
                    view_configs.viewManager = that;
                    view_configs.title = view_configs.title || '';
                    view_configs.isNested = view_configs.isNested || false; // nested view will not use the outer styling
                    view_configs.footerDirective = view_configs.footerDirective || that.footerDirective;

                    // min height is important for animations where views can be positioned absolutely on top of each other
                    if (that.views.length > 0) {

                        that.minHeight = $(that.views[that.views.length - 1].$element).height();
                        view_configs.previousViewTitle = that.views[that.views.length - 1].breadcrumb;

                    } else {

                        that.minHeight = 0;
                        view_configs.previousViewTitle = "";

                        // the first view gets special treatment
                        view_configs.firstView = true;
                    }

                    that.views.push(view_configs);
                    setFocusedView(that.views[that.views.length - 1]);

                    $rootScope.$broadcast('ViewManagerEntity.pushView', {
                        viewManagerId: that.viewManagerId,
                        id: view_configs.id,
                        title: view_configs.title,
                        breadcrumb: view_configs.breadcrumb,
                        backButton: view_configs.backButton,
                        backAction: view_configs.backAction
                    });
                };

                that.getCurrentView = function () {
                    return that.views[that.views.length -1];
                };

                that.getPreviousView = function () {
                    if (that.views.length - 2 >= 0) {
                        return that.views[that.views.length - 2];
                    } else {
                        return false;
                    }
                };

                that.popView = function () {
                    var oldView = that.views.pop();
                    var nextView = that.views[that.views.length - 1];

                    // since this view is typically constuctred off a workflow's scope we need to destroy it manually
                    oldView.scope.$destroy();

                    // min height is important for animations where views can be positioned absolutely on top of each other
                    that.minHeight = Math.max($(oldView.$element).height(), $(nextView.$element).height());
                    that.minHeight = 500;

                    setFocusedView(nextView);

                    $rootScope.$broadcast('ViewManagerEntity.popView', {
                        viewManagerId: that.viewManagerId,
                        id: nextView.id,
                        title: nextView.title,
                        breadcrumb: nextView.breadcrumb
                    });
                };

                that.setTitle = function (viewId, viewTitle) {
                    var currentView = _.find(that.views, function (thisView) {
                        return thisView.focused === true;
                    });

                    if (currentView && currentView.id === viewId) {

                        currentView.title = viewTitle;

                        $rootScope.$broadcast('ViewManagerEntity.currentViewConfigsChanged', {
                            viewManagerId: that.viewManagerId,
                            id: currentView.id,
                            title: currentView.title,
                            breadcrumb: currentView.breadcrumb,
                            backButton: currentView.backButton
                        });
                    }
                };

                // pass in true for immediate to skip animations if more than one view is being popped
                that.goToView = function (viewId, immediate) {

                    var currentView = that.views[that.views.length - 1];
                    if (currentView.id === viewId) {
                        return;
                    }

                    that.popView();
                    var nextView = that.views[that.views.length - 1];

                    var transitionTime = immediate ? 1 : Math.round(VIEW_MANAGER_TRANSITION_ANIMATION_TIME / 1.5);

                    // if the next view inline isn't it... recursively keep going
                    if (nextView.id !== viewId) {

                        // give the view a flag to help hide it on its way out
                        nextView.skipped = true;

                        $timeout(function () {
                            that.goToView(viewId, immediate);
                        }, transitionTime);
                    }
                };

                that.parentBackButton = function (nonNestedBackButtonLabel) {
                    if (that.parentViewManager) {
                        return that.parentViewManager.getCurrentView().previousViewTitle;
                    } else {
                        return nonNestedBackButtonLabel;
                    }
                };

                that.parentBackAction = function (actionCallBack) {
                    if (that.parentViewManager) {

                        return function () {
                            that.parentViewManager.popView();
                        };

                    } else {
                        return actionCallBack;
                    }
                };

                that.disableNavigation = function () {
                    that.isNavigationDisabled = true;
                    $rootScope.$broadcast('ViewManagerEntity.disableNavigation');
                };

                that.enableNavigation = function () {
                    that.isNavigationDisabled = false;
                    $rootScope.$broadcast('ViewManagerEntity.enableNavigation');
                };

                that.requestViewScrollUp = function (viewId, animationTime) {

                    animationTime = animationTime || 0;

                    var matchingView = _.find(that.views, function (view) {
                        return view.id === viewId;
                    });

                    if (matchingView) {

                        if (animationTime) {
                            $(matchingView.$element).animate({ scrollTop: "0px" }, animationTime);
                        } else {
                            $(matchingView.$element).scrollTop(0);
                        }
                    }
                };

            };


            /*============ PUBLIC STATIC METHODS ============*/

            ViewManagerEntity.setFooterDirective = function (footerDirective) {
                globalDefaults.footerDirective = footerDirective;
            };


            /*============ LISTENERS ============*/


            /*============ ENTITY PASSBACK ============*/

            return ViewManagerEntity;
        }
    ]);

})();
