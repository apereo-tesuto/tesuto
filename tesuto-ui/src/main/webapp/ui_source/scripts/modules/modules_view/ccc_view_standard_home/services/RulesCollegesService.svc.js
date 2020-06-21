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

    angular.module('CCC.View.Home').service('RulesCollegesService', [

        'RulesCollegesAPIService',

        function (RulesCollegesAPIService) {

            /*============ SERVICE DECLARATION ============*/

            var RulesCollegesService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var fetchRulesPromisesMap = {};

            var fetchRules = function (misCode, competency) {

                if (fetchRulesPromisesMap[misCode] && fetchRulesPromisesMap[misCode][competency]) {

                    return fetchRulesPromisesMap[misCode][competency];

                } else {

                    fetchRulesPromisesMap[misCode] = fetchRulesPromisesMap[misCode] || {};
                    fetchRulesPromisesMap[misCode][competency] = RulesCollegesAPIService.getPlacementMethodOptions(misCode, competency).then(function (rules) {
                        return rules;
                    });

                    return fetchRulesPromisesMap[misCode][competency];
                }
            };


            /*============ SERVICE DEFINITION AND PUBLIC METHODS ============*/

            RulesCollegesService = {};

            RulesCollegesService.getRulesByCategory = function (misCode, competency, category) {

                return fetchRules(misCode, competency).then(function (rules) {

                    // map all the key values in the filtered objects to ensure nobody pollutes the cached rule objects
                    return _.map(

                        _.filter(
                            rules,
                            function (rule) {
                                return rule.category === category;
                            }
                        ),
                        function (rule) {
                            return {
                                id: rule.id,
                                title: rule.title,
                                description: rule.description
                            };
                        }
                    );
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return RulesCollegesService;
        }
    ]);

})();
