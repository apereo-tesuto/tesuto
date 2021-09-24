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
     * Wrapper for Bootstrap Notify plugin
     * We do this so we can pepper on some additional logic to hide notifications on view / route change
     * We also added an optional UID property to prevent duplicate notifications
     */

    angular.module('CCC.Components').service('NotificationService', [

        '$rootScope',
        '$timeout',

        function ($rootScope, $timeout) {

            /*============ SERVICE DECLARATION ============*/

            var NotificationService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var notifications = [];
            var guid = 0;

            var getGuid = function () {
                return guid++;
            };

            var closeNotifications = function () {
                $.notifyClose();
            };

            var closeNotificationsByUid = function (uid) {

                _.each(notifications, function (notification, index) {

                    if (notification.uid === uid) {
                        $timeout(notification.notification.close, index);
                    }
                });
            };

            var closeNotificationByGuid = function (notificationGuid) {

                for (var i=0; i < notifications.length; i++) {
                    if (notifications[i].guid === notificationGuid) {
                        notifications.splice(i, 1);
                        break;
                    }
                }
            };


            /*============ SERVICE DEFINITION ============*/

            NotificationService = {

                open: function (options, settings) {

                    var newGuid = getGuid();

                    // prevent triggering duplicate notifications
                    if (options.uid) {
                        closeNotificationsByUid(options.uid);
                    }

                    var oldOnClosed = settings.onClosed || $.noop;

                    // Hook into jQuery notification onClose()
                    settings.onClosed = function () {
                        closeNotificationByGuid(newGuid);
                        oldOnClosed.call(this, arguments);
                    };

                    var newNotification = $.notify(options, settings);

                    notifications.push({
                        notification: newNotification,
                        guid: newGuid,
                        uid: options.uid
                    });

                    return newNotification;
                },

                closeAll: closeNotifications
            };


            /*============ LISTENERS ============*/

            $rootScope.$on('ViewManagerEntity.popView', function () {
                closeNotifications();
            });

            $rootScope.$on('ViewManagerEntity.pushView', function () {
                closeNotifications();
            });

            $rootScope.$on('$stateChangeStart', function () {
                closeNotifications();
            });


            /*============ SERVICE PASSBACK ============*/

            return NotificationService;

        }
    ]);

})();
