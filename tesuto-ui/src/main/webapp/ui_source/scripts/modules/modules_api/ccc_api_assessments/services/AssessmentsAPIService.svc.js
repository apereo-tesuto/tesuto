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
     * All API calls associated with assessments
     */

    angular.module('CCC.API.Assessments').service('AssessmentsAPIService', [

        '$rootScope',
        '$http',
        '$q',
        '$timeout',
        '$location',
        'ErrorHandlerService',
        'VersionableAPIClass',

        function ($rootScope, $http, $q, $timeout, $location, ErrorHandlerService, VersionableAPIClass) {

            /*============ SERVICE DECLARATION ============*/

            var AssessmentsAPIService = new VersionableAPIClass({id: 'assessments', resource: 'assessments'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            AssessmentsAPIService.getAssessments = function () {

                return $http.get(AssessmentsAPIService.getBasePath(), {}).then(function (results) {

                    return results.data;

                }, function (err) {

                    return ErrorHandlerService.reportServerError(err, []);

                // for simulation purposes we tack on a deferred with a timeout to simulate server latency
                }).then(function (results) {

                    return results;
                });

            };

            AssessmentsAPIService.getAssessmentsByLocation = function (location) {

                return $http.get(AssessmentsAPIService.getBasePath() + "/location/" + location.id, {}).then(function (results) {

                    return results.data;

                }, function (err) {

                    return ErrorHandlerService.reportServerError(err, []);

                // for simulation purposes we tack on a deferred with a timeout to simulate server latency
                }).then(function (results) {

                    return results;
                });
            };

            AssessmentsAPIService.getAssessmentVersions = function (assessment) {

                return $http.get(AssessmentsAPIService.getBasePath() + '/' + assessment.namespace + '/' + assessment.identifier + '/versions', {}).then(function (results) {

                    return results.data;

                }, function (err) {

                    return ErrorHandlerService.reportServerError(err, []);

                });
            };

            AssessmentsAPIService.getPrintURL = function (assessment, misCode, cccId) {

                var queryParams = '?miscode=' + misCode;
                if (cccId) {
                    queryParams = queryParams + '&cccid=' + cccId;
                }

                // TODO: not sure why this isn't versioned and is different than "assessment"
                return 'assessment-print' + queryParams + '&namespace=' + assessment.namespace + '&identifier=' +  assessment.identifier;
            };

            AssessmentsAPIService.printAssessment = function (assessmentId, misCode, userId) {

                var printURL = AssessmentsAPIService.getPrintURL(assessmentId, misCode, userId);
                window.open(printURL);

                return $q.when(true);
            };
            
            AssessmentsAPIService.getAssessmentsByTestLocationId = function (testLocationId) {

                return $http.get(AssessmentsAPIService.getBasePath() + '/test-location/' + testLocationId).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            AssessmentsAPIService.getAssessmentsByTestLocationIdWithCollegeAffiliation = function (testLocationId) {

                return $http.get(AssessmentsAPIService.getBasePath() + '/test-location/' + testLocationId + '/college_affiliation').then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };
            
            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return AssessmentsAPIService;

        }
    ]);

})();

