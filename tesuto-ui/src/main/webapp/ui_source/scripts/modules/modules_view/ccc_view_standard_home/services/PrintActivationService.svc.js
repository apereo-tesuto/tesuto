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
     * Duties include listening for requests to print activations
     */

    angular.module('CCC.View.Home').service('PrintActivationService', [

        '$rootScope',
        'LocationService',
        'AssessmentsAPIService',
        'NotificationService',

        function ($rootScope, LocationService, AssessmentsAPIService, NotificationService) {

            /*============ SERVICE DECLARATION ============*/

            var PrintActivationService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var printActivation = function (activation) {

                LocationService.getTestCenterById(activation.locationId).then(function (location) {

                    // TODO: Remove this condition once we are passing CurrentUser affiliated colleges into the activation card. *See notes in ccc-activation-card*.
                    if (location === undefined || location === null) {

                        NotificationService.open({
                            icon: 'fa fa-exclamation-triangle',
                            title: 'You are not authorized to print this assessment.',
                            message: '',
                            uid: 'PrintActivationService.errorNoLocation'
                        },
                        {
                            delay: 0,
                            type: "warning",
                            allow_dismiss: true
                        });

                        return;
                    }

                    AssessmentsAPIService.printAssessment(activation.assessmentScopedIdentifier, location.college.cccId, activation.userId).then(function () {

                        $rootScope.$broadcast('PrintActivationService.print', activation);

                    }).catch(function (error) {

                        // proctor is not authorized to print activation
                        if (error.status === 403) {

                            NotificationService.open({
                                icon: 'fa fa-exclamation-triangle',
                                title: 'You are not authorized to print this assessment.',
                                message: '',
                                uid: 'PrintActivationService.error403'
                            },
                            {
                                delay: 0,
                                type: "warning",
                                allow_dismiss: true
                            });
                        }

                        // activation has changed status, possibly complete or deactivated
                        if (error.status === 500) {

                            NotificationService.open({
                                icon: 'fa fa-exclamation-triangle',
                                title: 'Ooops! We weren\'t able to print this assessment. Refresh the page and try again.',
                                message: '',
                                uid: 'PrintActivationService.error500'
                            },
                            {
                                delay: 0,
                                type: "warning",
                                allow_dismiss: true
                            });
                        }
                    });
                });
            };


            /*============ SERVICE DEFINITION ============*/

            PrintActivationService = {

                print: function (activation) {
                    return printActivation(activation);
                }
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return PrintActivationService;

        }
    ]);

})();
