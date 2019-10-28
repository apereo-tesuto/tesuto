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
     * Handles hiding and showing of the background content (not the content itself, just the visibility)
     */

    angular.module('CCC.Components').service('BackgroundContentService', ['$rootScope', '$timeout', function ($rootScope, $timeout) {

        /*============ SERVICE DECLARATION ============*/

        var BackgroundContentService;


        /*============ PRIVATE METHODS AND VARIABLES ============*/

        var initialized = false;

        var BACKGROUND_ANIMATION_TIME = 600;

        var MIN_BACKGROUND_WIDTH = 400;
        var MAX_BACKGROUND_WIDTH = 650;
        var MIN_FOREGROUND_WIDTH = 75;

        var $backgroundContentContainer = {};
        var $foregroundContentContainer = {};
        var $foregroundContentContainerBlocker = {};

        var backgroundContentId = 0;

        var getNewBackgroundContentId = function () {
            backgroundContentId++;
            return 'background-' + backgroundContentId;
        };

        var updateBackgroundContentLayout = function () {

            // determine which side we are sliding in on
            var backgroundContentDirection = -1;
            if (BackgroundContentService.content.position === 'left') {
                backgroundContentDirection = 1;
            }

            // by default we determine the total width based on given percentage
            var backgroundContentWidth = (BackgroundContentService.content.width / 100) * $backgroundContentContainer.width();

            // but, you can specify a fixed px width too by adding px to the width string
            if (typeof BackgroundContentService.content.width === 'string' && BackgroundContentService.content.width.indexOf('px') !== -1) {
                backgroundContentWidth = parseFloat(BackgroundContentService.content.width.replace('px', ''));
            }

            // lastly make sure we are within some width guidelines... not too big, not too small
            backgroundContentWidth = Math.min(Math.max(MIN_BACKGROUND_WIDTH, backgroundContentWidth), MAX_BACKGROUND_WIDTH);

            if ($('body').width() - MIN_FOREGROUND_WIDTH < MIN_BACKGROUND_WIDTH) {
                backgroundContentWidth = $('body').width() - MIN_FOREGROUND_WIDTH;
            }

            if (BackgroundContentService.isOpen) {

                // set the proper width for the background content
                $backgroundContentContainer.addClass('showing');
                $backgroundContentContainer.find('.ccc-background-content').width(backgroundContentWidth);

                // set the proper left or right class
                $backgroundContentContainer.find('.ccc-background-content').removeClass('ccc-background-content-right');
                $backgroundContentContainer.find('.ccc-background-content').removeClass('ccc-background-content-left');

                if (BackgroundContentService.content.position === 'left') {
                    $backgroundContentContainer.find('.ccc-background-content').addClass('ccc-background-content-left');
                } else {
                    $backgroundContentContainer.find('.ccc-background-content').addClass('ccc-background-content-right');
                }

                // slide the foreground out
                $foregroundContentContainer.css({
                    '-webkit-transform': 'translate('+ backgroundContentDirection * backgroundContentWidth + 'px' +', 0px)',
                    '-moz-transform': 'translate('+ backgroundContentDirection * backgroundContentWidth + 'px' +', 0px)',
                    'transform': 'translate('+ backgroundContentDirection * backgroundContentWidth + 'px' +', 0px)'
                });

                // block clicks
                $foregroundContentContainerBlocker.show();

            } else {

                // slide foreground back in
                $foregroundContentContainer.css({
                    '-webkit-transform': 'translate(0px, 0px)',
                    '-moz-transform': 'translate(0px, 0px)',
                    'transform': 'translate(0px, 0px)'
                });

                $backgroundContentContainer.removeClass('showing');

                // stop blocking clicks
                $foregroundContentContainerBlocker.hide();
            }
        };


        /*============ SERVICE DEFINITION ============*/

        BackgroundContentService = {

            isOpen: false,
            content: false,

            // this should be called in your main modules "run" configuration
            // the class 'ccc-foreground and the ccc-background directive' should already be present on the page
            initialize: function () {

                initialized = true;

                $foregroundContentContainer = $('.ccc-foreground');
                $foregroundContentContainerBlocker = $('<div class="ccc-foreground-blocker"></div>');
                $backgroundContentContainer = $('ccc-background');

                // add the blocker container to the
                $foregroundContentContainerBlocker.appendTo($foregroundContentContainer);

                // when the user clicks on the foreground with the background content open, we should close
                $foregroundContentContainerBlocker.click(function () {
                    BackgroundContentService.closeBackgroundContent();
                });
            },

            openBackgroundContent: function (content_in) {

                if (!initialized) {
                    throw new Error('BackgroundContentService was not initialized');
                }

                $rootScope.$broadcast('ccc.dialog.onBeforeOpen');

                if (!content_in.id) {
                    content_in.id = getNewBackgroundContentId();
                }

                var doUpdateBackgroundContent = function () {
                    BackgroundContentService.content = content_in;
                    BackgroundContentService.isOpen = true;
                    updateBackgroundContentLayout();
                };

                // if the background content is already set, we need to animate it out
                if (BackgroundContentService.content) {

                    BackgroundContentService.closeBackgroundContent(doUpdateBackgroundContent);

                } else {
                    doUpdateBackgroundContent();
                }
            },

            getBackgroundContentId: function () {
                if (BackgroundContentService.content) {
                    return BackgroundContentService.content.id;
                } else {
                    return false;
                }
            },

            closeBackgroundContent: function (callBack) {

                callBack = callBack || $.noop;

                BackgroundContentService.isOpen = false;
                updateBackgroundContentLayout();

                var contentCloseCallBack = BackgroundContentService.content.onClose;

                $timeout(function () {
                    BackgroundContentService.content = false;
                    callBack();

                    // we allow the normal workflow to take palce when content gets killed
                    // before we call the content's callback (primarly to allow focus handlers to be removed)
                    $timeout(function () {
                        $rootScope.$broadcast('ccc.dialog.closed');
                        contentCloseCallBack();
                    }, 1);

                }, BACKGROUND_ANIMATION_TIME);
            }
        };

        /*============ LISTENERS ============*/

        $rootScope.$on('ccc-assess.resize', updateBackgroundContentLayout);


        /*============ SERVICE PASSBACK ============*/

        return BackgroundContentService;

    }]);

})();

