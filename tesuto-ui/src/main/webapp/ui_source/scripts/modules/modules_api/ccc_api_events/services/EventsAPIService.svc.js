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

    angular.module('CCC.API.Events').service('EventsAPIService', [

        '$http',
        '$q',
        '$timeout',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'CCCUtils',

        function ($http, $q, $timeout, ErrorHandlerService, VersionableAPIClass, CCCUtils) {

            /*============ SERVICE DECLARATION ============*/

            var EventsAPIService = new VersionableAPIClass({id: 'testEvent',  resource: 'test-event'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            EventsAPIService.getEventsByCollegeId = function (collegeId) {

                return $http.get(EventsAPIService.getBasePath() + '/college/' + collegeId, {}).then(function (results) {

                    return CCCUtils.coerce('EventClass', results.data);

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            EventsAPIService.getActivationsByEventId = function (eventId) {

                return $http.get(EventsAPIService.getBasePath() + '/' + eventId + '/activations', {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            EventsAPIService.create = function (testEventData) {

                return $http.post(EventsAPIService.getBasePath(), testEventData).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            EventsAPIService.update = function (testEventData) {

                return $http.put(EventsAPIService.getBasePath() + '/' + testEventData.testEventId, testEventData, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            EventsAPIService.cancelEvent = function (eventId) {

                return $http.put(EventsAPIService.getBasePath() + '/' + eventId + '/cancel', {}, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            EventsAPIService.updateStudents = function (testEventId, studentIds) {

                return $http.put(EventsAPIService.getBasePath() + '/' + testEventId + '/students', studentIds, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            EventsAPIService.creationEmail = function (testEventId) {

                return $http.put(EventsAPIService.getBasePath() + '/' + testEventId + '/creation-email', {}, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            // More semantic alias for creationEmail
            EventsAPIService.resendEmail = EventsAPIService.creationEmail;

            EventsAPIService.cancellationEmail = function (testEventId) {

                return $http.put(EventsAPIService.getBasePath() + '/' + testEventId + '/cancellation-email', {}, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            EventsAPIService.resetPasscode = function (testEventId) {

                return $http.post(EventsAPIService.getBasePath() + '/' + testEventId + '/passcode', {}, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return EventsAPIService;
        }
    ]);

})();

