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
     * A base entity that adds observable methods for events
     */

    angular.module('CCC.Components').factory('ObservableEntity', [

        function () {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            // default properties
            var defaults = {};


            /*============ CLASS DECLARATION ============*/

            var ObservableEntity = function (entityData) {

                // we simply tack on merged properties during initialization
                var that = this;
                $.extend(true, that, defaults, entityData, {});


                /*============ PRIVATE VARIABLES AND METHODS ============*/

                var listeners = {};


                /*============ PUBLIC METHODS ============*/

                that.addListener = function (eventName, callBack) {
                    if (!listeners[eventName]) {
                        listeners[eventName] = [];
                    }
                    listeners[eventName].push(callBack);
                };

                that.removeListener = function (eventName, callBack) {
                    listeners[eventName] = _.without(listeners[eventName], callBack);
                };

                that.fireEvent = function (eventName, data_args) {
                    if (listeners[eventName]) {
                        for (var i=0; i < listeners[eventName].length; i++) {
                            listeners[eventName][i].apply(that, data_args);
                        }
                    }
                };
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ ENTITY PASSBACK ============*/

            return ObservableEntity;
        }
    ]);

})();
