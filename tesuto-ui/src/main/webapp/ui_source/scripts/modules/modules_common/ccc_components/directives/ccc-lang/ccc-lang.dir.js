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

    angular.module('CCC.Components').directive('cccLang', [function () {

        return {
            restrict: 'A',

            controller: [

                '$scope',
                '$element',
                '$attrs',
                '$translate',

                function ($scope, $element, $attrs, $translate) {

                    /*=============== PRIVATE VARIABLES AND METHODS ==============*/

                    var currentLanguage = false;

                    var updateSystemLanguage = function (newSystemLanguage) {
                        if (!currentLanguage) {
                            $($element).attr('lang', newSystemLanguage).attr('xml:lang', newSystemLanguage);
                        }
                    };

                    var updateLanguage = function (newLanguage) {
                        if (currentLanguage) {
                            $($element).attr('lang', newLanguage).attr('xml:lang', newLanguage);
                        }
                    };


                    /*=============== LISTENERS ==============*/

                    $scope.$watch(function () {
                        return $translate.use();
                    }, updateSystemLanguage);

                    $scope.$watch(function () {
                        currentLanguage = $attrs.cccLang;
                        return currentLanguage;
                    }, updateLanguage);


                    /*=============== INITIALIZATION ==============*/

                }
            ]
        };
    }]);

})();
