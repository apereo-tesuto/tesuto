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

    angular.module('CCC.Components').directive('cccContentLoadingPlaceholder', function () {

        return {

            restrict: 'E',

            transclude: true,

            scope: {
                noResults: "=?",
                noResultsInfo: "=?",
                hideDefaultNoResultsText: "=?"  // this component can show some default no-result text... if you want to hide it then pass true
            },

            controller: [

                '$scope',

                function ($scope) {
                    $scope.hideDefaultNoResultsText = $scope.hideDefaultNoResultsText || false;
                }
            ],

            template: [
                '<div class="ccc-content-loading-placeholder-inner" ng-class="{\'no-results\': noResults, \'no-results-info\': noResultsInfo}">',

                    '<div ng-if="!noResults && !noResultsInfo">',
                        '<span translate="CCC_COMP.CONTENT_LOADING"></span> ',
                        '<i class="fa fa-spinner fa-spin" aria-hidden="true"></i>',
                    '</div>',

                    '<div ng-if="!hideDefaultNoResultsText && (noResults || noResultsInfo)">',
                        '<span translate="CCC_COMP.CONTENT_NO_RESULTS"></span> ',
                    '</div>',

                    '<div ng-show="noResults || noResultsInfo">',
                        '<div ng-transclude></div>',
                    '</div>',

                '</div>'
            ].join('')

        };

    });

})();
