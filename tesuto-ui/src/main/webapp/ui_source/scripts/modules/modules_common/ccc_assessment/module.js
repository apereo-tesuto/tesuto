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

    angular.module('CCC.Assessment', ['CCC.Assess', 'CCC.Math']);


    /*======================== LOAD VALUES/CONSTANTS ========================*/

    // this is the white list of attributes that will propagate from a QTI interaction through to the rendered interaction
    angular.module('CCC.Assessment').constant('CCC_INTERACTION_ATTR_WHITE_LIST', [
        'aria-labelledby',
        'aria-label',
        'id'
    ]);


    /*======================== LOAD CONFIGURATIONS ========================*/

    // REGISTER I18N FILES FOR THIS MODULE
    angular.module('CCC.Assessment').config(['TranslateFileServiceProvider', function (TranslateFileServiceProvider) {
        TranslateFileServiceProvider.addTranslateFile({
            prefix: 'ui/scripts/modules/modules_common/ccc_assessment/i18n/locale-',
            suffix: '.json'
        });
    }]);


    /*======================== INITIALIZATION ========================*/

    // todo: put this in a filter
    angular.module('CCC.Assessment').filter('uuid', function () {
        return function (input) {
            return ('uuid' + input).replace(/\-/g, "_");
        };
    });

    // here is the template we want to use for the errors in the tooltip
    angular.module('CCC.Assessment').run([
        '$templateCache',
        function ($templateCache) {

            $templateCache.put('ccc-interaction-popover-errors-template.html', [
                '<span ccc-lang>',
                    '<ul class="ccc-interaction-error-list ccc-fg">',
                        '<li tabindex="0" ng-repeat="error in interaction.validationErrors"><span class="text-warning" translate="{{error.msg}}"></span></li>',
                    '</ul>',
                '</span>'
            ].join(''));
        }
    ]);

})();

