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

    /*========== ENTRY POINT ON DOCUMENT READY FROM THE MAIN TEMPLATE ============*/

    angular.module('CCC.View.RemoteProctor', [

        // framework dependencies
        'CCC.Assess',

        // standard view common utils, directives, configuration
        'CCC.View.Layout',
        'CCC.View.Common',

        // Server API modules
        'CCC.API.RemoteProctor'

        // Common Modules

    ]);


    /*======================== LOAD VALUES/CONSTANTS ========================*/


    /*======================== LOAD CONFIGURATIONS ========================*/

    // INTERNATIONALIZATION CONFIGS
    // Each view will need to load it's own i18n files
    // 1) Global for all of CCC Assess
    // 2) Global for all of CCC Assess components
    // 3) Custom for the view specific phrases or standard view phrases
    angular.module('CCC.View.RemoteProctor').config([

        'TranslateFileServiceProvider',
        '$translateProvider',

        function (TranslateFileServiceProvider, $translateProvider) {

            // push in files needed within this module
            TranslateFileServiceProvider.addTranslateFile({
                prefix: 'ui/scripts/modules/modules_view/ccc_view_remote_proctor/i18n/locale-',
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


    /*======================== INITIALIZATION ========================*/

    // setting the footer directive
    angular.module('CCC.View.RemoteProctor').run([

        'ViewManagerEntity',

        function (ViewManagerEntity) {

            ViewManagerEntity.setFooterDirective('ccc-view-common-footer');
        }
    ])

    .run([

        'Idle',

        function (Idle) {

            // disable the idle timer since a remote proctor has no way to logout
            Idle.unwatch();
        }
    ]);

})();
