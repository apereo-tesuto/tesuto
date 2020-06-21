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

    angular.module('CCC.API.Events').factory('EventClass', [

        'Cloneable',
        'Moment',
        'EventsAPIService',

        function (Cloneable, Moment, EventsAPIService) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var EventClass = function (eventConfigs) {

                var defaults = {
                    testEventId: false,
                    name: '',
                    startDate: false,
                    endDate: false,
                    collegeId: false,
                    testLocationId: false,
                    proctorFirstName: '',
                    proctorLastName: '',
                    proctorEmail: '',
                    proctorPhone: '',
                    assessmentIdentifiers: [],
                    deliveryType: false,
                    students:[]
                };

                // var defaults = {
                //     testEventId: false,
                //     name: 'Test Event',
                //     startDate: new Moment().valueOf(),
                //     endDate: new Moment.add('d',1).valueOf(),
                //     collegeId: '71',
                //     testLocationId: '03f8657b-85c6-4796-8b73-64b3b806058e',
                //     proctorFirstName: 'Proctor',
                //     proctorLastName: 'Person',
                //     proctorEmail: 'proctor@proctor.com',
                //     proctorPhone: '',
                //     deliveryType: false,
                //     assessmentIdentifiers: [
                //         {
                //             identifier: "a001",
                //             namespace: "DEVELOPER"
                //         }
                //     ],
                //     students:[]
                // };

                // merge in the defaults onto the instance
                var that = this;
                // extend from the Cloneable class
                Cloneable.call(that);
                $.extend(true, that, defaults, eventConfigs || {});

                // coerce some values


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                /*=============== PUBLIC PROPERTIES =============*/

                /*=============== PUBLIC METHODS =============*/

                that.serialize = function () {

                    var serializeModel = {
                        name: that.name,
                        startDate: Moment(that.startDate).startOf('day').valueOf(),
                        endDate: Moment(that.endDate).endOf('day').valueOf(),
                        collegeId: that.collegeId,
                        testLocationId: that.testLocationId,
                        proctorFirstName: that.proctorFirstName,
                        proctorLastName: that.proctorLastName,
                        proctorEmail: that.proctorEmail,
                        deliveryType: that.deliveryType,
                        assessmentIdentifiers: that.assessmentIdentifiers
                    };

                    if ($.trim(that.proctorPhone)) {
                        serializeModel.proctorPhone = that.proctorPhone;
                    }

                    return serializeModel;
                };

                that.create = function () {
                    return EventsAPIService.create(that.serialize()).then(function (testEventId) {
                        // update this objects unique id once the id is returned after creation
                        that.testEventId = testEventId;
                    });
                };

                that.update = function () {

                    // for update we need to add in the testEventId
                    // TODO: maybe the endpoint should be test-event/<testEventId> to avoid this
                    var serializedEvent = that.serialize();
                    serializedEvent.testEventId = that.testEventId;

                    return EventsAPIService.update(serializedEvent);
                };

                that.save = function () {

                    if (that.testEventId !== false) {
                        return that.update();
                    } else {
                        return that.create();
                    }
                };

                that.cancel = function () {

                    if (that.testEventId === false) {
                        return;
                    }

                    return EventsAPIService.cancelEvent(that.testEventId).then(function (results) {
                        that.canceled = true;
                        return results;
                    });
                };

                that.updateStudents = function (studentIds) {
                    return EventsAPIService.updateStudents(that.testEventId, studentIds).then(function (results) {
                        that.students = studentIds;
                        return results;
                    });
                };


                /*=============== INITIALIZTION =============*/

            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return EventClass;
        }
    ]);

})();
