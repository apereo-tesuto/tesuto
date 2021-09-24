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
     * All API calls associated with general roles
     */

    angular.module('CCC.API.Roles').service('RolesAPIService', [

        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'CurrentUserService',

        function ($http, ErrorHandlerService, VersionableAPIClass, CurrentUserService) {

            /*============ SERVICE DECLARATION ============*/

            var RolesAPIService = new VersionableAPIClass({id: 'roles', resource: 'roles'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var allRolesPromise;
            var getAllRolesPromise = function () {

                if (allRolesPromise) {

                    return allRolesPromise;

                } else {

                    allRolesPromise = $http.get(RolesAPIService.getBasePath(), {}).then(function (results) {

                        return results.data;

                    }, function (err) {
                        return ErrorHandlerService.reportServerError(err, []);
                    });

                    return allRolesPromise;
                }
            };


            /*============ SERVICE DEFINITION ============*/

            RolesAPIService.getAllRoles = function () {

                return getAllRolesPromise().then(function (roles) {

                    if (_.isArray(roles)) {

                        if(!CurrentUserService.hasPermission('ASSIGN_STUDENT_ROLE')) {
                            var studentRoleIndex = roles.map(function (obj) {
                                return obj.groupName;
                            }).indexOf('STUDENT');
                            if (studentRoleIndex !== -1) {
                                roles.splice(studentRoleIndex, 1);
                            }
                        }

                        var developerRoleIndex = roles.map(function (obj) {
                            return obj.groupName;
                        }).indexOf('SUPER_ADMIN');
                        if (developerRoleIndex !== -1) {
                            roles.splice(developerRoleIndex, 1);
                        }

                        var operationsRoleIndex = roles.map(function (obj) {
                            return obj.groupName;
                        }).indexOf('OPERATIONS');
                        if (operationsRoleIndex !== -1) {
                            roles.splice(operationsRoleIndex, 1);
                        }

                        return angular.copy(roles);
                    } else {
                        return [];
                    }
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return RolesAPIService;

        }
    ]);

})();

