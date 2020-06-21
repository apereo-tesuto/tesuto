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

    var STATUS_ORDER_MAP = {
        'READY': 0,
        'PAUSED': 1,
        'IN_PROGRESS': 2,
        'PENDING_SCORING': 2,
        'COMPLETE': 3,
        'ASSESSED': 3
    };

    var STATUS_URGENCY_ORDER_MAP = {
        'PAUSED': 0,
        'IN_PROGRESS': 1,
        'PENDING_SCORING': 1,
        'READY': 2,
        'COMPLETE': 3,
        'ASSESSED': 3
    };



    /*========== ENTRY POINT ON DOCUMENT READY FROM THE MAIN TEMPLATE ============*/

    angular.module('CCC.API.Activations', ['CCC.Assess']);


    /*======================== CONSTANTS ========================*/

    // the ammount of time in milliseconds if the user should be warned it's about to expire
    angular.module('CCC.API.Activations').constant('EXPIRATION_WARNING_TIME_MS', 24 * (60 * 60 * 1000));

    // sort by status then by student name
    angular.module('CCC.API.Activations').constant('ACTIVATION_STATUS_COMPARATOR', function (a, b) {

        if (a.status === b.status) {

            if (a._nameCompare < b._nameCompare) {
                return -1;
            } else if (a._nameCompare > b._nameCompare) {
                return 1;
            } else {
                return 0;
            }

        } else {
            return STATUS_ORDER_MAP[a.status] - STATUS_ORDER_MAP[b.status];
        }
    });

    // sort on status and then by assessment name
    angular.module('CCC.API.Activations').constant('ACTIVATION_URGENCY_COMPARATOR', function (a, b) {

        if (a.status === b.status) {

            if (a._expirationDateUnix < b._expirationDateUnix) {
                return -1;
            } else if (a._expirationDateUnix > b._expirationDateUnix) {
                return 1;
            } else {
                return 0;
            }

        } else {
            return STATUS_URGENCY_ORDER_MAP[a.status] - STATUS_URGENCY_ORDER_MAP[b.status];
        }
    });

    angular.module('CCC.API.Activations').constant('ACTIVATION_STATUS_ORDER_MAP', STATUS_ORDER_MAP);
    angular.module('CCC.API.Activations').constant('ACTIVATION_STATUS_URGENCY_ORDER_MAP', STATUS_URGENCY_ORDER_MAP);

    angular.module('CCC.API.Activations').constant('ACTIVATION_STATUS_TITLE_MAP', {
        'READY': 'Ready',
        'IN_PROGRESS': 'In Progress',
        'COMPLETE': 'Complete',
        'PAUSED': 'Paused',
        'EXPIRED': 'Expired',
        'INCOMPLETE': 'Incomplete',
        'PENDING_SCORING': 'Pending Scoring',
        'ASSESSED': 'Assessed'
    });


    /*======================== LOAD CONFIGURATIONS ========================*/


})();
