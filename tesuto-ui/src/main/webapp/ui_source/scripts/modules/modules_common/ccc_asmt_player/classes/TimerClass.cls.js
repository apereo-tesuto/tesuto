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
     * Timer Class
     * A simple wrapper for marking down increments of time
     */

    angular.module('CCC.AsmtPlayer').factory('TimerClass', [

        function () {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            /*============ CLASS DEFAULT CONFIGS ============*/

            // it is expected that all of these methods are overridden or else we will throw an error
            var defaults = {};


            /*============ CLASS DECLARATION ============*/

            var TimerClass = function (timerConfigs) {

                var that = this;
                $.extend(true, that, defaults, timerConfigs || {});

                var times = [];
                var currentTime = false;

                var started = false;


                /*=============== PUBLIC METHODS =============*/

                that.clear = function () {

                    times = [];
                    started = false;
                };

                that.start = function (clear) {

                    if (clear) {
                        that.clear();
                    }

                    if (started) {
                        return;
                    }

                    started = true;

                    currentTime = {start: new Date().getTime(), end: false};
                    times.push(currentTime);
                };

                that.stop = function () {

                    if (!started) {
                        return;
                    }

                    currentTime.end = new Date().getTime();
                    started = false;
                };

                that.getTimeElapsed = function () {

                    var totalTime = 0;
                    var rightNow = new Date().getTime();

                    _.each(times, function (time) {

                        if (time.end === false) {
                            totalTime = totalTime + (rightNow - time.start);
                        } else {
                            totalTime = totalTime + (time.end - time.start);
                        }
                    });

                    return totalTime;
                };


                /*=============== MORE PUBLIC PROPERTIES =============*/

                /*=============== INITIALIZTION =============*/

            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return TimerClass;
        }
    ]);

})();


