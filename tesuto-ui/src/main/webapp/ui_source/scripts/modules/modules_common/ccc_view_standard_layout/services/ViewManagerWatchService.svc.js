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
     * The all seeing eye into previous and active ViewManagers
     */

    angular.module('CCC.Components').service('ViewManagerWatchService', [

        '$rootScope',
        '$translate',

        function ($rootScope, $translate) {

            /*============ SERVICE DECLARATION ============*/

            var ViewManagerWatchService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var allViews = [];
            var isNavigationDisabled = false;

            var pageTitleElement = false;
            var getPageTitleElement = function () {
                if (pageTitleElement) {
                    return pageTitleElement;
                } else {
                    pageTitleElement = $('title');
                    return pageTitleElement;
                }
            };

            var updatePageTitle = function (view_configs) {

                $translate(view_configs.title).then(function (translatedTitle) {
                    getPageTitleElement().text(translatedTitle);
                });

                $rootScope.$broadcast('ViewManagerWatchService.viewChanged', allViews[allViews.length - 1]);
            };

            var viewWasPushed = function (currentViewConfigs) {
                allViews.push(currentViewConfigs);
                updatePageTitle(currentViewConfigs);
            };

            var viewWasPopped = function (currentViewConfigs) {
                var foundViewIndex = false;

                _.find(allViews, function (view, viewIndex) {

                    // it's possible that a view manager can pop off more than one at a time so we need to search through the whole array to find the current matching view
                    if (view.id === currentViewConfigs.id && currentViewConfigs.viewManagerId === view.viewManagerId) {
                        foundViewIndex = viewIndex;
                    }
                });

                if (foundViewIndex !== false) {
                    allViews = allViews.slice(0, foundViewIndex + 1);

                    if (allViews.length) {
                        updatePageTitle(allViews[allViews.length - 1]);
                    }

                } else {
                    throw new Error('ViewManagerWatchService: could not find current view');
                }
            };


            /*============ LISTENERS ============*/

            $rootScope.$on('ViewManagerEntity.pushView', function (event, view_configs) {
                viewWasPushed(view_configs);
            });

            $rootScope.$on('ViewManagerEntity.currentViewConfigsChanged', function (event, view_configs) {
                allViews[allViews.length - 1] = view_configs;
            });

            $rootScope.$on('ViewManagerEntity.popView', function (event, view_configs) {
                viewWasPopped(view_configs);
            });

            $rootScope.$on('ViewManagerEntity.disableNavigation', function (event) {
                isNavigationDisabled = true;
            });

            $rootScope.$on('ViewManagerEntity.enableNavigation', function (event) {
                isNavigationDisabled = false;
            });

            $rootScope.$on('$stateChangeSuccess', function (event, next, current) {
                // we clear breadcrumbs and nestwatchers on state change
                allViews = [];
            });


            /*============ SERVICE DEFINITION ============*/

            ViewManagerWatchService = {

                getCurrentViewTitle: function () {
                    if (allViews.length) {
                        return allViews[allViews.length - 1].title;
                    } else {
                        return '';
                    }
                },

                getPreviousViewTitle: function () {
                    if (allViews.length > 1) {

                        var currentView = allViews[allViews.length - 1];
                        var previousView = allViews[allViews.length - 2];

                        if (currentView.backButton) {

                            return currentView.backButton;

                        } else {

                            // Special case to handle nested views / workflows
                            if (currentView.viewManagerId === previousView.viewManagerId) {
                                // Display a normal breadcrumb
                                return allViews[allViews.length - 2].breadcrumb;
                            } else {
                                // Account for the nested workflow and display the breadcrumbs breadcrumb.
                                return allViews[allViews.length - 3].breadcrumb;
                            }
                        }

                    } else {
                        return false;
                    }
                },

                setPreviousView: function () {
                    if (allViews.length === 1) {
                        throw new Error('ViewManagerWatchService: Only one view left, cannot go previous');
                    } else {

                        var currentView = allViews[allViews.length - 1];
                        var previousView = allViews[allViews.length - 2];

                        // the back button can have a backAction that is fired instead of letting things move through the navigation
                        if (currentView.backAction) {

                            currentView.backAction();

                        } else {

                            // Special case to handle nested views / workflows
                            if (currentView.viewManagerId === previousView.viewManagerId) {
                                $rootScope.$broadcast('ViewManagerWatchService.requestBack', currentView.viewManagerId);
                            } else {
                                $rootScope.$broadcast('ViewManagerWatchService.requestBack', previousView.viewManagerId);
                            }
                        }


                    }
                },

                isNavigationDisabled: function () {
                    return isNavigationDisabled;
                }
            };


            /*============ SERVICE PASSBACK ============*/

            return ViewManagerWatchService;
        }
    ]);

})();
