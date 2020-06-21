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

    angular.module('CCC.AsmtPlayer').service('ToolsPermissionService', [

        '$rootScope',

        function ($rootScope) {

            var ToolsPermissionService;

            /*============== PRIVATE VARIABLES ==============*/

            var permissions = {
                allowCalculator: false,
                allowDictionary: false,
                allowThesaurus: false
            };

            var clearAllPermissions = function () {

                for (var key in permissions) {
                    if (permissions.hasOwnProperty(key)) {
                        permissions[key] = false;
                    }
                }
            };

            var setPermissionsForTaskSet = function (task) {

                clearAllPermissions();

                _.each(task.itemSessions, function (itemSession) {

                    var tools = itemSession.assessmentItem.tools ? itemSession.assessmentItem.tools : {};

                    _.each(tools, function (toolValue, toolKey) {
                        permissions[toolKey] = permissions[toolKey] || (typeof toolValue === 'string' ? (toolValue.toLowerCase() === 'yes') : false);
                    });
                });
            };


            /*============== SERVICE DEFINITION ==============*/

            ToolsPermissionService = {

                getPermissions: function () {

                    // for a little extra security just send a copy of the object so nobody can change it
                    return $.extend({}, permissions);
                },

                setPermissionsForTaskSet: function (task) {
                    setPermissionsForTaskSet(task);
                }
            };

            return ToolsPermissionService;
        }
    ]);

})();
