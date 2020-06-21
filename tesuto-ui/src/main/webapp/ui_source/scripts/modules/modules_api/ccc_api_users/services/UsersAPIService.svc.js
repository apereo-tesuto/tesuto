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
     * All API calls associated with system users
     */

    angular.module('CCC.API.Users').service('UsersAPIService', [

        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'CCCUtils',
        '$timeout',
        '$q',

        function ($http, ErrorHandlerService, VersionableAPIClass, CCCUtils, $timeout, $q) {

            /*============ SERVICE DECLARATION ============*/

            var UsersAPIService = new VersionableAPIClass({id: 'user', resource: 'user'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ PUBLIC METHODS ============*/

            /**
             * Get a list of users that match the given search params, several are optional
             * @param  {object} searchParams_in pass in optional search params firstName, lastName, collegeId (which is a list of college ids)
             * @param  boolean  useLeanModel if passing in true, will remove properties not needed for viewing and editing
             * @return {promise}                 Promise resolves to an array of UserClass
             */
            UsersAPIService.userListSearch =  function (searchParams_in, useLeanModel) {

                var searchParams = {};
                if (searchParams_in.firstName) {
                    searchParams.firstName = searchParams_in.firstName;
                }
                if (searchParams_in.lastName) {
                    searchParams.lastName = searchParams_in.lastName;
                }
                if (searchParams_in.collegeId) {
                    searchParams.collegeId = searchParams_in.collegeId;
                }
                if (useLeanModel) {
                    searchParams.projection = 'slim';
                }

                return $http.get(UsersAPIService.getBasePath() + '/search', {

                    params: searchParams

                }).then(function (results) {

                    // if we get the lean projection of the data then don't use the class, just return the raw data... used for big search results etc...
                    if (useLeanModel) {
                        return results.data;
                    } else {
                        return CCCUtils.coerce('UserClass', results.data);
                    }

                }, function (err) {

                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            /**
             * Creates a user with the given userData
             * @param  {object} userData See UserClass.serialize method for the format (or check swagger)
             * @return {promise}          Promise which resolves to server return data
             */
            UsersAPIService.createUser = function (userData) {

                return $http.post(UsersAPIService.getBasePath(), userData).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['400']);
                });
            };

            UsersAPIService.updateUser = function (userData) {

                return $http.put(UsersAPIService.getBasePath() + '/' + userData.user.userAccountId, userData).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['400']);
                });
            };

            UsersAPIService.deleteUser = function (userData) {

                return $http.delete(UsersAPIService.getBasePath() + '/' + userData.user.userAccountId).then(function (results) {

                    return results;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['400']);
                });
            };

            UsersAPIService.getUsersByCollege = function (collegeId) {
                return $http.get(UsersAPIService.getBasePath() + '/college/' + collegeId).then(function (results) {

                    return CCCUtils.coerce('UserClass', results.data);

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            UsersAPIService.getUsersByTestLocation = function (testLocationId) {
                return $http.get(UsersAPIService.getBasePath() + '/test-location/' + testLocationId).then(function (results) {

                    return CCCUtils.coerce('UserClass', results.data);

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            UsersAPIService.getUserById = function (userAccountId) {
                return $http.get(UsersAPIService.getBasePath() + '/' + userAccountId).then(function (results) {

                    return CCCUtils.coerce('UserClass', results.data);

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return UsersAPIService;
        }
    ]);

})();

