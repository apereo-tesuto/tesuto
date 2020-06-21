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

    angular.module('CCC.View.Home').service('CompetencyMapsModelService', [

        'CompetencyMapsAPIService',

        function (CompetencyMapsAPIService) {

            /*============ SERVICE DECLARATION ============*/

            var CompetencyMapsModelService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var generateNestedItemsModel = function (nestedItems, competencies, forEachItem) {

                _.each(competencies, function (competency) {

                    var newItem = {

                        id: competency.identifier,
                        name: competency.description,
                        item: competency,

                        disabled: false,

                        children: []
                    };

                    forEachItem(newItem);

                    nestedItems.push(newItem);

                    if (competency.childCompetencyViewDtos && competency.childCompetencyViewDtos.length) {
                        generateNestedItemsModel(newItem.children, competency.childCompetencyViewDtos, forEachItem);
                    }
                });

                return nestedItems;
            };

            var getNestedItemsModel = function (competencyCode, competencyMapVersion, forEachItem) {

                return CompetencyMapsAPIService.getCompetencyMapByIdAndVersion(competencyCode, competencyMapVersion).then(function (competencyTree) {
                    return generateNestedItemsModel([], competencyTree.childCompetencyViewDtos, forEachItem);
                });
            };



            /*============ SERVICE DEFINITION ============*/

            CompetencyMapsModelService = {
                getNestedItemsModel: getNestedItemsModel
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return CompetencyMapsModelService;

        }
    ]);

})();


