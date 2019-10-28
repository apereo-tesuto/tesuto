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


    angular.module('CCC.Components').service('UtilsService', [

        '$timeout',

        function ($timeout) {

            /*============ SERVICE DECLARATION ============*/

            var UtilsService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/



            /*============ SERVICE DEFINITION ============*/

            UtilsService = {

                formatTenDigitPhone: function (phoneToFormat) {

                    return phoneToFormat && phoneToFormat.length === 10 ? phoneToFormat.replace(/(\d\d\d)(\d\d\d)(\d\d\d\d)/, '($1) $2-$3') : (phoneToFormat || '---');
                },

                focusOnFirstInvalidInput: function (containerElement) {

                    // skip a digest to make sure form validation had a chance to run
                    $timeout(function () {

                        // the selectors used target angular invalid as well as aria-invalid because sometimes we create radio boxes / checkboxes that don't have ng-model and will have manual aria-invalid managed
                        $($(containerElement).find('input.ng-invalid, textarea.ng-invalid, select.ng-invalid, input[aria-invalid="true"]')[0]).focus();
                    }, 1);
                }
            };

            /*============ LISTENERS ============*/


            /*============ INITIALIZATION ============*/


            /*============ SERVICE PASSBACK ============*/

            return UtilsService;
        }
    ]);

})();
