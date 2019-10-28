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

    angular.module('CCC.View.Home').directive('cccWorkflowManageUsers', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'ViewManagerEntity',
                'NotificationService',

                function ($scope, ViewManagerEntity, NotificationService) {


                    /*============ PRIVATE VARIABLES =============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addManageUsersCreateView = function () {

                        var viewScope = $scope.$new();
                        viewScope.parentViewManager = $scope.viewManager;

                        viewScope.$on('ccc-workflow-create-user.complete', function () {
                            $scope.$emit('ccc-workflow-manage-users.userUpdated');
                            $scope.$broadcast('ccc-manage-users.refresh');
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-workflow-create-user.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-users-create-user-workflow',
                            title: 'CCC_VIEW_HOME.WORKFLOW.MANAGE_USERS.BREADCRUMB.CREATE_USER',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.MANAGE_USERS.BREADCRUMB.CREATE_USER',
                            scope: viewScope,
                            isNested: true,
                            template: '<ccc-workflow-create-user parent-view-manager="parentViewManager"></ccc-workflow-create-user>'
                        });
                    };

                    var addEditUserConfirmationView = function (user) {

                        var viewScope = $scope.$new();

                        viewScope.user = user;

                        viewScope.$on('ccc-user-edit-confirm.okay', function () {
                            $scope.viewManager.goToView('ccc-workflow-manage-users-main', true);
                        });

                        $scope.viewManager.pushView({
                            id: 'ccc-workflow-manage-users-edit-confirm',
                            title: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.EDIT_USER_CONFIRM',
                            backButton: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.EDIT_USER_BACK',
                            backTarget: 'ccc-workflow-manage-users-main',
                            scope: viewScope,
                            template: '<ccc-user-edit-confirm user="user"></ccc-user-edit-confirm>'
                        });
                    };

                    var addEditUserView = function (user) {

                        var viewScope = $scope.$new();
                        viewScope.userAccountId = user.userAccountId;

                        viewScope.$on('ccc-user-edit.updated', function (e, user) {
                            $scope.$emit('ccc-workflow-manage-users.userUpdated');
                            $scope.$broadcast('ccc-manage-users.refresh');
                            addEditUserConfirmationView(user);
                        });

                        viewScope.$on('ccc-user-edit.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-user-edit.userDeleted', function () {

                            $scope.$broadcast('ccc-manage-users.refresh');
                            $scope.viewManager.goToView('ccc-workflow-manage-users-main', true);

                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Success.',
                                message: ' User was deleted.'
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'ccc-workflow-manage-users-edit',
                            title: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.EDIT_USER',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.EDIT_USER',
                            scope: viewScope,
                            template: '<ccc-user-edit-by-id user-account-id="userAccountId"></ccc-user-edit-by-id>'
                        });
                    };

                    var addManagerUsersDashboardView = function () {

                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-manage-users.addUser', function () {
                            addManageUsersCreateView();
                        });

                        viewScope.$on('ccc-manage-users.userSelected', function (e, user) {
                            addEditUserView(user);
                        });

                        $scope.viewManager.pushView({
                            id: 'ccc-workflow-manage-users-main',
                            title: 'CCC_VIEW_HOME.WORKFLOW.MANAGE_USERS.BREADCRUMB.MANAGE_USERS',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.MANAGE_USERS.BREADCRUMB.MANAGE_USERS',
                            scope: viewScope,
                            template: '<ccc-manage-users></ccc-manage-users>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addManagerUsersDashboardView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
