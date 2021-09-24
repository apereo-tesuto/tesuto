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

    angular.module('CCC.View.Home').directive('cccHome', function () {

        return {

            restrict: 'E',

            scope: {
                location: '=?'
            },

            controller: [

                '$scope',
                '$element',
                'CurrentUserService',
                'ASSESSMENTS_DISABLED',

                function ($scope, $element, CurrentUserService, ASSESSMENTS_DISABLED) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var getUserActions = function () {

                        var userActions = [];

                        if (!ASSESSMENTS_DISABLED && CurrentUserService.hasPermissions(['CREATE_ACTIVATION', 'VIEW_TEST_LOCATIONS_BY_USER', 'VIEW_ASSESSMENTS', 'CREATE_BATCH_ACTIVATION'])) {

                            userActions.push({
                                title: 'CCC_VIEW_HOME.ROLE_NAV.BULK_ACTIVATION',
                                state: 'bulkActivation',
                                icon: 'ui/resources/images/icons/attendance_list.png'
                            });
                        }

                        if (CurrentUserService.hasPermission('FIND_STUDENT')) {

                            userActions.push({
                                title: 'CCC_VIEW_HOME.ROLE_NAV.STUDENT_LOOKUP',
                                state: 'studentLookup',
                                icon: 'ui/resources/images/icons/zoom.png'
                            });
                        }

                        if (CurrentUserService.hasPermission('CREATE_USER')) {
                            userActions.push({
                                title: 'CCC_VIEW_HOME.ROLE_NAV.MANAGE_USERS',
                                state: 'manageUsers',
                                icon: 'ui/resources/images/icons/group.png'
                            });
                        }

                        if (CurrentUserService.hasPermission('VIEW_DISTRICTS_AND_COLLEGES_FOR_CURRENT_USER_WITH_ALL_TEST_LOCATIONS')) {

                            userActions.push({
                                title: 'CCC_VIEW_HOME.ROLE_NAV.MANAGE_LOCATIONS',
                                state: 'manageLocations',
                                icon: 'ui/resources/images/icons/school.png'
                            });
                        }

                        if (!ASSESSMENTS_DISABLED && CurrentUserService.hasPermission('PRINT_ASSESSMENT')) {

                            userActions.push({
                                title: 'CCC_VIEW_HOME.ROLE_NAV.PAPER_PENCIL',
                                state: 'paperPencil',
                                icon: 'ui/resources/images/icons/exam.png'
                            });
                        }

                        if (CurrentUserService.hasAtLeastOnePermission(['GET_DISCIPLINE'])) {

                            userActions.push({
                                title: 'CCC_VIEW_HOME.ROLE_NAV.MANAGE_PLACEMENTS',
                                state: 'managePlacements',
                                icon: 'ui/resources/images/icons/exam_pass.png'
                            });
                        }

                        if (!ASSESSMENTS_DISABLED && CurrentUserService.hasPermission('VIEW_TEST_EVENT')) {

                            userActions.push({
                                title: 'CCC_VIEW_HOME.ROLE_NAV.REMOTE_EVENTS',
                                state: 'remoteEvents',
                                icon: 'ui/resources/images/icons/external_contact.png'
                            });
                        }

                        if (!ASSESSMENTS_DISABLED && CurrentUserService.hasPermission('VIEW_INSTRUCTOR_CLASS_REPORT')) {

                            userActions.push({
                                title: 'CCC_VIEW_HOME.ROLE_NAV.CLASS_REPORT',
                                state: 'classReport',
                                icon: 'ui/resources/images/icons/grades_report.png'
                            });
                        }

                        return userActions;
                    };


                    /*============ MODEL ==============*/

                    $scope.currentUser = CurrentUserService.getUser() || null;
                    $scope.userActions = getUserActions() || null;

                    // permission tied to service/v1/activation-search/location-summary
                    $scope.canAccessProctorLocationSummary = CurrentUserService.hasPermission('VIEW_LOCATION_SUMMARY') && !ASSESSMENTS_DISABLED;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var updateFirstFocus = function (targetElement) {
                        $element.find('[ccc-autofocus]').removeAttr('ccc-autofocus');
                        targetElement.attr('ccc-autofocus', 'ccc-autofocus');
                    };


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-proctor-location-summary.setProctorLocation', function (event, location, locationElement) {
                        updateFirstFocus(locationElement);
                    });


                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<h2 class="sr-only" translate="CCC_VIEW_HOME.CCC-HOME.USER_ACTIONS"></h2>',
                '<ccc-role-navbar actions="userActions"></ccc-role-navbar>',
                '<ccc-proctor-location-summary ng-if="canAccessProctorLocationSummary"></ccc-proctor-location-summary>'

            ].join('')

        };

    });

})();
