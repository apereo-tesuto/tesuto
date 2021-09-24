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
     * All API calls associated with students
     */

    angular.module('CCC.API.Students').service('StudentsAPIService', [

        '$rootScope',
        '$q',
        '$http',
        '$timeout',
        '$location',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'StudentClass',
        'FakeData',
        'CCCUtils',

        function ($rootScope, $q, $http, $timeout, $location, ErrorHandlerService, VersionableAPIClass, StudentClass, FakeData, CCCUtils) {

            /*============ SERVICE DECLARATION ============*/

            var StudentsAPIService = new VersionableAPIClass({id: 'students', resource: 'students'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ PUBLIC METHODS ============*/

            StudentsAPIService.studentListSearch =  function (cccIdList) {

                if (cccIdList && cccIdList.length) {

                    // a flag to attempt the real search service
                    if ($location.search()['dev'] !== 'true') {

                        return $http.post(StudentsAPIService.getBasePath() + '/search', {

                            cccids: cccIdList

                        }).then(function (results) {
                            return CCCUtils.coerce(StudentClass, results.data);
                        });

                    } else {

                        return FakeData.studentListSearch(cccIdList).then(function (students) {
                            return CCCUtils.coerce(StudentClass, students);
                        });
                    }

                // if no cccIdList is given resolve empty student list immediatly
                } else {
                    return $q.when([]);
                }
            };

            StudentsAPIService.studentSearch =  function (requestData_in) {

                var cccids = [];
                if (requestData_in.cccId) {
                    cccids.push(requestData_in.cccId);
                }

                var requestData = {
                    cccids: cccids,
                    age: requestData_in.age,
                    firstName: requestData_in.firstName,
                    lastName: requestData_in.lastName,
                    middleName: requestData_in.middleInitial,
                    email: requestData_in.email,
                    phone: requestData_in.phone || ''
                };

                // force phone format to have hyphens (TODO: move this logic to the server)
                requestData.phone = requestData.phone.replace(/\D/g,'');
                if (requestData.phone && requestData.phone.length === 10) {
                    requestData.phone = requestData.phone.replace(/(\d\d\d)(\d\d\d)(\d\d\d\d)/, '$1-$2-$3');
                }

                // remove any empty, undefined, or null fields
                requestData = CCCUtils.cleanObject(requestData);

                // a flag to attempt the real search service
                if ($location.search()['dev'] !== 'true') {

                    return $http.post(StudentsAPIService.getBasePath() + '/search', requestData).then(function (results) {
                        return CCCUtils.coerce(StudentClass, results.data);
                    });

                } else {

                    return FakeData.studentSearch(requestData).then(function (students) {
                        return CCCUtils.coerce(StudentClass, students);
                    });
                }
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return StudentsAPIService;

        }
    ]);

})();

