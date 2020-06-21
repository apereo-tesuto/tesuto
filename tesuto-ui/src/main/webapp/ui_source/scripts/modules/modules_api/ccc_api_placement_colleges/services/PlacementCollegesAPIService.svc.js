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

    angular.module('CCC.API.PlacementColleges').service('PlacementCollegesAPIService', [

        '$rootScope',
        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        '$q',
        '$timeout',

        function ($rootScope, $http, ErrorHandlerService, VersionableAPIClass, $q, $timeout) {


            /*============ SERVICE DECLARATION ============*/

            var PlacementCollegesAPIService = new VersionableAPIClass({id: 'placementColleges', resource: 'colleges'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/



            /*============ SERVICE DEFINITION ============*/

            PlacementCollegesAPIService.getPlacementsForStudentByCollege = function (studentId, collegeId) {

                return $http.get(PlacementCollegesAPIService.getBasePath() + '/' + collegeId + '/cccid/' + studentId + '/placements', {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '401'], ['logout']);
                });
            };

            PlacementCollegesAPIService.getPlacementComponentsForStudentByCollege = function (studentId, collegeId) {

                return $http.get(PlacementCollegesAPIService.getBasePath() + '/' + collegeId + '/cccid/' + studentId + '/placement-components', {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '401'], ['logout']);
                });
            };

            PlacementCollegesAPIService.getStudentPlacementByCollege = function (collegeId) {

                return $http.get(PlacementCollegesAPIService.getBasePath() + '/' + collegeId + '/student-placements', {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '401'], ['logout']);
                });
            };

            PlacementCollegesAPIService.getPlacementAssessmentComponentMetaData = function (collegeId, placementId) {

                return $http.get(PlacementCollegesAPIService.getBasePath() + '/' + collegeId + '/placements/' + placementId, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '401'], ['logout']);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return PlacementCollegesAPIService;
        }
    ]);

})();
