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

    // this is a helper function to create easy to use simple handlers with the ability to do additional transforms if required
    var getEventTransform = function (eventString, additionalTransform) {

        // for the assessment player we want to track the assessmentSessionId, userId, currentTaskId, timestamp when applicable
        return function (event) {

            var transformedEvent =  {
                eName: eventString,
                data: {}, // if you want custom data based on events.args you'll need to do it manually by passing in an additionalTransform
                uId: event.userId,
                asId: event.assessmentSessionId,
                ctId: event.currentTaskId,
                t: event.timestamp
            };

            if (!additionalTransform) {
                return transformedEvent;
            } else {

                // the additionalTransform will get passed the original event and the current transformation
                // the additionalTransform should return the final transformedEvent
                return additionalTransform(event, transformedEvent);
            }
        };
    };

    angular.module('CCC.AsmtPlayer').value('ASMT_PLAYER_EVENT_FILTER_TRANSFORM_MAP', {

        /*============ GENERAL APPLICATION EVENTS ============*/

        // application startup
        'ccc-asmt-player.initialized': getEventTransform('app.started', function (event, transformedEvent) {
            transformedEvent.data.w = $(window).width();
            transformedEvent.data.h = $(window).height();
            transformedEvent.data.uA = navigator ? navigator.userAgent : 'unknown';
            return transformedEvent;
        }),

        // application server errors
        'ccc.serverError': getEventTransform('app.server.error', function (event, transformedEvent) {
            transformedEvent.data.type = (event.eventArgs && event.eventArgs.length) ? event.eventArgs[0] : 'unknown';
            return transformedEvent;
        }),

        // application window focus
        'WindowFocusService.focus': getEventTransform('app.window.focus'),
        'WindowFocusService.blur': getEventTransform('app.window.blur'),

        // application window visibility (when a tab switch covers the window)
        'WindowFocusService.madeVisible': getEventTransform('app.window.visible'),
        'WindowFocusService.madeHidden': getEventTransform('app.window.hidden'),

        // application resizing
        'ccc-assess.resizeDebounced': getEventTransform('app.window.resize', function (event, transformedEvent) {
            transformedEvent.data.w = event.eventArgs[0];
            transformedEvent.data.h = event.eventArgs[1];
            return transformedEvent;
        }),


        /*============ ASSESSMENT PLAYER NAVIGATION ============*/

        // player navigation
        'AsmtService.newTask': getEventTransform('player.navigation.taskLoaded'),
        'AsmtService.taskReloaded': getEventTransform('player.navigation.taskReloaded'),
        'ccc-asmt-player.validationFailures': getEventTransform('player.navigation.validationFailed'),
        'AsmtService.noNextTaskSet': getEventTransform('player.navigation.complete'),
        'ccc-asmt-player.requestPause': getEventTransform('player.navigation.paused'),
        'AsmtService.progressNotFoundError': getEventTransform('player.navigation.notFoundError'),


        /*============ ASSESSMENT PLAYER SETTINGS ============*/

        'ccc-asmt-player-student-options.open': getEventTransform('app.settings.opened'),
        'ccc-asmt-player-student-options.baseFontSizeUpdated': getEventTransform('app.settings.baseFontSizeUpdated', function (event, transformedEvent) {
            transformedEvent.data.size = event.eventArgs[0];
            return transformedEvent;
        }),
        'ccc-asmt-player-student-options.mathRenderMethodUpdated': getEventTransform('app.settings.mathRenderMethodUpdate', function (event, transformedEvent) {
            transformedEvent.data.method= event.eventArgs[0];
            return transformedEvent;
        }),


        /*============ ASSESSMENT PLAYER INTERACTIONS ============*/

        'ccc-assessment-item.valueCommited': getEventTransform('player.interaction', function (event, transformedEvent) {
            transformedEvent.data.type = event.eventArgs[0].type;
            transformedEvent.data.resp = event.eventArgs[0].resp;
            transformedEvent.data.isId = event.eventArgs[0].itemSessionId;
            return transformedEvent;
        }),


        /*============ ASSESSMENT PLAYER TOOL ============*/

        'CalculatorToolService.calculatorOpened': getEventTransform('player.tool.calculator.opened'),
        'CalculatorToolService.calculatorClosed': getEventTransform('player.tool.calculator.closed'),
        'ccc-calculator.evaluated': getEventTransform('player.tool.calculator.evaluated', function (event, transformedEvent) {
            transformedEvent.data.a = event.eventArgs[0].stateBeforeEvaluation.a;
            transformedEvent.data.b = event.eventArgs[0].stateBeforeEvaluation.b;
            transformedEvent.data.o = event.eventArgs[0].stateBeforeEvaluation.o;
            transformedEvent.data.val = event.eventArgs[0].value;

            return transformedEvent;
        }),
        'ccc-draggable-calculator.dragged': getEventTransform('player.tool.calculator.dragged', function (event, transformedEvent) {

            var windowHeight = $(window).height();
            var windowWidth = $(window).width();

            if (windowWidth && windowHeight) {
                transformedEvent.data.offset = {leftP: Math.round((100 * event.eventArgs[0].left) / $(window).width()), top: Math.round((100 * event.eventArgs[0].top) / $(window).height())};
            } else {
                transformedEvent.data.offset = {leftP: false, topP: false};
            }

            return transformedEvent;
        })

    });

})();
