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
     * Re-Usable Utils
     */

    angular.module('CCC.Assess').service('CCCUtils', [

        '$rootScope',
        '$injector',
        '$q',

        function ($rootScope, $injector, $q) {

            /*============ SERVICE DECLARATION ============*/

            var CCCUtilsService;


            /*============ CONFIGURABLE PRIVATE VARIABLES / METHODS ============*/


            /*============ SERVICE DEFINITION ============*/

            CCCUtilsService = {

                // this will remove all empty strings, null/undefined fields from an object
                cleanObject: function (dirtyObject) {

                    var cleanObject = {};

                    for (var key in dirtyObject) {
                        if (dirtyObject.hasOwnProperty(key)) {

                            if (typeof dirtyObject[key] === 'string') {
                                if ($.trim(dirtyObject[key]) !== "") {
                                    cleanObject[key] = dirtyObject[key];
                                }
                            } else if (dirtyObject[key] !== null && dirtyObject[key] !== undefined) {
                                cleanObject[key] = dirtyObject[key];
                            }
                        }
                    }

                    return cleanObject;
                },

                coerce: function (ClassReference, data) {

                    // you can pass a class reference or the injectable name of the service as well
                    if (typeof ClassReference === 'string') {
                        ClassReference = $injector.get(ClassReference);
                    }

                    if (data instanceof Array) {

                        return _.map(data, function (classProps) {
                            return new ClassReference(classProps);
                        });

                    } else {
                        return new ClassReference(data);
                    }
                },

                // quick way to strip out properties you don't want
                objectWithoutProperties: function (obj, keys) {

                    var target = {};
                    for (var i in obj) {
                        if (Object.prototype.hasOwnProperty.call(obj, i)) {
                            if (keys.indexOf(i) === -1) {
                                target[i] = obj[i];
                            }
                        }
                    }
                    return target;
                }
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return CCCUtilsService;

        }
    ]);

})();

