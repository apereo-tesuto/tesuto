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
     * Remote Proctor Assistant
     */

    angular.module('CCC.View.RemoteProctor').service('RemoteProctorService', [

        '$rootScope',
        'RemoteProctorAPIService',
        'SESSION_CONFIGS',

        function ($rootScope, RemoteProctorAPIService, SESSION_CONFIGS) {

            /*============ SERVICE DECLARATION ============*/

            var RemoteProctorService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            // objects expected from remoteProctorView.jsp
            var remoteProctor = {
                user: SESSION_CONFIGS.user ? JSON.parse(SESSION_CONFIGS.user) : null,
                testEvent: SESSION_CONFIGS.testEvent ? JSON.parse(SESSION_CONFIGS.testEvent) : null,
                assessments: SESSION_CONFIGS.assessments ? JSON.parse(SESSION_CONFIGS.assessments) : null
            };

            var coerceTestEventMetaData = function () {

                // mimic the metaData property added in RemoteEventService.attachAllMetaData()
                remoteProctor.testEvent.metaData = {
                    assessments: _.map(remoteProctor.assessments, function (assessment) {
                        return {title: assessment};
                    }),
                    assessmentsLoading: false,
                    college: remoteProctor.user.colleges[0],
                    hasStudentsLoadingError: false,
                    location: remoteProctor.user.colleges[0].testLocations[0],
                    locationLoading: false,
                    students: [],
                    studentsLoading: false
                };
            };

            var coerceStudentData = function (activations) {

                var students = [];

                // grab the student from each activation
                _.each(activations, function(activation) {
                    students.push(activation.student);
                });

                // return a duplicate-free student list
                students = _.uniq(students, function(student, key, cccId) {
                    return student.cccId;
                });

                // TODO: backend needs to fix middleName / middleInitial switchup
                _.each(students, function (student) {
                    student.middleInitial = student.middleName;
                });

                // populate the testEvent student list
                remoteProctor.testEvent.metaData.students = students;
            };

            var populateStudentList = function () {

                remoteProctor.testEvent.metaData.studentsLoading = true;

                RemoteProctorAPIService.getActivations(remoteProctor.testEvent.testEventId).then(function (activations) {

                    coerceStudentData(activations);

                }, function (err) {

                    remoteProctor.testEvent.metaData.hasStudentsLoadingError = true;

                }).finally(function () {

                    remoteProctor.testEvent.metaData.studentsLoading = false;
                });
            };


            /*============ SERVICE DEFINITION ============*/

            RemoteProctorService = {

                getTestEvent: function () {
                    populateStudentList();
                    return remoteProctor.testEvent;
                }

            };


            /*============ LISTENERS ============*/

            /*============ INITIALIZATION ============*/

            coerceTestEventMetaData();


            /*============ SERVICE PASSBACK ============*/

            return RemoteProctorService;

        }
    ]);

})();
