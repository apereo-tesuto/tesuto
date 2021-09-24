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
     * Activation Class
     */

    angular.module('CCC.API.Activations').factory('ActivationClass', [

        '$rootScope',
        '$translate',
        '$q',
        'Moment',
        'EXPIRATION_WARNING_TIME_MS',
        'ActivationsAPIService',
        'AssessmentLaunchService',
        'StudentsAPIService',
        'StudentClass',
        'Cloneable',

        function ($rootScope, $translate, $q, Moment, EXPIRATION_WARNING_TIME_MS, ActivationsAPIService, AssessmentLaunchService, StudentsAPIService, StudentClass, Cloneable) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var ActivationClass = function (activationData) {

                var defaults = {

                    // the activationId
                    activationId: null,

                    locationId: null,

                    // student info for this activation (must be an instance of StudentClass)
                    student: null,

                    // general dumping ground for accommodations and other information
                    attributes: {},

                    // the assessment informations
                    assessmentScopedIdentifier: null,
                    assessmentTitle: null,

                    // status and state related properties
                    status: null,
                    statusChangeHistory: [],
                    timeElapsed: null,
                    startDate: null,
                    endDate: null,
                    completedDate: null,
                    isInFuture: false,

                    // Location Service sets these attributes
                    locationLabel: null,  //test center
                    collegeName: null
                };

                // merge in the defaults onto the instance
                var that = this;
                // extend from the Cloneable class
                Cloneable.call(that);
                $.extend(true, that, defaults, activationData || {});

                // coerce some values
                that.student = that.student || new StudentClass({});
                that.attributes = $.extend(true, {}, that.attributes || {}); // the server may send null, and we must have some bare mimimums for attributes

                that.attributes.accommodations = that.attributes.accommodations || [];
                that.attributes.accommodationsOther = that.attributes.accommodationsOther || '';

                // attribute values are always stored as strings, so if this is instance is being created form data from the server then ensure we coerce the attributes value into an array of objects
                if (angular.isString(that.attributes.accommodations)) {
                    that.attributes.accomodations = JSON.parse(that.attributes.accommodations);
                }


                /*============== PRIVATE VARIABLES =============*/

                var assessmentSessionUrl = false;
                var proctorCode = false;


                /*============== PRIVATE METHODS =============*/

                var setAsyncCompletionString = function () {
                    if (that.status === 'COMPLETE') {

                        var completeMoment = new Moment(that.statusChangeHistory[0].changeDate);
                        that.completedDate = new Moment(completeMoment).format("MMMM D, YYYY");
                        $translate('CCC_STUDENT.ACTIVATIONS.COMPLETED').then(function (completionString) {
                            that.asyncCompletionString = completionString + ' ' + completeMoment.calendar();
                        });

                    } else if (that.status === 'DEACTIVATED') {

                        var deactivateMoment = new Moment(that.statusChangeHistory[0].changeDate);
                        $translate('CCC_STUDENT.ACTIVATIONS.DEACTIVATED').then(function (completionString) {
                            that.asyncCompletionString = completionString + ' ' + deactivateMoment.calendar();
                        });

                    } else {

                        var expirationMoment = new Moment(that.endDate);
                        $translate('CCC_STUDENT.ACTIVATIONS.EXPIRES_IN').then(function (completionString) {
                            that.asyncCompletionString = completionString + ' ' + expirationMoment.fromNow(true);
                        });
                    }
                };

                var initialize = function () {
                    setAsyncCompletionString();
                };


                /*=============== PUBLIC PROPERTIES =============*/

                that.asyncCompletionString = "";


                /*=============== PUBLIC METHODS =============*/

                that.start = function (proctorCode_in) {

                    if (proctorCode_in) {
                        proctorCode = proctorCode_in;
                    }

                    return ActivationsAPIService.startActivation({id: that.activationId, proctorCode: proctorCode}).then(function (assessmentSessionUrl_in) {
                        assessmentSessionUrl = assessmentSessionUrl_in;
                        that.open();
                    });
                };

                that.authorizeProctorCode = function (proctorCode_in) {

                    // once they authorize, store this activation proctor code, they can then start with this stored
                    proctorCode = proctorCode_in;

                    return ActivationsAPIService.authorizeActivation({id: that.activationId, proctorCode: proctorCode});
                };

                that.authorizeWithoutPasscode = function () {
                    return ActivationsAPIService.authorizeActivationWithoutPasscode({id: that.activationId});
                };

                that.open = function () {
                    AssessmentLaunchService.openAssessmentSession(assessmentSessionUrl);
                };

                that.deactivate = function (reason) {
                    return ActivationsAPIService.cancelActivation(that.activationId, reason).then(function () {
                        that.status = 'DEACTIVATED';
                        that.reason = reason;

                    }, function (err) {
                        return $q.reject(err);
                    });
                };

                // this will search through the statusChangeHistory and add properties as needed (userName for now)
                that.hydrateStatusChangeHistory = function () {

                    // find all userIds that don't have corresponding userNames
                    var userIdList = _.unique(
                        _.map(
                            _.filter(
                                that.statusChangeHistory,
                                function (historyEntry) {
                                    return historyEntry.userId !== undefined && historyEntry.userId !== null && !historyEntry.userName;
                                }
                            ),
                            function (historyEntry) {
                                return historyEntry.userId;
                            }
                        )
                    );

                    if (userIdList.length) {

                        return StudentsAPIService.studentListSearch(userIdList).then(function (students) {

                            var userIdToUserNameMap = _.reduce(students, function (memo, student) {
                                memo[student.cccId] = student.displayName;
                                return memo;
                            }, {});

                            _.each(that.statusChangeHistory, function (historyEntry) {

                                if (userIdToUserNameMap[historyEntry.userId] && !historyEntry.userName) {
                                    historyEntry.userName = userIdToUserNameMap[historyEntry.userId];
                                }
                            });
                        });

                    } else {
                        return $q.when(true);
                    }
                };

                that.update = function () {

                    // we need to do some small manipulation of the attributes for the server but we don't want to alter the data structure of this instance
                    var attributes = $.extend(true, {}, that.attributes);

                    // all values in attributes must be a string
                    attributes.accommodations = JSON.stringify(that.attributes.accommodations);

                    return ActivationsAPIService.updateActivation({
                        activationId: that.activationId,
                        assessmentScopedIdentifier: that.assessmentScopedIdentifier,
                        attributes: attributes,
                        locationId: that.locationId
                    });
                };


                /*=============== MORE PUBLIC PROPERTIES =============*/

                // set warning flag for endDate
                var timeTillExpiration_MS = new Moment(that.endDate).diff(new Moment());
                that.showExpirationWarning = that.status !== 'COMPLETE' && timeTillExpiration_MS <= EXPIRATION_WARNING_TIME_MS;

                // used for sorting by date (NOTE: if it was just created it wont have any status change history)
                if (that.statusChangeHistory.length) {
                    that._completedDateTimeStamp = new Moment.utc(that.statusChangeHistory[0].changeDate).valueOf();
                } else {
                    that._completedDateTimeStamp = new Moment.utc(that.createDate).valueOf();
                }

                that._expirationDateUnix = new Moment.utc(that.endDate).unix();

                var firstName = that.student.firstName ? that.student.firstName : '';
                var lastName  = that.student.lastName  ? that.student.lastName  : '';
                var middleName = that.student.middleName ? that.student.middleName : '';

                // add a string that can be used to quickly sort by student name
                that._nameCompare = $.trim(lastName.toLowerCase()) + $.trim(middleName.toLowerCase()) + $.trim(firstName.toLowerCase());

                // adding a string specific for searching (with a space to prevent false positives on searches that span both last and full name)
                that._nameSearch = $.trim(lastName.toLowerCase()) + ' ' + $.trim(middleName.charAt(0).toLowerCase()) + ' ' + $.trim(firstName.toLowerCase());

                that.isInFuture = new Moment().isBefore(new Moment.utc(that.startDate));


                /*=============== INITIALIZTION =============*/

                initialize();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return ActivationClass;
        }
    ]);

})();


