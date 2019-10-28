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

    angular.module('CCC.API.ClassReport').service('ClassReportAPIService', [

        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'CCCUtils',
        '$q',
        '$timeout',

        function ($http, ErrorHandlerService, VersionableAPIClass, CCCUtils, $q, $timeout) {

            /*============ SERVICE DECLARATION ============*/

            var ClassReportAPIService = new VersionableAPIClass({id: 'classReport', resource: 'class-report'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            ClassReportAPIService.upload = function (file) {
                var fd = new window.FormData();
                fd.append('file', file);

                return $http.post(ClassReportAPIService.getBasePath() + '/upload', fd,
                    {
                        transformRequest: angular.identity,
                        headers: {'Content-Type': undefined
                    }

                }).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['400']);
                });
            };

            ClassReportAPIService.generate = function (classReportForm) {

                return $http.post(ClassReportAPIService.getBasePath() + '/generate', classReportForm).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['400']);
                });
            };

            ClassReportAPIService.validateAndGenerate = function (data) {

                return $http.post(ClassReportAPIService.getBasePath() + '/validateAndGenerate', {}, {
                    params: {
                        importedFilename: data.importedFilename,
                        competencyName: data.competencyName,
                        collegeId: data.collegeId,
                        courseId: data.courseId,
                        notes: data.notes
                    }
                }).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['400']);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return ClassReportAPIService;
        }
    ]);

})();

