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

    /**
     * In order to use this service you will need to do the following:
     * 1) Grab the RouterPermissionsServiceProvider during the config phase and call setPermissions with an array of strings
     * 2) Protect routes by placing an array of permissions the user must have in the route config
     * 3) Mark views with defaultView: true for potential fallbacks if the user does not have permission
     */

    angular.module('CCC.Assess').provider('RoutePermissionsService', function () {

        /*=========== SERVICE PROVIDER API ===========*/

        var RoutePermissionsService;

        var permissionList = [];

        // this is how we can set the permissions list that is used in the RoutePermissionsService
        this.setPermissions = function (permissions) {
            permissionList = permissions;
        };


        /*============ SERVICE DECLARATION ============*/

        this.$get = [

            '$state',

            function ($state) {

                /*============ PRIVATE VARIABLES AND METHODS ============*/


                /*============ SERVICE API ============*/

                RoutePermissionsService = {

                    /*============ PUBLIC METHODS ============*/

                    hasPermission: function (perm) {

                        // if perm is an array, it will be treated like an OR within the nested list
                        if (angular.isArray(perm)) {

                            var hasAtLeastOnePerm = false;

                            _.each(perm, function (permItem) {

                                hasAtLeastOnePerm = hasAtLeastOnePerm || permissionList.indexOf(permItem) !== -1;
                            });

                            return hasAtLeastOnePerm;

                        } else {
                            return permissionList.indexOf(perm) !== -1;
                        }
                    },

                    getDefaultViews: function () {
                        return _.filter($state.get(), function (view) {
                            return view.defaultView && RoutePermissionsService.getViewIsAllowed(view);
                        });
                    },

                    getViewIsAllowed: function (view) {

                        return _.reduce(view.permissions || [], function (memo, permission) {
                            return memo && RoutePermissionsService.hasPermission(permission);
                        }, true);
                    }
                };


                /*============ LISTENERS ===========*/

                /*============ INITIALIZATION ============*/


                /*============ SERVICE PASSBACK ============*/

                return RoutePermissionsService;
            }
        ];
    });


    /*============ NOW WIRE UP THE ROUTE LISTENER AND PERMISSIONS LOGIC ============*/

    angular.module('CCC.Assess').run([

        '$rootScope',
        '$state',
        'RoutePermissionsService',

        function ($rootScope, $state, RoutePermissionsService) {

            // store current white listed query params
            $rootScope.$on('$stateChangeStart', function (e, toState, toParams) {

                // if the user is not allowed to see this view, then try to find one they can view
                if (!RoutePermissionsService.getViewIsAllowed(toState)) {

                    var defaultViews = RoutePermissionsService.getDefaultViews();

                    // there should always be at least one view accessible to everyone, could even be a default "your lost" page
                    if (defaultViews.length) {

                        e.preventDefault();
                        $state.go(defaultViews[0], {}, {location: 'replace'});

                    } else {
                        throw new Error('RouterPermissionService.noDefaultView found for permissions:' + JSON.stringify(toState.permissions + '. Please set defaultView on at least one route config.'));
                    }
                }
            });
        }
    ]);

})();
