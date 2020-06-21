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

    angular.module('CCC.View.Home').constant('USER_ROLE_TITLE_MAP', {
        'PAPER_PENCIL_SCORER': 'Paper Pencil Scorer',
        'COUNSELOR': 'Counselor',
        'PROCTOR': 'Proctor',
        'LOCAL_ADMIN': 'Local Admin',
        'FACULTY': 'Faculty',
        'PLACEMENT_MANAGER': 'Placement Manager',
        'PLACEMENT_READ_ONLY': 'Placement Read Only',
        'REMOTE_PROCTORING_MANAGER': 'Remote Proctoring Manager',
        'SUPER_ADMIN': 'Developer',
        'STUDENT': 'Student'
    });

    // USERS WITH THESE ROLES SHOULD BE FILTERED OUT OF USER RESULTS
    angular.module('CCC.View.Home').constant('USER_ROLE_BLACK_LIST', ['SUPER_ADMIN', 'STUDENT', 'OPERATIONS', 'REMOTE_PROCTOR']);

    // A LIST OF ROLES ASSOCIATED WITH ASSESSMENTS
    angular.module('CCC.View.Home').constant('ASSESSMENT_RELATED_ROLES', ['PAPER_PENCIL_SCORER', 'REMOTE_PROCTOR', 'PROCTOR', 'REMOTE_PROCTORING_MANAGER']);

    /*============ ROLES SERVICE ============*/

    angular.module('CCC.View.Home').service('RolesService', [

        '$rootScope',
        'USER_ROLE_TITLE_MAP',
        'USER_ROLE_BLACK_LIST',

        function ($rootScope, USER_ROLE_TITLE_MAP, USER_ROLE_BLACK_LIST) {

            /*============ SERVICE DECLARATION ============*/

            var RolesService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var currentRole;
            var markUsersWithBlackListedRoles = function (user) {

                user._hasBlackListedRoles = false;

                _.each(user.roles, function (roleObj) {
                    currentRole = roleObj.role.toUpperCase();
                    user._hasBlackListedRoles = user._hasBlackListedRoles || USER_ROLE_BLACK_LIST.indexOf(currentRole) !== -1;
                });
            };
            var markLeanUsersWithBlackListedRoles = function (user) { // this method is for the lean version of user objects used in the user faceted search

                user._hasBlackListedRoles = false;

                _.each(user.roles, function (role) {
                    currentRole = role.toUpperCase();
                    user._hasBlackListedRoles = user._hasBlackListedRoles || USER_ROLE_BLACK_LIST.indexOf(currentRole) !== -1;
                });
            };


            /*============ SERVICE DEFINITION ============*/

            RolesService = {

                getRoleTitle: function (role) {
                    role = role.toUpperCase();
                    return USER_ROLE_TITLE_MAP[role] ? USER_ROLE_TITLE_MAP[role] : role.toUpperCase();
                },

                markUsersWithBlackListedRoles: function (users) {
                    _.each(users, markUsersWithBlackListedRoles);
                },

                markLeanUsersWithBlackListedRoles: function (users) {
                    _.each(users, markLeanUsersWithBlackListedRoles);
                }
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return RolesService;

        }
    ]);

})();
