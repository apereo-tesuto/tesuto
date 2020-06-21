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
// we will wrap moment in this service
var moment = moment || {};

(function () {

    /**
     * Wrapper for Moment.js
     */

    angular.module('CCC.Assess').service('Moment', [

        function () {

            /*============ SERVICE DECLARATION ============*/

            var MomentService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            MomentService = moment;

            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return MomentService;
        }
    ]);

})();

