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

    angular.module('CCC.View.Common').directive('cccStudentUser', function () {

        return {

            restrict: 'E',

            scope: {
                user: '='    // an instance of StudentClass
            },

            template: [

                '<div class="identity">',
                    '<h3 class="name">',
                        '<ccc-user-name last-name="user.lastName" first-name="user.firstName" middle-initial="user.middleInitial"></ccc-user-name>',
                    '</h3>',
                    '<div class="ccc-user-data-items">',
                        '<div class="ccc-user-data-id"><span class="ccc-user-data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC_USER.CCCID"></span> <span class="ccc-user-data-value">{{::user.cccId}}</span></div>',
                        '<div class="ccc-user-data-age"><span class="ccc-user-data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC_USER.AGE"></span> <span class="ccc-user-data-value">{{::user.age}}</span></div>',
                        '<div class="ccc-user-data-phone"><span class="ccc-user-data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC_USER.PHONE"></span> <span class="ccc-user-data-value">{{::user.phoneFormatted}}</span></div>',
                        '<div class="ccc-user-data-email"><span class="ccc-user-data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC_USER.EMAIL"></span> <span class="ccc-user-data-value">{{::user.email}}</span></div>',
                    '</div>',
                '</div>'

            ].join('')

        };
    });

    angular.module('CCC.View.Common').directive('cccSystemUser', function () {

        return {

            restrict: 'E',

            scope: {
                user: '='    // an instance of UserClass
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ MODEL DEPENDENT METHODS ============*/

                    var setRoleList = function () {

                        $scope.user.getRoleList().then(function (roleList) {

                            $scope.roleList = _.sortBy(roleList, function (role) {
                                if (role !== undefined) {
                                    return role.toLowerCase();
                                } else {
                                    return 'undefined';
                                }
                            }).join(', ');
                        });
                    };

                    var setCollegeList = function () {

                        $scope.user.getColleges().then(function (collegeList) {

                            var collegeNames = _.map(collegeList, function (college) {
                                return college.name;
                            });

                            $scope.colleges = _.sortBy(collegeNames, function (college) {
                                return college.toLowerCase();
                            }).join(', ');
                        });
                    };


                    /*============ MODEL ============*/

                    $scope.roleList = '';
                    $scope.colleges = '';


                    /*============ INITIALIZATION ============*/

                    $scope.user.updateMetaData();

                    setRoleList();
                    setCollegeList();
                }
            ],

            template: [

                '<div class="identity">',
                    '<h3 class="name">',
                        '<ccc-user-name last-name="user.lastName" first-name="user.firstName" middle-initial="user.middleInitial"></ccc-user-name>',
                    '</h3>',
                    '<div class="ccc-user-data-items">',
                        '<div class="ccc-user-data-phone">',
                            '<span class="ccc-user-data-label">Phone:</span> <span class="ccc-user-data-value">',
                                '{{::user.phoneFormatted}}',
                                '<span ng-if="user.extension && user.phoneNumber"> ext. {{::user.extension}}</span>',
                            '</span> ',
                        '</div>',
                        '<div class="ccc-user-data-email"><span class="ccc-user-data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC_USER.EMAIL"></span> <span class="ccc-user-data-value">{{::user.emailAddress}}</span></div>',
                        '<div class="ccc-user-data-roles"><span class="ccc-user-data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC_USER.ROLES"></span> <span class="ccc-user-data-value">{{roleList}}</span></div>',
                        '<div class="ccc-user-data-colleges"><span class="ccc-user-data-label" translate="CCC_VIEW_STANDARD_COMMON.CCC_USER.COLLEGES"></span> <span class="ccc-user-data-value">{{colleges}}</span></div>',
                    '</div>',
                '</div>'

            ].join('')

        };
    });

    angular.module('CCC.View.Common').directive('cccUserName', function () {

        return {

            restrict: 'E',

            scope: {
                lastName: '=',
                firstName: '=',
                middlInitial: '='
            },

            template: [

                '<span class="last-name">{{::lastName}}</span>, ',
                '<span ng-if="::firstName">{{::firstName}}</span> ',
                '<span ng-if="::!firstName">--</span> ',
                '<span ng-if="::middleInitial">{{::middleInitial}}.</span>',
                '<span ng-if="::!firstName && !middleInitial">-</span>',
                '<span class="text-fade"></span>'

            ].join('')

        };
    });

})();



