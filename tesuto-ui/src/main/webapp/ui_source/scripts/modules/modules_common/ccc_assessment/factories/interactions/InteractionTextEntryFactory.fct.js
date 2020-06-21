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
     * InteractionTextEntry class factory
     */

    angular.module('CCC.Assessment').factory('InteractionTextEntryFactory', [

        'InteractionFactory',

        function (InteractionFactory) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var getValidateResponsesValidations = function () {

                var that = this;
                var validateResponseValidations = [];

                var currentValue = $.trim(that.values[0]);

                if (that.regex) {

                    if (currentValue.match(that.regex) === null) {

                        validateResponseValidations.push({
                            error: 'interaction.textEntry.patternMask.invalid',
                            msg: 'CCC_ASSESSMENT.INTERACTION.TEXT_ENTRY.VALIDATION.MASK_INVALID'
                        });
                    }
                }


                return validateResponseValidations;
            };

            var getAllowSkippingValidations = function () {

                var that = this;
                var allowSkippingValidations = [];

                var isStringEmpty = $.trim(that.values[0]) === '';

                if (isStringEmpty) {

                    allowSkippingValidations.push({
                        error: 'interaction.textEntry.value.invalid',
                        msg: 'CCC_ASSESSMENT.INTERACTION.TEXT_ENTRY.VALIDATION.EMPTY'
                    });
                }

                return allowSkippingValidations;
            };


            /*============ CLASS DECLARATION ============*/

            var InteractionTextEntry = function (interactionData) {

                /*============= DEFAULTS =============*/

                var defaults = {

                    // QTI Specs: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10333
                    placeholderText: null,      // In visual environments, string interactions are typically represented by empty boxes into which the candidate writes or types. However, in speech based environments it is helpful to have some placeholder text that can be used to vocalize the interaction.
                    expectedLength: 0,          // The expectedLength attribute provides a hint to the candidate as to the expected overall length of the desired response measured in number of characters. A Delivery Engine should use the value of this attribute to set the size of the response box, where applicable. This is not a validity constraint.
                    patternMask: null,          // If given, the pattern mask specifies a regular expression that the candidate's response must match in order to be considered valid.

                    // VALIDATION METHOD OVERRIDES
                    getAllowSkippingValidations: getAllowSkippingValidations,
                    getValidateResponsesValidations: getValidateResponsesValidations
                };


                /*=========== INSTANCE INIT AND CONFIG MERGE ============*/

                // Gather final configs and Extend the InteractionFactory base class
                var that = this;
                var interactionConfigs = $.extend(true, {}, defaults, interactionData, {
                    regex: false
                });
                InteractionFactory.call(that, interactionConfigs);


                /*=========== PRIVATE VARIABLES AND METHODS =============*/

                var regexRegex = /^\/(.*)\/([a-z]+)$/i; // :-) this is a regex to test regex format

                var validateModel = function () {
                    // coerce some values
                    that.placeholderText = that.placeholderText || "";
                    that.expectedLength = parseInt(that.expectedLength);

                    // determine if there is a valid pattern mask
                    if (that.patternMask) {
                        try {

                            var matches = regexRegex.exec(that.patternMask);
                            var regexPattern = matches[1];
                            var regexFlags = matches[2];
                            that.regex = new RegExp(regexPattern, regexFlags);

                        } catch (e) {
                            // todo, log this as an error to ther server
                            console.log("Error parsing regex patternMask for textEntry", e, that.patternMask);
                        }
                    }
                };


                /*============ INITIALIZATION ============*/

                validateModel();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return InteractionTextEntry;

        }
    ]);

})();


