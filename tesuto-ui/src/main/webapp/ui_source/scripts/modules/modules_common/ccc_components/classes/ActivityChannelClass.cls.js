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

    angular.module('CCC.Components').factory('ActivityChannelClass', [

        '$rootScope',
        '$timeout',
        '$q',

        function ($rootScope, $timeout, $q) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var defaultPostActivity = function () {
                throw new Error('ActivityChannelClass.postActivityMethodNotDefined');
            };


            /*============ CLASS DECLARATION ============*/

            var ActivityChannelClass = function (channelConfigs) {

                /*============ CLASS DEFAULT CONFIGS ============*/

                var defaults = {
                    id: false,
                    required: false,
                    eventFilterMap: {},
                    postActivity: defaultPostActivity
                };

                /*============ MERGE IN DEFAULTS ============*/

                var that = this;
                $.extend(true, that, defaults, channelConfigs || {});


                /*=============== PRIVATE VARIABLES AND METHODS ============*/

                // save the configured postActivity method cause we are going to wrap it
                var configuredPostActivity = that.postActivity;


                /*=============== PUBLIC METHODS =============*/

                that.transformActivity = function (activity) {
                    return that.eventFilterMap[activity.eventKey](activity);
                };

                that.postActivity = function (transformedActivityList) {
                    return configuredPostActivity(transformedActivityList);
                };

                /*=============== INITIALIZATION =============*/
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ STATIC LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return ActivityChannelClass;
        }
    ]);

})();
