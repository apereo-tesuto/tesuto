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
     * Interaction abstract class factory : base class for all interaction factories
     */

    angular.module('CCC.Assessment').factory('InteractionFactory', [

        function () {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var validationNoop = function () {
                throw new Error('Interaction: validation method must be overwritten');
            };


            /*============ CLASS DECLARATION ============*/

            var Interaction = function (interactionConfigs) {

                // basic contract for properties that should be on the data passed in during initialization
                var defaults = {
                    id: false,
                    type: '',
                    prompt: '',
                    responseIdentifier: false,

                    // the final set of values to be collected and sent to the server
                    values: [],

                    useSoftValidation: false,                           // if validateResponses is false and allowSkipping is true, the user can still see validation messages but will still be able to move along

                    hideInternalResponseValidationErrors: false,        // CONTRACT : Interaction must watch this flag and hide it's own internal validateResponeErrors ( like match interaction )

                    allowSkipping: false,                               // this will force the interaction to validate that it is answered (non empty)
                    allowSkippingErrors: [],                            // array of errors from getValidateResponsesValidations

                    validateResponses: true,                           // this will force the interaction to spit back all validation errors based on configs
                    validateResponsesErrors: [],                        // array of errors from getAllowSkippingValidations

                    getAllowSkippingValidations: validationNoop,        // CONTRACT : interaction class must override
                    getValidateResponsesValidations: validationNoop,    // CONTRACT : interaction class must override

                    validationErrors: []                                // Just a roll up of the other validation arrays
                };

                // we simply tack on merged properties during initialization
                var that = this;
                $.extend(true, that, defaults, interactionConfigs);


                /*============== PRIVATE VARIABLES / METHODS ============*/


                /*============== PUBLIC METHODS =============*/

                // this method should return an array of error messages that indicate it is not suitable for submission
                that.validate = function () {

                    that.validationErrors = [];

                    // first we gather all validateResponse errors
                    that.validateResponsesErrors = that.getValidateResponsesValidations.call(that);

                    // next we gather all allowSkipping validations
                    that.allowSkippingErrors = that.getAllowSkippingValidations.call(that);

                    // if validateResponses is true then we have to add on the validateResponseErrors
                    if (that.validateResponses) {
                        that.validationErrors = that.validationErrors.concat(that.validateResponsesErrors);
                    }

                    // if the author wants the user to see validation hints but not enforce them for submission
                    // then they can do so by having validateResponses to false, allowSkipping to true, and useSoftValidation to true
                    if (!that.validateResponses && that.useSoftValidation && that.allowSkipping) {
                        that.validationErrors = that.validationErrors.concat(that.validateResponsesErrors);
                    }

                    // if we haven't tripped validation on normal QTI based validations, then consider adding allowSkipping errors
                    // this will reduce the number of errors the user sees and avoid showing errors that will likely be cleared once the validationErrors are cleared
                    if (that.validationErrors.length === 0) {

                        // here we take a look at the flags and determine which sets of validationErrors should be added to the global group that will end up triggering a validation warning
                        // if allowSkipping is true, then don't add in any allowSkippingErrors
                        if (!that.allowSkipping) {
                            that.validationErrors = that.validationErrors.concat(that.allowSkippingErrors);
                        }
                    }

                    // validation errors list all errors that should show up in the UI and that should trigger a modal warning
                    return that.validationErrors;
                };


                /*============ INITIALIZATION ============*/

                // this is the once use case where we want the interaction to hide all of it's internal validation
                // interactions should visually and non-visually hide any indication there is an error
                if (!that.validateResponses && !that.useSoftValidation) {
                    that.hideInternalResponseValidationErrors = true;
                }
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return Interaction;

        }
    ]);

})();
