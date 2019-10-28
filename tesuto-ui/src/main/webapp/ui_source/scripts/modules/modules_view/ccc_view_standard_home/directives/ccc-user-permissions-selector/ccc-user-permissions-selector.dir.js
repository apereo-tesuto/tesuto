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

    angular.module('CCC.View.Home').directive('cccUserPermissionsSelector', function () {

        return {

            restrict: 'E',

            scope: {
                user: '=',
                disableCustomizations: '=?',
                isDisabled: '=',
                labelledBy: '@?'
            },

            controller: [

                '$scope',
                '$timeout',
                'RolesAPIService',
                'RolesService',
                'USER_ROLE_BLACK_LIST',
                'ASSESSMENT_RELATED_ROLES',
                'ASSESSMENTS_DISABLED',

                function ($scope, $timeout, RolesAPIService, RolesService, USER_ROLE_BLACK_LIST, ASSESSMENT_RELATED_ROLES, ASSESSMENTS_DISABLED) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var FINAL_ROLE_BLACKLIST = USER_ROLE_BLACK_LIST;

                    if (ASSESSMENTS_DISABLED) {
                        FINAL_ROLE_BLACKLIST = USER_ROLE_BLACK_LIST.concat(ASSESSMENT_RELATED_ROLES);
                    }

                    var allRoles = [];

                    var permissionsMap = {

                        'LOCAL_ADMIN': [
                            {
                                id: '3a',
                                title: 'Can search for users'
                            },
                            {
                                id: '1a',
                                title: 'Can create, edit and disable users'
                            },
                            {
                                id: '2a',
                                title: 'Can create, edit and disable locations'
                            }
                        ],

                        'PLACEMENT_MANAGER': [
                            {
                                id: 'placementManager1',
                                title: 'Can create, edit, publish and unpublish subject area\'s'
                            },
                            {
                                id: 'placementManager2',
                                title: 'Can create, edit and delete courses from subject area'
                            },
                            {
                                id: 'placementManager3',
                                title: 'Can create, edit and delete competencies linked to courses'
                            }
                        ],

                        'PLACEMENT_READ_ONLY': [
                            {
                                id: 'placementReadOnly1',
                                title: 'Can see subject areas'
                            },
                            {
                                id: 'placementReadOnly2',
                                title: 'Can see courses and courses details'
                            },
                            {
                                id: 'placementReadOnly3',
                                title: 'Can see competencies mapped to courses'
                            },
                            {
                                id: 'placementReadOnly4',
                                title: 'Can see subject area publish/ unpublish status'
                            }
                        ],

                        'COUNSELOR': [
                            {
                                id: 'counselor1',
                                title: 'Can search for a student'
                            },
                            {
                                id: 'counselor2',
                                title: 'Can see a students assessment history'
                            },
                            {
                                id: 'counselor3',
                                title: 'Can view the student\'s placement components'
                            },
                            {
                                id: 'counselor4',
                                title: 'Can view the counselor and student versions of the placement report'
                            }
                        ],

                        'FACULTY': [
                            {
                                id: 'faculty1',
                                title: 'Can upload class roster'
                            },
                            {
                                id: 'faculty2',
                                title: 'Can generate a class report based on the uploaded class roster'
                            }
                        ],

                        'PROCTOR': [
                            {
                                id: '1b',
                                title: 'Can search for a student'
                            },
                            {
                                id: '2b',
                                title: 'Can see a students assessment history'
                            },
                            {
                                id: '3b',
                                title: 'Can view proctor dashboards for locations user has access to'
                            },
                            {
                                id: '4b',
                                title: 'Can create, edit and deactivate activation\'s'
                            },
                            {
                                id: '5b',
                                title: 'Can generate a passcode'
                            },
                            {
                                id: '6b',
                                title: 'Can start and resume assessments'
                            }
                        ],

                        'PAPER_PENCIL_SCORER': [
                            {
                                id: 'processor1',
                                title: 'Can search for a student'
                            },
                            {
                                id: 'processor2',
                                title: 'Can create an activation'
                            },
                            {
                                id: 'processor3',
                                title: 'Can search for an activation'
                            },
                            {
                                id: 'processor4',
                                title: 'Can enter in students assessment responses'
                            }
                        ],

                        'REMOTE_PROCTORING_MANAGER': [
                            {
                                id: '1remoteProctor',
                                title: 'Can create, edit and cancel remote events'
                            },
                            {
                                id: '2remoteProctor',
                                title: 'Can add and remove students from the remote event'
                            },
                            {
                                id: '3remoteProctor',
                                title: 'Can create, edit and cancel students activation'
                            },
                            {
                                id: '4remoteProctor',
                                title: 'Can resend remote email'
                            },
                            {
                                id: '5remoteProctor',
                                title: 'Can generate a passcode'
                            }
                        ]
                    };

                    var getPermissionsForRole = function (roleName) {
                        if (permissionsMap[roleName]) {
                            return angular.copy(permissionsMap[roleName]);  // we have to copy to wipe out any state added to them
                        } else {
                            return [{
                                id: '1d',
                                title: 'No permissions information available'
                            }];
                        }
                    };


                    /*============ MODEL ============*/

                    $scope.nestedItems = [];

                    $scope.selectedRoles = [];

                    $scope.loading = true;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getUserHasRole = function (role) {
                        return $scope.user.roleIds.indexOf(role.securityGroupId) !== -1;
                    };

                    var onRoleSelected = function (roleItem) {
                        $scope.user.roleIds.push(roleItem.id);
                    };
                    var onRoleDeselected = function (roleItem) {
                        var foundIndex = $scope.user.roleIds.indexOf(roleItem.id);
                        if (foundIndex !== -1) {
                            $scope.user.roleIds.splice(foundIndex, 1);
                        }
                    };

                    var generateNestedItemsModel = function (roles) {

                        $scope.nestedItems = [];

                        _.each(roles, function (role) {

                            var newItem = {

                                id: role.securityGroupId,
                                name: role.title,
                                item: role,

                                selected: getUserHasRole(role),
                                disabled: false,

                                onSelected: onRoleSelected,
                                onDeselected: onRoleDeselected,

                                children: []
                            };

                            _.each(role.permissions, function (permission) {

                                newItem.children.push({

                                    id: permission.id,
                                    name: permission.title,
                                    item: permission,

                                    selected: newItem.selected,
                                    disabled: true
                                });
                            });

                            $scope.nestedItems.push(newItem);
                        });
                    };

                    // loop through the list of passed in permissions and populate the selected permissions
                    var getRolePermissionMap = function (rolesList) {

                        var roles = _.sortBy(rolesList, function (role) {
                            return role.groupName;
                        });

                        var rolePermissionMap = [];

                        _.each(roles, function (role) {

                            if (FINAL_ROLE_BLACKLIST.indexOf(role.groupName) === -1) {

                                var roleObject = {
                                    securityGroupId: role.securityGroupId,
                                    groupName: role.groupName,
                                    title: RolesService.getRoleTitle(role.groupName),
                                    permissions: getPermissionsForRole(role.groupName)
                                };

                                roleObject.selected = getUserHasRole(roleObject);

                                rolePermissionMap.push(roleObject);
                            }
                        });

                        _.each(rolePermissionMap, function (role) {

                            _.each(role.permissions, function (permission) {
                                permission.selected = role.selected;
                            });
                        });

                        return rolePermissionMap;
                    };

                    var loadRoles = function () {

                        RolesAPIService.getAllRoles().then(function (roleList) {

                            allRoles = roleList;
                            $scope.rolePermissionMap = getRolePermissionMap(allRoles);
                            generateNestedItemsModel($scope.rolePermissionMap);

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    loadRoles();
                }
            ],

            template: [

                '<div ng-form="cccUserPermissionsForm">',

                    '<ccc-content-loading-placeholder class="ccc-user-permissions-selector-loader" ng-hide="rolePermissionMap.length > 0 && !loading" no-results="rolePermissionMap.length === 0 && !loading"></ccc-content-loading-placeholder>',

                    '<ccc-nested-item-selector class="ccc-nested-item-selector-style-two-level" items="nestedItems" ng-if="nestedItems.length > 0 && !loading" enable-enforce-parents-selected="true" is-disabled="isDisabled" labelled-by="ccc-college-selector-errors"></ccc-nested-item-selector>',

                    '<input class="hidden" autocomplete="off" name="permissionsSelector" ng-model="user.roleIds" ng-model-options="{allowInvalid: true}" ccc-required-array="1" />',

                    '<div id="ccc-user-permissions-list-errors" class="ccc-validation-messages ccc-validation-messages-standalone ccc-validation-messages-permissions">',
                        '<p ng-if="!user.roleIds.length">',
                            '<i class="fa fa-exclamation-triangle color-warning"></i> ',
                            '<span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.PERMISSIONS"></span>',
                        '</p>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
