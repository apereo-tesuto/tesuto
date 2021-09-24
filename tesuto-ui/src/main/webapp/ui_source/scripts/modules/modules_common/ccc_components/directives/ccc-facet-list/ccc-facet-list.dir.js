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

    angular.module('CCC.Components').directive('cccFacetList', function () {

        return {

            restrict: 'E',

            scope: {
                facetedSearchManager: "="
            },

            controller: [

                '$scope',

                function ($scope) {


                    /*============ MODEL ============*/

                    $scope.facetData = $scope.facetedSearchManager.getFacetData();
                    $scope.selectedFacetsCount = 0;

                    /*============ BEHAVIOR ============*/

                    $scope.toggleFacetValue = function (facet, facetValue) {
                        $scope.facetedSearchManager.toggleFacet(facet.id, facetValue.id);
                    };

                    $scope.clearAll = function () {
                        $scope.facetedSearchManager.clearAllFacets();
                    };


                    /*============ LISTENERS ============*/

                    $scope.facetedSearchManager.addListener('facetDataUpdated', function (facetData) {
                        $scope.facetData = facetData;
                        $scope.selectedFacetsCount = $scope.facetedSearchManager.getSelectedFacetsCount();
                    });

                }
            ],

            template: [
                '<div class="well well-thin">',
                    '<div class="ccc-facet-list-header row">',
                        '<div class="col-xs-7">',
                            '<span class="ccc-facet-list-title" translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.REFINE_BY">{{::facetedSearchManager.title}}</span>',
                        '</div>',
                        '<div class="col-xs-5 text-right">',
                            '<span class="ccc-facet-list-clear-all">',
                                '<a href="#" class="btn-link btn-full-width-when-small" ng-click="clearAll()" ng-disabled="selectedFacetsCount == 0" translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.CLEAR"><span class="sr-only" translate="CCC_VIEW_HOME.ACTIVATION_FSEARCH.ALL_FACETS_SR"> </span><i class="fa fa-times-circle"></i></a>',
                            '</span>',
                        '</div>',
                    '</div>',
                    '<div class="ccc-facet-list-facets">',
                        '<div class="ccc-facet-list-facet" ng-class="{selected: facet.selected}" ng-repeat="facet in facetData track by facet.id">',
                            '<h4 class="ccc-facet-list-facet-title" translate="{{::facet.title}}"></h4>',
                            '<div class="ccc-facet-list-facet-values" role="group">',
                                '<div class="ccc-facet-list-facet-value" ng-repeat="value in facet.values track by value.id" role="checkbox" ng-checked="value.selected">',
                                    '<input id="ccc-facet-input-{{::facet.id + \'-\' + value.id}}" type="checkbox" ng-checked="value.selected" ng-click="toggleFacetValue(facet, value)"/>',
                                    '<label for="ccc-facet-input-{{::facet.id + \'-\' + value.id}}" class="ccc-facet-list-facet-value-title">{{::value.title}}</label>',
                                    '<span class="ccc-facet-list-facet-value-count">( {{value.count}} )</span>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>'
            ].join('')

        };
    });

})();
