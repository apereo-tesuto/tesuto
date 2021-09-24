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

    angular.module('CCC.View.AssessmentPreview', [
        'CCC.AsmtPlayer',
        'CCC.Calculator',
        'CCC.API.AssessmentSessionsPreview'
    ]);

    /*========== CONFIGURATION PHASE ============*/

    // INTERNATIONALIZATION CONFIGS
    // Each view will need to load it's own i18n files
    // 1) Global for all of CCC Assess
    // 2) Global for all of CCC Assess components
    // 3) Custom for the view specific phrases or standard view phrases
    angular.module('CCC.View.AssessmentPreview').config([

        'TranslateFileServiceProvider',
        '$translateProvider',

        function (TranslateFileServiceProvider, $translateProvider) {

            // push in files needed within this module
            TranslateFileServiceProvider.addTranslateFile({
                prefix: 'ui/scripts/modules/modules_view/ccc_view_assessment_preview/i18n/locale-',
                suffix: '.json'
            });

            // now fetch all registered i18n files from all dependent modules
            $translateProvider.useStaticFilesLoader({
                files: TranslateFileServiceProvider.getTranslateFiles()
            });

            // lastly configure language preferences
            $translateProvider.preferredLanguage('en');
            $translateProvider.fallbackLanguage('en');
        }
    ]);

})();
