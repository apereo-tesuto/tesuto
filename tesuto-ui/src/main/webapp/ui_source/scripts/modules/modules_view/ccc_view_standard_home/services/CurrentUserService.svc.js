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

    angular.module('CCC.View.Home').provider('CurrentUserService', function () {

        /*=========== SERVICE PROVIDER API ===========*/

        // current user reference and an outline of what we expect to come from the server
        var currentUser = {
            username: null,
            userAccountId: null,
            displayName: null,
            colleges: null,
            securityPermissions: null,
            securityGroups: null
        };

        // the provider allows you to set the current user during the config phase
        // we get json data from the JSP and pass that in as the intial assessment data
        this.setUser = function (user_in) {
            currentUser = user_in;
        };

        this.getPermissions = function () {
            return currentUser.securityPermissions.slice(0); // a copy
        };


        /*============ SERVICE DECLARATION ============*/

        this.$get = [

            '$rootScope',
            '$q',
            'RoleService',
            'CollegesAPIService',

            function ($rootScope, $q, RoleService, CollegesAPIService) {

                var CurrentUserService;


                /*============ PRIVATE VARIABLES AND METHODS ============*/

                var completeCollegeLocationPromise = false;
                var getCompleteCollegeLocationPromise = function () {

                    if (!completeCollegeLocationPromise) {
                        completeCollegeLocationPromise = CollegesAPIService.getColleges();
                    }

                    return completeCollegeLocationPromise;
                };


                /*============ SERVICE API ============*/

                CurrentUserService = {

                    /*============ PUBLIC PROPERTIES ============*/


                    /*============ PUBLIC METHODS ============*/

                    getUser: function () {
                        return currentUser;
                    },

                    getSecurityGroups: function () {
                        return currentUser.securityGroups.slice(0); // a copy
                    },

                    isOnlyCounselor: function () {
                        var securityGroups = CurrentUserService.getSecurityGroups();
                        return (securityGroups.length === 1) && (securityGroups.indexOf('COUNSELOR') !== -1);
                    },

                    hasPermission: function (perm) {
                        return currentUser.securityPermissions.indexOf(perm) !== -1;
                    },

                    hasPermissions: function (perms) {
                        var hasPerm = true;
                        _.each(perms, function (perm) {
                            hasPerm = hasPerm && CurrentUserService.hasPermission(perm);
                        });
                        return hasPerm;
                    },

                    hasAtLeastOnePermission: function (perms) {
                        var hasPerm = false;
                        _.each(perms, function (perm) {
                            hasPerm = hasPerm || CurrentUserService.hasPermission(perm);
                        });
                        return hasPerm;
                    },

                    getCollegeList: function (includeAllTestLocations) {

                        // if we just want the college list with locations the current user has access to
                        if (!includeAllTestLocations || !CurrentUserService.hasPermission('VIEW_COLLEGES_WITH_ALL_LOCATIONS')) {

                            return $q.when(angular.copy(currentUser.colleges));

                        } else {

                            return getCompleteCollegeLocationPromise().then(function (collegesList) {
                                return angular.copy(collegesList);
                            });
                        }
                    },

                    getLocationsList: function (includeAllTestLocations) {

                        return CurrentUserService.getCollegeList(includeAllTestLocations).then(function (colleges) {

                            var locations = [];

                            _.each(colleges, function (college) {

                                _.each(college.testLocations, function (location) {

                                    // we attach the college because we may need to display more specific information about that location
                                    location.college = college;
                                    locations.push(location);
                                });
                            });

                            return locations;
                        });
                    },

                    getCollege: function (collegeId, includeAllTestLocations) {
                        return CurrentUserService.getCollegeList(includeAllTestLocations).then(function (colleges) {
                            return _.find(colleges, function (college) {
                                return college.cccId === collegeId;
                            });
                        });
                    },

                    getLocation: function (locationId, includeAllTestLocations) {
                        return CurrentUserService.getLocationsList(includeAllTestLocations).then(function (locations) {
                            return _.find(locations, function (location) {
                                return location.id === locationId;
                            });
                        });
                    },

                    getCollegeBycccId: function (cccId) {

                        return CurrentUserService.getCollegeList().then(function (collegeList) {
                            return _.find(collegeList, function (college) {
                                return college.cccId === cccId;
                            });
                        });
                    }
                };


                /*=========== LISTENERS ===========*/


                /*============ INITIALIZATION ============*/

                RoleService.setRole(currentUser.securityPermissions);

                return CurrentUserService;
            }
        ];
    });

})();
