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

    angular.module('CCC.View.Home').directive('cccActionList', function () {

        return {

            restrict: 'E',

            scope: {
                actions: '='
            },

            template: [
                '<div class="well well-thin actions">',
                    '<div ng-if="userActions.length === 0" translate="CCC_VIEW_HOME.ROUTES.HOME.NO_ACTIONS"></div>',
                    '<button class="btn btn-default ccc-action-button-{{action.state}}" ng-repeat="action in actions" ui-sref="{{action.state}}" translate="{{action.title}}"></button>',
                '</div>'
            ].join('')

        };

    });

})();




