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

    angular.module('CCC.Components').factory('ActivityServiceClass', [

        '$rootScope',
        '$timeout',
        '$q',
        'ActivityChannelClass',

        function ($rootScope, $timeout, $q, ActivityChannelClass) {

            /*============ PRIVATE STATIC CONSTANTS ============*/

            var ONE_MINUTE = 1000 * 60;

            // TODO these need to be configurable per class instance
            var ACTIVITY_FLUSH_INTERVAL = ONE_MINUTE * 5;       // how often do we automatically flush and send all unrecorded activity to the server
            var MANDATORY_POST_ATTEMPTS = 3;                    // how many subsequent attempts to retry flushing before giving up due to posting service failure?
            var FAILED_POST_RETRY_INTERVAL = 5000;              // how long to wait to retry a failed post attempt

            // The retry will halt any new flushes but it doesn't make since to have a silly configuration
            if (MANDATORY_POST_ATTEMPTS * FAILED_POST_RETRY_INTERVAL >= ACTIVITY_FLUSH_INTERVAL * 0.5) {
                throw new Error('ActivityServiceClass.SillyRetryInterval');
            }


            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            // the default transform for all raw angular events
            var identityTransform = function (eventKey, eventObj, eventArgs) {
                return {
                    eventKey: eventKey,         // the key of the event
                    eventObj: eventObj,         // the first argument should always be an angular event
                    eventArgs: eventArgs        // any additional arguments passed along with the event, could be null
                };
            };


            /*============ CLASS DECLARATION ============*/

            var ActivityServiceClass = function (activityServiceClassConfigs) {

                /*============ CLASS DEFAULT CONFIGS ============*/

                var defaults = {
                    channels: [// one or more channels should be added which will each recieve the activity list, this allows for you to also transform and send events to multiple endpoints
                        // {
                        //     id: 'ccc', <- a unique id for this channel
                        //     required: true/false,  <- a flag to tell the ActivityService that we would like to keep trying if it fails
                        //     eventFilterMap: {}, <- a key value hash map, key is angular event name, value is a transform function that accepts event arguments and returns a data object used in postActivity
                        //     postActivity: function (activityList) { return promise; } <- a promise for posting a list of activity objects, can resolve or reject
                        // }
                    ],
                    eventTransform: identityTransform,      // an optional function to transform raw angular events into a new standard form desired by the user of this class, if not overwritten identityTransform will be used
                    keepAllActivities: false                // decide if the UI should cache all activities even if logged, this should only be set to true for testing purposes and visualiztion
                };

                /*============ MERGE IN DEFAULTS ============*/

                var that = this;
                $.extend(true, that, defaults, activityServiceClassConfigs || {});


                /*=============== PRIVATE VARIABLES AND METHODS ============*/

                var activityChannelInstanceMap = {};        // a map of the activityChannelclass instances by channeldId
                var eventsToChannelMap = {};                // a map of events to channels that require them be passed on
                var channelActivityListMap = {};            // a map of activities by channelId
                var persistedChannelActivityListMap = {};   // a map of persisted activites by channelId

                var getChannelIds = function () {
                    return _.map(that.channels, function (channel) {
                        return channel.id;
                    });
                };

                // here is a method that will post the activity list to specified ActivityChannelClass instances
                // it will keep trying to resolve all channels a max of MANDATORY_POST_ATTEMPTS attempts
                var flushChannelActivities = function (currentAttempts, channelIds) {

                    var deferred = $q.defer();

                    // first check if we have maxed out on attempts
                    if (currentAttempts === MANDATORY_POST_ATTEMPTS) {

                        deferred.reject();

                    // otherwise let's attempt to persist
                    } else {

                        var postActivityStatus = {
                            status: false,
                            successfulChannelIds: [],
                            failedChannelIds: [],
                            failedRequiredChannelIds: []
                        };

                        // setup an array of promises for each channel
                        var channelFlushPromises = [];

                        // loop through each channel and post the corresponding activityList if there are activities associated with that channel
                        _.each(channelIds, function (channelId) {

                            var activityChannelInstance = activityChannelInstanceMap[channelId];

                            // if this channels has events add a promise for it's postActivity method
                            if (channelActivityListMap[channelId] && channelActivityListMap[channelId].length) {

                                // for each channel post, track it's success or failure
                                var channelPostPromise = activityChannelInstance.postActivity(channelActivityListMap[channelId]).then(function () {

                                    // keep a UI cache of all persisted activities if this flag is set ( used for testing and visualization )
                                    if (that.keepAllActivities) {

                                        // make sure we have a bucket to store the activities
                                        if (!persistedChannelActivityListMap[channelId]) {
                                            persistedChannelActivityListMap[channelId] = [];
                                        }
                                        persistedChannelActivityListMap[channelId] = persistedChannelActivityListMap[channelId].concat(channelActivityListMap[channelId]);
                                    }

                                    // on success clear out the bucket for this channel and track the success
                                    channelActivityListMap[channelId] = [];
                                    postActivityStatus.successfulChannelIds.push(channelId);

                                }, function () {

                                    // on failure mark failure and/or if this is a required channel that failed
                                    postActivityStatus.failedChannelIds.push(channelId);
                                    if (activityChannelInstance.required) {
                                        postActivityStatus.failedRequiredChannelIds.push(channelId);
                                    }
                                });

                                channelFlushPromises.push(channelPostPromise);
                            }
                        });

                        // now wait for all channels to finish and decide if we need to try again or not based on status
                        $q.all(channelFlushPromises).then(function () {

                            // here is where the recursive logic starts to ensure all required adpaters have resolved within MANDATORY_POST_ATTEMPTS attempts
                            if (postActivityStatus.failedRequiredChannelIds.length === 0) {

                                deferred.resolve();

                            } else {

                                // we have failed so use the FAILED_POST_RETRY_INTERVAL to try again
                                $timeout(function () {

                                    // here is where we start retrying with all failed required channels from the previous attempt
                                    flushChannelActivities(currentAttempts + 1, postActivityStatus.failedRequiredChannelIds).then(function () {
                                        deferred.resolve();
                                    }, function () {
                                        deferred.reject();
                                    });

                                }, FAILED_POST_RETRY_INTERVAL);
                            }
                        });
                    }

                    return deferred.promise;
                };

                var flushActivityLogTimeout = false;
                var flushActivityLog;

                var startFlushInterval = function () {
                    flushActivityLogTimeout = $timeout(flushActivityLog, ACTIVITY_FLUSH_INTERVAL);
                };

                /**
                 * Attempt to flush ALL unpersisted activites across ALL channels
                 * @return {promise} This promise will be resolved if ALL channel persistance were successful even if it took MANDATORY_POST_ATTEMPTS attempts. It will reject if it could not do so.
                 */
                flushActivityLog = function () {

                    var flushActivityDeferred = $q.defer();     // this is the promise returned which represents the complete success or failure of a flush

                    // immediatly kill any pending flushActivityLog calls. NOTE: Eventually we need to startFlushInterval() to keep the flush loop going
                    $timeout.cancel(flushActivityLogTimeout);

                    // A high level promise around flushing ALL channels, once done, start the next interval
                    flushChannelActivities(0, getChannelIds()).then(function () {

                        flushActivityDeferred.resolve();
                        $rootScope.$broadcast('ActivityService.activityUpdated');

                    }, function () {

                        flushActivityDeferred.reject();

                    }).finally(function (){
                        startFlushInterval();
                    });

                    return flushActivityDeferred.promise;
                };

                // a list of keys that are used to develop other parts of the object, these and any others with circular references should be included here
                // basically keys that should not be included in every event, especially those with circular references
                var badKeys = ['eventObj', 'eventArgs'];

                // a method that makes a top level copy of an activity without any badKeys and their values
                var cleanActivity = function (activity) {

                    var freshObj = {};

                    for (var key in activity) {
                        if (badKeys.indexOf(key) === -1) {
                            freshObj[key] = activity[key];
                        }
                    }

                    return freshObj;
                };


                // this method adds the final transformed activity object into each channels queue
                var addToActivities = function (eventKey, eventObj, eventArgs) {

                    // run this data through the transform for all events
                    var transformedEvent = that.eventTransform(eventKey, eventObj, eventArgs);

                    // next, run through all channel buckets that need to record this event
                    var channelIdsForEventKey = eventsToChannelMap[eventKey];
                    _.each(channelIdsForEventKey, function (channelId) {

                        // make sure we create a bucket if it doesn't exist
                        if (!channelActivityListMap[channelId]) {
                            channelActivityListMap[channelId] = [];
                        }

                        // we give the channel a chance to transform it before we save it for later use
                        var channelTransformedEvent = activityChannelInstanceMap[channelId].transformActivity(transformedEvent);

                        // lastly we strip out any objects not needed
                        channelTransformedEvent = cleanActivity(channelTransformedEvent);

                        // push in the transformed event into this channels queue
                        channelActivityListMap[channelId].push(channelTransformedEvent);
                    });

                    $rootScope.$broadcast('ActivityService.activityUpdated');
                };

                var wireUpActivityChannels = function () {

                    _.each(that.channels, function (channel) {

                        // setup some variables and maps for this channel
                        channelActivityListMap[channel.id] = [];
                        persistedChannelActivityListMap[channel.id] = [];
                        activityChannelInstanceMap[channel.id] = new ActivityChannelClass(channel);

                        // we need to add this channelId to each mapped event name it requires so when an event comes in we have a quick look up of channels that need a refernce to the activity
                        _.each(channel.eventFilterMap, function (eventFilterValue, eventFilterKey) {

                            // first create the eventName bucket if it doesn't already exist
                            if (!eventsToChannelMap[eventFilterKey]) {
                                eventsToChannelMap[eventFilterKey] = [];
                            }

                            // then push on this channel's id for later use
                            eventsToChannelMap[eventFilterKey].push(channel.id);
                        });
                    });

                    // now we have collected all possible events from all channels so let's add angular listeners
                    // NOTE: all events must be available to be picked up on $rootScope, so if you aren't firing from $rootScope, the event must be emitted and allowed to bubble up to $rootScope
                    _.each(eventsToChannelMap, function (eventValue, eventKey) {
                        $rootScope.$on(eventKey, function () {

                            // arguments is not an array so let's make one out of it
                            // so we can pass along the event args seperate from the event object itself
                            var args = Array.prototype.slice.call(arguments);

                            var eventObj = args ? args [0] : null;
                            var argArray = args && args.length > 1 ? args.splice(1, args.length - 1) : null;

                            // this format seperates out eventKey, the actual event object, and additional arugments fired out with the event
                            // the configured eventTransform will be passed these parameters in this order
                            addToActivities(eventKey, eventObj, argArray);
                        });
                    });
                };


                /*=============== PUBLIC METHODS =============*/

                /**
                 * Persist all stored activities through channels
                 * @return {promise} promise that will resolve or reject after MANDATORY_POST_ATTEMPTS attempts on the configured channels
                 */
                that.flushActivityLog = flushActivityLog;

                /**
                 * Clear all persisted activities, used in testing only
                 */
                that.clearPersistedActivities = function () {
                    for (var key in persistedChannelActivityListMap) {
                        if (persistedChannelActivityListMap.hasOwnProperty(key)) {
                            persistedChannelActivityListMap[key] = [];
                        }
                    }
                };

                /**
                 * For UI testing, you can setKeepAllActivities(true) and get both persisted and ready activities
                 * @return {[type]} [description]
                 */
                that.getCopyOfAllActivityForChannel = function (channelKey) {

                    var readyActivities = channelActivityListMap[channelKey].slice();
                    var persistedActivities = persistedChannelActivityListMap[channelKey].slice();

                    return {
                        ready: readyActivities,
                        persisted: persistedActivities
                    };
                };


                /*=============== INITIALIZTION =============*/

                wireUpActivityChannels();
                startFlushInterval();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ STATIC LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return ActivityServiceClass;
        }
    ]);

})();
