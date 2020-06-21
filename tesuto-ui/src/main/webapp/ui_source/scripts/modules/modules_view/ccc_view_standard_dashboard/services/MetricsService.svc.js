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

    angular.module('CCC.View.Dashboard').provider('MetricsService', function () {

        /*=========== SERVICE PROVIDER API ===========*/

        // current user reference and an outline of what we expect to come from the server
        var initialMetrics = {};

        // the provider allows you to set the current user during the config phase
        // we get json data from the JSP and pass that in as the initial assessment data
        this.setInitialMetrics = function (metrics_in) {
            initialMetrics = metrics_in;
        };


        /*============ SERVICE DECLARATION ============*/

        this.$get = [

            '$rootScope',

            function ($rootScope) {

                var MetricsService;


                /*============ PRIVATE VARIABLES AND METHODS ============*/


                /*============ SERVICE API ============*/

                MetricsService = {

                    /*============ PUBLIC PROPERTIES ============*/

                    /*============ PUBLIC METHODS ============*/

                    getInitialMetrics: function () {
                        return JSON.parse(JSON.stringify(initialMetrics));
                    }
                };


                /*=========== LISTENERS ===========*/

                /*============ INITIALIZATION ============*/

                return MetricsService;
            }
        ];
    });

})();
