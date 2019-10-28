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
    /**
     * All API calls associated with Placement
     */

    angular.module('CCC.API.Placement').service('PlacementAPIService', [

        '$rootScope',
        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        '$q',
        '$timeout',

        function ($rootScope, $http, ErrorHandlerService, VersionableAPIClass, $q, $timeout) {


            /*============ SERVICE DECLARATION ============*/

            var PlacementAPIService = new VersionableAPIClass({id: 'placement', resource: 'placement'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/



            /*============ SERVICE DEFINITION ============*/

            PlacementAPIService.getPlacementDecisionCounselor = function (collegeMisCode, assessmentSessionId) {

                return $http.get(PlacementAPIService.getBasePath() + '/decision/' + collegeMisCode + '/' + assessmentSessionId, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '401'], ['logout']);
                });
            };

            PlacementAPIService.getPlacementDecisionStudent = function (collegeMisCode, assessmentSessionId) {

                return $http.get(PlacementAPIService.getBasePath() + '/decision/' + collegeMisCode + '/' + assessmentSessionId + '/student', {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '401'], ['logout']);
                });
            };

            PlacementAPIService.getPlacementsForStudentByCollege = function (studentId, collegeId) {
                throw new Error('can we delete this?');
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return PlacementAPIService;

        }
    ]);

})();
