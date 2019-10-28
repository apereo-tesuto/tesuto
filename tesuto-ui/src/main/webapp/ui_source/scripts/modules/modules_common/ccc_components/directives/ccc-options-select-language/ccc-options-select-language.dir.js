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

    angular.module('CCC.Components').directive('cccOptionsSelectLanguage', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'LanguageService',

                function ($scope, LanguageService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    /*=========== MODEL ===========*/

                    /*=========== BEHAVIOR ===========*/

                    $scope.selectLanguage = function (languageKey) {
                        LanguageService.setLanguage(languageKey);
                    };

                    /*=========== LISTENERS ===========*/

                    /*=========== INITIALIZATION ===========*/

                }
            ],

            template: [
                '<div class="ccc-form-section">',
                    '<h4 class="ccc-form-section-title"><i class="fa fa-globe" aria-hidden="true"></i> <span translate="CCC_COMP.GLOBAL_OPTIONS.LANGUAGE.HEADER"></span></h4>',
                    '<div class="ccc-form-section-content">',
                        '<div class="btn-group-vertical btn-full-width" role="group">',
                            '<button class="btn btn-default" ng-click="selectLanguage(\'en\')">English</button>',
                            '<button class="btn btn-default" ng-click="selectLanguage(\'es\')">Spanish</button>',
                            '<button class="btn btn-default" ng-click="selectLanguage(\'none\')">Show Phrase Keys</button>',
                        '</div>',
                    '</div>',
                '</div>'
            ].join('')

        };

    });

})();
