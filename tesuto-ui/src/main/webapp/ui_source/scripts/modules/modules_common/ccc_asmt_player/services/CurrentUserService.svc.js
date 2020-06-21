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

    angular.module('CCC.AsmtPlayer').provider('CurrentUserService', function () {

        /*=========== SERVICE PROVIDER API ===========*/

        // current user reference and an outline of what we expect to come from the server
        var currentUser = {};

        // the provider allows you to set the current user during the config phase
        // we get json data from the JSP and pass that in as the intial assessment data
        this.setUser = function (user_in) {
            currentUser = user_in;
        };


        /*============ SERVICE DECLARATION ============*/

        this.$get = [

            '$rootScope',
            'RoleService',

            function ($rootScope, RoleService) {

                var CurrentUserService;

                /*============ PRIVATE VARIABLES AND METHODS ============*/

                var properties = {};


                /*============ SERVICE API ============*/

                CurrentUserService = {

                    /*============ PUBLIC PROPERTIES ============*/

                    /*============ PUBLIC METHODS ============*/

                    getUser: function () {
                        return currentUser;
                    },

                    setProperty: function (propKey, propValue) {
                        properties[propKey] = propValue;
                    },

                    getProperty: function (propKey, defaultValue) {
                        if (properties[propKey] === undefined) {
                            properties[propKey] = defaultValue;
                        }
                        return properties[propKey];
                    }
                };


                /*=========== LISTENERS ===========*/

                /*============ INITIALIZATION ============*/

                RoleService.setRole(['STUDENT']);

                return CurrentUserService;

            }
        ];
    });

})();
