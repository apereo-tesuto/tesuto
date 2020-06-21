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

    angular.module('CCC.View.AssessmentPrototype', [
        'CCC.AsmtPlayer',
        'CCC.Calculator',
        'CCC.API.FakeData',
        'CCC.ActivityMonitor'
    ]);

    // override the query param white list to add some flags for testing item session configs
    angular.module('CCC.View.AssessmentPrototype').value('QUERY_PARAM_WHITE_LIST', [
        'dev',                  // may enable dev related flags
        'focused',              // enable focused view where chrome, navigation, is reviewed (good for iframing in a standard view)
        'allowSkipping',        // override itemSession allowSkipping setting
        'validateResponses',    // override itemSession validateResponses setting
        'startingTaskSetIndex'  // choose which task set to start at in the fake data
    ]);

    /*========== CONFIGURATION PHASE ============*/

    // INTERNATIONALIZATION CONFIGS
    // Each view will need to load it's own i18n files
    // 1) Global for all of CCC Assess
    // 2) Global for all of CCC Assess components
    // 3) Custom for the view specific phrases or standard view phrases
    angular.module('CCC.View.AssessmentPrototype').config([

        'TranslateFileServiceProvider',
        '$translateProvider',

        function (TranslateFileServiceProvider, $translateProvider) {

            // push in files needed within this module
            // NO i18n specific to the prototype player

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
