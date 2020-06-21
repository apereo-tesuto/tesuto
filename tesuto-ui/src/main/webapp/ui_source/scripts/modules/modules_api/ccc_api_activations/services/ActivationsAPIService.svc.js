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
     * All API calls associated with activations
     */

    angular.module('CCC.API.Activations').service('ActivationsAPIService', [

        '$rootScope',
        'ErrorHandlerService',
        'VersionableAPIClass',
        '$http',
        '$q',
        '$timeout',
        'CCCUtils',
        'Moment',

        function ($rootScope, ErrorHandlerService, VersionableAPIClass, $http, $q, $timeout, CCCUtils, Moment) {

            /*============ SERVICE DECLARATION ============*/

            var ActivationsAPIService = new VersionableAPIClass({id: 'activations', resource: 'activations'});


            /*============ SERVICE DEFINITION ============*/

            ActivationsAPIService.createActivation = function (activationData_in) {

                return $http.post(ActivationsAPIService.getBasePath(), activationData_in).then(function (results) {

                    return results;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            ActivationsAPIService.updateActivation = function (activationData_in) {

                return $http.put(ActivationsAPIService.getBasePath(), activationData_in).then(function (results) {

                    return results;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            ActivationsAPIService.getActivation = function (activationId) {

                return $http.get(ActivationsAPIService.getBasePath() + "/" +activationId).then(function (results) {

                    return results;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            ActivationsAPIService.authorizeActivation = function (activationData_in) {

                return $http.put(ActivationsAPIService.getBasePath() + '/' + activationData_in.id + '/authorize',  activationData_in.proctorCode).then(function (results) {

                    return results;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['400', '403', '405', '429']);
                });
            };

            ActivationsAPIService.authorizeActivationWithoutPasscode = function (activationData_in) {

                return $http.put(ActivationsAPIService.getBasePath() + '/' + activationData_in.id + '/authorize', 'nopasscode').then(function (results) {

                    return results;

                }, function (err) {

                    // a 403 is actually what we want to get if they are denied access without entering a passcode so tell the ErrorHandlerService to not handle them and pass them along
                    return ErrorHandlerService.reportServerError(err, ['403']);

                }).then(function () {

                    return true;

                }, function (err) {
                    return false;
                });
            };

            ActivationsAPIService.startActivation = function (activationData_in) {

                return $http.post(ActivationsAPIService.getBasePath() + '/' + activationData_in.id + '/launch').then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['403', '500']);
                });
            };

            ActivationsAPIService.startActivationWithVersion = function (activationId, version) {

                return $http.post(ActivationsAPIService.getBasePath() + '/' + activationId + '/launch/paper', {}, {params: {version: version}}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['403', '500']);
                });
            };

            ActivationsAPIService.startPaperActivation = function (activationData_in) {

                return $http.post(ActivationsAPIService.getBasePath() + '/' + activationData_in.id + '/launch/paper').then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['403', '500']);
                });
            };

            ActivationsAPIService.pauseActivationByAssessmentSessionId = function (assessmentSessionId) {

                return $http.put(ActivationsAPIService.getBasePath() + '/pause', {}, {
                    params: {assessmentSessionId: assessmentSessionId}
                }).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            ActivationsAPIService.getActivationsForStudent = function (data_in) {

                return $http.get(ActivationsAPIService.getBasePath() + "/mine/all").then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });

            };

            ActivationsAPIService.cancelActivation = function (activationId, reason) {

                return $http.put(ActivationsAPIService.getBasePath() + '/' + activationId + '/cancellation', {}, {params: {reason: reason}}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            ActivationsAPIService.reactivateActivation = function (activationId) {

                return $http.put(ActivationsAPIService.getBasePath() + '/' + activationId + '/reactivate').then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return ActivationsAPIService;

        }
    ]);

})();

