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
     * Handles hiding and showing of the foreground tray
     */

    angular.module('CCC.Components').service('ForegroundTrayService', ['$rootScope', '$timeout', function ($rootScope, $timeout) {

        /*============ SERVICE DECLARATION ============*/

        var ForegroundTrayService;


        /*============ PRIVATE METHODS AND VARIABLES ============*/

        var initialized = false;

        var opening = false;
        var closing = false;

        var options = {
            leftEdgeElementSelector: null,
            rightEdgeElementSelector: null
        };

        var FOREGROUND_ANIMATION_TIME = 450; // this should match the animation time in the ccc-foreground-tray.less files duration + delay

        var MIN_FOREGROUND_WIDTH = 100;
        var EDGE_PADDING = 30;
        var CONTENT_PADDING = 30;

        var $foregroundTrayContainer = {};

        var foregroundTrayId = 0;
        var getNewForegroundTrayId = function () {
            foregroundTrayId++;
            return 'foreground-' + foregroundTrayId;
        };

        var updateForegroundTrayLayout = function () {

            var bodyWidth = $('body').width();

            var foregroundTrayWidth = ForegroundTrayService.content.width;

            var leftEdgeElement = $(options.leftEdgeElementSelector);

            // it may not be rendered at the moment
            if (leftEdgeElement.length === 0) {
                return;
            }

            var leftOffset = Math.max(Math.min(leftEdgeElement.offset().left + leftEdgeElement.outerWidth(true) + EDGE_PADDING, bodyWidth - foregroundTrayWidth - CONTENT_PADDING), 0);

            // lastly make sure we are within some width guidelines... not too big, not too small
            foregroundTrayWidth = Math.min(Math.max(MIN_FOREGROUND_WIDTH, foregroundTrayWidth), bodyWidth);

            if (ForegroundTrayService.isOpen) {

                // set the proper width for the foreground content
                $foregroundTrayContainer.addClass('showing');
                $foregroundTrayContainer.find('.ccc-foreground-tray-content-container').width(foregroundTrayWidth);

                // slide foreground tray in
                $foregroundTrayContainer.css({
                    'left': leftOffset + 'px',
                    '-webkit-transform': 'translate(0px, 0px)',
                    '-moz-transform': 'translate(0px, 0px)',
                    'transform': 'translate(0px, 0px)'
                });

            } else {

                // slide foreground tray out
                $foregroundTrayContainer.css({
                    'left': '100%',
                    '-webkit-transform': 'translate(' + (bodyWidth - leftOffset) + 'px, 0px)',
                    '-moz-transform': 'translate(' + (bodyWidth - leftOffset) + 'px, 0px)',
                    'transform': 'translate(' + (bodyWidth - leftOffset) + 'px, 0px)'
                });

                $foregroundTrayContainer.removeClass('showing');
            }
        };

        var setInitialPosition = function () {
            $timeout(updateForegroundTrayLayout, 1000);
        };

        // we want to honor the last open statement
        var openDelayedTimeout;
        var delayOpen = function (content_in) {

            $timeout.cancel(openDelayedTimeout);
            openDelayedTimeout = $timeout(function () {
                ForegroundTrayService.open(content_in);
            }, 200);
        };

        // we want to honor the last close statement
        var closeDelayedTimeout;
        var delayClose = function (callBack) {

            $timeout.cancel(closeDelayedTimeout);
            closeDelayedTimeout = $timeout(function () {
                ForegroundTrayService.close(callBack);
            }, 200);
        };


        /*============ SERVICE DEFINITION ============*/

        ForegroundTrayService = {

            isOpen: false,
            content: false,

            // this should be called in your main modules "run" configuration
            // the class 'ccc-foreground and the ccc-foreground directive' should already be present on the page
            initialize: function (options_in) {

                initialized = true;

                // set some options and set some dom references
                $.extend(options, options_in);
                $foregroundTrayContainer = $('ccc-foreground-tray');

                setInitialPosition();
            },

            isOpening: function () {
                return opening;
            },

            isClosing: function () {
                return closing;
            },

            open: function (content_in) {

                if (opening || closing) {
                    delayOpen(content_in);
                }

                // if it is open right now, then we need to close and re-open
                if (ForegroundTrayService.isOpen) {
                    ForegroundTrayService.close(function () {
                        ForegroundTrayService.open(content_in);
                    });
                    return;
                }

                opening = true;

                if (!initialized) {
                    throw new Error('ForegroundTrayService was not initialized');
                }

                if (!content_in.id) {
                    content_in.id = getNewForegroundTrayId();
                }

                var doUpdateForegroundTray = function () {
                    ForegroundTrayService.content = content_in;
                    ForegroundTrayService.isOpen = true;
                    updateForegroundTrayLayout();
                };

                // if the foreground content is already set, we need to animate it out
                if (ForegroundTrayService.content) {

                    ForegroundTrayService.close(doUpdateForegroundTray);

                } else {

                    doUpdateForegroundTray();

                    // if a callback is provided, call it and pass the root tray element in case the caller wants to set focus or find an element in the rendered foreground tray
                    $timeout(function () {

                        opening = false;

                        if (ForegroundTrayService.content.onOpenComplete) {
                            ForegroundTrayService.content.onOpenComplete($foregroundTrayContainer);
                        }
                    }, FOREGROUND_ANIMATION_TIME + 10);
                }
            },

            getForegroundTrayId: function () {
                if (ForegroundTrayService.content) {
                    return ForegroundTrayService.content.id;
                } else {
                    return false;
                }
            },

            close: function (callBack) {

                if (opening || closing) {
                    delayClose(callBack);
                    return;
                }

                closing = true;

                callBack = callBack || $.noop;

                ForegroundTrayService.isOpen = false;
                updateForegroundTrayLayout();

                var contentCloseCallBack = ForegroundTrayService.content.onClose || $.noop;

                $timeout(function () {

                    closing = false;

                    ForegroundTrayService.content = false;
                    callBack();

                }, FOREGROUND_ANIMATION_TIME + 10);

                // we allow the normal workflow to take palce when content gets killed
                // before we call the content's callback (primarly to allow focus handlers to be removed)
                $timeout(function () {
                    contentCloseCallBack();
                }, 1);

                ForegroundTrayService.itemDragEnd();
            },

            // these two methods allow an item being dragged OUT of the tray to do so
            // by manipulating overflow values that need to change due to the need to have a scrollable tray
            itemDragStart: function () {
                $foregroundTrayContainer.addClass('ccc-foreground-tray-item-being-dragged');
            },
            itemDragEnd: function () {
                $foregroundTrayContainer.removeClass('ccc-foreground-tray-item-being-dragged');
            }
        };

        /*============ LISTENERS ============*/

        $rootScope.$on('ccc-assess.resize', updateForegroundTrayLayout);


        /*============ SERVICE PASSBACK ============*/

        return ForegroundTrayService;

    }]);

})();
