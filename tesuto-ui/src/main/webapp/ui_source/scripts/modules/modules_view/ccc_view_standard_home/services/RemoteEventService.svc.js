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

    angular.module('CCC.View.Home').service('RemoteEventService', [

        '$q',
        '$timeout',
        'CCCUtils',
        'CurrentUserService',
        'EventsAPIService',
        'StudentsAPIService',
        'AssessmentsAPIService',

        function ($q, $timeout, CCCUtils, CurrentUserService, EventsAPIService, StudentsAPIService, AssessmentsAPIService) {

            /*============ SERVICE DECLARATION ============*/

            var RemoteEventService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var ensureMetaData = function (remoteEvent) {
                if (!remoteEvent.metaData) {
                    remoteEvent.metaData = {};
                }
            };

            var addMetaDataToRemoteEvent = function (remoteEvent, metaDataKey, metaDataValue) {

                ensureMetaData(remoteEvent);

                remoteEvent.metaData[metaDataKey] = metaDataValue;
                return remoteEvent.metaData[metaDataKey];
            };

            var getCollegesAndLocations = function (remoteEvent) {

                addMetaDataToRemoteEvent(remoteEvent, 'locationLoading', true);

                var locationPromises = [
                    CurrentUserService.getCollege(remoteEvent.collegeId),
                    CurrentUserService.getLocation(remoteEvent.testLocationId)
                ];

                return $q.all(locationPromises).finally(function () {
                    addMetaDataToRemoteEvent(remoteEvent, 'locationLoading', false);
                });
            };

            var getAssessements = function (remoteEvent) {

                addMetaDataToRemoteEvent(remoteEvent, 'assessmentsLoading', true);

                return AssessmentsAPIService.getAssessments().then(function (assessments) {

                    return _.filter(assessments, function (assessment) {
                        return _.find(remoteEvent.assessmentIdentifiers, function (identifierInfo) {
                            return identifierInfo.identifier === assessment.identifier && identifierInfo.namespace === assessment.namespace;
                        });
                    });

                }).finally(function () {
                    addMetaDataToRemoteEvent(remoteEvent, 'assessmentsLoading', false);
                });
            };

            var getStudents = function (remoteEvent) {

                addMetaDataToRemoteEvent(remoteEvent, 'studentsLoading', true);
                addMetaDataToRemoteEvent(remoteEvent, 'hasStudentsLoadingError', false);

                return EventsAPIService.getActivationsByEventId(remoteEvent.testEventId).then(function (activations) {

                    var activeActivations = _.filter(activations, function (activation) {
                        return activation.status !== 'DEACTIVATED';
                    });

                    var studentList = _.map(activeActivations, function (activation) {
                        return activation.userId;
                    });

                    studentList = _.unique(studentList);

                    return StudentsAPIService.studentListSearch(studentList).then(function (students) {

                        return students;

                    }, function () {

                        addMetaDataToRemoteEvent(remoteEvent, 'hasStudentsLoadingError', true);
                        return $q.reject();

                    }).finally(function () {
                        addMetaDataToRemoteEvent(remoteEvent, 'studentsLoading', false);
                    });
                });
            };


            /*============ SERVICE DEFINITION ============*/

            RemoteEventService = {

                attachLocationMetaData: function (remoteEvent, refresh) {

                    ensureMetaData(remoteEvent);

                    if (!refresh && remoteEvent.metaData && remoteEvent.metaData.location) {

                        return $q.when(remoteEvent.metaData.location);

                    } else {

                        return getCollegesAndLocations(remoteEvent).then(function (collegeAndLocationData) {

                            // since this item is cloneable, let's not attache the references to test locations because there are references back to the college objects by using objectWithoutProperties
                            addMetaDataToRemoteEvent(remoteEvent, 'college', CCCUtils.objectWithoutProperties(collegeAndLocationData[0], ['testLocations']));
                            addMetaDataToRemoteEvent(remoteEvent, 'location', collegeAndLocationData[1] ? CCCUtils.objectWithoutProperties(collegeAndLocationData[1], ['college']) : {name: '(location not found)'});

                            return remoteEvent.metaData;
                        });
                    }
                },

                attachAssessmentMetaData: function (remoteEvent, refresh) {

                    ensureMetaData(remoteEvent);

                    if (!refresh && remoteEvent.metaData && remoteEvent.metaData.assessments) {

                        return $q.when(remoteEvent.metaData.assessments);

                    } else {

                        return getAssessements(remoteEvent).then(function (assessments) {
                            return addMetaDataToRemoteEvent(remoteEvent, 'assessments', assessments);
                        });
                    }
                },

                attachStudentMetaData: function (remoteEvent, refresh) {

                    ensureMetaData(remoteEvent);

                    if (!refresh && remoteEvent.metaData && remoteEvent.metaData.students) {

                        return $q.when(remoteEvent.metaData.students);

                    } else {

                        return getStudents(remoteEvent).then(function (students) {
                            return addMetaDataToRemoteEvent(remoteEvent, 'students', students);
                        });
                    }
                },

                attachAllMetaData: function (remoteEvent, refresh) {

                    return $q.all([
                        RemoteEventService.attachLocationMetaData(remoteEvent, refresh),
                        RemoteEventService.attachAssessmentMetaData(remoteEvent, refresh),
                        RemoteEventService.attachStudentMetaData(remoteEvent, refresh)
                    ]);
                }
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return RemoteEventService;

        }
    ]);

})();

