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

    angular.module('CCC.Components', ['CCC.Assess']);


    /*======================== LOAD VALUES/CONSTANTS ========================*/


    /*======================== OVERRIDES TO THIRD PARTY MODULES ========================*/

    // override the template/modal/window.html template to add some custom accessibility
    angular.module("uib/template/modal/window.html", []).run(["$templateCache", function ($templateCache) {
        $templateCache.put("uib/template/modal/window.html", [
            '<div modal-render="{{$isRendered}}" tabindex="-1"',
                'ccc-responds-to-dialogs="true"',
                'role="dialog" class="modal"',
                'aria-labelledby="{{$parent.modal.labelId}}"',
                'aria-describedby="{{$parent.modal.contentId}}"',
                'uib-modal-animation-class="fade"',
                'modal-in-class="in"',
                'ng-style="{\'z-index\': 1050 + index*10, display: \'block\'}" ng-click="close($event)">',
                '<div class="modal-dialog" ng-class="size ? \'modal-\' + size : \'\'">',
                    '<div class="modal-content ccc-bg ccc-fg" uib-modal-transclude></div>',
                '</div>',
            "</div>"
        ].join(' '));
    }]);

    angular.module("uib/template/datepicker/popup.html", []).run(["$templateCache", function($templateCache) {
      $templateCache.put("uib/template/datepicker/popup.html",
        "<ul class=\"dropdown-menu\" dropdown-nested ng-if=\"isOpen\" style=\"display: block\" ng-style=\"{top: position.top+'px', left: position.left+'px'}\" ng-keydown=\"keydown($event)\" ng-click=\"$event.stopPropagation()\">\n" +
        "   <li ng-transclude></li>\n" +
        "   <li class=\"date-picker-button-bar\" ng-if=\"showButtonBar\" style=\"padding:10px 9px 2px\">\n" +
        "       <span class=\"btn-group pull-left\">\n" +
        "           <button type=\"button\" class=\"btn btn-sm btn-default\" ng-click=\"select('today')\" ng-disabled=\"isDisabled('today')\">{{ getText('current') }}</button>\n" +
        "           <button type=\"button\" class=\"btn btn-sm btn-default\" ng-click=\"select(null)\">{{ getText('clear') }}</button>\n" +
        "       </span>\n" +
        "       <button type=\"button\" class=\"btn btn-sm btn-default pull-right\" ng-click=\"close()\">{{ getText('close') }}</button>\n" +
        "   </li>\n" +
        "</ul>\n" +
        "");
    }]);

    angular.module("uib/template/datepicker/datepicker.html", []).run(["$templateCache", function($templateCache) {
      $templateCache.put("uib/template/datepicker/datepicker.html",
        "<div class=\"date-picker-container\" ng-switch=\"datepickerMode\">\n" +
        "  <span class='date-picker-container-triangle'></span>\n" +
        "  <div uib-daypicker ng-switch-when=\"day\" tabindex=\"0\" class=\"uib-daypicker\"></div>\n" +
        "  <div uib-monthpicker ng-switch-when=\"month\" tabindex=\"0\" class=\"uib-monthpicker\"></div>\n" +
        "  <div uib-yearpicker ng-switch-when=\"year\" tabindex=\"0\" class=\"uib-yearpicker\"></div>\n" +
        "</div>\n" +
        "");
    }]);


    /*======================== LOAD CONFIGURATIONS ========================*/

    // REGISTER I18N FILES FOR THIS MODULE
    angular.module('CCC.Components').config(['TranslateFileServiceProvider', function (TranslateFileServiceProvider) {
        TranslateFileServiceProvider.addTranslateFile({
            prefix: 'ui/scripts/modules/modules_common/ccc_components/i18n/locale-',
            suffix: '.json'
        });
    }]);


    /*======================== INITIALIZATION ========================*/

    angular.module('CCC.Components');

})();

