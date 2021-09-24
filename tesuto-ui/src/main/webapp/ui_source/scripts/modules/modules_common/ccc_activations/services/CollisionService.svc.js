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
     * Collision detection for duplicate activations
     */

    angular.module('CCC.Activations').service('CollisionService', [

        '$rootScope',
        '$q',
        'ActivationSearchAPIService',
        'ModalService',

        function ($rootScope, $q, ActivationSearchAPIService, ModalService) {

            /*============ SERVICE DECLARATION ============*/

            var CollisionService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var getActiveActivations = function (student) {

                return ActivationSearchAPIService.getActiveActivationsForStudent([student.cccId]).then(function (activations) {

                    var activeActivations = [];

                    _.each(activations, function (activation) {

                        if (activation.status === "READY" || activation.status === "IN_PROGRESS" || activation.status === "PAUSED") {

                            activeActivations.push(activation);
                        }
                    });

                    return activeActivations;
                });
            };

            var checkForDuplicateActivations = function (newActivations, student) {

                return getActiveActivations(student).then(function (oldActivations) {

                    var duplicateActivations = [];

                    _.each(newActivations, function (newActivation) {
                        _.each(oldActivations, function (oldActivation) {

                            if (newActivation.activationId) {
                                return;
                            }

                            if (newActivation.assessmentScopedIdentifier.identifier === oldActivation.assessmentScopedIdentifier.identifier) {

                                duplicateActivations.push(oldActivation);
                            }
                        });
                    });

                    return duplicateActivations;
                });
            };

            var getActivationsToDeactivate = function (duplicateActivations, student) {
                var deferred = $q.defer();

                if (duplicateActivations.length > 0) {

                    var completionModal = ModalService.openCustomModal(
                        // title
                        "Activation Collision",
                        // template
                        "<ccc-modal-activation-collision student='student' activation='duplicateActivations' modal='modal'></ccc-modal-activation-collision>",
                        // data
                        {student: student, duplicateActivations: duplicateActivations},
                        // preventClose
                        true);

                    completionModal.result.then(function () {

                        deferred.resolve(duplicateActivations);

                    }, function () {

                        deferred.reject();

                    });

                } else {
                    deferred.resolve([]);
                }

                return deferred.promise;
            };

            var deactivateActivations = function (activationsToDeactivate) {
                var deactivationPromises = [];
                var reason = 'Collision';

                _.each(activationsToDeactivate, function (activation) {
                    deactivationPromises.push(activation.deactivate(reason));
                });

                return $q.all(deactivationPromises);
            };


            /*============ SERVICE DEFINITION ============*/

            CollisionService = {

                /**
                 * @param  {array} activationsIn
                 * @param  {object} student
                 * @return {promise} Resolves to list of duplicate activations.
                 */
                getDuplicateActivations: function (activationsIn, student) {
                    return checkForDuplicateActivations(activationsIn, student);
                },

                /**
                 * @param  {array} duplicateActivations
                 * @param  {object} student
                 * @return {promise} Resolves to list of deactivated activations. Rejects to 'true' if user cancels, and to (err) if deactivation failed.
                 */
                openResolutionModal: function (duplicateActivations, student) {

                    return getActivationsToDeactivate(duplicateActivations, student).then(function (activationsToDeactivate) {

                        return deactivateActivations(activationsToDeactivate).then(function () {

                            return activationsToDeactivate;

                        }, function (err) {
                            // actual error
                            return $q.reject(err);
                        });

                    }, function () {
                        // user rejected
                        return $q.reject(true);
                    });
                }
            };


            /*============ LISTENERS ============*/

            /*============ INITIALIZATION ============*/



            /*============ SERVICE PASSBACK ============*/

            return CollisionService;

        }
    ]);

})();
