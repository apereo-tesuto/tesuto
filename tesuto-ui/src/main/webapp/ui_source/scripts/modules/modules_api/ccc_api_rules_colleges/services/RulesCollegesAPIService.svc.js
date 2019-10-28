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

    angular.module('CCC.API.RulesColleges').service('RulesCollegesAPIService', [

        '$http',
        '$q',
        '$timeout',
        'ErrorHandlerService',
        'VersionableAPIClass',

        function ($http, $q, $timeout, ErrorHandlerService, VersionableAPIClass) {

            /*============ SERVICE DECLARATION ============*/

            var RulesCollegesAPIService = new VersionableAPIClass({id: 'rulesColleges', resource: 'colleges'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            RulesCollegesAPIService.getPlacementMethodOptions = function (collegeMisCode, competency) {

                var url = RulesCollegesAPIService.getBasePath() + '/' + collegeMisCode + '/competency/' + competency + '/ruleset';

                return $http.get(url, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '401'], ['logout']);
                });

                // var deferred = $q.defer();

                // $timeout(function () {

                //     deferred.resolve([
                //         {
                //             "id": "f11c8117-d23e-4c5f-9646-cb24edd94648",
                //             "title": "Highest/Best (placement)",
                //             "category": "Placement",
                //             "description": "Selects the highest level from the placement components."
                //         },
                //         {
                //             "id": "a61cbe33-2e10-41d1-b0b9-91a2b7a2a486",
                //             "title": "Highest/Best (placement)",
                //             "category": "Placement",
                //             "description": "Selects the highest level from the placement components."
                //         },
                //         {
                //             "id": "f11c8117-d23e-4c5f-9646-cb24edd94648",
                //             "title": "Highest/Best (assigned placement)",
                //             "category": "Assigned Placement",
                //             "description": "Selects the highest level from the placement components."
                //         },
                //         {
                //             "id": "a61cbe33-2e10-41d1-b0b9-91a2b7a2a486",
                //             "title": "Highest/Best (assigned placement)",
                //             "category": "Assigned Placement",
                //             "description": "Selects the highest level from the placement components."
                //         },
                //         {
                //             id: 1,
                //             "category": "Decision Logic",
                //             title: 'FAKE: Statewide Decision Logic ONE',
                //             description: 'Fake Description for decision logic that explains the difference between the other decsion logic options.'
                //         },
                //         {
                //             id: 2,
                //             "category": "Decision Logic",
                //             title: 'FAKE: Statewide Decision Logic TWO',
                //             description: 'Fake Description for decision logic that explains the difference between the other decsion logic options.'
                //         },
                //         {
                //             id: 3,
                //             "category": "Decision Logic",
                //             title: 'FAKE: Statewide Decision Logic THREE',
                //             description: null
                //         }
                //     ]);

                // }, 2000);

                // return deferred.promise;
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return RulesCollegesAPIService;

        }
    ]);

})();

