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
     * We write this entity in the provider format so we can expose some provider methods to be used:
     * setServiceBasePath()         -pass in the base path for all service calls
     * setDefaultVersion()          -if no version is set for the api, use this default version
     * setAPIVersionMap()           -pass in the api version map, key value pairs where the key is the api path
     */

    angular.module('CCC.Assess').provider('VersionableAPIClass', function () {

        /*============ PROVIDER PRIVATE VARIABLES / METHODS ============*/

        // default version path
        var DEFAULT_VERSION = '';

        var apiVersionMap = {};

        var getVersionedPath = function () {
            var that = this;
            var resourcePath = that.resource ? '/' + that.resource : '';
            return that.path + that.version + resourcePath;
        };


        /*=========== PROVIDER API ===========*/

        this.setDefaultVersion = function (defaultVersion_in) {
            DEFAULT_VERSION = defaultVersion_in;
        };

        this.setAPIVersionMap = function (apiVersionMap_in) {
            apiVersionMap = apiVersionMap_in;
        };


        /*============ ENTITY DECLARATION ============*/

        this.$get = [

            '$rootScope',

            function ($rootScope) {

                // default properties for the class
                var defaults = {
                    id: '',
                    path: '',       // will be pulled in from the apiVersionMap if not set
                    version: null   // if not set, then the DEFAULT_VERSION_PATH will be used (which can be configured via the provider)
                };

                var VersionableAPIClass = function (entityData) {

                    // we simply tack on merged properties during initialization
                    var that = this;
                    $.extend(true, that, defaults, entityData, {});


                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ PUBLIC METHODS ============*/

                    that.getBasePath = function () {
                        return getVersionedPath.call(that);
                    };


                    /*============ INITIALIZATION ============*/

                    that.path = (apiVersionMap[that.id] && apiVersionMap[that.id].baseUrl) ? apiVersionMap[that.id].baseUrl : 'WARNING_NO_BASE_URL_SET/';

                    that.version = (apiVersionMap[that.id] && apiVersionMap[that.id].version !== undefined && apiVersionMap[that.id].version !== null) ? (apiVersionMap[that.id].version) : DEFAULT_VERSION;
                };


                /*============ PUBLIC STATIC METHODS ============*/

                /*============ LISTENERS ============*/


                /*============ ENTITY PASSBACK ============*/

                return VersionableAPIClass;

            }
        ];
    });

})();
