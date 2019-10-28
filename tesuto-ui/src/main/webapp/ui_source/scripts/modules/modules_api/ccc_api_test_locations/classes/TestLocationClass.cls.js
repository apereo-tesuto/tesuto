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

    angular.module('CCC.API.TestLocations').factory('TestLocationClass', [

        '$q',
        '$timeout',
        'Cloneable',
        'TestLocationsAPIService',

        function ($q, $timeout, Cloneable, TestLocationsAPIService) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var TestLocationClass = function (configs) {

                var defaults = {
                    id: null,
                    assessments: [],
                    capacity: null,
                    collegeId: null,
                    collegeName: null,
                    enabled: true,
                    locationStatus: 'ACTIVE',
                    locationType: 'ON_SITE',
                    name: ''
                };

                // merge in the defaults onto the instance
                var that = this;
                // extend from the Cloneable class
                Cloneable.call(that);
                $.extend(true, that, defaults, configs || {});

                // coerce some values


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                /*=============== PUBLIC PROPERTIES =============*/

                /*=============== PUBLIC METHODS =============*/

                that.serialize = function () {

                    return {
                        id: that.id,
                        assessments: that.assessments,
                        testLocationDto: {
                            name: that.name,
                            capacity: that.capacity,
                            collegeId: that.collegeId,
                            collegeName: that.collegeName,
                            enabled: that.enabled,
                            locationStatus: that.locationStatus,
                            locationType: that.locationType
                        }
                    };
                };

                that.create = function () {
                    return TestLocationsAPIService.createTestLocation(that.serialize()).then(function (testLocationId) {
                        // update this objects unique id once the id is returned after creation
                        that.id = testLocationId;
                    });
                };

                that.update = function () {
                    return TestLocationsAPIService.updateTestLocation(that.serialize());
                };

                that.save = function () {

                    if (that.testEventId !== false) {
                        return that.update();
                    } else {
                        return that.create();
                    }
                };

                that.delete = function () {

                    if (that.testEventId === false) {
                        return;
                    }

                    return TestLocationsAPIService.deleteTestLocation(that.testEventId).then(function (results) {
                        that.canceled = true;
                        return results;
                    });
                };

                that.setEnabled = function (isEnabled) {

                    return TestLocationsAPIService.setTestLocationEnabled(that.id, isEnabled).then(function (results) {
                        that.enabled = isEnabled;
                        return results;
                    });
                };


                /*=============== INITIALIZTION =============*/

            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return TestLocationClass;
        }
    ]);

})();
