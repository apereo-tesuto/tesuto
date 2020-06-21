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

    angular.module('CCC.View.Home').directive('cccManageUsers', function () {

        return {

            restrict: 'E',

            scope: {
                college: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'ModalService',

                function ($scope, $element, $timeout, ModalService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.loading = false;


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/

                    $scope.addUser = function () {
                        $scope.$emit('ccc-manage-users.addUser');
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-user-faceted-search.userSelected', function (e, user) {
                        $scope.$emit('ccc-manage-users.userSelected', user);
                    });

                    $scope.$on('ccc-manage-users.refresh', function () {
                        $scope.$broadcast('ccc-user-faceted-search.refresh');
                    });


                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<div class="row">',
                    '<div class="col-xs-12">',
                        '<h2 tabindex="-1" ccc-autofocus class="no-outline" id="manage-subject-areas-header"><strong>{{college.name}}</strong> <span class="sr-only">subject areas</span></h2>',
                    '</div>',
                '</div>',

                '<div class="well">',
                    '<button ng-click="addUser()" ng-disabled="loading" class="btn btn-primary ccc-manage-subject-areas-button-add" ccc-focusable>',
                        '<i class="fa fa-plus" aria-hidden="true"></i> ',
                        '<span translate="CCC_VIEW_HOME.CCC-MANAGE-USERS.BUTTON_ADD_USER"></span>',
                    '</button>',
                '</div>',

                '<div class="margin-bottom">',
                    '<ccc-user-faceted-search></ccc-user-faceted-search>',
                '</div>'

            ].join('')

        };

    });

})();
