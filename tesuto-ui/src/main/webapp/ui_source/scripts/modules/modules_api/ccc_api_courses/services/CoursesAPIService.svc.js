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
var FORCE_SERVER_ERROR = FORCE_SERVER_ERROR || false;

(function () {

    angular.module('CCC.API.Courses').service('CoursesAPIService', [

        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'CCCUtils',
        '$q',
        '$timeout',

        function ($http, ErrorHandlerService, VersionableAPIClass, CCCUtils, $q, $timeout) {

            /*============ SERVICE DECLARATION ============*/

            var CoursesAPIService = new VersionableAPIClass({id: 'courses', resource: 'courses'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            CoursesAPIService.getCompetencyGroups = function (courseId) {

                return $http.get(CoursesAPIService.getBasePath() + '/' + courseId + '/competency-groups', {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            CoursesAPIService.createCompetencyGroup = function (courseId, data) {

                // var deferred = $q.defer();

                // $timeout(function () {

                //     if (FORCE_SERVER_ERROR) {
                //         deferred.reject({status: FORCE_SERVER_ERROR, statusCode: FORCE_SERVER_ERROR});
                //     } else {
                //         deferred.resolve(Math.round(Math.random() * 999999999999));
                //     }

                // }, 300);

                // return deferred.promise;

                return $http.post(CoursesAPIService.getBasePath() + '/' + courseId + '/competency-groups', data).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return CoursesAPIService;
        }
    ]);

})();

