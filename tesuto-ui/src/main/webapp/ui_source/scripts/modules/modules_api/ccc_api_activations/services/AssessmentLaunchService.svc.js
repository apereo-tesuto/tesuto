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
     * Service to track open windows
     */

    angular.module('CCC.API.Activations').service('AssessmentLaunchService', [

        function () {

            /*============ SERVICE DECLARATION ============*/

            var AssessmentLaunchService = {};


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var winRefs = {
                assessment: false
            };


            /*============ SERVICE DEFINITION ============*/

            AssessmentLaunchService.openAssessmentSession = function (assessmentSessionUrl) {

                if (winRefs['assessment'] === false || winRefs['assessment'] && winRefs['assessment'].closed) {

                    var width = window.screen.availWidth;
                    var height;

                    // addresses an issue with assessment player controls being hidden by Windows taskbar
                    var isWindows = navigator.platform.toUpperCase().indexOf('WIN') !== -1;

                    if (isWindows) {
                        height = window.screen.availHeight - 60;
                    } else {
                        height = window.screen.availHeight;
                    }

                    winRefs['assessment'] = window.open(assessmentSessionUrl, 'liveMatches', 'width=' + width + ',height=' + height + ',directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=yes');

                    if (winRefs['assessment'] === null || winRefs['assessment'] === undefined) {
                        winRefs['assessment'] = false;
                    }

                } else {

                    window.winRefs = winRefs;

                    winRefs['assessment'].focus();
                }
            };

            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return AssessmentLaunchService;

        }
    ]);

})();
