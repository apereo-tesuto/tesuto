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

    /*========== MODULE FOR TYPICAL VIEW SETUP AND DIRECTIVES / SERVICES ============*/

    angular.module('CCC.View.Common', [
        // Needs common dependencies (which includes some run configs and components like the modal)
        'CCC.Assess'
    ]);


    /*======================== LOAD VALUES/CONSTANTS ========================*/


    /*======================== LOAD CONFIGURATIONS ========================*/

    // REGISTER I18N FILES FOR THIS MODULE
    angular.module('CCC.View.Common').config(['TranslateFileServiceProvider', function (TranslateFileServiceProvider) {
        TranslateFileServiceProvider.addTranslateFile({
            prefix: 'ui/scripts/modules/modules_common/ccc_view_standard_common/i18n/locale-',
            suffix: '.json'
        });
    }]);


    /*======================== INITIALIZATION ========================*/

})();
