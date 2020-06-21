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
     * All API calls associated with preview assessmentSessions
     */

    angular.module('CCC.API.AssessmentSessionsPreview').service('AssessmentSessionsPreviewAPIService', [

        '$rootScope',
        'ErrorHandlerService',
        'VersionableAPIClass',
        '$http',

        function ($rootScope, ErrorHandlerService, VersionableAPIClass, $http) {

            /*============ SERVICE DECLARATION ============*/

            var AssessmentSessionsPreviewAPIService = new VersionableAPIClass({id: 'previewAssessmentSessions', resource: 'assessmentsessions'});


            /*============ SERVICE DEFINITION ============*/

            // generally not used because we inject this object into the JSP so we have the assessmentTitle right away
            // returns an assessment session, including the current taskSet
            AssessmentSessionsPreviewAPIService.get = function (assessmentSessionId) {

                return $http.get(AssessmentSessionsPreviewAPIService.getBasePath() + '/' + assessmentSessionId, {}).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            AssessmentSessionsPreviewAPIService.updateTaskSetResponses = function (assessmentSessionId, taskSetId, responses) {

                return $http.put(AssessmentSessionsPreviewAPIService.getBasePath() + '/' + assessmentSessionId + '/' + taskSetId + '/response', responses).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, [], ['serverErrorModal', 'timeoutModal', 'notFoundModal']);
                });
            };

            AssessmentSessionsPreviewAPIService.completeTaskSet = function (assessmentSessionId, taskSetId, responses) {

                return $http.post(AssessmentSessionsPreviewAPIService.getBasePath() + '/' + assessmentSessionId + '/' + taskSetId + '/completion', responses).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, [], ['serverErrorModal', 'timeoutModal', 'notFoundModal']);
                });
            };

            AssessmentSessionsPreviewAPIService.scoreTaskSetResponse = function (assessmentSessionId, taskSetId, responses) {

                return $http.post(AssessmentSessionsPreviewAPIService.getBasePath() + '/' + assessmentSessionId + '/' + taskSetId + '/score', responses).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            AssessmentSessionsPreviewAPIService.scoreTaskResponse = function (assessmentSessionId, taskSetId, taskId, responses) {

                return $http.post(AssessmentSessionsPreviewAPIService.getBasePath() + '/' + assessmentSessionId + '/' + taskSetId + '/' + taskId + '/score', responses).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };



            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return AssessmentSessionsPreviewAPIService;

        }
    ]);

})();
