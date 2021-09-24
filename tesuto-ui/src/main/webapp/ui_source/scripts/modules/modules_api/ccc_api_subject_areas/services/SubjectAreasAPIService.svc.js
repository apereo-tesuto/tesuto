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

    angular.module('CCC.API.SubjectAreas').service('SubjectAreasAPIService', [

        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'CCCUtils',
        '$timeout',
        '$q',

        function ($http, ErrorHandlerService, VersionableAPIClass, CCCUtils, $timeout, $q) {

            /*============ SERVICE DECLARATION ============*/

            var SubjectAreasAPIService = new VersionableAPIClass({id: 'subject-areas', resource: 'subject-areas'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ PUBLIC METHODS ============*/

            SubjectAreasAPIService.subjectAreas = {

                get: function (collegeIdList) {

                    return $http.get(SubjectAreasAPIService.getBasePath(), {
                        params: {
                            collegeIds: collegeIdList || []
                        }
                    }).then(function (results) {

                        return CCCUtils.coerce('SubjectAreaClass', results.data);

                    }, function (err) {

                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                publish: function (subjectAreaId) {

                    return $http.put(SubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/publish', {}).then(function (results) {

                        return results.data;

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                getPublishedVersion: function (subjectAreaId, version) {

                    return $http.get(SubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/version/' + version, {}).then(function (results) {

                        return CCCUtils.coerce('SubjectAreaClass', results.data);

                    }, function (err) {

                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                /**
                 * Creates a subjectArea with the given data
                 * @param  {object} data See SubjectAreaClass.serialize method for the format (or check swagger)
                 * @return {promise}          Promise which resolves to server return data
                 */
                create: function (data) {

                    return $http.post(SubjectAreasAPIService.getBasePath(), data).then(function (results) {

                        return results.data; // this should be the id of the created item

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                /**
                 * Update a subjectArea with the given data
                 * @param  {object} data See SubjectAreaClass.serialize method for the format (or check swagger)
                 * @return {promise}          Promise which resolves to server return data
                 */
                update: function (subjectAreaId, data) {

                    return $http.put(SubjectAreasAPIService.getBasePath() + '/' + subjectAreaId, data).then(function (results) {

                        return results.data;

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                /**
                 * Delete a subjectArea with the given data
                 * @param  {object} data See SubjectAreaClass.serialize method for the format (or check swagger)
                 * @return {promise}          Promise which resolves to server return data
                 */
                delete: function (subjectAreaId) {

                    return $http.delete(SubjectAreasAPIService.getBasePath() + '/' + subjectAreaId).then(function (results) {

                        return results.data;

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                getSequences: function (subjectAreaId) {

                    return $http.get(SubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/sequences', {}).then(function (results) {

                        return CCCUtils.coerce('SubjectAreaSequenceClass', results.data);

                    }, function (err) {

                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                getCourses: function (subjectAreaId) {

                    return $http.get(SubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/courses', {}).then(function (results) {

                        return CCCUtils.coerce('SubjectAreaCourseClass', results.data);

                    }, function (err) {

                        return ErrorHandlerService.reportServerError(err, []);
                    });
                }
            };


            SubjectAreasAPIService.subjectAreaSequences = {

                /**
                 * Update a subjectArea sequence with the given data
                 * @param  {object} data See SubjectAreaClass.serialize method for the format (or check swagger)
                 * @return {promise}          Promise which resolves to server return data
                 */
                update: function (subjectAreaId, cb21Code, courseGroup, data) {

                    return $http.put(SubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/sequences/' + cb21Code + '/' + courseGroup, data).then(function (results) {

                        return results.data;

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });
                }
            };


            SubjectAreasAPIService.subjectAreaCourses = {

                /**
                 * Create a course within a scequence for a subject area
                 * @param  {object} data See SubjectAreaCourseClass.serialize method for the format (or check swagger)
                 * @return {promise}          Promise which resolves to server return data
                 */
                create: function (subjectAreaId, data) {

                    // return deferred.promise;
                    return $http.post(SubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/courses', data).then(function (results) {

                        return results.data;

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                /**
                 * Update a course within a scequence for a subjectArea
                 * @param  {object} data See SubjectAreaCourseClass.serialize method for the format (or check swagger)
                 * @return {promise}          Promise which resolves to server return data
                 */
                update: function (subjectAreaId, courseId, data) {

                    // return deferred.promise;
                    return $http.put(SubjectAreasAPIService.getBasePath() + '/' + subjectAreaId + '/courses/' + courseId, data).then(function (results) {

                        return results.data;

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                /**
                 * Delete a course within a scequence for a subject area
                 * @return {promise}          Promise which resolves to server return data
                 */
                delete: function (subjectAreaId, cb21Code, courseId) {

                    // return deferred.promise;
                    return $http.delete(SubjectAreasAPIService.getBasePath() + '/courses/' + courseId).then(function (results) {

                        return results.data;

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });
                }
            };


            SubjectAreasAPIService.competencyGroups = {

                get: function (courseId) {

                    var deferred = $q.defer();

                    $timeout(function () {

                        if (FORCE_SERVER_ERROR) {
                            deferred.reject({status: FORCE_SERVER_ERROR, statusCode: FORCE_SERVER_ERROR});
                        } else {
                            deferred.resolve(CCCUtils.coerce('CompetencyGroupClass', angular.copy([])));
                        }

                    }, 1000);

                    return deferred.promise;

                    // return $http.get(SubjectAreasAPIService.getBasePath() + '/' + courseId + '/competencygroups/', {}).then(function (results) {

                    //     return CCCUtils.coerce('CompetencyGroupClass', results.data);

                    // }, function (err) {

                    //     return ErrorHandlerService.reportServerError(err, []);
                    // });
                },

                /**
                 * Creates a course competency group with the given data
                 * @param  {object} data See CompetencyGroupClass.serialize method for the format (or check swagger)
                 * @return {promise}          Promise which resolves to server return data
                 */
                create: function (courseId, data) {

                    // var deferred = $q.defer();

                    // $timeout(function () {

                    //     if (FORCE_SERVER_ERROR) {
                    //         deferred.reject({status: FORCE_SERVER_ERROR, statusCode: FORCE_SERVER_ERROR});
                    //     } else {
                    //         // should pass back some type of id
                    //         deferred.resolve(Math.round(Math.random() * 99999999));
                    //     }

                    // }, 1000);

                    // return deferred.promise;

                    return $http.post(SubjectAreasAPIService.getBasePath() + '/' + courseId + '/competencygroups/', data).then(function (results) {

                        return results.data; // this should be the id of the created item

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });
                },

                /**
                 * Update a course competency group with the given data
                 * @param  {object} data See CompetencyGroupClass.serialize method for the format (or check swagger)
                 * @return {promise}          Promise which resolves to server return data
                 */
                update: function (courseId, competencyGroupId, data) {

                    var deferred = $q.defer();

                    $timeout(function () {

                        if (FORCE_SERVER_ERROR) {
                            deferred.reject({status: FORCE_SERVER_ERROR, statusCode: FORCE_SERVER_ERROR});
                        } else {
                            deferred.resolve(true);
                        }

                    }, 1000);

                    return deferred.promise;

                    // return $http.put(SubjectAreasAPIService.getBasePath() + '/' + courseId + '/competencygroups/' + competencyGroupId, data).then(function (results) {

                    //     return results.data; // this should be the id of the created item

                    // }, function (err) {
                    //     return ErrorHandlerService.reportServerError(err, []);
                    // });
                },

                /**
                 * Delete a course competency group
                 * @param  {object} data See CompetencyGroupClass.serialize method for the format (or check swagger)
                 * @return {promise}          Promise which resolves to server return data
                 */
                delete: function (subjectAreaId, cb21Code, data) {

                    var deferred = $q.defer();

                    $timeout(function () {

                        if (FORCE_SERVER_ERROR) {
                            deferred.reject({status: FORCE_SERVER_ERROR, statusCode: FORCE_SERVER_ERROR});
                        } else {
                            deferred.resolve(true);
                        }

                    }, 1000);

                    return deferred.promise;

                    // return $http.delete(SubjectAreasAPIService.getBasePath() + '/' + courseId + '/competencygroups/' + competencyGroupId, data).then(function (results) {

                    //     return true;

                    // }, function (err) {
                    //     return ErrorHandlerService.reportServerError(err, []);
                    // });
                }
            };



            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return SubjectAreasAPIService;
        }
    ]);

})();

