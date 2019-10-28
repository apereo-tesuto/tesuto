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

    angular.module('CCC.API.MMSubjectAreas').service('MMSubjectAreasAPIService', [

        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'CCCUtils',
        '$timeout',
        '$q',

        function ($http, ErrorHandlerService, VersionableAPIClass, CCCUtils, $timeout, $q) {

            /*============ SERVICE DECLARATION ============*/

            var MMSubjectAreasAPIService = new VersionableAPIClass({id: 'subject-areas', resource: 'subject-areas'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ PUBLIC METHODS ============*/

            MMSubjectAreasAPIService.get = function (collegeIdList) {

                return $http.get(MMSubjectAreasAPIService.getBasePath(), {
                    params: {
                        collegeIds: collegeIdList || []
                    }
                }).then(function (results) {

                    return CCCUtils.coerce('MMSubjectAreaClass', results.data);

                }, function (err) {

                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            MMSubjectAreasAPIService.publish = function (subjectAreaId) {

                return $http.put(MMSubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/publish', {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            MMSubjectAreasAPIService.getPublishedVersion = function (subjectAreaId, version) {

                return $http.get(MMSubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/version/' + version, {}).then(function (results) {

                    return CCCUtils.coerce('MMSubjectAreaClass', results.data);

                }, function (err) {

                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            /**
             * Creates a subjectArea with the given data
             * @param  {object} data See SubjectAreaClass.serialize method for the format (or check swagger)
             * @return {promise}          Promise which resolves to server return data
             */
            MMSubjectAreasAPIService.create = function (data) {

                return $http.post(MMSubjectAreasAPIService.getBasePath(), data).then(function (results) {

                    return results.data; // this should be the id of the created item

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            /**
             * Update a subjectArea with the given data
             * @param  {object} data See SubjectAreaClass.serialize method for the format (or check swagger)
             * @return {promise}          Promise which resolves to server return data
             */
            MMSubjectAreasAPIService.update = function (subjectAreaId, data) {

                return $http.put(MMSubjectAreasAPIService.getBasePath() + '/' + subjectAreaId, data).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            /**
             * Delete a subjectArea with the given data
             * @param  {object} data See SubjectAreaClass.serialize method for the format (or check swagger)
             * @return {promise}          Promise which resolves to server return data
             */
            MMSubjectAreasAPIService.delete = function (subjectAreaId) {

                return $http.delete(MMSubjectAreasAPIService.getBasePath() + '/' + subjectAreaId).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            MMSubjectAreasAPIService.getSequences = function (subjectAreaId) {

                return $http.get(MMSubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/sequences', {}).then(function (results) {

                    return CCCUtils.coerce('SubjectAreaSequenceClass', results.data);

                }, function (err) {

                    return ErrorHandlerService.reportServerError(err, []);
                });
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return MMSubjectAreasAPIService;
        }
    ]);

})();

