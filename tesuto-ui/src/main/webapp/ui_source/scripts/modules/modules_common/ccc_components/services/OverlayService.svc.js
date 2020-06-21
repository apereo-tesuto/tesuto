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
     * This service will render an absolutely positioned overlay element relative to a specified target
     * It will close if another overlay opens or if focus leaves the overlay
     * It will find it's first scrollable parent and use that to optimize where it renders on the screen
     */

    angular.module('CCC.Components').service('OverlayService', [

        '$rootScope',
        '$timeout',
        '$compile',
        '$q',

        function ($rootScope, $timeout, $compile, $q) {

            /*============ SERVICE DECLARATION ============*/

            var OverlayService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var OVERLAY_PADDING = 10;
            var OVERLAY_ARROW_HEIGHT = 14;
            var PERCENT_WIGGLE_ROOM = 2.0;  // this is the percent of the overlay height that the scrollableParent height must be to allow re-positioning

            var currentOverlay;

            var getScrollableParent = function (node) {

                var scrollableParent = $(node).closest(':hasScroll(y)');

                if (!scrollableParent.length) {
                    return $('body');
                } else {
                    return scrollableParent;
                }
            };

            var attachScrollListener = function (overlay) {

                overlay.scrollableParent = getScrollableParent($(currentOverlay.target)[0]);

                if (overlay.scrollableParent.length) {
                    $(overlay.scrollableParent).scroll(overlay.rePosition);
                }
            };

            var detachScrollListener = function (overlay) {

                if (overlay.scrollableParent.length) {
                    $(overlay.scrollableParent).off('scroll', overlay.rePosition);
                }
            };

            // NOTE: This currently does not take into effect, horizontal scrolling.. we hope to never have to deal with that :-)
            var repositionOverlay = function (overlay) {

                // NOTE: We make all our calculations of offset relative to the scrollable parent, then at the end we adjust the positioning
                // to be relative to the overlay.target because that is where the relatively positioned wrapper is injected and the absolute
                // styling of the overlay will be relative to.
                // So we will find the position of the top left corner of the overlay relative to the scrollable parent (easier),
                // Then at the very end transform these coordinates to be offsets relative to the overlay.target (simply subtract the target offset)

                // Enforce the width and height from the overlay configuations (NOTE: all overlays must have fixed width and height for positioning to be straight forward)
                overlay.overlayElementContent.css('padding', OVERLAY_PADDING);
                overlay.overlayElementContent.width(overlay.width);
                overlay.overlayElementContent.height(overlay.height);

                // calculated some needed values to make additional position calculations
                var scrollableParentWidth = $(overlay.scrollableParent).outerWidth();
                var scrollableParentHeight = $(overlay.scrollableParent).outerHeight();
                var scrollableParentScroll = $(overlay.scrollableParent).scrollTop();

                // first get the offset of the overlay.target (ignore how much is scrolled by adding back in scrollableParentScroll)
                var overlayTargetOffset = {
                    top: $(overlay.target).offset().top - $(overlay.scrollableParent).offset().top + scrollableParentScroll,
                    left: $(overlay.target).offset().left - $(overlay.scrollableParent).offset().left
                };

                // determine the default positionTarget offset relative to the scrollable parent (ignore ammount scrolled by adding back in scrollableParentScroll)
                // NOTE: The render target may be the same element as the original target, or could be a child of the target
                var defaultPositionTargetOffset = {
                    top: $(overlay.positionTarget).offset().top - $(overlay.scrollableParent).offset().top + scrollableParentScroll,
                    left: $(overlay.positionTarget).offset().left - $(overlay.scrollableParent).offset().left
                };

                // determine the dimensions of the target
                var positionTargetDims = {width: $(overlay.positionTarget).outerWidth(), height: $(overlay.positionTarget).outerHeight()};

                // calculate remaining top, bottom, left, right display space within the scrollable parent from the default position
                var space = {
                    left: defaultPositionTargetOffset.left,
                    right: scrollableParentWidth - defaultPositionTargetOffset.left - positionTargetDims.width,
                    top: defaultPositionTargetOffset.top - scrollableParentScroll,
                    bottom: scrollableParentHeight - (defaultPositionTargetOffset.top - scrollableParentScroll) - positionTargetDims.height
                };

                var offset = {
                    left: defaultPositionTargetOffset.left - overlayTargetOffset.left + ($(overlay.positionTarget).outerWidth() / 2)
                };

                // We only update the vertical positioning in two cases:
                // 1) We need to and the scrollable parent height is large enough to avoid constant repositioning
                // 2) We need to and the scrollable parent height is NOT large enough and we haven't repositioned yet (allows for one initial repositioning)
                // NOTE: If we hit a window.resize event, we clear the flag to allow for one more reposition if necessary
                if (!overlay.positionChecked || (scrollableParentHeight > PERCENT_WIGGLE_ROOM * overlay.height)){

                    var needsMoreTopSpace =  space.top < overlay.height + (OVERLAY_PADDING * 2) + OVERLAY_ARROW_HEIGHT;
                    var needsMoreBottomSpace = space.bottom < overlay.height + (OVERLAY_PADDING * 2) + OVERLAY_ARROW_HEIGHT;

                    // determine how to update the render offset and vertical position preference based on available space
                    // we only flip the position if it gets us in a better situation or there is no good situation
                    if (overlay.position === 'top' && needsMoreTopSpace && !needsMoreBottomSpace) {
                        overlay.position = 'bottom';
                    } else if (overlay.position === 'bottom' && needsMoreBottomSpace && !needsMoreTopSpace) {
                        overlay.position = 'top';
                    // here we need space in both directions, so prefer bottom positioning so that scrollable area will grow and the picker will be visible
                    } else if (overlay.position === 'top' && needsMoreTopSpace && needsMoreBottomSpace) {
                        overlay.position = 'bottom';
                    }

                    overlay.positionChecked = true;
                }

                // calculate any horizontal offset due to lack of horizontal space available
                var horizontalOffset = 0;
                if ((overlay.width / 2 + OVERLAY_PADDING) > space.right) {
                    horizontalOffset = space.right - (overlay.width / 2 + OVERLAY_PADDING);
                }
                if ((overlay.width / 2 + OVERLAY_PADDING) > space.left) {
                    horizontalOffset = -1 * (space.left - (overlay.width / 2 + OVERLAY_PADDING));
                }

                // set the proper class, this helps styling of the arrow
                $(overlay.overlayElement).removeClass('ccc-overlay-position-top').removeClass('ccc-overlay-position-bottom');
                $(overlay.overlayElement).addClass('ccc-overlay-position-' + overlay.position);

                // finally calculatet the final top offset
                if (overlay.position === 'top') {
                    offset.top = defaultPositionTargetOffset.top - overlayTargetOffset.top - overlay.height - (OVERLAY_PADDING * 2) - OVERLAY_ARROW_HEIGHT;
                } else {
                    offset.top = defaultPositionTargetOffset.top - overlayTargetOffset.top + $(overlay.positionTarget).outerHeight() + OVERLAY_ARROW_HEIGHT;
                }

                // set the offset styles
                $(overlay.overlayElement).css({top: offset.top, left: offset.left - (overlay.width / 2) - OVERLAY_PADDING + horizontalOffset});

                // set the arrow offset styles
                if (overlay.position === 'bottom') {
                    $(overlay.overlayElement).find('.ccc-overlay-arrow').css({top: - (2 * OVERLAY_ARROW_HEIGHT) + 2, left: (overlay.width / 2) + OVERLAY_PADDING - horizontalOffset});
                } else {
                    $(overlay.overlayElement).find('.ccc-overlay-arrow').css({top: overlay.height + (2 * OVERLAY_PADDING) - 2, left: (overlay.width / 2) + OVERLAY_PADDING - horizontalOffset});
                }
            };

            // a small class to help refine what is needed when creating an overlay
            var Overlay = function (overlayOptions) {

                var that = this;

                var defaults = {
                    scope: null,                    // optional scope
                    width: 200,
                    height: 200,
                    target: null,                   // mandatory target element that will be wrapped with a ccc-overlay-wrapper element
                    positionTarget: null,           // not mandatory, will default to target, this setup gives flexibility in what element is wrapped with our ccc-overlay-wrapper element vs which element is the reference for positioning
                    position: 'bottom',             // other options is 'bottom'
                    template: '',                   // a template that will be compiled against the provided scope
                    positionChecked: false         // a flag to help us avoid repositioning the overlay if the scrollableParent viewport is not very big (overlay will keep shifting with minimal scrolling wich is a bad UX)
                };

                $.extend(that, defaults, overlayOptions);

                // the position target can be used to center the item to a child of the regular target
                // if no positionTarget is provided then the regular target will also act as the render target
                if (!that.positionTarget) {
                    that.positionTarget = that.target;
                }

                that.scope = that.scope || $rootScope.$new();

                // expose the overlay instance to scope
                that.scope.overlay = that;

                that.scope.overlay.close = function () {
                    OverlayService.close();
                };

                that.element = $('<ccc-overlay><span class="ccc-overlay-arrow"></span></div><div class="ccc-overlay-content"></div></ccc-overlay>');
                that.element.find('.ccc-overlay-content').html(that.template);
            };

            var ensureEventTargetWithinCurrentOverlay = function (eventTarget) {

                if (currentOverlay) {

                    var overlayElement = $(currentOverlay.overlayElement)[0];

                    if (!$.contains(overlayElement, eventTarget)) {
                        OverlayService.close();
                    }
                }
            };

            var repositionCurrentOverlay = function (resetPositionChecked) {

                if (currentOverlay) {

                    if (resetPositionChecked) {
                        currentOverlay.positionChecked = false;
                    }
                    currentOverlay.rePosition();
                }
            };


            /*============ SERVICE DEFINITION ============*/

            OverlayService = {

                open: function (overlayOptions) {

                    if (currentOverlay) {
                        currentOverlay.close();
                    }

                    currentOverlay = new Overlay(overlayOptions);

                    // setup a promise system similar to the ModalService
                    currentOverlay.deferred = $q.defer();
                    currentOverlay.result = currentOverlay.deferred.promise.then(function (resolvedValue) {
                        currentOverlay.close(true); // here we trigger the close to get everything cleaned up but pass in true because the promise was already resolved
                        return resolvedValue;
                    });

                    // add a rePosition function to the overlay
                    currentOverlay.rePosition = _.debounce(function () {
                        repositionOverlay(currentOverlay);
                        $rootScope.$apply();
                    }, 50);

                    // begin to actually add the overlay to the dom (we wrap the target so we can position the overlay relative to the wrapper)
                    var wrapper = $('<ccc-overlay-wrapper></ccc-overlay-wrapper>');
                    currentOverlay.target.wrap(wrapper);
                    currentOverlay.target.after(currentOverlay.element);

                    // now we angularize the template and set some convenience references of dom nodes in the overlay
                    $compile(currentOverlay.element)(currentOverlay.scope);
                    currentOverlay.overlayElement = $(currentOverlay.target).closest('ccc-overlay-wrapper').find('ccc-overlay');
                    currentOverlay.overlayElementContent = currentOverlay.overlayElement.find('.ccc-overlay-content');

                    // we can reposition during scrolling to keep the overlay visible
                    attachScrollListener(currentOverlay);

                    // kick us off with some initial positioning
                    repositionOverlay(currentOverlay);

                    // after compile finishes, we can now move browser focus on the rendered template
                    $timeout(function () {

                        if (currentOverlay) {

                            // first look for configured firstFocus selector to focus on
                            if (currentOverlay.firstFocus) {

                                currentOverlay.overlayElement.find(currentOverlay.firstFocus).focus();

                            // next look for our standard ccc-firstfocus
                            } else {
                                currentOverlay.overlayElement.find('[ccc-autofocus]').focus();
                            }
                        }
                    }, 1);

                    return currentOverlay;
                },

                // this is a syncrounous method, so you can call OverlayService.close() and OverlayService.open() synchronously and not get into a bad state
                close: function (promiseAlreadyResolved) {

                    // remove global scroll listener
                    detachScrollListener(currentOverlay);

                    // clean up the angular scope tree
                    currentOverlay.scope.$destroy();

                    // clean up the dom
                    $(currentOverlay.target).unwrap();
                    currentOverlay.overlayElement.remove();

                    // on close we reject, the promise may have already been resolved by the actual template directive by calling $scope.overlay.deferred.resolve(<stuff>);
                    if (!promiseAlreadyResolved) {
                        currentOverlay.deferred.reject();
                    }

                    currentOverlay = null;
                }
            };


            /*============ LISTENERS ============*/

            // if focus changes to any element outside of the overlay we need to do something about it
            document.addEventListener('focusin', function(e) {
                ensureEventTargetWithinCurrentOverlay(e.target);
            });

            $('body').mousedown(function (e) {
                ensureEventTargetWithinCurrentOverlay(e.target);
            });

            $(window).resize(function () {
                repositionCurrentOverlay(true);
            });


            /*============ SERVICE PASSBACK ============*/

            return OverlayService;
        }
    ]);

})();

