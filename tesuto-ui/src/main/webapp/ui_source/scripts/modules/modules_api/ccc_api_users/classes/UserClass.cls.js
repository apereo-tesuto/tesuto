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

    angular.module('CCC.API.Users').factory('UserClass', [

        '$q',
        'Moment',
        '$translate',
        'Cloneable',
        'UsersAPIService',
        'RolesAPIService',
        'CurrentUserService',
        'RolesService',
        'UtilsService',

        function ($q, Moment, $translate, Cloneable, UsersAPIService, RolesAPIService, CurrentUserService, RolesService, UtilsService) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var UserClass = function (configs_in) {

                var defaults = {
                    userAccountId: '',
                    username: '',
                    firstName: '',
                    lastName: '',
                    middleInitial: '',
                    phoneNumber: '',
                    extension: '',
                    emailAddress: '',
                    colleges: [],   // the server will populate this heirarchical structure of courses and testLocations, we use it to p
                    collegeIds: [],
                    primaryCollegeId: null,
                    testLocationIds: [],
                    roles: [],
                    roleIds: [],
                    temporary: false,
                    startDate: new Moment(),
                    endDate: null
                };

                // merge in the defaults onto the instance
                var that = this;
                // extend from the Cloneable class
                Cloneable.call(that);
                $.extend(true, that, defaults, configs_in || {});

                // coerce some values
                that.firstName = that.firstName || '';
                that.lastName = that.lastName || '';
                that.middleName = that.middleName || '';
                that.phoneNumber = that.phoneNumber || '';

                that.roles = that.roles || [];
                that.colleges = that.colleges || [];


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                var updateRolesFromServerData = function () {

                    if (that.securityGroupDtos) {

                        that.roles = _.map(that.securityGroupDtos, function (securityDto) {
                            return {role: securityDto.groupName.toUpperCase(), title: RolesService.getRoleTitle(securityDto.groupName), securityGroupId: securityDto.securityGroupId};
                        });

                    } else {
                        that.roles = [];
                    }

                    that.roleIds = _.map(that.roles, function (role) { return role.securityGroupId; });
                };

                var updateCollegesFromServerData = function () {

                    that.collegeIds = _.map(that.colleges, function (college) {
                        return college.cccId;
                    });

                    var testLocationIdLists = _.map(that.colleges, function (college) {
                        return _.map(college.testLocations, function (testLocation) {
                            return testLocation.id;
                        });
                    });

                    that.testLocationIds = _.reduce(testLocationIdLists, function (memo, testLocationIdList) {
                        return memo.concat(testLocationIdList);
                    }, []);
                };

                var updateFormattedPhone = function () {
                    // generate formatted phone number
                    var phoneToFormat = that.phoneNumber ? that.phoneNumber : (that.mobilePhone ? that.mobilePhone : false);
                    that.phoneFormatted = UtilsService.formatTenDigitPhone(phoneToFormat);
                };

                var updateMetaData = function () {
                    updateFormattedPhone();
                };

                // generate useful meta data from initial config data (from server etc...)
                var initialize = function () {

                    // normalize the phone number (turn to all digits)
                    that.phoneNumber = that.phoneNumber.replace(/\D/g,'');

                    // generate the middleInitial
                    var hasMiddleName = that.middleName && $.trim(that.middleName).length;
                    if (hasMiddleName) {
                        that.middleInitial = that.middleName.substring(0,1);
                    }

                    updateRolesFromServerData();
                    updateCollegesFromServerData();

                    updateMetaData();
                };

                var getRoleStringPromises = function () {

                    var roleStringPromises = [];

                    _.each(that.roles, function (role) {
                        roleStringPromises.push($q.when(role.role));
                    });

                    return $q.all(roleStringPromises);
                };

                // because we may have just created a user that only has roleId's and not securityGroupDTOs, we need to manually get the role data
                var populateRoleData = function () {

                    return RolesAPIService.getAllRoles().then(function (roleList) {

                        // attach the roles that this user has
                        that.securityGroupDtos = _.filter(roleList, function (role) {
                            return that.roleIds.indexOf(role.securityGroupId) !== -1;
                        });

                        updateRolesFromServerData();
                    });
                };


                /*=============== PUBLIC PROPERTIES =============*/


                /*=============== PUBLIC METHODS =============*/

                that.getRoleList = function () {

                    // if the roles are populated, use them, otherwise we may need to populate them from the server
                    if (that.roles && that.roles.length) {
                        return getRoleStringPromises();
                    } else {
                        return populateRoleData().then(getRoleStringPromises);
                    }
                };

                that.populateCollegeDataFromCollegeIds = function () {

                    return CurrentUserService.getCollegeList(true).then(function (colleges) {

                        that.colleges = _.filter(colleges, function (college) {
                            return that.collegeIds.indexOf(college.cccId) !== -1;
                        });

                        _.each(that.colleges, function (college) {
                            college.testLocations = _.filter(college.testLocations, function (testLocation) {
                                return that.testLocationIds.indexOf(testLocation.id) !== -1;
                            });
                        });
                    });
                };

                that.getColleges = function () {

                    // if the colleges are populated, use them, otherwise we need to construct college data differently
                    if (that.colleges && that.colleges.length) {
                        return $q.when(angular.copy(that.colleges));
                    } else {
                        return that.populateCollegeDataFromCollegeIds().then(function () {
                            return $q.when(angular.copy(that.colleges));
                        });
                    }
                };

                that.getCollegesAndLocationsNotIn = function (collegeList) {

                    var collegeIdMap = {};
                    var testLocationIdMap = {};

                    // first let's create a map of all existing colleges and test locations
                    _.each(collegeList, function (college) {

                        collegeIdMap[college.cccId + ''] = college;

                        _.each(college.testLocations, function (testLocation) {
                            testLocationIdMap[testLocation.id] = testLocation;
                        });
                    });

                    var collegesNotIn = [];
                    var locationsNotIn = [];

                    // next loop through this users colleges and find colleges not in the maps above
                    // also find test locations not in the maps above
                    _.each(that.colleges, function (userCollege) {

                        var isCollegeInList = collegeIdMap[userCollege.cccId + ''];

                        // if the college is not in the list, then we need to add it to our array
                        if (!isCollegeInList) {

                            collegesNotIn.push(userCollege);

                        // if the college is in the list, then we just need to compare the testLocations
                        // it's possible the user has access to a location that the incoming collegeList does not
                        } else {

                            _.each(userCollege.testLocations, function (testLocation) {

                                var isTestLocationIn = testLocationIdMap[testLocation.id];

                                if (!isTestLocationIn) {
                                    locationsNotIn.push({
                                        collegeId: userCollege.cccId,
                                        testLocation: testLocation
                                    });
                                }
                            });
                        }
                    });

                    // the caller of this method recieves all colleges not in the given list which will also have contain all testing locations for this user
                    // additionally, they will recieve a list of testLocations that are not in the provided collegeList, but where their corresponding college is in the collegeList
                    // the caller of this method can then use this information to augment an existing list.
                    // NOTE: the testLocations will be an array of objects {collegeId: <collegeId>, testLocation: testLocation}, to help merge missing test locations into existing colleges
                    return {

                        // we need to return the entire colleges and locations in case they need to be inserted somewhere in the UI (not just the ids)
                        // these colleges will have their corresponding test locations within them
                        colleges: angular.copy(collegesNotIn),

                        // this list is a list of testLocations that are not in collegeList but their corresponding college is in the list
                        testLocations: angular.copy(locationsNotIn)
                    };
                };

                // method used to send data for creation and update
                that.serialize = function () {

                    // this is the format the backend needs for creation and updating
                    var serializeModel = {
                        user: {
                            userAccountId: that.userAccountId,
                            username: that.username,
                            firstName: that.firstName,
                            lastName: that.lastName,
                            emailAddress: that.emailAddress,
                            primaryCollegeId: that.primaryCollegeId,
                            enabled: that.enabled
                        },
                        collegeIds: that.collegeIds,
                        testLocationIds: that.testLocationIds,
                        roleIds: that.roleIds
                    };

                    if ($.trim(that.middleInitial)) {
                        serializeModel.user.middleInitial = that.middleInitial;
                    }
                    if ($.trim(that.phoneNumber)) {
                        serializeModel.user.phoneNumber = that.phoneNumber;
                    }
                    if ($.trim(that.extension)) {
                        serializeModel.user.extension = that.extension;
                    }
                    // TODO: make it so the startDate stays as a moment object?
                    if (that.temporary) {
                        serializeModel.user.startDate = new Moment(that.startDate).utc().format();
                        serializeModel.user.endDate = new Moment(that.endDate).utc().format();
                    }

                    return serializeModel;
                };

                that.create = function () {
                    return UsersAPIService.createUser(that.serialize()).then(function (userAccountId) {
                        // server is returning userAccountId as an array and we need a string.
                        that.userAccountId = userAccountId.join();
                    });
                };

                that.update = function () {

                    return UsersAPIService.updateUser(that.serialize()).then(function () {

                        // since we really only update roleIds and collegeIds
                        // let's blank out these values to force the UI to fetch new ones
                        that.colleges = [];
                        that.roles = [];
                    });
                };

                that.delete = function () {
                    return UsersAPIService.deleteUser(that.serialize());
                };

                that.updateMetaData = updateMetaData;


                /*=============== INITIALIZTION =============*/

                initialize();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return UserClass;
        }
    ]);

})();
