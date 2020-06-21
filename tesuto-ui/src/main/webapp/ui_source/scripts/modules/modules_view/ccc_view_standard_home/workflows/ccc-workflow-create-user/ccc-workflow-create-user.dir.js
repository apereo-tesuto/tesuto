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

    angular.module('CCC.View.Home').directive('cccWorkflowCreateUser', function () {

        return {

            restrict: 'E',

            scope: {
                parentViewManager: '=?',
                initialUserFields: '=?'
            },

            controller: [

                '$scope',
                'ViewManagerEntity',

                function ($scope, ViewManagerEntity) {

                    /*============ PRIVATE VARIABLES =============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({parentViewManager: $scope.parentViewManager});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addCreateUserConfirmationView = function (user) {

                        var viewScope = $scope.$new();

                        viewScope.user = user;

                        var createAnotherUser = function () {
                            $scope.$broadcast('ccc-user-create.requestReset');
                            $scope.viewManager.popView();
                        };

                        viewScope.$on('ccc-user-create-confirm.okay', function () {
                            $scope.$emit('ccc-workflow-create-user.complete');
                        });

                        viewScope.$on('ccc-user-create-confirm.repeat', createAnotherUser);

                        $scope.viewManager.pushView({
                            id: 'manage-workflow-create-user-confirm',
                            title: 'CCC_VIEW_HOME.WORKFLOW.CREATE_USER.BREADCRUMB.CREATE_USER_CONFIRM',
                            backButton: $scope.viewManager.parentBackButton('Add Another User'),
                            backAction: $scope.viewManager.parentBackAction(createAnotherUser),
                            scope: viewScope,
                            template: '<ccc-user-create-confirm user="user"></ccc-user-create-confirm>'
                        });
                    };

                    var addCreateUserView = function () {

                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-user-create.cancel', function () {
                            $scope.$emit('ccc-workflow-create-user.cancel');
                        });

                        viewScope.$on('ccc-user-create.userCreated', function (e, user) {
                            addCreateUserConfirmationView(user);
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-workflow-create-user-create',
                            title: 'CCC_VIEW_HOME.WORKFLOW.CREATE_USER.BREADCRUMB.CREATE_USER',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.CREATE_USER.BREADCRUMB.CREATE_USER',
                            scope: viewScope,
                            template: '<ccc-user-create initial-user-fields="initialUserFields"></ccc-user-create>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    addCreateUserView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
