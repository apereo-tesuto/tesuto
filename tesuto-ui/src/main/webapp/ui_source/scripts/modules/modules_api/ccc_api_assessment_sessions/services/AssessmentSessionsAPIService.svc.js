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
     * All API calls associated with assessmentSessions
     */

    angular.module('CCC.API.AssessmentSessions').service('AssessmentSessionsAPIService', [

        '$rootScope',
        'ErrorHandlerService',
        'VersionableAPIClass',
        '$http',
        '$q',

        function ($rootScope, ErrorHandlerService, VersionableAPIClass, $http, $q) {

            /*============ SERVICE DECLARATION ============*/

            var AssessmentSessionsAPIService = new VersionableAPIClass({id: 'assessmentSessions', resource: 'assessmentsessions'});


            /*============ SERVICE DEFINITION ============*/

            // generally not used because we inject this object into the JSP so we have the assessmentTitle right away
            // returns an assessment session, including the current taskSet
            // NOTE: now used for printing... the backend will put ALL tasks within current task set as a flattened array
            AssessmentSessionsAPIService.get = function (assessmentSessionId) {

                return $http.get(AssessmentSessionsAPIService.getBasePath() + '/' + assessmentSessionId, {}).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            AssessmentSessionsAPIService.getAsProcessor = function (assessmentSessionId, assessedDate) {

                return $http.get(AssessmentSessionsAPIService.getBasePath() + '/' + assessmentSessionId + '/processor', {params: {assessedDate: assessedDate}}).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            AssessmentSessionsAPIService.updateTaskSetResponses = function (assessmentSessionId, taskSetId, responses) {

                return $http.put(AssessmentSessionsAPIService.getBasePath() + '/' + assessmentSessionId + '/' + taskSetId + '/response', responses).then(function (results) {
                    return results.data;
                }, function (err) {
                    // NOTE: we let 404's come through so the UI can handle a strange situation where the currentTaskSet is out of sync with the server
                    return ErrorHandlerService.reportServerError(err, ['404']);
                });
            };

            AssessmentSessionsAPIService.completeTaskSet = function (assessmentSessionId, taskSetId, responses) {

                return $http.post(AssessmentSessionsAPIService.getBasePath() + '/' + assessmentSessionId + '/' + taskSetId + '/completion', responses).then(function (results) {
                    return results.data;
                }, function (err) {
                    // NOTE: since the player has it's own modal for completion failures, let's ignore certain error reporting channels
                    // NOTE: we let 404's come through so the UI can handle a strange situation where the currentTaskSet is out of sync with the server
                    return ErrorHandlerService.reportServerError(err, ['404'], ['serverErrorModal', 'timeoutModal', 'notFoundModal']);
                });
            };

            AssessmentSessionsAPIService.completePaperAssessment = function (assessmentSessionId, taskSetId, responses) {

                return $http.post(AssessmentSessionsAPIService.getBasePath() + '/' + assessmentSessionId + '/' + taskSetId + '/papercompletion', responses).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['406'], []);
                });
            };

            AssessmentSessionsAPIService.completeTaskSetPrevious = function (assessmentSessionId, taskSetId, responses) {

                return $http.post(AssessmentSessionsAPIService.getBasePath() + '/' + assessmentSessionId + '/' + taskSetId + '/previous', responses).then(function (results) {
                    return results.data;
                }, function (err) {
                    // NOTE: since the player has it's own modal for completion failures, let's ignore certain error reporting channels
                    // NOTE: we let 404's come through so the UI can handle a strange situation where the currentTaskSet is out of sync with the server
                    return ErrorHandlerService.reportServerError(err, ['404'], ['serverErrorModal', 'timeoutModal', 'notFoundModal']);
                });
            };

            AssessmentSessionsAPIService.getTaskSetById = function (assessmentSessionId, taskSetId) {};

            AssessmentSessionsAPIService.getCompetencyMastery = function (assessmentSessionId) {

                return $http.get(AssessmentSessionsAPIService.getBasePath() + '/' + assessmentSessionId + '/competency/mastery', {}).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '500']);
                });
            };

            AssessmentSessionsAPIService.getCompetencyStudentMastery = function (assessmentSessionId) {

                return $http.get(AssessmentSessionsAPIService.getBasePath() + '/' + assessmentSessionId + '/competency/studentmastery', {}).then(function (results) {
                    return results.data;
                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '500']);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return AssessmentSessionsAPIService;

        }
    ]);

})();
