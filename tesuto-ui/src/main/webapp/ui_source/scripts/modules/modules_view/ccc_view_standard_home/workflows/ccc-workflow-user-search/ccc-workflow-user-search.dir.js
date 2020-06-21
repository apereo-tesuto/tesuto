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

    angular.module('CCC.View.Home').directive('cccWorkflowUserSearch', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'ViewManagerEntity',
                'NotificationService',

                function ($scope, ViewManagerEntity, NotificationService) {

                    /*============ PRIVATE VARIABLES / METHODS ===========*/


                    /*============ MODEL ==============*/

                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addCreateUserView = function (firstName, lastName) {

                        var viewScope = $scope.$new();
                        viewScope.parentViewManager = $scope.viewManager;

                        viewScope.initialUserFields = {
                            firstName: firstName,
                            lastName: lastName
                        };

                        viewScope.$on('ccc-workflow-create-user.complete', function () {
                            $scope.$broadcast('ccc-user-lookup.requestClear');
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
                            template: '<ccc-workflow-create-user parent-view-manager="parentViewManager" initial-user-fields="initialUserFields"></ccc-workflow-create-user>'
                        });
                    };

                    var addEditUserConfirmationView = function (user) {

                        var viewScope = $scope.$new();

                        viewScope.user = user;

                        viewScope.$on('ccc-user-edit-confirm.okay', function () {
                            $scope.viewManager.goToView('ccc-workflow-user-search-search', true);
                        });

                        $scope.viewManager.pushView({
                            id: 'ccc-workflow-user-edit-confirm',
                            title: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.EDIT_USER_CONFIRM',
                            backButton: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.EDIT_USER_BACK',
                            backTarget: 'ccc-workflow-user-search-search',
                            scope: viewScope,
                            template: '<ccc-user-edit-confirm user="user"></ccc-user-edit-confirm>'
                        });
                    };

                    var addEditUserView = function (user) {

                        var viewScope = $scope.$new();
                        viewScope.user = user.clone();

                        viewScope.$on('ccc-user-edit.updated', function () {

                            $scope.$broadcast('ccc-user-lookup.requestClear');
                            $scope.$emit('ccc-workflow-user-search.userUpdated');
                            addEditUserConfirmationView(viewScope.user);
                        });

                        viewScope.$on('ccc-user-edit.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'ccc-workflow-user-search-edit',
                            title: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.EDIT_USER',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.EDIT_USER',
                            scope: viewScope,
                            template: '<ccc-user-edit user="user"></ccc-user-edit>'
                        });
                    };

                    var addUserSearchView = function () {

                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-user-list.selected', function (e, user) {
                            addEditUserView(user);
                        });

                        viewScope.$on('ccc-user-lookup.createUser', function (e, firstName, lastName) {
                            addCreateUserView(firstName, lastName);
                        });

                        $scope.viewManager.pushView({
                            id: 'ccc-workflow-user-search-search',
                            title: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.SEARCH_USERS',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.SEARCH_USERS.BREADCRUMB.SEARCH_USERS',
                            scope: viewScope,
                            template: '<ccc-user-lookup></ccc-user-lookup>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addUserSearchView();
                }
            ],

            template: [

                '<section>',
                    '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>',
                '</section>'

            ].join('')
        };
    });

})();
