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
var SeededRandom = SeededRandom || {};
var FORCE_SERVER_ERROR = FORCE_SERVER_ERROR || false;

(function () {

    /**
     * All API calls associated with activations
     */

    angular.module('CCC.API.FakeData').service('FakeData', [

        '$q',
        '$http',
        '$timeout',
        'CCCUtils',
        'Moment',
        'ErrorHandlerService',

        function ($q, $http, $timeout, CCCUtils, Moment, ErrorHandlerService) {

            /*============ SERVICE DECLARATION ============*/

            var FakeDataService = {};


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            // random seed for fake data
            var seed = 1;
            var getNewSeededRandom = function () {
                seed++;
                return new SeededRandom(seed);
            };

            var getRandomItem = function (array_in, seededRandom) {
                if (!seededRandom) {
                    seededRandom = Math;
                }
                var randomIndex = Math.round(seededRandom.random() * (array_in.length-1));
                return array_in[randomIndex];
            };

            var getActivationsForLocationFake = function () {

                var deferred = $q.defer();

                var seededRandom = getNewSeededRandom();
                var activations = [];

                var studentOptions = [

                    {
                        firstName: 'Larry',
                        lastName: 'Local',
                        displayName: 'Larry Local',
                        middleInitial: 'L',
                        preferredName: 'llocal',
                        age: 45,
                        email: 'llocal@local.com',
                        phone: '9918276152',
                        cccId: 'A123456',
                        collegeStatuses: {
                            "ZZ1": 1
                        }
                    },
                    {
                        firstName: 'Amanda',
                        lastName: 'Local',
                        displayName: 'Amanda Local',
                        middleInitial: 'A',
                        age: 45,
                        email: 'alocal@democollege.edu',
                        phone: '9918276152',
                        cccId: 'A123457',
                        collegeStatuses: {
                            "ZZ1": 1
                        }
                    },
                    {
                        firstName: 'Nancy',
                        lastName: 'Local',
                        displayName: 'Nancy Local',
                        middleInitial: 'A',
                        age: 45,
                        email: 'nlocal@democollege.edu',
                        phone: '9918276152',
                        cccId: 'A123458',
                        collegeStatuses: {
                            "ZZ3": 1
                        }
                    },
                    {
                        firstName: 'Chris',
                        lastName: 'Local',
                        displayName: 'Chris Local',
                        middleInitial: 'A',
                        age: 45,
                        email: 'nlocal@democollege.edu',
                        phone: '9918276152',
                        cccId: 'A123459',
                        collegeStatuses: {
                            "ZZ1": 1
                        }
                    },
                    {
                        firstName: 'Valid',
                        lastName: 'Local',
                        displayName: 'Valid Local',
                        middleInitial: 'A',
                        age: 45,
                        email: 'nlocal@democollege.edu',
                        phone: '9918276152',
                        cccId: 'A123460',
                        collegeStatuses: {
                            "ZZ1": 1
                        }
                    },
                    {
                        firstName: 'Amber',
                        lastName: 'Ambiguous',
                        displayName: 'Amber Ambiguous',
                        middleInitial: 'A',
                        preferredName: 'aambiguous',
                        dateOfBirth: 708937200000,
                        age: 25,
                        email: 'ambera@gmail.com',
                        phone: '7651827365',
                        cccId: 'C100000'
                    },
                    {
                        firstName: 'Amber',
                        lastName: 'Ambiguous',
                        displayName: 'Amber Ambiguous',
                        middleInitial: 'B',
                        preferredName: 'aambiguous',
                        dateOfBirth: 708937200000,
                        age: 25,
                        email: 'amberb@gmail.com',
                        phone: '8176253928',
                        cccId: 'C100001'
                    },
                    {
                        firstName: 'Amber',
                        lastName: 'Ambiguous',
                        displayName: 'Amber Ambiguous',
                        middleInitial: '',
                        preferredName: 'aambiguous',
                        dateOfBirth: 708937200000,
                        age: 22,
                        email: 'amberb@yahoo.com',
                        phone: '9187263524',
                        cccId: 'C100002'
                    },
                    {
                        firstName: 'Abril',
                        lastName: 'Ambiguous',
                        displayName: 'Abril Ambiguous',
                        middleInitial: '',
                        preferredName: 'aambiguous',
                        dateOfBirth: 708937200000,
                        age: 19,
                        email: 'abril@yahoo.com',
                        phone: '9198476253',
                        cccId: 'C100003'
                    },
                    {
                        firstName: 'Amber',
                        lastName: 'Ambiguous',
                        displayName: 'Amber Ambiguous',
                        middleInitial: '',
                        preferredName: 'aambiguous',
                        dateOfBirth: 708937200000,
                        age: 65,
                        email: 'goldengirl@yahoo.com',
                        phone: '8179277162',
                        cccId: 'C100004'
                    },
                    {
                        firstName: 'Amber',
                        lastName: 'Ambiguous',
                        displayName: 'Amber Ambiguous',
                        middleInitial: 'Z',
                        preferredName: 'aambiguous',
                        dateOfBirth: 708937200000,
                        age: 25,
                        email: 'nottheamber@uwerelookingfor.com',
                        phone: '6548176253',
                        cccId: 'C100005'
                    }
                ];

                var getRandomStudent = function () {
                    return getRandomItem(studentOptions, seededRandom);
                };

                var assessmentOptions = [
                    {
                        id: 1,
                        title: 'Math'
                    },
                    {
                        id: 2,
                        title: 'English'
                    },
                    {
                        id: 3,
                        title: 'ELL'
                    }
                ];
                var getRandomAssessment = function () {
                    return getRandomItem(assessmentOptions, seededRandom);
                };

                var statusOptions = ['READY', 'IN_PROGRESS', 'COMPLETE'];
                var getRandomStatus = function () {
                    return getRandomItem(statusOptions, seededRandom);
                };

                var activationId = 0;
                var getRandomActivation = function () {

                    activationId++;
                    var student = getRandomStudent();
                    var assessment = getRandomAssessment();

                    return {

                        id: activationId,

                        studentCCCID: student.cccId,
                        studentFirstName: student.firstName,
                        studentLastName: student.lastName,

                        assessmentId: assessment.id,
                        assessmentTitle: assessment.title,

                        status: getRandomStatus(),

                        timeElapsed: Math.round(Math.random() * 1000000)
                    };
                };

                var totalActivations = Math.round(seededRandom.random() * 100);

                for (var i=0; i < totalActivations; i++) {
                    activations.push(getRandomActivation());
                }

                $timeout(function () {
                    deferred.resolve(activations);
                }, Math.round(Math.random() * 2000) + 500);

                return deferred.promise;
            };


            var fakeActivations = [
                {
                    id: '147468',
                    assessmentTitle: 'English Placement',
                    status: 'READY',
                    endDate: new Moment().utc().add(6, 'h').toISOString(),
                    completedDate: null
                },
                {
                    id: '147469',
                    assessmentTitle: 'Math Placement',
                    status: 'READY',
                    endDate: new Moment().utc().add(34, 'd').toISOString(),
                    completedDate: null
                }
            ];

            var getActivationsForStudentFake = function () {

                var deferred = $q.defer();

                $timeout(function () {

                    deferred.resolve(fakeActivations);

                }, Math.round(Math.random() * 2000) + 500);

                return deferred.promise;
            };

            var fakeTestingLocations = [
                {
                    location: 'Test Center A',
                    activations: 73,
                    activationDetails: {
                        ready: 25,
                        inProgress: 42,
                        complete: 6
                    }
                },
                {
                    location: 'Test Center B',
                    activations: 34,
                    activationDetails: {
                        ready: 11,
                        inProgress: 15,
                        complete: 8
                    }
                },
                {
                    location: 'Test Center C',
                    activations: 44,
                    activationDetails: {
                        ready: 9,
                        inProgress: 25,
                        complete: 10
                    }
                },
                {
                    location: 'Test Center D',
                    activations: 29,
                    activationDetails: {
                        ready: 7,
                        inProgress: 14,
                        complete: 8
                    }
                },
                {
                    location: 'Test Center E',
                    activations: 56,
                    activationDetails: {
                        ready: 25,
                        inProgress: 18,
                        complete: 3
                    }
                }
            ];

            var getFakeTestingLocations = function () {

                var deferred = $q.defer();

                $timeout(function () {

                    deferred.resolve(fakeTestingLocations);

                }, Math.round(Math.random() * 2000) + 500);

                return deferred.promise;
            };

            var getActivationAuthorizationFake = function () {

                var deferred = $q.defer();

                $timeout(function () {

                    deferred.resolve({authorized: 'true'});

                }, Math.round(Math.random() * 1000) + 500);

                return deferred.promise;
            };

            var getActivationStartFake = function (activationData) {

                var deferred = $q.defer();

                var activation = _.find(fakeActivations, {id: activationData.id});
                activation.status = 'IN_PROGRESS';

                $timeout(function () {

                    deferred.resolve(147468);

                }, Math.round(Math.random() * 1000) + 500);

                return deferred.promise;
            };

            var getFakeActivationById = function (activationData) {

                var deferred = $q.defer();
                var activation = _.find(fakeActivations, {id: activationData.id});

                activation.status = 'COMPLETE';
                activation.completedDate = new Moment().utc().toISOString();

                $timeout(function () {
                    deferred.resolve(activation);
                }, 1000);

                return deferred.promise;
            };

            var randomStudents = [
                {
                    firstName: 'Sammy',
                    lastName: 'Samuelson',
                    displayName: 'Sammy Samuelson',
                    middleName: 'Muelson',
                    preferredName: 'Samurai',
                    dateOfBirth: 708937200000,
                    age: 19,
                    email: 'sammyrules@gmail.com',
                    phone: '718-723-9876',
                    cccId: "A123457"
                },
                {
                    firstName: 'John',
                    lastName: 'Johnson',
                    displayName: 'John Johnson',
                    middleName: 'Johnny',
                    preferredName: 'jjohnson',
                    dateOfBirth: 708937200000,
                    age: 22,
                    email: 'jjohnson@yahoo.com',
                    phone: '718-918-6152',
                    cccId: "A123458"
                },
                {
                    firstName: 'Robert',
                    lastName: 'Robertson',
                    displayName: 'Robert Robertson',
                    middleName: 'Robby',
                    preferredName: 'rrobertson',
                    birthMonth: 708937200000,
                    age: 35,
                    email: 'robby123@gmail.com',
                    phone: '909-718-2634',
                    cccId: "A123459"
                },
                {
                    firstName: 'Sarah',
                    lastName: 'Sahara',
                    displayName: 'Sarah Sahara',
                    middleName: 'Sandy',
                    preferredName: 'ssahara',
                    dateOfBirth: 708937200000,
                    age: 23,
                    email: 'sandcastlesarah@hotmail.com',
                    phone: '817-6251-9827',
                    cccId: "A123460"
                },
                {
                    firstName: 'Patricia',
                    lastName: 'Patriot',
                    displayName: 'Patricia P',
                    middleName: 'Patty',
                    preferredName: 'ppatriot',
                    dateOfBirth: 708937200000,
                    age: 24,
                    email: 'ppatriot@us.gov',
                    phone: '817-625-1827',
                    cccId: 'AAA4319'
                },
                {
                    firstName: 'Larry',
                    lastName: 'Local',
                    displayName: 'Larry Local',
                    middleName: 'Lawrence',
                    preferredName: 'llocal',
                    dateOfBirth: 708937200000,
                    age: 45,
                    email: 'llocal@local.com',
                    phone: '991-827-6152',
                    cccId: 'A123456',
                    collegeStatuses: {
                        "ZZ1": 1,
                        "ZZ2": 1,
                        "71": 0,
                        "61": 1,
                        "72": 2,
                        "73": 1
                    }
                },
                {
                    firstName: 'Amberamberamber',
                    lastName: 'Ambiguous',
                    displayName: 'Amber Ambiguous',
                    middleName: 'Always',
                    preferredName: 'aambiguous',
                    dateOfBirth: 708937200000,
                    age: 25,
                    email: 'ambera@gmail.com',
                    phone: '765-182-7365',
                    cccId: "A123460"
                },
                {
                    firstName: 'Amber',
                    lastName: 'Ambiguous',
                    displayName: 'Amber Ambiguous',
                    middleName: 'Brittany',
                    preferredName: 'aambiguous',
                    dateOfBirth: 708937200000,
                    age: 25,
                    email: 'amberb@gmail.com',
                    phone: '817-625-3928',
                    cccId: "AAA9643"
                },
                {
                    firstName: 'Amber',
                    lastName: 'Ambiguous',
                    displayName: 'Amber Ambiguous',
                    middleName: '',
                    preferredName: 'aambiguous',
                    dateOfBirth: 708937200000,
                    age: 22,
                    email: 'amberb@yahoo.com',
                    phone: '918-726-3524',
                    cccId: "AAA8345"
                },
                {
                    firstName: 'Abril',
                    lastName: 'Ambiguous',
                    displayName: 'Abril Ambiguous',
                    middleName: '',
                    preferredName: 'aambiguous',
                    dateOfBirth: 708937200000,
                    age: 19,
                    email: 'abril@yahoo.com',
                    phone: '919-847-6253',
                    cccId: "AAA8346"
                },
                {
                    firstName: 'Amber',
                    lastName: 'Ambiguous',
                    displayName: 'Amber Ambiguous',
                    middleName: '',
                    preferredName: 'aambiguous',
                    dateOfBirth: 708937200000,
                    age: 65,
                    email: 'aperson@yahoo.com',
                    phone: '927-7162',
                    cccId: "AAA8347"
                },
                {
                    firstName: 'Amber',
                    lastName: 'Ambiguous',
                    displayName: 'Amber Ambiguous',
                    middleName: 'Zelda',
                    preferredName: 'aambiguous',
                    dateOfBirth: 708937200000,
                    age: 25,
                    email: 'nottheamber@uwerelookingfor.com',
                    cccId: "AAA8349"
                },
                {
                    firstName: '',
                    lastName: 'Ambiguous',
                    displayName: 'Ambiguous',
                    middleName: '',
                    preferredName: 'aambiguous',
                    dateOfBirth: 708937200000,
                    age: 25,
                    email: 'someamber@amber.com',
                    phone: '987-654-3127',
                    cccId: "AAA8350"
                },
                {
                    firstName: '',
                    lastName: 'Ambiguous',
                    displayName: 'Ambiguous',
                    middleName: '',
                    preferredName: 'aambiguous',
                    dateOfBirth: 708937200000,
                    age: 25,
                    email: 'someotheramber@amber.com',
                    phone: '876-537-8172',
                    cccId: "AAA8351"
                },
                {
                    firstName: null,
                    lastName: 'Ambiguous',
                    displayName: 'Ambiguous',
                    middleName: null,
                    preferredName: 'aambiguous',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAA8352"
                },
                {
                    firstName: 'Ernie',
                    lastName: 'Bert',
                    displayName: 'Bert',
                    middleName: null,
                    preferredName: 'Ernie',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAA8353"
                },
                {
                    firstName: 'Will',
                    lastName: 'Smith',
                    displayName: 'Will',
                    middleName: null,
                    preferredName: 'Will Smith',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAP2174"
                },
                {
                    firstName: 'Bill',
                    lastName: 'Smith',
                    displayName: 'Bill',
                    middleName: null,
                    preferredName: 'Bill Smith',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAP1600"
                },
                {
                    firstName: 'Gary',
                    lastName: 'Corbin',
                    displayName: 'Gary',
                    middleName: null,
                    preferredName: 'Gary',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAP1601"
                },
                {
                    firstName: 'Nichole',
                    lastName: 'Du Bose',
                    displayName: 'Nichole',
                    middleName: null,
                    preferredName: 'Nichole',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAP1602"
                },
                {
                    firstName: 'James',
                    lastName: 'Cota',
                    displayName: 'James',
                    middleName: null,
                    preferredName: 'Cota',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAP1603"
                },
                {
                    firstName: 'Scott',
                    lastName: 'Bear',
                    displayName: 'Scott',
                    middleName: null,
                    preferredName: 'Scott',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAP1604"
                },
                {
                    firstName: 'David',
                    lastName: 'Parker',
                    displayName: 'David',
                    middleName: null,
                    preferredName: 'David',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAP1605"
                },
                {
                    firstName: 'Chris',
                    lastName: 'Jones',
                    displayName: 'Chris',
                    middleName: null,
                    preferredName: 'CJ',
                    dateOfBirth: 708937200000,
                    age: null,
                    email: null,
                    phone: null,
                    cccId: "AAP1606"
                }
            ];

            var studentSearch = function (requestData) {
                var deferred = $q.defer();

                var students = [];

                if (requestData.cccids && requestData.cccids.length) {

                    var cccidToFind = requestData.cccids[0];

                    students = _.filter(randomStudents, function (student) {
                        return $.trim(student.cccId + '') === $.trim(cccidToFind + '');
                    });

                } else {

                    students = _.filter(randomStudents, function (student) {
                        var matches = true;

                        if (requestData.firstName) {
                            matches = matches && student.firstName && student.firstName.toLowerCase().indexOf(requestData.firstName.toLowerCase()) !== -1;
                        }

                        if (requestData.lastName) {
                            matches = matches && student.lastName.toLowerCase().indexOf(requestData.lastName.toLowerCase()) !== -1;
                        }

                        if (requestData.middleName) {
                            matches = matches && student.middleName && student.middleName.toLowerCase().indexOf(requestData.middleName.toLowerCase()) !== -1;
                        }

                        if (requestData.age) {
                            matches = matches && (student.age + '') === (requestData.age + '');
                        }

                        if (requestData.phone) {
                            matches = matches && (student.phone + '') === (requestData.phone + '');
                        }

                        if (requestData.email) {
                            matches = matches && student.email && student.email.toLowerCase() === requestData.email.toLowerCase();
                        }

                        return matches;
                    });
                }

                $timeout(function () {
                    deferred.resolve(students);
                }, Math.round(Math.random() * 500) + 300);

                return deferred.promise;
            };

            var studentListSearch = function (studentList) {
                var deferred = $q.defer();

                studentList = studentList || [];

                var students = _.filter(randomStudents, function (student) {
                    return studentList.indexOf(student.cccId) !== -1;
                });

                $timeout(function () {
                    deferred.resolve(students);
                }, Math.round(Math.random() * 500) + 300);

                return deferred.promise;
            };


            /*============ FOR FAKE SYSTEM USERS ============*/

            var userListSearch = function () {
                var deferred = $q.defer();

                $timeout(function () {
                    deferred.resolve(randomStudents);
                }, Math.round(Math.random() * 500) + 300);

                return deferred.promise;
            };


            /*============ FAKE DATA FOR FAKE ASSESSMENT =============*/

            var assessmentTaskCount = -1;

            var fakeAssessmentMap = {
                0: 'ui/data/task_match_examples.json',
                1: 'ui/data/task_choice_examples.json',
                2: 'ui/data/task_choice_with_horiz_examples.json',
                3: 'ui/data/task_text_entry_examples.json',
                4: 'ui/data/task_extended_text_examples.json',
                5: 'ui/data/task_inline_choice_examples.json',
                6: 'ui/data/task_item_with_stimulus.json',
                7: 'ui/data/task_item_bundle_no_stimulus_item_with_stimulus.json',
                8: 'ui/data/task_item_bundle_with_stimulus_single_item.json',
                9: 'ui/data/task_item_bundle_lsi_samples.json',
                10: 'ui/data/task_mathml_within_choice.json',
                11: 'ui/data/task_mathml_stimulus.json'
            };

            var TOTAL_TASKS = 12;
            var fakeAssessmentCache = {};

            var getTaskPromise = function (taskKey) {
                return $http.get(fakeAssessmentMap[taskKey]).then(function (results) {
                    results.data.taskSetIndex = parseInt(taskKey) + 1;
                    fakeAssessmentCache[taskKey] = results.data;
                });
            };

            var reNumberFakeAssessmentItems = function () {

                var taskNum = 1;

                for (var i=0; i < TOTAL_TASKS; i++) {
                    _.each(fakeAssessmentCache[i].tasks, function (task) {
                        _.each(task.itemSessions, function (itemSession) {
                            itemSession.itemSessionIndex = taskNum;
                            taskNum++;
                        });
                    });
                }
            };

            var loadAllFakeTasks = function () {

                var fakeTaskPromises = [];

                for (var key in fakeAssessmentMap) {
                    if (fakeAssessmentMap.hasOwnProperty(key)) {
                        fakeTaskPromises.push(getTaskPromise(key));
                    }
                }

                return $q.all(fakeTaskPromises).then(reNumberFakeAssessmentItems);
            };


            var getAssessmentTask = function (assessmentTaskNum) {

                if (fakeAssessmentCache[assessmentTaskNum]) {

                    return $q.when(fakeAssessmentCache[assessmentTaskNum]);

                } else {

                    // we load them all so we can run through and assigne their itemSessionIndex correctly
                    return loadAllFakeTasks().then(function () {
                        return fakeAssessmentCache[assessmentTaskNum];
                    });
                }
            };

            var updateTaskSetResponses = function (assessmentSessionId, taskSetId, responses) {
                if (FORCE_SERVER_ERROR) {
                    return $q.reject({statusCode: FORCE_SERVER_ERROR});
                } else {
                    return $q.when(true);
                }
            };

            var getCurrentTaskSet = function (assessmentSessionId, currentTaskSetId) {
                return getAssessmentTask(assessmentTaskCount).then(function (taskSet) {
                    return {
                        currentTaskSet: taskSet
                    };
                });
            };

            var getNextTaskSet = function (assessmentSessionId, currentTaskSetId) {
                if (assessmentTaskCount === TOTAL_TASKS - 1) {
                    return $q.when(false);
                } else {
                    assessmentTaskCount++;
                    return getAssessmentTask(assessmentTaskCount);
                }
            };

            var getPreviousTaskSet = function (assessmentSessionId, currentTaskSetId) {
                if (assessmentTaskCount === 0) {
                    return $q.when(false);
                } else {
                    assessmentTaskCount--;
                    return getAssessmentTask(assessmentTaskCount);
                }
            };

            var completeTaskSet = function (assessmentSessionId, taskSetId) {
                if (FORCE_SERVER_ERROR) {

                    // SIMULATE ERROR HANDLING FOR THIS CALL: since the player has it's own modal for completion failures, let's ignore certain error reporting channels
                    ErrorHandlerService.reportServerError({status: FORCE_SERVER_ERROR}, [], ['serverErrorModal', 'timeoutModal', 'notFoundModal']);
                    return $q.reject({status: FORCE_SERVER_ERROR + '', statusCode: FORCE_SERVER_ERROR + ''});

                } else {
                    return $q.when(true);
                }
            };

            var setInitialTaskSetIndex = function (taskSetIndex) {
                assessmentTaskCount = taskSetIndex - 1;
            };

            var getAllFakeTasksItems = function () {
                // we load them all so we can run through and assigne their itemSessionIndex correctly
                return loadAllFakeTasks().then(function () {

                    var taskSets = [];
                    for (var i in fakeAssessmentCache) {
                        if (fakeAssessmentCache.hasOwnProperty(i)) {
                            taskSets.push(fakeAssessmentCache[i]);
                        }
                    }

                    return taskSets;
                });
            };


            var postActivity = function (activityList_in) {
                if (FORCE_SERVER_ERROR) {
                    return $q.reject({statusCode: FORCE_SERVER_ERROR});
                } else {
                    return $q.resolve({data: {success: true}});
                }
            };




            /*================ START FAKE ACTIVATION CREATION ==================*/

            var letters = ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'];

            var getRandomLetter = function () {
                return letters[Math.round(Math.random() * (letters.length - 1))];
            };

            var getRandomName = function () {
                var randomLength = Math.round(Math.random() * 8) + 4;
                var randomString = '';

                for (var i=0; i < randomLength; i++) {
                    if (i === 0) {
                        randomString = getRandomLetter().toUpperCase();
                    } else {
                        randomString = randomString + getRandomLetter();
                    }
                }

                return randomString;
            };

            var generateFakeStudents = function (numberOfStudents) {
                var students = [];
                for (var i=0; i < numberOfStudents; i++) {
                    students.push(CCCUtils.coerce('StudentClass', {
                        cccId: i,
                        email: "email@yahoo.com",
                        firstName: getRandomName(),
                        lastName: getRandomName(),
                        middleInitial: "",
                        middleName: getRandomName(),
                        mobilePhone: "",
                        phone: "9198476253"
                    }));
                }
                return students;
            };

            var getFakeActivations = function (numberOfStudents_in) {
                var fakeActivations = [];
                var TOTAL_STUDENTS = numberOfStudents_in;
                var students = generateFakeStudents(TOTAL_STUDENTS);

                var fakeAssessments = [
                    {
                        assessmentId: {
                            identifier: 1,
                            namespace: 'Developer'
                        },
                        assessmentTitle: 'Mathematics',
                        deliveryType: 'ONLINE'
                    },
                    {
                        assessmentId: {
                            identifier: 2,
                            namespace: 'Developer'
                        },
                        assessmentTitle: 'English Paper Pencil',
                        deliveryType: 'PAPER'
                    },
                    {
                        assessmentId: {
                            identifier: 3,
                            namespace: 'Developer'
                        },
                        assessmentTitle: 'English',
                        deliveryType: 'ONLINE'
                    },
                    {
                        assessmentId: {
                            identifier: 4,
                            namespace: 'Developer'
                        },
                        assessmentTitle: 'ELL',
                        deliveryType: 'ONLINE'
                    }
                ];

                var getFakeAssessments = function (num) {

                    var assessmentList = [];
                    var startingIndex = Math.round(Math.random() * (fakeAssessments.length - 1));

                    for (var j=0; j < num; j++) {
                        assessmentList.push(fakeAssessments[(startingIndex + j) %fakeAssessments.length]);
                    }

                    return assessmentList;
                };

                var statusList = ['READY', 'COMPLETE', "IN_PROGRESS"];
                var getRandomStatus = function () {
                    return statusList[Math.round(Math.random() * (statusList.length - 1))];
                };

                var thisStudent;
                var fakeActivation1;
                var fakeActivation2;
                var fassessments;
                for (var i = 0; i < TOTAL_STUDENTS; i++) {

                    fassessments = getFakeAssessments(2);

                    thisStudent = students[i];

                    fakeActivation1 = CCCUtils.coerce('ActivationClass', {
                        activationId: i * 2,
                        student: thisStudent,
                        assessmentId: fassessments[0].assessmentId,
                        assessmentTitle: fassessments[0].assessmentTitle,
                        deliveryType: fassessments[0].deliveryType,
                        userId: thisStudent.cccId,
                        status: getRandomStatus(),
                        statusChangeHistory: [{changeDate: new Date().getTime()}]
                    });

                    fakeActivation2 = CCCUtils.coerce('ActivationClass', {
                        activationId: i * 2 + 1,
                        student: thisStudent,
                        assessmentId: fassessments[1].assessmentId,
                        assessmentTitle: fassessments[1].assessmentTitle,
                        deliveryType: fassessments[1].deliveryType,
                        userId: thisStudent.cccId,
                        status: getRandomStatus(),
                        statusChangeHistory: [{changeDate: new Date().getTime()}]
                    });

                    fakeActivations.push(fakeActivation1);
                    fakeActivations.push(fakeActivation2);
                }

                return fakeActivations;
            };




            /*================ END FAKE ACTIVATION CREATOIN ===================*/




            /*============ PUBLIC METHODS =============*/

            // api for fake data prototype player
            FakeDataService.updateTaskSetResponses = updateTaskSetResponses;
            FakeDataService.getCurrentTaskSet = getCurrentTaskSet;
            FakeDataService.getNextTaskSet = getNextTaskSet;
            FakeDataService.getPreviousTaskSet = getPreviousTaskSet;
            FakeDataService.completeTaskSet = completeTaskSet;
            FakeDataService.setInitialTaskSetIndex = setInitialTaskSetIndex;

            // activations
            FakeDataService.getActivationsForLocationFake = getActivationsForLocationFake;
            FakeDataService.getFakeActivationById = getFakeActivationById;
            FakeDataService.getActivationAuthorizationFake = getActivationAuthorizationFake;
            FakeDataService.getActivationStartFake = getActivationStartFake;
            FakeDataService.getActivationsForStudentFake = getActivationsForStudentFake;
            FakeDataService.getFakeTestingLocations = getFakeTestingLocations;

            // activity
            FakeDataService.postActivity = postActivity;

            // student search
            FakeDataService.studentSearch = studentSearch;
            FakeDataService.studentListSearch = studentListSearch;

            // system user search
            FakeDataService.userListSearch = userListSearch;

            // assessment print
            FakeDataService.getAllFakeTasksItems = getAllFakeTasksItems;

            // fake activations (used for perf testing ui rendering in proctor dashboard)
            FakeDataService.getFakeActivations = getFakeActivations;


            /*============ SERVICE PASSBACK =============*/

            return FakeDataService;

        }
    ]);

})();
