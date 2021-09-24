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

    angular.module('CCC.Activations').service('ActivationLogService', [

        '$q',
        'Moment',
        '$translate',

        function ($q, Moment, $translate) {

            /*============ SERVICE DECLARATION ============*/

            var ActivationLogService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            // http://momentjs.com/docs/#/displaying/
            var LOG_FORMAT = 'L hh:mma';

            var getCreationEntryPromise = function (activation) {

                return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.CREATED', {
                    creatorName: activation.creatorName
                }).then(function (translation) {
                    return {
                        timestamp: activation.createDate,
                        timestampReadable: new Moment(activation.createDate).format(LOG_FORMAT),
                        message: translation
                    };
                });
            };

            var isEdited = function (historyEntry) {
                // If activation has been edited, server will return 'Updated' as reason.
                return historyEntry.reason === 'Updated';
            };

            var isCollision = function (historyEntry) {
                return historyEntry.reason === 'Collision';
            };

            var isTestEventEdited = function (historyEntry) {
                // If activation has been edited, server will return 'Updated' as reason.
                return historyEntry.reason === 'Updated test event';
            };

            var getEditedHistoryMessage = function (historyEntry) {
                return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.EDITED', {
                    userName: historyEntry.userName
                });
            };

            var getEditedTestEventHistoryMessage = function (historyEntry) {
                return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.EDITED_TEST_EVENT', {
                    userName: historyEntry.userName
                });
            };

            var getCollsionHistoryMessage = function (historyEntry) {
                return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.COLLISION', {
                    userName: historyEntry.userName
                });
            };

            var getHistoryMessage = function (historyEntry) {

                // first a few status agnostic cases
                if (isTestEventEdited(historyEntry)) {
                    historyEntry.showReason = true;
                    return getEditedTestEventHistoryMessage(historyEntry);
                }
                if (isEdited(historyEntry)) {
                    historyEntry.showReason = false;
                    return getEditedHistoryMessage(historyEntry);
                }
                if (isCollision(historyEntry)) {
                    historyEntry.showReason = true;
                    return getCollsionHistoryMessage(historyEntry);
                }

                // now some status dependent cases
                switch (historyEntry.newStatus) {

                    case 'IN_PROGRESS':
                        return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.IN_PROGRESS', {
                            userName: historyEntry.userName
                        });

                    case 'COMPLETE':
                        return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.COMPLETE', {
                            userName: historyEntry.userName
                        });

                    case 'DEACTIVATED':
                        return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.DEACTIVATED', {
                            userName: historyEntry.userName
                        });

                    case 'PAUSED':
                        return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.PAUSED', {
                            userName: historyEntry.userName
                        });

                    case 'READY':
                        return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.REACTIVATED', {
                            userName: historyEntry.userName
                        });

                    case 'PENDING_SCORING':
                        return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.PENDING_SCORING', {
                            userName: historyEntry.userName
                        });

                    case 'ASSESSED':
                        return $translate('CCC_ACTIVATIONS.LOGS.STATUS_MESSAGES.ASSESSED', {
                            userName: historyEntry.userName
                        });

                    default:
                        return $q.when('no message configured');
                }
            };

            var getStatusChangeHistoryPromise = function (historyEntry) {

                return getHistoryMessage(historyEntry).then(function (historyMessage) {
                    return {
                        timestamp: historyEntry.changeDate,
                        timestampReadable: new Moment(historyEntry.changeDate).format(LOG_FORMAT),
                        message: historyMessage,
                        reason: historyEntry.reason
                    };
                });
            };


            /*============ SERVICE DEFINITION ============*/

            ActivationLogService = {

                // NOTE: We use promises because we use angular translate which gives promises on some translations
                getLogsForActivation: function (activation) {

                    var logPromises = [];

                    // first add on the creation details that come from the activation itself
                    logPromises.push(getCreationEntryPromise(activation));

                    // then rip through the history and add promises for each of those
                    _.each(activation.statusChangeHistory, function (historyEntry) {
                        logPromises.push(getStatusChangeHistoryPromise(historyEntry));
                    });

                    return $q.all(logPromises).then(function (logs) {
                        return _.sortBy(logs, function(log){ return log.timestamp; });
                    });
                }
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return ActivationLogService;
        }
    ]);

})();

