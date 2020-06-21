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
     * Service to add angular events for when window is focused or blurred
     * http://stackoverflow.com/questions/1060008/is-there-a-way-to-detect-if-a-browser-window-is-not-currently-active
     */

    angular.module('CCC.Components').service('WindowFocusService', [

        '$rootScope',

        function ($rootScope) {

            /*============ SERVICE DECLARATION ============*/

            var WindowFocusService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var initialActiveComplete = false;

            var hidden = "hidden";

            var onchange = function (evt) {

                if (evt.type === 'focusin') {
                    return;
                }

                var v = "focus";
                var h = "blur";

                var evtMap = {
                    focus: v,
                    focusin: v,
                    pageshow: v,
                    blur: h,
                    focusout: h,
                    pagehide: h
                };

                var status;
                var eventType = 'focus';
                evt = evt || window.event;
                if (evt.type in evtMap) {
                    status = evtMap[evt.type];
                } else {
                    eventType = 'visibility';
                    status = this[hidden] ? h : v;
                }

                // let's not count the first active event firing
                if (!initialActiveComplete && status === v) {
                    initialActiveComplete = true;
                    return;
                }

                // we make a distinguishment between when a window gets focus and when it becomes visible from a tab switch
                // focus and blur will fire as expected
                // visible and not visible will fire if the user opens a tab within the same browser window or the user changes desktops on a mac
                if (eventType === 'focus') {
                    $rootScope.$broadcast('WindowFocusService.' + status);
                } else {
                    $rootScope.$broadcast('WindowFocusService.' + (status === v ? 'madeVisible' : 'madeHidden'));
                }
            };

            var wireUpHandlers = function () {

                // everyone gets this
                window.onfocus = window.onblur = onchange;

                if (hidden in document) {
                    document.addEventListener("visibilitychange", onchange);
                } else if ((hidden = "mozHidden") in document) {
                    document.addEventListener("mozvisibilitychange", onchange);
                } else if ((hidden = "webkitHidden") in document) {
                    document.addEventListener("webkitvisibilitychange", onchange);
                } else if ((hidden = "msHidden") in document) {
                    document.addEventListener("msvisibilitychange", onchange);
                } else {
                    window.onpageshow = window.onpagehide = onchange;
                }

                // set the initial state (but only if browser supports the Page Visibility API)
                if (document[hidden] !== undefined) {
                    onchange({type: document[hidden] ? "blur" : "focus"});
                }
            };

            var startBlockingCloseWindow = function (message, callBack) {

                callBack = callBack || $.noop;

                window.onbeforeunload = function (e) {

                    // seems strange, but without this Firefox does not show anything
                    var firefox = /Firefox[\/\s](\d+)/.test(navigator.userAgent);
                    if (firefox) {
                        //Add custom dialog
                        //Firefox does not accept window.showModalDialog(), window.alert(), window.confirm(), and window.prompt() furthermore
                        var dialog = document.createElement("div");
                        document.body.appendChild(dialog);
                        dialog.id = "dialog";
                        dialog.style.visibility = "hidden";
                        dialog.innerHTML = message;
                        var left = document.body.clientWidth / 2 - dialog.clientWidth / 2;
                        dialog.style.left = left + "px";
                        dialog.style.visibility = "visible";
                        var shadow = document.createElement("div");
                        document.body.appendChild(shadow);
                        shadow.id = "shadow";
                        //tip with setTimeout
                        setTimeout(function () {
                            document.body.removeChild(document.getElementById("dialog"));
                            document.body.removeChild(document.getElementById("shadow"));
                        }, 0);
                    }

                    callBack();

                    return message;
                };
            };

            var stopBlockingCloseWindow = function () {
                window.onbeforeunload = $.noop;
            };


            /*============ SERVICE DEFINITION ============*/

            WindowFocusService = {

                /**
                 * Tell the service to go ahead and wire up all handlers
                 */
                initialize: function () {
                    wireUpHandlers();
                },

                /**
                 * You can start blocking the user form leaving the window and fire a call back when they attempt to
                 * @type {string} message a message to display to the user
                 * @type {function} callback a callback method that runs when the user tries to close the window
                 */
                startBlockingCloseWindow: startBlockingCloseWindow,

                /**
                 * Remove the handler to prevent a user from closing the window
                 */
                stopBlockingCloseWindow: stopBlockingCloseWindow
            };


            /*============ LISTENERS ============*/


            /*============ INITIALIZATION ============*/


            /*============ SERVICE PASSBACK ============*/

            return WindowFocusService;
        }
    ]);

})();

