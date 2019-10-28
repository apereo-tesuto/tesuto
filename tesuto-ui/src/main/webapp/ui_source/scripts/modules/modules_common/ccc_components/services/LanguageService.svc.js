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
     * Wrapper for setting the language via $translate
     */

    angular.module('CCC.Components').service('LanguageService', ['$translate', function ($translate) {

        /*============ SERVICE DECLARATION ============*/

        var LanguageService;


        /*============ PRIVATE METHODS AND VARIABLES ============*/


        /*============ SERVICE DEFINITION ============*/

        LanguageService = {

            setLanguage: function (languageKey) {
                if (languageKey === 'none') {
                    $translate.preferredLanguage('none');
                    $translate.fallbackLanguage(['none']);
                } else {
                    $translate.preferredLanguage('en');
                    $translate.fallbackLanguage(['en']);
                }
                $translate.use(languageKey);
            }
        };

        /*============ LISTENERS ============*/

        /*============ SERVICE PASSBACK ============*/

        return LanguageService;

    }]);

})();

